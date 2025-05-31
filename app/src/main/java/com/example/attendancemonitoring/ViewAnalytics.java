package com.example.attendancemonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.PieChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class ViewAnalytics extends AppCompatActivity {

    PieChart attendancePieChart;
    TextView tvStudentAttended;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String teacher_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_analytics);
        attendancePieChart =findViewById(R.id.attendancePiechart);
        tvStudentAttended =findViewById(R.id.tvStudentAttended);
        PieData attendancePieData = new PieData();
        List<PieEntry> attendancePieEntry = new ArrayList<>();
        teacher_email = getIntent().getStringExtra("teacher_email");
        ValueAnimator animator = ValueAnimator.ofInt(1,100);
        animator.setDuration(500);
        Handler handler = new Handler();
        db.collection("Subjects").where(Filter.equalTo("teacher",teacher_email)).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot subjectSnapshot:queryDocumentSnapshots){
                db.collection("Attendance").where(Filter.equalTo("sectionCode",subjectSnapshot.getString("section_code")))
                        .get().addOnSuccessListener(attendanceQueryDocumentSnapshots ->{
                            int totalPresent = 0;
                            int totalAbsent = 0;
                            int totalLate = 0;
                            int totalExcused = 0;
                            for (DocumentSnapshot attendanceSnapshot:attendanceQueryDocumentSnapshots){
                                if (attendanceSnapshot.getString("attendance_remark").trim().equals("Present")){
                                    totalPresent+=1;
                                }
                                if (attendanceSnapshot.getString("attendance_remark").trim().equals("Late")){
                                    totalLate+=1;
                                }
                                if (attendanceSnapshot.getString("attendance_remark").trim().equals("Absent")){
                                    totalAbsent+=1;
                                }
                                if (attendanceSnapshot.getString("attendance_remark").trim().equals("Excused")){
                                    totalExcused+=1;
                                }
                            }
                            attendancePieEntry.add(new PieEntry(totalPresent ,"On time"));
                            attendancePieEntry.add(new PieEntry(totalLate ,"Late"));
                            attendancePieEntry.add(new PieEntry(totalAbsent ,"Absent"));
                            attendancePieEntry.add(new PieEntry(totalExcused ,"Excused"));

                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                                    float timerValue = (int)valueAnimator.getAnimatedValue()/100;
                                    tvStudentAttended.setText(String.valueOf(attendanceQueryDocumentSnapshots.getDocuments().size() * (int)timerValue));

                                }
                            });
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    animator.start();
                                }
                            },500);
                            PieDataSet attendancePieDataSet = new PieDataSet(attendancePieEntry,"");
                            attendancePieChart.getDescription().setText("LATE / ABSENCES");
                            attendancePieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                            attendancePieData.setDataSet(attendancePieDataSet);
                            attendancePieChart.setData(attendancePieData);


                            attendancePieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                @Override
                                public void onValueSelected(Entry e, Highlight h) {
                                    PieEntry selectedEntry = (PieEntry) e;
                                    new MaterialAlertDialogBuilder(ViewAnalytics.this).setTitle(selectedEntry.getLabel()).setMessage(String.valueOf((int)selectedEntry.getValue())).create().show();
                                }

                                @Override
                                public void onNothingSelected() {

                                }
                            });
                        });
            }
        });



    }

}