package com.example.attendancemonitoring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class StudentAttendanceItemAdapter extends ArrayAdapter<StudentAttendanceItem> {
    public StudentAttendanceItemAdapter(@NonNull Context context, ArrayList<StudentAttendanceItem> studentAttendances) {
        super(context, 0, studentAttendances);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        StudentAttendanceItem item =getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.student_attendance_item, parent, false);
        }

        TextView tvDate = convertView.findViewById(R.id.tvDate);
        TextView  tvTime = convertView.findViewById(R.id.tvTime);
        TextView tvSectionCode = convertView.findViewById(R.id.tvSectionCode);
        TextView tvSubject = convertView.findViewById(R.id.tvSubject);
        TextView tvDescriptiveTitle = convertView.findViewById(R.id.tvDescriptiveTitle);
        TextView tvRoom = convertView.findViewById(R.id.tvRoom);

        tvDate.setText(item.date.toString());
        tvTime.setText(item.time);
        tvSubject.setText(item.subjectCode);
        tvDescriptiveTitle.setText(item.descriptiveTitle);
        tvRoom.setText(item.room);
        tvSectionCode.setText(String.valueOf(item.sectionCode));
        return convertView;
    }
}
