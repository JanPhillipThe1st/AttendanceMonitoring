package com.example.attendancemonitoring;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AdminDashBoardActivity extends AppCompatActivity {
    private LinearLayout subject, btnViewAnalytics, manageUser,btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);

        subject=findViewById(R.id.btnSubject);
        btnViewAnalytics=findViewById(R.id.btnViewAnalytics);
        btnLogout=findViewById(R.id.btnLogout);
        manageUser=findViewById(R.id.btnManageUsers);

        subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAddSubject = new Intent(AdminDashBoardActivity.this,AddSubjectActivity.class);
                startActivity(goToAddSubject);
            }
        });

        btnViewAnalytics.setOnClickListener(view ->{

        });
        manageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AdminDashBoardActivity.this, ManageUser.class);
                startActivity(intent);
            }
        });
        btnLogout.setOnClickListener(view ->{
            AlertDialog.Builder logoutDialog = new AlertDialog.Builder(AdminDashBoardActivity.this);
            logoutDialog.setTitle("System Message");
            logoutDialog.setMessage("Are you sure you want to log out?");
            logoutDialog.setNegativeButton("YES",(dialogInterface,i) ->{
                //Case for confirming log out
                dialogInterface.dismiss();
                finish();
            });
            logoutDialog.setPositiveButton("NO",(dialogInterface,i) ->{
                //Case for cancelling log out
            });
            logoutDialog.create().show();
        });
    }
}

