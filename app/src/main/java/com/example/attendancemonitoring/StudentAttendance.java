package com.example.attendancemonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

public class StudentAttendance extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String edpNumber = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);
        TextView tvTitle = findViewById(R.id.tvTitle);
        androidx.appcompat.widget.Toolbar tb = findViewById(R.id.tb);
        setSupportActionBar(findViewById(R.id.tb));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tb.setNavigationOnClickListener(v -> {
            finish();
        });
        Intent intent = getIntent();
        edpNumber = intent.getStringExtra("EDPNumber");
        ArrayList<StudentAttendanceItem> studentAttendanceItems= new ArrayList<StudentAttendanceItem>();
        StudentAttendanceItemAdapter studentAttendanceItemAdapter =new StudentAttendanceItemAdapter(this,studentAttendanceItems);
        db.collection("Attendance").where(Filter.equalTo("EDPNumber",edpNumber)).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot attendanceData:queryDocumentSnapshots.getDocuments()){
                //After getting all attendance data, get the subject data with the specified sectionCode
                db.collection("Subjects").document(attendanceData.getString("sectionCode")).get().addOnCompleteListener(subjectDataTask -> {
                    DocumentSnapshot subjectData = subjectDataTask.getResult();
                    Calendar calendar =Calendar.getInstance();
                    studentAttendanceItems.add(new StudentAttendanceItem(attendanceData.getId(),
                            attendanceData.getString("date"),
                            attendanceData.getString("name"),
                            attendanceData.getString("sectionCode"),
                            (attendanceData.getString("time")),
                            subjectData.getString("room"),
                            subjectData.getString("course_code"),
                            subjectData.getString("descriptive_title")
                    ));
                    tb.setTitle("My Attendance ("+queryDocumentSnapshots.size()+")");
                    if(queryDocumentSnapshots.size() >= 2){
                        tb.setTitle("My Attendances ("+queryDocumentSnapshots.size()+")");
                    }
                    ListView lvStudentAttendances = findViewById(R.id.lvAttendance);
                    lvStudentAttendances.setAdapter(studentAttendanceItemAdapter);

                });

            }


        });

    }
}