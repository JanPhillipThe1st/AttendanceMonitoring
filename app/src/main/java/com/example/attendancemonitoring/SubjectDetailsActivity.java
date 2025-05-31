package com.example.attendancemonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SubjectDetailsActivity extends AppCompatActivity {
    private EditText sCode, cCode, dt, cUnits,tvRoom;

    private Spinner scheduleDay,categoriesSpinner;
    private Button saveButton, btnSelectTimeStart,btnSelectTimeEnd;
    private FirebaseFirestore firestore;
    private ProgressBar pb;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_details);

        sCode=findViewById(R.id.txtSectionCode);
        cCode=findViewById(R.id.txtCourseCode);
        scheduleDay = findViewById(R.id.spinnerDay);
        tvRoom = findViewById(R.id.tvRoom);
        categoriesSpinner = findViewById(R.id.spinnerCategory);
        dt=findViewById(R.id.txtDescriptitive);
        final Calendar timeStart = Calendar.getInstance();
        final Calendar timeEnd = Calendar.getInstance();
        cUnits=findViewById(R.id.txtUnits);
        firebaseAuth = FirebaseAuth.getInstance();

        btnSelectTimeStart=findViewById(R.id.btnSelectTimeStart);
        btnSelectTimeEnd=findViewById(R.id.btnSelectTimeEnd);
        firestore=FirebaseFirestore.getInstance();
        saveButton=findViewById(R.id.btnSaveDetails);
        pb=findViewById(R.id.subjectProgressbar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.class_schedules_array,
                R.layout.custom_spinner_item
        );
        ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.subject_categories_array,
                R.layout.custom_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        // Apply the adapter to the spinner.
        scheduleDay.setAdapter(adapter);

        // Specify the layout to use when the list of choices appears.
        categoriesAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        // Apply the adapter to the spinner.
        categoriesSpinner.setAdapter(categoriesAdapter);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSubject();
            }
        });
        btnSelectTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // our day, month and year.
                int month1,date1,year1;
                month1 = timeStart.get(Calendar.MONTH);
                date1 = timeStart.get(Calendar.DAY_OF_MONTH);
                year1 = timeStart.get(Calendar.YEAR);
                // on below line we are creating a variable for date picker dialog.

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        // on below line we are passing context.
                        SubjectDetailsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                timeStart.set(Calendar.HOUR_OF_DAY,i);
                                timeStart.set(Calendar.MINUTE,i1);
                                timeStart.set(Calendar.SECOND,0);
                                timeStart.set(Calendar.MILLISECOND,0);
                                String ampmString = "";
                                if(timeStart.get(Calendar.HOUR_OF_DAY) < 12){
                                    ampmString = "AM";
                                }else{
                                    ampmString = "PM";
                                }
                                String minuteString = String.format("%02d", timeStart.get(Calendar.MINUTE));
                                btnSelectTimeStart.setText(
                                        timeStart.get(Calendar.HOUR) + ":"+
                                                minuteString +" "+ ampmString
                                );
                            }
                        },0,0,true);
                // at last we are calling show to
                // display our date picker dialog.
                timePickerDialog.show();
            }
        });

        btnSelectTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // our day, month and year.
                int month1,date1,year1;
                month1 = timeEnd.get(Calendar.MONTH);
                date1 = timeEnd.get(Calendar.DAY_OF_MONTH);
                year1 = timeEnd.get(Calendar.YEAR);
                // on below line we are creating a variable for date picker dialog.

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        // on below line we are passing context.
                        SubjectDetailsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                timeEnd.set(Calendar.HOUR_OF_DAY,i);
                                timeEnd.set(Calendar.MINUTE,i1);
                                timeEnd.set(Calendar.SECOND,0);
                                timeEnd.set(Calendar.MILLISECOND,0);
                                String ampmString = "";
                                if(timeEnd.get(Calendar.HOUR_OF_DAY) < 12){
                                    ampmString = "AM";
                                }else{
                                    ampmString = "PM";
                                }
                                String minuteString = String.format("%02d", timeEnd.get(Calendar.MINUTE));
                                btnSelectTimeEnd.setText(
                                        timeEnd.get(Calendar.HOUR) + ":"+
                                                minuteString +" "+ ampmString
                                );
                            }
                        },0,0,true);
                // at last we are calling show to
                // display our date picker dialog.
                timePickerDialog.show();
            }
        });

    }



    private void saveSubject()
    {
        pb.setVisibility(View.VISIBLE);
        String sectionCode = sCode.getText().toString().trim();
        String courseCode = cCode.getText().toString().trim();
        String descriptiveTitle = dt.getText().toString().trim();
        String units = cUnits.getText().toString().trim();
        String day=scheduleDay.getSelectedItem().toString().trim();
        String category=categoriesSpinner.getSelectedItem().toString().trim();
        String time_start= btnSelectTimeStart.getText().toString().trim();
        String time_end= btnSelectTimeEnd.getText().toString().trim();
        String room= tvRoom.getText().toString().trim();

        Map<String,Object> subjectMap = new HashMap<String,Object>();

        subjectMap.put("section_code",sectionCode);
        subjectMap.put("course_code",courseCode);
        subjectMap.put("descriptive_title",descriptiveTitle);
        subjectMap.put("units",units);
        subjectMap.put("day",day);
        subjectMap.put("category",category);
        subjectMap.put("time_start",time_start);
        subjectMap.put("room",room);
        subjectMap.put("time_end",time_end);
        subjectMap.put("time_end",time_end);
        ArrayList<String> emptyArray = new ArrayList<String>();
        subjectMap.put("students",emptyArray );
        String teacher = firebaseAuth.getCurrentUser().getEmail();
        subjectMap.put("teacher",teacher);


        firestore.collection("Subjects")
                .document(sectionCode)
                .set(subjectMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pb.setVisibility(View.GONE);
                        sCode.setText("");
                        sCode.setFocusable(true);
                        cCode.setText("");
                        dt.setText("");
                        btnSelectTimeEnd.setText("TIME END (click to edit/set)");
                        btnSelectTimeStart.setText("TIME START (click to edit/set)");
                        cUnits.setText("");
                        Toast.makeText(SubjectDetailsActivity.this, "Subject added successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pb.setVisibility(View.GONE);
                        Toast.makeText(SubjectDetailsActivity.this, "Failed to save subject"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }




}