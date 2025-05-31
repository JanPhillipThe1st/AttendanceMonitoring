package com.example.attendancemonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private EditText txtEmail, txtPassword;
    private TextView registerClick,btnResetPassword;
    private FirebaseAuth auth;
    FirebaseFirestore db;
    private ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin=findViewById(R.id.btnLogin);
        txtEmail=findViewById(R.id.txtEmailLogin);
        txtPassword=findViewById(R.id.txtPasswordLogin);
        registerClick=findViewById(R.id.txtRegAccount);
        btnResetPassword=findViewById(R.id.btnResetPassword);
        auth=FirebaseAuth.getInstance();
        pb=findViewById(R.id.progressLogin);
        db=FirebaseFirestore.getInstance();

        txtEmail.setFocusableInTouchMode(true);
        txtEmail.requestFocus();
        registerClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToReg = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(goToReg);
            }
        });
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ResetPassword.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void login() {
        pb.setVisibility(View.VISIBLE);
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (email.equals("Admin") && password.equals("Password123"))
        {
            Intent gotoTeacherDashboard = new Intent(LoginActivity.this,AdminDashBoardActivity.class);
            startActivity(gotoTeacherDashboard);
            finish();
        } else
            try {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user= auth.getCurrentUser();
                                    pb.setVisibility(View.INVISIBLE);
                                    getUserDetails(user);
                                } else {
                                    pb.setVisibility(View.INVISIBLE);
                                    MaterialAlertDialogBuilder invalidCredentials = new MaterialAlertDialogBuilder(LoginActivity.this);
                                    invalidCredentials.setTitle("Login");
                                    invalidCredentials.setMessage("Invalid username/password. Please login again.");
                                    invalidCredentials.setPositiveButton("OK",(dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        txtEmail.setText("");
                                        txtPassword.setText("");
                                        txtEmail.setFocusableInTouchMode(true);
                                        txtEmail.requestFocus();
                                    });
                                    invalidCredentials.create().show();
                                }
                            }
                        });
            } catch (Exception e) {
                pb.setVisibility(View.INVISIBLE);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
    }
    private void getUserDetails(FirebaseUser user)
    {
        if(user!=null)
        {
            db.collection("TeachersAccount").document(user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                DocumentSnapshot document = task.getResult();
                                if(document.exists())
                                {
                                        String userType = document.getString("userType");
                                        if(userType!=null && userType.equals("Teacher"))
                                        {
                                            Intent intent = new Intent(LoginActivity.this,TeacherDashBoard.class);
                                            startActivity(intent);
                                            finish();
                                        } else if (userType!=null && userType.equals("Student"))
                                        {
                                            Intent intent = new Intent(LoginActivity.this,StudentDashboardActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                }
                                else
                                {
                                    db.collection("StudentAccount").document(user.getUid()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        DocumentSnapshot document = task.getResult();
                                                        if(document.exists())
                                                        {
                                                            String userType = document.getString("userType");
                                                            if(userType!=null && userType.equals("Teacher"))
                                                            {
                                                                Intent intent = new Intent(LoginActivity.this,TeacherDashBoard.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                            else if (userType!=null && userType.equals("Student"))
                                                            {
                                                                Intent intent = new Intent(LoginActivity.this,StudentDashboardActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });
        }
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            findViewById(R.id.studentDashboard).setBackgroundColor(Color.parseColor("#ffffff"));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

}
