package com.example.attendancemonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;

import java.util.List;

public class AdminSubjectAdapter extends RecyclerView.Adapter<AdminSubjectAdapter.SubjectHolder> {

    Context context;
    List<SubjectClass> list;
    String sectionCode;


    public AdminSubjectAdapter(Context context, List<SubjectClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AdminSubjectAdapter.SubjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subjectdisplay,parent,false);
        return new SubjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminSubjectAdapter.SubjectHolder holder, int position) {
        SubjectClass subjectClass = list.get(position);
        holder.txtDescriptiveTitle.setText(subjectClass.getDescriptiveTitle());
        holder.txtSectionCode.setText(subjectClass.getCodeSection());
        holder.txtCourseCode.setText(subjectClass.getCourseCode());
        holder.txtDay.setText(subjectClass.getDay());
        holder.txtTimeStart.setText(subjectClass.getTime_start());
        holder.txtTimeEnd.setText(subjectClass.getTime_end());

        switch (subjectClass.getCategory()) {
            case "TECHVOC":
                holder.ivCategory.setImageResource(R.drawable.techvoc);
                break;
            case "SCIENCE":
                holder.ivCategory.setImageResource(R.drawable.science);
                break;
            case "LANGUAGE":
                holder.ivCategory.setImageResource(R.drawable.language);
                break;
            case "PE":
                holder.ivCategory.setImageResource(R.drawable.phys_ed);
                break;
            case "REL_ED":
                holder.ivCategory.setImageResource(R.drawable.rel_ed);
                break;
            case "GEN_ED":
                holder.ivCategory.setImageResource(R.drawable.gen_ed);
                break;

        }


        holder.btnGenerateQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userType = "Teacher";
                Intent checkAttendance = new Intent(context, CheckAttendance.class);
                checkAttendance.putExtra("userType",userType);
                checkAttendance.putExtra("section",subjectClass.getCodeSection());
                checkAttendance.putExtra("time_start",subjectClass.getTime_start());
                checkAttendance.putExtra("day",subjectClass.getDay());
                checkAttendance.putExtra("time_end",subjectClass.getTime_end());
                context.startActivity(checkAttendance);
            }
        });


        holder.btnAttendee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attendee = new Intent(context, ViewStudentInSubject.class);
                attendee.putExtra("subject_code",subjectClass.getCodeSection());
                context.startActivity(attendee);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public static class SubjectHolder extends RecyclerView.ViewHolder{

        TextView txtDescriptiveTitle;
        ImageView ivCategory;
        TextView txtSectionCode;
        TextView txtCourseCode;
        TextView txtDay;
        TextView txtTimeStart;
        TextView txtTimeEnd;
        Button btnGenerateQRCode, btnAttendee;
        public SubjectHolder(@NonNull View itemView) {
            super(itemView);
            txtDescriptiveTitle = itemView.findViewById(R.id.txtDDescriptiveTitle);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            txtSectionCode = itemView.findViewById(R.id.txtDSectionCode);
            txtCourseCode = itemView.findViewById(R.id.txtDCourseCode);
            txtDay = itemView.findViewById(R.id.txtDDay);
            txtTimeStart = itemView.findViewById(R.id.txtTimeStart);
            txtTimeEnd = itemView.findViewById(R.id.txtTimeEnd);
            btnGenerateQRCode=itemView.findViewById(R.id.btnGenerateQRCode);
            btnAttendee=itemView.findViewById(R.id.btnAttendee);
        }
    }
}
