package com.example.attendancemonitoring;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentJoinRequestAdapter extends RecyclerView.Adapter<StudentJoinRequestAdapter.StudentInformationHolder> {

    Context context;
    List<Map<String, Object>> list;
    String sectionCode;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public StudentJoinRequestAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public StudentJoinRequestAdapter.StudentInformationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_join_request_card,parent,false);
        return new StudentInformationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentJoinRequestAdapter.StudentInformationHolder holder, int position) {
        Map<String,Object> studentData = list.get(position);
        String studentName = studentData.get("firstname").toString() +" " + studentData.get("lastName").toString();
        if (studentName.length()>20) studentName = studentName.substring(0,20);
        holder.tvStudentName.setText(studentName);
        holder.tvStudentGrade.setText("GRADE "+studentData.get("grade").toString());
        holder.tvStudentStrand.setText("TRACK/STRAND: "+ studentData.get("track_strand").toString());
        Picasso.get().load(studentData.get("imageUrl").toString()).into(holder.civStudentImage);
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context);
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Processing request...");
            alertDialogBuilder.setTitle("Manage Requests");
            alertDialogBuilder.setNegativeButton("NO",(dialogInterface, i) -> {
                dialogInterface.dismiss();
            });

        holder.btnAccept.setOnClickListener(view -> {
            alertDialogBuilder.setPositiveButton("YES",(dialogInterface, i) -> {
                progressDialog.show();
                dialogInterface.dismiss();
                db.collection("Subjects").document(studentData.get("section_code").toString()).update("students", FieldValue.arrayUnion(studentData.get("EDPNumber").toString())).addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    MaterialAlertDialogBuilder acceptConfirmed = new MaterialAlertDialogBuilder(context);
                    acceptConfirmed.setTitle("Student Added.");
                    acceptConfirmed.setPositiveButton("NICE!",(dialogInterface1, i1) -> {
                        dialogInterface1.dismiss();
                        db.collection("Requests").where(Filter.equalTo("requested_by",studentData.get("EDPNumber").toString()))
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (DocumentSnapshot request : queryDocumentSnapshots){
                                        db.collection("Requests").document(request.getId()).delete();
                                    }
                                    Activity activity = (Activity) holder.itemView.getContext();
                                    activity.finish();
                                });
                    });
                    acceptConfirmed.create().show();

                });
            });
            alertDialogBuilder.setMessage("Accept this student?");
            alertDialogBuilder.create().show();

        });
        holder.btnReject.setOnClickListener(view -> {
            alertDialogBuilder.setPositiveButton("YES",(dialogInterface, i) -> {
                dialogInterface.dismiss();
                    MaterialAlertDialogBuilder acceptConfirmed = new MaterialAlertDialogBuilder(context);
                    progressDialog.setTitle("Deleting request...");
                    progressDialog.show();
                    acceptConfirmed.setTitle("This request has been deleted");
                    acceptConfirmed.setPositiveButton("OK",(dialogInterface1, i1) -> {
                        dialogInterface1.dismiss();
                        db.collection("Requests").where(Filter.equalTo("requested_by",studentData.get("EDPNumber").toString()))
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (DocumentSnapshot request : queryDocumentSnapshots){
                                        db.collection("Requests").document(request.getId()).delete();
                                    }
                                    progressDialog.dismiss();

                                });
                    });
                    acceptConfirmed.create().show();

            });
            alertDialogBuilder.setMessage("Delete this request?");
            alertDialogBuilder.create().show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public static class StudentInformationHolder extends RecyclerView.ViewHolder{

        CircleImageView civStudentImage;
        TextView tvStudentName;
        TextView tvStudentGrade;
        TextView tvStudentStrand;
        LinearProgressIndicator lpiPunctuality;
        ImageButton btnReject,btnAccept;
        public StudentInformationHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentGrade = itemView.findViewById(R.id.tvStudentGrade);
            tvStudentStrand = itemView.findViewById(R.id.tvStudentStrand);
            lpiPunctuality = itemView.findViewById(R.id.lpiPunctuality);
            civStudentImage = itemView.findViewById(R.id.civStudentImage);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnAccept = itemView.findViewById(R.id.btnAccept);
        }
    }
}
