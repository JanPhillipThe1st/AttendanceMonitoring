package com.example.attendancemonitoring;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewStudentInSubject extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button btnAddStudents;

    private TextView badgeTextView;
    private int badgeCount = 0;
    ImageButton btnNotifications;
    RecyclerView rvStudentList;
    FirebaseAuth currentUser = FirebaseAuth.getInstance();
    private List<Map<String,Object>> studentClasses;
    StudentInformationAdapter adapter;
    int VIEW_REQUEST_CODE = 69;
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_in_subject);
        btnAddStudents = findViewById(R.id.btnAddStudents);
        btnNotifications = findViewById(R.id.btnNotifications);
        rvStudentList = findViewById(R.id.rvStudentList);
        studentClasses = new ArrayList<Map<String,Object>>();
        Intent intent = getIntent();
        String subjectDocumentID = intent.getStringExtra("section_code");
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setPositiveButton("OK",(dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        alertDialogBuilder.setTitle("Class Students");

        rvStudentList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentInformationAdapter(this, studentClasses);
        rvStudentList.setAdapter(adapter);

            Intent addStudent = new Intent(ViewStudentInSubject.this, AddStudentInSubject.class);
        //Get students in this subject
             btnAddStudents.setOnClickListener(view -> {
            addStudent.putExtra("sectionCode",subjectDocumentID);
            startActivityForResult(addStudent,VIEW_REQUEST_CODE);
        });


        try {
            //
        DocumentReference subject = db.collection("Subjects").document(subjectDocumentID);
        subject.get().addOnSuccessListener(documentSnapshot -> {
           Map<String,Object> subjectDataMap = documentSnapshot.getData();

            ArrayList<String> studentIDs = (ArrayList<String>) subjectDataMap.get("students");
            //Now we iterate through the array of numbers
            if (studentIDs.size() < 1){
                alertDialogBuilder.setNegativeButton("ADD STUDENTS",(dialogInterface, i) -> {

                    addStudent.putExtra("sectionCode",subjectDocumentID);
                    startActivity(addStudent);
                });
                alertDialogBuilder.setMessage("No students in this class");
                alertDialogBuilder.create().show();
            }
            for(String studentID : studentIDs){
                //Get the corresponding details with this data
                db.collection("StudentAccount").where(Filter.equalTo("EDPNumber",studentID)).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for(DocumentSnapshot studentInformation: queryDocumentSnapshots){
                    //Now we get the absences for each student
                            Map<String,Object> studentInformationMap = studentInformation.getData();
                        db.collection("Attendance").where(Filter.and(Filter.equalTo("attendance_remark","Absent"),Filter.equalTo("EDPNumber",studentInformationMap.get("EDPNumber")))).get().addOnSuccessListener(studentAttendanceSnapshot ->{
                           if (!studentAttendanceSnapshot.getDocuments().isEmpty()){
                            studentInformationMap.put("absences",String.valueOf(studentAttendanceSnapshot.size()));
                            studentInformationMap.put("section_code",studentAttendanceSnapshot.getDocuments().get(0).get("sectionCode").toString());
                            studentClasses.add(studentInformationMap);
                            adapter.notifyDataSetChanged();
                           }
                        });
                    }
                });
            }
        });
        }
        catch (Exception ex){
            Toast.makeText(this, "Error getting document: "+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //Next, we need a notification badge
        badgeTextView = findViewById(R.id.badge_text);

        // Set initial badge visibility and count
        updateBadge();
        db.collection("Requests").where(Filter.and(Filter.equalTo("request_type","join_class"),Filter.equalTo("section_code",subjectDocumentID))).limit(15).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.size()>0){
                badgeCount = queryDocumentSnapshots.size();
            }
                updateBadge();
        });
        // Button click listener to demonstrate badge update
        btnNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the student join requests
               Intent viewJoinRequests = new Intent(ViewStudentInSubject.this,ViewJoinRequests.class);
               viewJoinRequests.putExtra("section_code",subjectDocumentID);
               startActivityForResult(viewJoinRequests,VIEW_REQUEST_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIEW_REQUEST_CODE){


            studentClasses = new ArrayList<Map<String,Object>>();
            adapter = new StudentInformationAdapter(this, studentClasses);
            rvStudentList.setAdapter(adapter);

            Intent intent = getIntent();
            String subjectDocumentID = intent.getStringExtra("section_code");
            Intent addStudent = new Intent(ViewStudentInSubject.this, AddStudentInSubject.class);
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder.setPositiveButton("OK",(dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            alertDialogBuilder.setTitle("Class Students");
            try {
                //
                DocumentReference subject = db.collection("Subjects").document(subjectDocumentID);
                subject.get().addOnSuccessListener(documentSnapshot -> {
                    Map<String,Object> subjectDataMap = documentSnapshot.getData();

                    ArrayList<String> studentIDs = (ArrayList<String>) subjectDataMap.get("students");
                    //Now we iterate through the array of numbers
                    if (studentIDs.size() < 1){
                        alertDialogBuilder.setNegativeButton("ADD STUDENTS",(dialogInterface, i) -> {

                            addStudent.putExtra("sectionCode",subjectDocumentID);
                            startActivity(addStudent);
                        });
                        alertDialogBuilder.setMessage("No students in this class");
                        alertDialogBuilder.create().show();
                    }
                    for(String studentID : studentIDs){
                        //Get the corresponding details with this data
                        db.collection("StudentAccount").where(Filter.equalTo("EDPNumber",studentID)).get().addOnSuccessListener(queryDocumentSnapshots -> {
                            for(DocumentSnapshot studentInformation: queryDocumentSnapshots){
                                Map<String,Object> studentInformationMap = studentInformation.getData();
                                studentClasses.add(studentInformationMap);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                db.collection("Requests").where(Filter.and(Filter.equalTo("request_type","join_class"),Filter.equalTo("section_code",subjectDocumentID))).limit(15).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.size()>0){
                        badgeCount = queryDocumentSnapshots.size();
                    }
                        updateBadge();
                });

            }
            catch (Exception ex){
                Toast.makeText(this, "Error getting document: "+ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to update the badge count and visibility
    private void updateBadge() {
        if (badgeCount > 0) {
            badgeTextView.setText(String.valueOf(badgeCount));
            badgeTextView.setVisibility(View.VISIBLE);
        } else {
            badgeTextView.setVisibility(View.GONE);
        }
    }
}