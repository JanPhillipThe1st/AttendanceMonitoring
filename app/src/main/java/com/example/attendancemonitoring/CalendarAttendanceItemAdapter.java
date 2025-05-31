package com.example.attendancemonitoring;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.hdodenhof.circleimageview.CircleImageView;

public class CalendarAttendanceItemAdapter extends RecyclerView.Adapter<CalendarAttendanceItemAdapter.EventViewHolder> {
    private List<CalendarAttendanceClass> eventList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public CalendarAttendanceItemAdapter(List<CalendarAttendanceClass> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        CalendarAttendanceClass event = eventList.get(position);
        Picasso.get().load(event.studentImageUrl).into(holder.civStudentImage);
        holder.tvStudentName.setText(event.studentName);
        holder.tvStudentRemark.setText(event.studentAttendanceRemark);
        if (event.studentAttendanceRemark.equals("Absent")){
        holder.tvStudentRemark.setTextColor(Color.RED);
        } else if (event.studentAttendanceRemark.equals("Late")) {
        holder.tvStudentRemark.setTextColor(Color.parseColor("#DD8D02"));
        }
        else if (event.studentAttendanceRemark.equals("Excused")) {
            holder.tvStudentRemark.setTextColor(Color.parseColor("#FF9D3D"));
        }
        holder.tvDateAttended.setText(event.date + " "+ event.time);

        holder.itemView.setOnLongClickListener(view -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(view.getContext());
            alertDialogBuilder.setTitle("Mark as excused").setMessage("Are you sure you want to mark this student as excused?")
                    .setPositiveButton("YES",(dialogInterface, i) -> {
                        Map<String,Object> objectMap = new HashMap<>();
                        objectMap.put("attendance_remark","Excused");
                        db.collection("Attendance").document(event.documentID).update(objectMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(view.getContext(), "Student Successfully marked as excused!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).setNegativeButton("NO",(dialogInterface, i) -> {dialogInterface.dismiss();}).create().show();
            return  true;
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civStudentImage;
        TextView tvStudentName,tvStudentRemark, tvDateAttended;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            civStudentImage = itemView.findViewById(R.id.civStudentImage);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentRemark = itemView.findViewById(R.id.tvStudentRemark);
            tvDateAttended = itemView.findViewById(R.id.tvDateAttended);
        }
    }
}
