package com.example.attendancemonitoring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceHolder> {
    Context context;
    List<AttendanceClass> list;

    public AttendanceAdapter(Context context, List<AttendanceClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AttendanceAdapter.AttendanceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendancedetails,parent,false);
        return new AttendanceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.AttendanceHolder holder, int position)
    {
        AttendanceClass attendanceClass = list.get(position);
        holder.name.setText(attendanceClass.getName());
        holder.aTime.setText(attendanceClass.getTime());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AttendanceHolder extends RecyclerView.ViewHolder {
        TextView name, aTime;
        public AttendanceHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.txtAttendanceName);
            aTime=itemView.findViewById(R.id.txtAttendanceTime);
        }
    }


}
