package com.example.attendancemonitoring;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewStudentAttendance extends AppCompatActivity {

    private CompactCalendarView compactCalendarView;
    private RecyclerView eventsRecyclerView;
    private CalendarAttendanceItemAdapter eventAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView tvMonthName;
    String sectionCode;
    private List<CalendarAttendanceClass> allEvents;
    private List<CalendarAttendanceClass> dailyEvents;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_attendance);

        sectionCode = getIntent().getStringExtra("sectionCode");
        // Initialize the calendar and RecyclerView
        compactCalendarView = findViewById(R.id.compactCalendarView);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        tvMonthName = findViewById(R.id.tvMonthName);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize event lists
        allEvents = new ArrayList<>();
        dailyEvents = new ArrayList<>();
        eventAdapter = new CalendarAttendanceItemAdapter(dailyEvents);

        eventsRecyclerView.setAdapter(eventAdapter);

        // Load sample events
        loadSampleEvents();


        // Set listener for date selection
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(java.util.Date dateClicked) {
                loadSampleEvents();
                showEventsForSelectedDate(dateClicked);
            }

            @Override
            public void onMonthScroll(java.util.Date firstDayOfNewMonth) {
                // Optionally handle month change
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(firstDayOfNewMonth);
                tvMonthName.setText(dateFormatMonth.format(firstDayOfNewMonth));
                loadSampleEvents();
            }
        });
    }

    private void loadSampleEvents() {

        // Add a sample event on a specific date
        db.collection("Attendance").where(Filter.equalTo("sectionCode",sectionCode)).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                Map<String,Object> attendanceMap =  documentSnapshot.getData();
                attendanceMap.get("date");
                attendanceMap.get("name");
                attendanceMap.get("sectionCode");
                attendanceMap.get("time");
                attendanceMap.get("attendance_remark");
                db.collection("StudentAccount").where(Filter.equalTo("EDPNumber",attendanceMap.get("EDPNumber").toString())).get()
                        .addOnSuccessListener(studentAccountDocumentSnapshots -> {
                            for( DocumentSnapshot studentAccountDocument:studentAccountDocumentSnapshots){
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(Date.parse(attendanceMap.get("date").toString()));
                                allEvents.add(new CalendarAttendanceClass(attendanceMap.get("name").toString(),
                                        studentAccountDocument.getString("imageUrl"),attendanceMap.get("date").toString()
                                        ,attendanceMap.get("time").toString()
                                        ,attendanceMap.get("EDPNumber").toString()
                                        ,attendanceMap.get("attendance_remark").toString(),
                                        calendar,documentSnapshot.getId()));
                                compactCalendarView.addEvent(new Event(android.graphics.Color.BLUE, calendar.getTimeInMillis()));
                                eventAdapter.notifyDataSetChanged();
                            }
                        });
            }
        });



    }

    private void showEventsForSelectedDate(java.util.Date dateClicked) {
        dailyEvents.clear();
        // Filter events that match the selected date
        Calendar clickedDate = Calendar.getInstance();
        clickedDate.setTime(dateClicked);

        for (CalendarAttendanceClass event : allEvents) {
            if (event.calendar.get(Calendar.YEAR) == clickedDate.get(Calendar.YEAR)
                    && event.calendar.get(Calendar.MONTH) == clickedDate.get(Calendar.MONTH)
                    && event.calendar.get(Calendar.DAY_OF_MONTH) == clickedDate.get(Calendar.DAY_OF_MONTH)) {
                dailyEvents.add(event);
            }
        }

        // Notify adapter of data change
        eventAdapter.notifyDataSetChanged();
    }
}