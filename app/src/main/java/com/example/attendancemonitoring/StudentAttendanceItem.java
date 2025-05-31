package com.example.attendancemonitoring;

import java.util.Date;

public class StudentAttendanceItem {
    public String id = "";
    public String date = new Date().toString();
    public String name = "";
    public String sectionCode = "";
    public String time = "";
    public String room = "";
    public String subjectCode = "";
    public String descriptiveTitle = "";

    public StudentAttendanceItem(String id, String date, String name, String sectionCode, String time) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.sectionCode = sectionCode;
        this.time = time;
    }
    public StudentAttendanceItem(String id, String date, String name, String sectionCode, String time,String room,String subjectCode,String descriptiveTitle) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.sectionCode = sectionCode;
        this.time = time;
        this.room = room;
        this.subjectCode = subjectCode;
        this.descriptiveTitle = descriptiveTitle;
    }
}
