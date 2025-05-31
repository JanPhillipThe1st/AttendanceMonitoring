package com.example.attendancemonitoring;

import java.util.HashMap;
import java.util.Map;

public class AttendanceQRParser {
    public  String attendanceString = "";
    public Map<String,String> parseAttendanceString(String attendanceString){
    Map<String,String> attendanceDetailsMap = new HashMap<String,String>();
        //This will function as a way to get the right attendance details
        /*Having the section code is not enough. We need to cross-match the section code
        with the section name, teacher and passcode
        1. Each student passcode will be different
        */
        return  attendanceDetailsMap;

    }
}
