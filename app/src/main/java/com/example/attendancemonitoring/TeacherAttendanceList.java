package com.example.attendancemonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TeacherAttendanceList extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    AttendanceAdapter adapter;
    private List<AttendanceClass> list;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendance_list);
        list = new ArrayList<>();
        recyclerView=findViewById(R.id.attendanceList);
        adapter = new AttendanceAdapter(this,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db= FirebaseFirestore.getInstance();
        recyclerView.setAdapter(adapter);


        retrieveData();
    }

    private void retrieveData()
    {
        try
        {
            db.collection("Attendance")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {
                                for(QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                    AttendanceClass attendanceClass =documentSnapshot.toObject(AttendanceClass.class);
                                    list.add(attendanceClass);
                                }
                                adapter.notifyDataSetChanged();
                            }else {
                                Toast.makeText(TeacherAttendanceList.this, "Failed Loading the attendance. Please Check internet connectivity.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }catch (Exception e){
            Toast.makeText(this, "Error in loading attendance: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


}