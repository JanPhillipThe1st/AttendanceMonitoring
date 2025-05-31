package com.example.attendancemonitoring;

import java.util.Calendar;
public class CalendarAttendanceClass {
    public CalendarAttendanceClass(String studentName, String studentImageUrl, String date, String time, String studentEDP, String studentAttendanceRemark,Calendar calendar,String documentID) {
        this.studentName = studentName;
        this.studentImageUrl = studentImageUrl;
        this.date = date;
        this.calendar = calendar;
        this.time = time;
        this.studentEDP = studentEDP;
        this.studentAttendanceRemark = studentAttendanceRemark;
        this.documentID = documentID;
    }

    public String studentName;
    public String documentID;
    public Calendar calendar;
    public String studentImageUrl;
    public String date;
    public String time;
    public String studentEDP;
    public String studentAttendanceRemark;


}

