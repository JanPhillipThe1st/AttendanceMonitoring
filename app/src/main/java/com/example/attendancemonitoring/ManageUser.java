package com.example.attendancemonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ManageUser extends AppCompatActivity {
    private EditText name, address, contactNo, email, password;
    private Button btnReg;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private TextView loading;
    private ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);
        name=findViewById(R.id.txtRegName);
        address=findViewById(R.id.txtRegAddress);
        contactNo=findViewById(R.id.txtRegContactNo);
        email=findViewById(R.id.txtRegEmail);
        password=findViewById(R.id.txtRegPassword);
        btnReg=findViewById(R.id.btnRegAccountForTeachers);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        pb=findViewById(R.id.progressBtnForTeacher);
        loading=findViewById(R.id.txtLoading);




        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTeacherAccount();
            }
        });

    }

    private void addTeacherAccount()
    {
        pb.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        String teacherName=name.getText().toString().trim();
        String teacherAddress= address.getText().toString().trim();
        String teacherContactNo = contactNo.getText().toString().trim();
        String teacherEmail = email.getText().toString().trim();
        String teacherPassword = password.getText().toString().trim();

        if(teacherName.isEmpty() && teacherAddress.isEmpty() && teacherContactNo.isEmpty() && teacherEmail.isEmpty() && teacherPassword.isEmpty())
        {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }else
        {
            auth.createUserWithEmailAndPassword(teacherEmail,teacherPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                TeacherAccountClass accountClass = new TeacherAccountClass(teacherName,teacherAddress,teacherContactNo,teacherEmail,teacherPassword,"Teacher");
                                firestore.collection("TeachersAccount")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .set(accountClass)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                name.setText("");
                                                address.setText("");
                                                contactNo.setText("");
                                                email.setText("");
                                                password.setText("");
                                                Toast.makeText(ManageUser.this, "New teacher added successfully", Toast.LENGTH_SHORT).show();
                                                pb.setVisibility(View.INVISIBLE);
                                                loading.setVisibility(View.INVISIBLE);
                                                auth.signOut();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ManageUser.this, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                pb.setVisibility(View.INVISIBLE);
                                                loading.setVisibility(View.INVISIBLE);
                                            }
                                        });
                            }
                        }
                    });
        }
    }
}