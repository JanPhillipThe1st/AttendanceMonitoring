package com.example.attendancemonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectHolder> {

    Context context;
    List<SubjectClass> list;
    Calendar calendar = Calendar.getInstance();
    String sectionCode;


    public SubjectAdapter(Context context, List<SubjectClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SubjectAdapter.SubjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subjectdisplay,parent,false);
        return new SubjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectAdapter.SubjectHolder holder, int position) {
        SubjectClass subjectClass = list.get(position);
        holder.txtDescriptiveTitle.setText(subjectClass.getDescriptiveTitle());
        holder.txtSectionCode.setText(subjectClass.getCodeSection());
        holder.txtCourseCode.setText(subjectClass.getCourseCode());
        holder.txtDay.setText(subjectClass.getDay());
        holder.txtTimeStart.setText(subjectClass.getTime_start());
        holder.txtTimeEnd.setText(subjectClass.getTime_end());
        if (subjectClass.teacherEmail.equals("")){
            holder.lvTeacherEmail.setVisibility(View.GONE);
        }else{
            holder.lvTeacherEmail.setVisibility(View.VISIBLE);
        }
        holder.tvTeacherEmail.setText(subjectClass.teacherEmail.toString());

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
                //Check if we can check the attendance today
                //Now we need to check if our student is allowed to enter the class
                //First, we get the current time
                Calendar time_of_attendance = Calendar.getInstance();
                //Then get the int equivalent
                int time_of_attendance_int_value = 0;
                time_of_attendance_int_value+= (time_of_attendance.get(Calendar.HOUR_OF_DAY) *60);
                time_of_attendance_int_value += time_of_attendance.get(Calendar.MINUTE);
                //Now we ask if he is within the time schedule

                // Create the dialog
                MaterialAlertDialogBuilder notTimeDialog = new MaterialAlertDialogBuilder(context);
                notTimeDialog.setTitle("Attendance Not Available")
                        .setMessage("You cannot check attendance because it is not time yet. Please try again later.")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            // Dismiss the dialog when OK is clicked
                            dialogInterface.dismiss();
                        });

                if (subjectClass.getDay().toString().contains("M-F") && (calendar.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY && calendar.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY)){
                    if(time_of_attendance_int_value >= Integer.parseInt(barcodeDataParser(subjectClass).get("time_start_int_value").toString())
                            && time_of_attendance_int_value <= Integer.parseInt(barcodeDataParser(subjectClass).get("time_end_int_value").toString())){
                        String userType = "Teacher";
                        Intent checkAttendance = new Intent(context, CheckAttendance.class);
                        checkAttendance.putExtra("userType",userType);
                        checkAttendance.putExtra("section",subjectClass.getCodeSection());
                        checkAttendance.putExtra("time_start",subjectClass.getTime_start());
                        checkAttendance.putExtra("day",subjectClass.getDay());
                        checkAttendance.putExtra("time_end",subjectClass.getTime_end());
                        context.startActivity(checkAttendance);
                    }
                    else{
                        notTimeDialog.create().show();
                    }
                } else if (subjectClass.getDay().toString().contains("MW") && (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)) {
                    if(time_of_attendance_int_value >= Integer.parseInt(barcodeDataParser(subjectClass).get("time_start_int_value").toString())
                            && time_of_attendance_int_value <= Integer.parseInt(barcodeDataParser(subjectClass).get("time_end_int_value").toString())){
                        String userType = "Teacher";
                        Intent checkAttendance = new Intent(context, CheckAttendance.class);
                        checkAttendance.putExtra("userType",userType);
                        checkAttendance.putExtra("section",subjectClass.getCodeSection());
                        checkAttendance.putExtra("time_start",subjectClass.getTime_start());
                        checkAttendance.putExtra("day",subjectClass.getDay());
                        checkAttendance.putExtra("time_end",subjectClass.getTime_end());
                        context.startActivity(checkAttendance);
                    }
                    else{
                        notTimeDialog.create().show();
                    }
                }else if (subjectClass.getDay().toString().contains("TTH") && (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)) {
                    if(time_of_attendance_int_value >= Integer.parseInt(barcodeDataParser(subjectClass).get("time_start_int_value").toString())
                            && time_of_attendance_int_value <= Integer.parseInt(barcodeDataParser(subjectClass).get("time_end_int_value").toString())){
                        String userType = "Teacher";
                        Intent checkAttendance = new Intent(context, CheckAttendance.class);
                        checkAttendance.putExtra("userType",userType);
                        checkAttendance.putExtra("section",subjectClass.getCodeSection());
                        checkAttendance.putExtra("time_start",subjectClass.getTime_start());
                        checkAttendance.putExtra("day",subjectClass.getDay());
                        checkAttendance.putExtra("time_end",subjectClass.getTime_end());
                        context.startActivity(checkAttendance);
                    }
                    else{
                        notTimeDialog.create().show();
                    }
                }else if (subjectClass.getDay().toString().contains("SAT") && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    if(time_of_attendance_int_value >= Integer.parseInt(barcodeDataParser(subjectClass).get("time_start_int_value").toString())
                            && time_of_attendance_int_value <= Integer.parseInt(barcodeDataParser(subjectClass).get("time_end_int_value").toString())){
                        String userType = "Teacher";
                        Intent checkAttendance = new Intent(context, CheckAttendance.class);
                        checkAttendance.putExtra("userType",userType);
                        checkAttendance.putExtra("section",subjectClass.getCodeSection());
                        checkAttendance.putExtra("time_start",subjectClass.getTime_start());
                        checkAttendance.putExtra("day",subjectClass.getDay());
                        checkAttendance.putExtra("time_end",subjectClass.getTime_end());
                        context.startActivity(checkAttendance);
                    }
                    else{
                        notTimeDialog.create().show();
                    }
                }else{
//                    String userType = "Teacher";
//                    Intent checkAttendance = new Intent(context, CheckAttendance.class);
//                    checkAttendance.putExtra("userType",userType);
//                    checkAttendance.putExtra("section",subjectClass.getCodeSection());
//                    checkAttendance.putExtra("time_start",subjectClass.getTime_start());
//                    checkAttendance.putExtra("day",subjectClass.getDay());
//                    checkAttendance.putExtra("time_end",subjectClass.getTime_end());
//                    context.startActivity(checkAttendance);
//
                    notTimeDialog.create().show();
                }







            }
        });



        holder.btnAttendee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attendee = new Intent(context, ViewStudentInSubject.class);
                attendee.putExtra("section_code",subjectClass.getCodeSection());
                context.startActivity(attendee);
            }
        });
        holder.btnViewStudentAttendance.setOnClickListener(v -> {
            Intent attendee = new Intent(context, ViewStudentAttendance.class);
            attendee.putExtra("sectionCode",subjectClass.getCodeSection());
            context.startActivity(attendee);
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
        LinearLayout lvTeacherEmail;
        ImageView ivCategory;
        TextView txtSectionCode;
        TextView tvTeacherEmail;
        TextView txtCourseCode;
        TextView txtDay;
        TextView txtTimeStart;
        TextView txtTimeEnd;
        Button btnGenerateQRCode, btnAttendee,btnViewStudentAttendance;
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
            btnViewStudentAttendance=itemView.findViewById(R.id.btnViewStudentAttendance);
            tvTeacherEmail=itemView.findViewById(R.id.tvTeacherEmail);
            lvTeacherEmail=itemView.findViewById(R.id.lvTeacherEmail);
            btnAttendee=itemView.findViewById(R.id.btnAttendee);
        }
    }
    protected Map<String,Object> barcodeDataParser(SubjectClass subjectClass){
        Map<String,Object> result = new HashMap<String,Object>();
        //get the time (e.g.: 7:00 AM, 8:00 AM)
        result.put("time_start_int_value",timeStringToInt(subjectClass.getTime_start()));
        //get the time (e.g.: 7:00 AM, 8:00 AM)
        result.put("time_end_int_value",timeStringToInt(subjectClass.getTime_end()));
        //Algorithm to transform a time string to
        return result;
    }
    protected int timeStringToInt(String timeString){
        int result = 0;
        //Sample Time string should look like: '07:00 AM'
        //First, split the time string to an array ["07:00","AM"]
        String[] splitTimeString = timeString.split(" ");
        //The notation we will use is simple: "00:00" = 0, "01:00" = 60
        //if it is "PM", we assume it is past noon
        //That means it is now 12*60 = 720
        if(splitTimeString[1].contains("PM")){
            result += 720;
        }
        //Now we get the hour of day "07" from "07:00" and then multiply by 60
        result += Integer.parseInt(splitTimeString[0].split(":")[0]) * 60;
        //And then the minutes which just needs to be added
        result += Integer.parseInt(splitTimeString[0].split(":")[1]);
        return result;
    }
}
