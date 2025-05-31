package com.example.attendancemonitoring;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddSubjectActivity extends AppCompatActivity {
    private Button addBtn;
    private RecyclerView subjects;
    private SubjectAdapter adapter;
    private FirebaseFirestore db;
    private ProgressBar pb;
    private TextView loadingtxt;
    private String date, time;
    private List<SubjectClass> subjectClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);
        addBtn = findViewById(R.id.btnAddDetails);
        subjects = findViewById(R.id.subjectList);
        subjectClasses = new ArrayList<>();
        pb = findViewById(R.id.pbSubject);
        loadingtxt = findViewById(R.id.loadingText);
        db = FirebaseFirestore.getInstance();
        subjects.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectAdapter(this, subjectClasses);
        subjects.setAdapter(adapter);
        getDateAndTime();

        populateList();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToDetails = new Intent(AddSubjectActivity.this, SubjectDetailsActivity.class);
                startActivity(goToDetails);
            }
        });
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
    private void populateList() {
        try {


            db.collection("Subjects")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    SubjectClass subjectClass = new SubjectClass(documentSnapshot.getString("section_code"),
                                            documentSnapshot.getString("course_code"),
                                            documentSnapshot.getString("descriptive_title"),
                                            documentSnapshot.getString("units"),
                                            documentSnapshot.getString("day"),
                                            documentSnapshot.getString("time_start"),
                                            documentSnapshot.getString("time_end"),
                                            documentSnapshot.getString("category")
                                            ).setTeacherEmail(documentSnapshot.getString("teacher"));
                                    subjectClasses.add(subjectClass);
                                    pb.setVisibility(View.INVISIBLE);
                                    loadingtxt.setVisibility(View.INVISIBLE);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(AddSubjectActivity.this, "Cant retrieve data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            String content = intentResult.getContents();

            if (content != null) {

                String section = adapter.getSectionCode();
                checkDuplicate(content,section);


                //Open camera again

                openCamera();

            }
        }
    }

    private void openCamera()
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan QR Code for Attendance");
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.initiateScan();
    }

    private void checkDuplicate(String content, String section)
    {
        db.collection("Attendance")
                .whereEqualTo("name", content)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Check if any documents are returned
                            if (task.getResult().isEmpty()) {
                                // If no documents exist, proceed to add the new document
                                saveAttendance(content, section);
                            } else {
                                // If a document already exists, display a message to the user
                                openCamera();
                                Toast.makeText(AddSubjectActivity.this, "Attendance already exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle errors
                            Toast.makeText(AddSubjectActivity.this, "Error checking attendance: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveAttendance(String content, String section)
    {
        AttendanceClass attendanceClass = new AttendanceClass(section,content,date,time);
        db.collection("Attendance")
                .add(attendanceClass)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddSubjectActivity.this, "Attendance added successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddSubjectActivity.this, "Attendance failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


}