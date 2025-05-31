package com.example.attendancemonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRCodeGeneratorActivity extends AppCompatActivity {
    private ImageView qrView;
    private Button downloadBtn;
    String time_start,time_end,day;
    private String userType;
    private String section;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_generator);
        qrView=findViewById(R.id.qrImage);
        downloadBtn=findViewById(R.id.btnDownload);
        Intent retrieveData =getIntent();
        userType = retrieveData.getStringExtra("userType");
        section=retrieveData.getStringExtra("section");
        time_start=retrieveData.getStringExtra("time_start");
        time_end=retrieveData.getStringExtra("time_end");
        day=retrieveData.getStringExtra("day");
        getIntentAccountType(userType);




        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

    private void getIntentAccountType(String userType)
    {
        if(userType.equals("Teacher"))
            {
                generateTeacherQR(section);
            } else if (userType.equals("Student")) {
                downloadBtn.setVisibility(View.INVISIBLE);
                generateStudentQR(getIntent().getStringExtra("EDPNumber"));
            }

    }

    private void generateStudentQR(String edpNumber)
    {
        MultiFormatWriter writer = new MultiFormatWriter();
        try
        {
            BitMatrix matrix = writer.encode(edpNumber, BarcodeFormat.QR_CODE,400,400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap=barcodeEncoder.createBitmap(matrix);
            qrView.setImageBitmap(bitmap);
        }catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void generateTeacherQR(String section)
    {
        MultiFormatWriter writer = new MultiFormatWriter();
        try
        {
            String qrString = "";
            qrString = "sc:"+section+";scd:"+day+";ts:"+time_start+";te:"+time_end+";";
            BitMatrix matrix = writer.encode(qrString, BarcodeFormat.QR_CODE,400,400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap=barcodeEncoder.createBitmap(matrix);
            qrView.setImageBitmap(bitmap);
        }catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}