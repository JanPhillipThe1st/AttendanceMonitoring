package com.example.attendancemonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class FingerprintAuth extends AppCompatActivity {
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Button btnExit ,scanAgain;
    private String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_auth);
        btnExit=findViewById(R.id.btnAuthExit);
        scanAgain=findViewById(R.id.btnScanAgain);
        Intent getUser = getIntent();
        userType=getUser.getStringExtra("userType");




        //FingerprintScanner
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(FingerprintAuth.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(FingerprintAuth.this, "You cannot go to dashboard unless you pass this security requirement.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                if(userType.equals("Teacher"))
                {
                    Intent intent = new Intent(FingerprintAuth.this, TeacherDashBoard.class);
                    startActivity(intent);
                    finish();
                }else
                {
                        Intent intent1 = new Intent(FingerprintAuth.this, StudentDashboardActivity.class);
                        startActivity(intent1);
                        finish();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Please scan to verify your identity")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")// Set a non-empty negative button text
                .build();
        authenticateUser();


        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        scanAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser();
            }
        });
    }

    private void authenticateUser()
    {
        biometricPrompt.authenticate(promptInfo);
    }
}