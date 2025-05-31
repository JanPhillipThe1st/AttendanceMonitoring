package com.example.attendancemonitoring;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcherKt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewJoinRequests extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button btnAddStudents;

    private int badgeCount = 0;
    ImageButton btnNotifications;
    RecyclerView rvStudentList;
    FirebaseAuth currentUser = FirebaseAuth.getInstance();
    private List<Map<String,Object>> studentClasses;
    StudentJoinRequestAdapter adapter;
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);
        rvStudentList = findViewById(R.id.rvStudentList);
        studentClasses = new ArrayList<Map<String,Object>>();
        Intent intent = getIntent();
        String subjectDocumentID = intent.getStringExtra("section_code");
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setPositiveButton("OK",(dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        alertDialogBuilder.setTitle("Join Requests");

        rvStudentList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentJoinRequestAdapter(this, studentClasses);
        rvStudentList.setAdapter(adapter);



        try {
            //
         db.collection("Requests").where(Filter.equalTo("section_code",subjectDocumentID)).get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.getDocuments().size() < 1){
                    alertDialogBuilder.setMessage("No requests...");
                    alertDialogBuilder.create().show();
                }
                else{
                    for (DocumentSnapshot requestDataDocumentSnapshot:queryDocumentSnapshots.getDocuments()){
                        Map<String, Object> requestData = requestDataDocumentSnapshot.getData();
                            //Get the corresponding details with this data
                            db.collection("StudentAccount").where(Filter.equalTo("EDPNumber",requestData.get("requested_by").toString())).get().addOnSuccessListener(studenDocumentSnapshots -> {
                                for(DocumentSnapshot studentInformation: studenDocumentSnapshots){
                                    Map<String,Object> studentInformationMap = studentInformation.getData();
                                    studentInformationMap.put("section_code",subjectDocumentID);
                                    studentClasses.add(studentInformationMap);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                    }
                }
        });

        }
        catch (Exception ex){
            Toast.makeText(this, "Error getting document: "+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}