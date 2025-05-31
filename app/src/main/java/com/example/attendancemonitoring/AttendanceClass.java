package com.example.attendancemonitoring;

import androidx.annotation.NonNull;

public class AttendanceClass {
   private String sectionCode, name, date,time;

   public AttendanceClass(){}

    public AttendanceClass(String sectionCode, String name, String date, String time) {
        this.sectionCode = sectionCode;
        this.name = name;
        this.date = date;
        this.time = time;
    }


    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
