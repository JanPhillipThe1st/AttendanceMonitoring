package com.example.attendancemonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class IdentityVerificationScreen extends AppCompatActivity {
    LinearLayout btnFaceRecognition, btnFingerprintRecognition;


    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_verification_screen);
        btnFaceRecognition= findViewById(R.id.btnFaceRecognition);
        btnFingerprintRecognition = findViewById(R.id.btnFingerprintRecognition);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(IdentityVerificationScreen.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Verification for Attendance")
                .setSubtitle("Scan your fingerprint to proceed")
                .setNegativeButtonText("Use account password")
                .build();


        btnFingerprintRecognition.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Toast.makeText(IdentityVerificationScreen.this,"Fingerprint Verification Requested",Toast.LENGTH_SHORT).show();
                biometricPrompt.authenticate(promptInfo);
                return  false;
            }
        });

        btnFingerprintRecognition.setOnClickListener(view ->{

        });

    }
}