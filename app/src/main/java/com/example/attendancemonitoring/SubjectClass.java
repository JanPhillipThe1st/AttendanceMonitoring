package com.example.attendancemonitoring;

public class SubjectClass
{
    private String codeSection, courseCode, descriptiveTitle, units,day,time_start,time_end,category,room;
    public String documentId;

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public SubjectClass setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
        return  this;
    }

    public String teacherEmail = "";
    public SubjectClass() {
    }
    public SubjectClass(String codeSection, String courseCode, String descriptiveTitle, String units, String day, String time_start,String time_end,String category) {
        this.codeSection = codeSection;
        this.courseCode = courseCode;
        this.descriptiveTitle = descriptiveTitle;
        this.units = units;
        this.day = day;
        this.time_start = time_start;
        this.time_end = time_end;
        this.category = category;
    }
    ///@documentID
    public SubjectClass(String documentId,String codeSection, String courseCode, String descriptiveTitle, String units, String day, String time_start,String time_end,String category,String room) {
        this.documentId = documentId;
        this.codeSection = codeSection;
        this.courseCode = courseCode;
        this.descriptiveTitle = descriptiveTitle;
        this.units = units;
        this.day = day;
        this.time_start = time_start;
        this.time_end = time_end;
        this.room = room;
        this.category = category;
    }

    public String getCodeSection() {
        return codeSection;
    }

    public void setCodeSection(String codeSection) {
        this.codeSection = codeSection;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getDescriptiveTitle() {
        return descriptiveTitle;
    }

    public void setDescriptiveTitle(String descriptiveTitle) {
        this.descriptiveTitle = descriptiveTitle;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
