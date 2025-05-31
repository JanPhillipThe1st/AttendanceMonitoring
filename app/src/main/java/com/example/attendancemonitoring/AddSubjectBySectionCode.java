package com.example.attendancemonitoring;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddSubjectBySectionCode extends AppCompatActivity {

    Button btnSearchStudentByEDP;
    TextView tvSectionCode;
    LinearLayout btnScanStudentQR;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String studentEDP = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject_by_section_code);
        btnSearchStudentByEDP  = findViewById(R.id.btnSearchStudentByEDP);
        btnScanStudentQR = findViewById(R.id.btnScanStudentQR);
        tvSectionCode = findViewById(R.id.tvSectionCode);
        studentEDP = getIntent().getStringExtra("EDPNumber");
        //Get the section code as an intent
        String sectionCode = getIntent().getStringExtra("sectionCode");
        //Create a progress dialog feedback for searching student
        ProgressDialog progressDialog = new ProgressDialog(AddSubjectBySectionCode.this);
        progressDialog.setTitle("Searching for Subject...");
        progressDialog.setMessage("This may take a while depending on your connection.");


        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.subject_details_card, null);

        TextView tvSubjectDescription = dialogView.findViewById(R.id.tvSubjectDescription);
        TextView tvSubjectRoom = dialogView.findViewById(R.id.tvSubjectRoom);
        TextView tvCourseCode = dialogView.findViewById(R.id.tvCourseCode);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnJoin = dialogView.findViewById(R.id.btnJoin);

        //We also need to create a feedback dialog for displaying query result if no student is found
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddSubjectBySectionCode.this);
        alertDialogBuilder.setTitle("Subject Search");
        alertDialogBuilder.setMessage("Sorry! Search returned no result. Please try again.");
        alertDialogBuilder.setPositiveButton("TRY AGAIN.",(dialogInterface, i) -> {
            dialogInterface.dismiss();
            //Then focus on the EDP number text field
            tvSectionCode.setFocusableInTouchMode(true);
            tvSectionCode.requestFocus();
        });

        btnSearchStudentByEDP.setOnClickListener(view -> {
            //Show the progress dialog here while we search for the student online
            progressDialog.show();
            db.collection("Subjects").where(Filter.equalTo("section_code",tvSectionCode.getText().toString())).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                            progressDialog.dismiss();
                        if (queryDocumentSnapshots.size() < 1){
                            //Show empty result dialog
                            alertDialogBuilder.create().show();
                        }
                        else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setView(dialogView);
                            AlertDialog dialog = builder.create();
                            DocumentSnapshot subjectSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            tvSubjectDescription.setText(subjectSnapshot.getString("descriptive_title").toString());
                            tvSubjectRoom.append(subjectSnapshot.getString("room").toString());
                            tvCourseCode.append(subjectSnapshot.getString("course_code").toString());
                            btnCancel.setOnClickListener(view1 -> {
                                dialog.dismiss();
                            });
                            btnJoin.setOnClickListener(view1 -> {

                                dialog.dismiss();
                                progressDialog.setMessage("Joining Class...");
                                progressDialog.show();
                                Map<String,Object> requestMap = new HashMap<>();
                                requestMap.put("request_created", Calendar.getInstance().getTime());
                                requestMap.put("requested_by", studentEDP);
                                requestMap.put("request_type", "join_class");
                                requestMap.put("section_code", tvSectionCode.getText().toString());

                                //Check if the student is already in this subject
                                db.collection("Subjects").document(tvSectionCode.getText().toString()).get().addOnSuccessListener(documentSnapshot -> {
                                    Map<String,Object> subjectDataMap = documentSnapshot.getData();
                                    ArrayList<String> studentIDs = (ArrayList<String>) subjectDataMap.get("students");
                                    for (String studentID:studentIDs){
                                        if(studentID.trim().equals(studentEDP.trim().toString())){
                                            MaterialAlertDialogBuilder enrollAlertDialogBuilder = new MaterialAlertDialogBuilder(AddSubjectBySectionCode.this);
                                            enrollAlertDialogBuilder.setTitle("You are already enrolled in this class");
                                            enrollAlertDialogBuilder.setPositiveButton("OK",(enrollDialogInterface, enrollIndex) -> {
                                                dialog.dismiss();
                                                progressDialog.dismiss();
                                            });
                                            enrollAlertDialogBuilder.create().show();
                                            break;
                                        }
                                    }
                                    if (!studentIDs.contains(studentEDP.trim().toString())){
                                        //Check if student has already made the request

                                        db.collection("Requests").get().addOnSuccessListener(requestQueryDocumentSnapshots ->{
                                            boolean isAlreadyRequesting = false;
                                            for(DocumentSnapshot requestQueryDocument: requestQueryDocumentSnapshots.getDocuments()){
                                                if (requestQueryDocument.getString("requested_by").equals(studentEDP.trim().toString())){
                                                    isAlreadyRequesting = true;
                                                    break;
                                                }
                                            }
                                            if(!isAlreadyRequesting){
                                                db.collection("Requests").add(requestMap).addOnSuccessListener(unused -> {
                                                    progressDialog.dismiss();
                                                    MaterialAlertDialogBuilder alertDialogBuilderComplete = new MaterialAlertDialogBuilder(AddSubjectBySectionCode.this);
                                                    alertDialogBuilderComplete.setPositiveButton("OK",(dialogInterface1, i1) -> {
                                                        dialogInterface1.dismiss();
                                                        AddSubjectBySectionCode.this.finish();
                                                    });
                                                    alertDialogBuilderComplete.setMessage("Join request was sent. Please wait for the teacher to accept.");
                                                    alertDialogBuilderComplete.create().show();

                                                });
                                            }
                                            else{
                                                MaterialAlertDialogBuilder requestAlreadySentDialog = new MaterialAlertDialogBuilder(AddSubjectBySectionCode.this);
                                                requestAlreadySentDialog.setTitle("Join Request...").setMessage("You already sent a request to join this class. Please wait for confirmation")
                                                        .setPositiveButton("OK",(dialogInterface, i) -> {
                                                            dialogInterface.dismiss();
                                                            progressDialog.dismiss();
                                                        }).create().show();
                                            }
                                        });

                                    }
                                });

                            });
                            dialog.show();

                        }
                    });

        });
        btnScanStudentQR.setOnClickListener(view -> {
        });
    }

}