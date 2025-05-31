package com.example.attendancemonitoring;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeReader;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentDashboardActivity extends AppCompatActivity {
    private LinearLayout btnMyAttendance,logout,scan,generate,btnJoinClass;
    CircleImageView imgTeacherProfile;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private FirebaseAuth auth;

    Map<String,Object>attendanceMap = new HashMap<String,Object>();
    private TextView name,tvEdpNumber,tvGrade;

    private String date, time;
    private FirebaseFirestore db;
    int attendance_interval = 0;
    String edpNumber = "";

    private SimpleDateFormat dateFormatDefault = new SimpleDateFormat("MM/dd yyyy", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        btnMyAttendance=findViewById(R.id.btnMyAttendance);
        logout=findViewById(R.id.btnLogout);
        scan=findViewById(R.id.btnScanQRCode);
        imgTeacherProfile=findViewById(R.id.imgTeacherProfile);
        btnJoinClass=findViewById(R.id.btnJoinClass);
        name=findViewById(R.id.tvStudentsName);
        tvEdpNumber=findViewById(R.id.tvEdpNumber);
        tvGrade=findViewById(R.id.tvGrade);
        generate=findViewById(R.id.btnGenerateQRCode);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();


        //Fingerprint scanner code
        OnBackPressedDispatcher onBackPressedDispatcher =getOnBackPressedDispatcher();

        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (shouldShowConfirmationDialog()) {
                    showConfirmationDialog();
                } else {
                    finish(); // or super.onBackPressed();
                }
            }
        });

        //FingerprintScanner

        loadUserDetails();

        getDateAndTime();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent gotoLogin = new Intent(StudentDashboardActivity.this,LoginActivity.class);
                startActivity(gotoLogin);
                finish();
            }
        });

        generate.setOnClickListener(v -> {
            showQRCodeDialog();
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeReader qrCodeReader = new QRCodeReader();


                IntentIntegrator intentIntegrator = new IntentIntegrator(StudentDashboardActivity.this);

                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Scan QR Code for Attendance");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();
            }
        });



    }

    private void showConfirmationDialog()
    {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    private boolean shouldShowConfirmationDialog()
    {
        return  true;
    }


    private void getDateAndTime()
    {
        Calendar calendar= Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so we add 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        date = month+"/"+day+" "+ year;
        time = hour+ ":"+ minute+":"+second;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult!=null)
        {
            String content=intentResult.getContents();
            if(content !=null)
            {
                String sectionResult = intentResult.getContents();
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(StudentDashboardActivity.this);
                alertDialogBuilder.setTitle("Barcode Data");
                Map<String,Object> barcodeData = barcodeDataParser(sectionResult);
                MaterialAlertDialogBuilder attendanceAlertDialog = new MaterialAlertDialogBuilder(StudentDashboardActivity.this);
                //Now we need to check if our student is allowed to enter the class
                //First, we get the current time
                Calendar time_of_attendance = Calendar.getInstance();
                //Then get the int equivalent
                int time_of_attendance_int_value = 0;
                time_of_attendance_int_value+= (time_of_attendance.get(Calendar.HOUR_OF_DAY) *60);
                time_of_attendance_int_value += time_of_attendance.get(Calendar.MINUTE);
                //Now we ask if he is within the time schedule
                if(time_of_attendance_int_value >= Integer.parseInt(barcodeData.get("time_start_int_value").toString())
                        && time_of_attendance_int_value <= Integer.parseInt(barcodeData.get("time_end_int_value").toString())){

                    attendance_interval = Integer.parseInt(barcodeData.get("time_start_int_value").toString())-time_of_attendance_int_value;
                    if(time_of_attendance_int_value - Integer.parseInt(barcodeData.get("time_start_int_value").toString()) >= 15){
                        attendanceAlertDialog.setMessage("Attendance saved but marked late!");
                        attendanceAlertDialog.setTitle("Late Attendance");
                        attendanceMap.put("attendance_remark","Late");
                        attendanceMap.put("attendance_interval",Integer.parseInt(barcodeData.get("time_start_int_value").toString())-time_of_attendance_int_value);
                        attendanceAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        Executor executor = ContextCompat.getMainExecutor(StudentDashboardActivity.this);
                        biometricPrompt = new BiometricPrompt(StudentDashboardActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                                super.onAuthenticationError(errorCode, errString);
                                Toast.makeText(StudentDashboardActivity.this, "You cannot go to dashboard unless you pass this security requirement.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);
                                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(StudentDashboardActivity.this);
                                alertDialogBuilder.setOnDismissListener((dialogInterface -> {
                                    saveAttendance(barcodeData.get("section_code").toString(),"Late");
                                })).setTitle("Attendance Validation").setPositiveButton("OK",(dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                }).setMessage("Authentication Success!").create().show();
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                                .setTitle("Please scan to verify your identity")
                                .setSubtitle("Validate your attendance.")
                                .setNegativeButtonText("Cancel")// Set a non-empty negative button text
                                .build();
                        authenticateUser();
                    }
                    else if(time_of_attendance_int_value - Integer.parseInt(barcodeData.get("time_start_int_value").toString()) >= 30){

                        attendanceAlertDialog.setMessage("Sorry! It seems that you are too late in catching this attendance!");
                        attendanceAlertDialog.setTitle("Invalid Attendance");
                        attendanceAlertDialog.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
                        attendanceAlertDialog.create().show();
                    }
                    else{
                        attendanceAlertDialog.setMessage("Attendance saved successfully!");
                        attendanceAlertDialog.setTitle("Good attendance");
                        attendanceMap.put("attendance_remark","Present");
                        attendanceMap.put("attendance_interval",Integer.parseInt(barcodeData.get("time_start_int_value").toString())-time_of_attendance_int_value);
                        attendanceAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        Executor executor = ContextCompat.getMainExecutor(StudentDashboardActivity.this);
                        biometricPrompt = new BiometricPrompt(StudentDashboardActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                                super.onAuthenticationError(errorCode, errString);
                                Toast.makeText(StudentDashboardActivity.this, "You cannot go to dashboard unless you pass this security requirement.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);
                                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(StudentDashboardActivity.this);
                                alertDialogBuilder.setOnDismissListener((dialogInterface -> {
                                    saveAttendance(barcodeData.get("section_code").toString(),"Present");
                                })).setTitle("Attendance Validation").setPositiveButton("OK",(dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                }).setMessage("Authentication Success!").create().show();
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                                .setTitle("Please scan to verify your identity")
                                .setSubtitle("Validate your attendance.")
                                .setNegativeButtonText("Cancel")// Set a non-empty negative button text
                                .build();
                        authenticateUser();
                    }

                }
                else{
                    attendanceAlertDialog.setMessage("Sorry! It seems that you are too late in catching this attendance!");
                    attendanceAlertDialog.setTitle("Invalid Attendance");
                    attendanceAlertDialog.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
                    attendanceAlertDialog.create().show();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected Map<String,Object> barcodeDataParser(String barcodeString){
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("section_code",barcodeString.split(";")[0].split(":")[1]);
        result.put("day",barcodeString.split(";")[1].split(":")[1]);
        //get the time (e.g.: 7:00 AM, 8:00 AM)
        result.put("time_start",barcodeString.split(";")[2].substring(0,barcodeString.split(";")[2].indexOf('M')+1).substring(barcodeString.split(";")[2].substring(0,barcodeString.split(";")[2].indexOf('M')+1).indexOf(':')+1,barcodeString.split(";")[2].substring(0,barcodeString.split(";")[2].indexOf('M')+1).indexOf('M')+1));
        result.put("time_start_int_value",timeStringToInt(result.get("time_start").toString()));
        //get the time (e.g.: 7:00 AM, 8:00 AM)
        result.put("time_end",barcodeString.split(";")[3].substring(0,barcodeString.split(";")[3].indexOf('M')+1).substring(barcodeString.split(";")[3].substring(0,barcodeString.split(";")[3].indexOf('M')+1).indexOf(':')+1,barcodeString.split(";")[3].substring(0,barcodeString.split(";")[3].indexOf('M')+1).indexOf('M')+1));
        result.put("time_end_int_value",timeStringToInt(result.get("time_end").toString()));
        //Algorithm to transform a time string to
        return result;
    }
    private void authenticateUser()
    {
        biometricPrompt.authenticate(promptInfo);
    }
    protected int timeStringToInt(String timeString){
        int result = 0;
        //Sample Time string should look like: '07:00 AM'
        //First, split the time string to an array ["07:00","AM"]
        String[] splitTimeString = timeString.split(" ");
        //The notation we will use is simple: "00:00" = 0, "01:00" = 60
        //if it is "PM", we assume it is past noon
        //That means it is now 12*60 = 720
        if(splitTimeString[1].contains("PM")){
            result += 720;
        }
        //Now we get the hour of day "07" from "07:00" and then multiply by 60
        result += Integer.parseInt(splitTimeString[0].split(":")[0]) * 60;
        //And then the minutes which just needs to be added
        result += Integer.parseInt(splitTimeString[0].split(":")[1]);
        return result;
    }
    private void saveAttendance(String sectionResult,String attendance_remark)
    {
        String fullName = name.getText().toString().trim();



        AttendanceClass attendanceClass = new AttendanceClass(sectionResult,fullName,date,time);
        Calendar calendar = Calendar.getInstance();
        attendanceMap.put("attendance_remark",attendance_remark);
        attendanceMap.put("attendance_interval",attendance_interval);
        FirebaseAuth.getInstance().getCurrentUser().getEmail();
        attendanceMap.put("sectionCode",sectionResult);
        attendanceMap.put("name",fullName);
        attendanceMap.put("date",dateFormatDefault.format(calendar.getTime()));
        attendanceMap.put("time",time);
        attendanceMap.put("EDPNumber",edpNumber);
        db.collection("Attendance").where(Filter.and(Filter.equalTo("EDPNumber",edpNumber),Filter.equalTo("date",dateFormatDefault.format(calendar.getTime()))))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String,Object> attendanceDocument = queryDocumentSnapshots.getDocuments().get(0).getData();
                    db.collection("Attendance")
                            .document(queryDocumentSnapshots.getDocuments().get(0).getId()).update(attendanceMap)
                            .addOnSuccessListener(documentReference -> Toast.makeText(StudentDashboardActivity.this, "Attendance added successfully", Toast.LENGTH_SHORT).show()).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(StudentDashboardActivity.this, "Attendance failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                });


    }

    private void loadUserDetails()
    {
        FirebaseUser user= auth.getCurrentUser();
        if(user!=null)
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
                                    String firstName = document.getString("firstname");
                                    String middleName = document.getString("middleName");
                                    String lastName = document.getString("lastName");
                                    String fullName = firstName +" "+ middleName +" "+ lastName;
                                    name.setText(fullName);
                                    tvEdpNumber.setText("EDP Number: "+document.getString("EDPNumber"));
                                    tvGrade.setText("GRADE: "+document.getString("grade"));
                                    Picasso.get().load(document.getString("imageUrl")).into(imgTeacherProfile);
                                    btnMyAttendance.setOnClickListener(v -> {
                                        Intent viewAttendanceIntent = new Intent(StudentDashboardActivity.this,StudentAttendance.class);
                                        viewAttendanceIntent.putExtra("EDPNumber",edpNumber);
                                        startActivity(viewAttendanceIntent);
                                    });
                                    edpNumber = document.getString("EDPNumber");
                                    btnJoinClass.setOnClickListener(view -> {
                                        Intent joinClassIntent = new Intent(StudentDashboardActivity.this,AddSubjectBySectionCode.class);
                                        joinClassIntent.putExtra("EDPNumber",edpNumber);
                                        startActivity(joinClassIntent);
                                    });

                                }else {
                                    Toast.makeText(StudentDashboardActivity.this, "Data not exist", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(StudentDashboardActivity.this, "Failed to retrieve data.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void showQRCodeDialog() {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.student_edp_qr_dialog, null);

        // Get references to the views in the layout
        TextView qrCodeTitle = dialogView.findViewById(R.id.qrCodeTitle);
        ImageView qrCodeImageView = dialogView.findViewById(R.id.qrCodeImageView);
        Button closeButton = dialogView.findViewById(R.id.btnClose);

        MultiFormatWriter writer = new MultiFormatWriter();
        try
        {
            BitMatrix matrix = writer.encode(edpNumber, BarcodeFormat.QR_CODE,1600,1600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap=barcodeEncoder.createBitmap(matrix);
            qrCodeImageView.setImageBitmap(bitmap);

            // Build the AlertDialog with the custom view
            MaterialAlertDialogBuilder qrCodeDialog = new MaterialAlertDialogBuilder(this);
            qrCodeDialog.setView(dialogView);

            // Create and show the dialog
            androidx.appcompat.app.AlertDialog dialog = qrCodeDialog.create();

            // Set close button listener
            closeButton.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        }catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }



}