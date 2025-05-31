package com.example.attendancemonitoring;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class CheckAttendance extends AppCompatActivity {

    private ImageView qrView;
    private Button btnDownloadQR,btnShare;
    String time_start,time_end,day;
    private String userType;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String section;
    Bitmap bitmap;
    String fileName = "";
    private SimpleDateFormat dateFormatDefault = new SimpleDateFormat("MM/dd yyyy", Locale.getDefault());
    String resultPath ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);

        btnDownloadQR = findViewById(R.id.btnDownload);
        btnShare = findViewById(R.id.btnShare);
        Intent retrieveData =getIntent();
        qrView=findViewById(R.id.qrImage);
        userType = retrieveData.getStringExtra("userType");
        section=retrieveData.getStringExtra("section");
        time_start=retrieveData.getStringExtra("time_start");
        time_end=retrieveData.getStringExtra("time_end");
        day=retrieveData.getStringExtra("day");

        generateTeacherQR(section);
        btnDownloadQR.setOnClickListener(view ->{

            resultPath = saveToInternalStorage(bitmap);
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(CheckAttendance.this);
            alertDialogBuilder.setTitle("Image Saved!");
            alertDialogBuilder.setMessage("Your image is saved in:\n"+ resultPath+"/"+fileName);
            alertDialogBuilder.setNegativeButton("Close", (dialogInterface, i) -> dialogInterface.dismiss());
            alertDialogBuilder.setPositiveButton("Open Gallery", (dialogInterface, i) -> {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
            alertDialogBuilder.create().show();
        });

        btnShare.setOnClickListener(view ->{
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            Uri screenshotUri = Uri.parse(resultPath+"/"+fileName);
            sharingIntent.setType("image/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            startActivity(Intent.createChooser(sharingIntent, "Share image using"));

        });
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        File  directory = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            directory = new File(Environment.getExternalStorageDirectory() + File.separator + "DCIM"+ File.separator +"Screenshots");
        }
        if (!directory.exists()) {
            directory.mkdirs();
        }
        fileName = "QR_"+section+".png";
        File pictureFile = new File(directory, fileName);
        try {
            pictureFile.createNewFile();
        } catch (IOException e) {
            Toast.makeText(CheckAttendance.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        try {
            FileOutputStream out = new FileOutputStream(pictureFile);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(CheckAttendance.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return directory.getAbsolutePath();
    }

    private void generateTeacherQR(String section)
    {
        db.collection("Attendance").where(Filter.and(Filter.equalTo("date",dateFormatDefault.format(Calendar.getInstance().getTime())),Filter.equalTo("sectionCode",section))).limit(1).get().addOnSuccessListener(attendanceQuerySnapshot ->{
            if (attendanceQuerySnapshot.getDocuments().size() > 0){

            }
            else{
                db.collection("Subjects").document(section).get().addOnSuccessListener(documentSnapshot -> {
                    ArrayList<String> studentsInSubject = new ArrayList<>();
                    studentsInSubject = (ArrayList<String>) documentSnapshot.getData().get("students");
                    for(String studentEDP:studentsInSubject){
                        db.collection("StudentAccount")
                                .where(Filter.equalTo("EDPNumber",studentEDP)).get().addOnSuccessListener(queryDocumentSnapshots -> {
                                    Map<String,Object> studentInfoMap = queryDocumentSnapshots.getDocuments().get(0).getData();
                                    Map<String,Object> emptyAttendanceItem = new HashMap<>();
                                    emptyAttendanceItem.put("EDPNumber",studentEDP);
                                    emptyAttendanceItem.put("attendance_remark","Absent");
                                    emptyAttendanceItem.put("date", dateFormatDefault.format(Calendar.getInstance().getTime()));
                                    emptyAttendanceItem.put("name",studentInfoMap.get("firstname").toString() + " "+studentInfoMap.get("lastName").toString());
                                    emptyAttendanceItem.put("sectionCode",section);
                                    emptyAttendanceItem.put("time","00:00:00");
                                    db.collection("Attendance").add(emptyAttendanceItem).addOnSuccessListener(documentReference -> {
                                        MultiFormatWriter writer = new MultiFormatWriter();
                                        try
                                        {
                                            String qrString = "";
                                            qrString = "sc:"+section+";scd:"+day+";ts:"+time_start+";te:"+time_end+";";
                                            BitMatrix matrix = writer.encode(qrString, BarcodeFormat.QR_CODE,400,400);
                                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                            bitmap=barcodeEncoder.createBitmap(matrix);
                                            qrView.setImageBitmap(bitmap);
                                        }catch (Exception e)
                                        {
                                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(CheckAttendance.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                });
                    }
                });
            }
        });


    }
}