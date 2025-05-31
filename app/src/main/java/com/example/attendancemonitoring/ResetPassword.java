package com.example.attendancemonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    Button btnPasswordreset ;
    TextInputEditText tvEmail;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        firebaseAuth = FirebaseAuth.getInstance();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Password reset");
        progressDialog.setMessage("Sending Email...");
        btnPasswordreset  = findViewById(R.id.btnPasswordreset);
        tvEmail  = findViewById(R.id.tvEmail);
        btnPasswordreset.setOnClickListener(view ->{
            progressDialog.show();
        firebaseAuth.sendPasswordResetEmail(tvEmail.getText().toString()).addOnSuccessListener(task->{
            progressDialog.dismiss();
            new MaterialAlertDialogBuilder(getBaseContext()).setMessage("Password reset link sent to: "+ tvEmail.getText().toString()).setTitle("Success!")
                    .setPositiveButton("OK",(dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finish();
                    });
        });
        });
    }
}