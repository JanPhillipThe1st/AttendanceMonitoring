package com.example.attendancemonitoring;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;

public class TeacherScannerActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String date, time, section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_scanner);
        Intent getSection = getIntent();
        section=getSection.getStringExtra("sectionCode");
        scanQR();
        db=FirebaseFirestore.getInstance();
        getDateAndTime();




    }

    private void scanQR()
    {
        IntentIntegrator intentIntegrator = new IntentIntegrator(TeacherScannerActivity.this);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setPrompt("Scan QR Code for Attendance");
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult!=null)
        {
            String content=intentResult.getContents();
            if(content !=null)
            {
                String fullName = intentResult.getContents();
                saveAttendance(fullName);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveAttendance(String fullName)
    {
        try{
            AttendanceClass attendanceClass = new AttendanceClass(section,fullName,date,time);
            db.collection("Attendance")
                    .add(attendanceClass)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            scanQR();
                            Toast.makeText(TeacherScannerActivity.this, "Attendance added successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TeacherScannerActivity.this, "Attendance Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }catch (Exception e){
            Toast.makeText(this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private void getDateAndTime()
    {
        Calendar calendar= Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so we add 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        date = month+"/"+day+" "+ year;
        time = hour+ ":"+ minute+":"+second;
    }


}