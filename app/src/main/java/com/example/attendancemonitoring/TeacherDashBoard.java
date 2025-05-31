package com.example.attendancemonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class TeacherDashBoard extends AppCompatActivity {
    private FirebaseAuth auth;
    TextView tvTeacherName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button  scanQrCode, manageUser ;
    LinearLayout btnSubjects, btnViewAnalytics, logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dash_board);
        btnSubjects=findViewById(R.id.btnSubjectTeacher);
        btnViewAnalytics=findViewById(R.id.btnVisualizationTeacher);
        tvTeacherName=findViewById(R.id.tvTeacherName);
        logout=findViewById(R.id.btnLogOutTeacher);
        auth=FirebaseAuth.getInstance();
        String userEmail = auth.getCurrentUser().getEmail();
        db.collection("TeachersAccount").where(Filter.equalTo("email",userEmail)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size()>=1){
                    tvTeacherName.setText("Welcome, "+queryDocumentSnapshots.getDocuments().get(0).getString("name").toString()+"!");
                }

            }
        });

        btnSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAddSubject = new Intent(TeacherDashBoard.this,TeacherSubjects.class);
                startActivity(goToAddSubject);
            }
        });
        btnViewAnalytics.setOnClickListener(view -> {
            Intent viewAnal = new Intent(TeacherDashBoard.this, ViewAnalytics.class);
            viewAnal.putExtra("teacher_email",userEmail);
            startActivity(viewAnal);
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(TeacherDashBoard.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}