package com.example.attendancemonitoring;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.qrcode.QRCodeReader;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddStudentInSubject extends AppCompatActivity {

    Button btnSearchStudentByEDP;
    TextView tvEDPNumber;
    LinearLayout btnScanStudentQR;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student_in_subject);
        btnSearchStudentByEDP  = findViewById(R.id.btnSearchStudentByEDP);
        btnScanStudentQR = findViewById(R.id.btnScanStudentQR);
        tvEDPNumber = findViewById(R.id.tvEDPNumber);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.student_profile_card, null);

        CircleImageView profileImageView = dialogView.findViewById(R.id.civStudentImage);
        TextView tvStudentName = dialogView.findViewById(R.id.tvStudentName);
        TextView tvGrade = dialogView.findViewById(R.id.tvGrade);
        TextView tvTrackStrand = dialogView.findViewById(R.id.tvTrackStrand);
        Button btnAddStudent = dialogView.findViewById(R.id.btnAddStudent);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        //Get the section code as an intent
        String sectionCode = getIntent().getStringExtra("sectionCode");

        //Scan QR
         final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
                result -> {
                    if(result.getContents() == null) {
                        Toast.makeText(AddStudentInSubject.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else {
                        MaterialAlertDialogBuilder qrScanResult = new MaterialAlertDialogBuilder(AddStudentInSubject.this);
                        db.collection("StudentAccount").where(Filter.equalTo("EDPNumber",result.getContents())).get().addOnSuccessListener(queryDocumentSnapshots -> {
                            List<Map<String,Object>> studentList = new ArrayList<>();
                            for(DocumentSnapshot studentAccounts:queryDocumentSnapshots){
                                studentList.add(studentAccounts.getData());
                            }
                            if(studentList.size()>0){
                                Map<String,Object> studentInfoMap = new HashMap<>();
                                studentInfoMap = studentList.get(0);
                                String studentName = studentInfoMap.get("firstname").toString() + " " + studentInfoMap.get("lastName").toString();
                                String studentGrade = studentInfoMap.get("grade").toString();
                                String studentTrackStrand = studentInfoMap.get("track_strand").toString();
                                String studentImageUrl = studentInfoMap.get("imageUrl").toString();
                                //Making profile card
                                tvStudentName.setText(studentName);
                                tvGrade.setText(studentGrade);
                                tvTrackStrand.setText(studentTrackStrand);
                                Picasso.get().load(studentImageUrl).into(profileImageView);

                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setView(dialogView);
                                AlertDialog dialog = builder.create();

                                // Set button click listeners
                                Map<String, Object> finalStudentInfoMap = studentInfoMap;
                                btnAddStudent.setOnClickListener(v -> {
                                    // Handle "Add Student" button click
                                    //Check if student is already in the subject
                                    db.collection("Subjects").document(sectionCode).get().addOnSuccessListener(documentSnapshot -> {
                                        //get the result as map
                                        Map<String,Object> subjectMap = new HashMap<>();
                                        subjectMap = documentSnapshot.getData();
                                        ArrayList<String> studentEDPs = new ArrayList<>();
                                        studentEDPs = (ArrayList<String>) subjectMap.get("students");
                                        if (studentEDPs.contains(finalStudentInfoMap.get("EDPNumber").toString())){
                                            MaterialAlertDialogBuilder duplicateAlertDialog = new MaterialAlertDialogBuilder(AddStudentInSubject.this);
                                            duplicateAlertDialog.setTitle("This student is already enrolled in the subject!");
                                            duplicateAlertDialog.setPositiveButton("OK",(dialogInterface, i) -> {
                                                dialogInterface.dismiss();
                                                dialog.dismiss();
                                            });
                                            duplicateAlertDialog.create().show();
                                        }
                                        else{
                                            //Code to add the student to the subject
                                            db.collection("Subjects").document(sectionCode).update("students",FieldValue.arrayUnion(finalStudentInfoMap.get("EDPNumber").toString()))
                                                    .addOnSuccessListener(unused -> {
                                                        MaterialAlertDialogBuilder successDialog = new MaterialAlertDialogBuilder(AddStudentInSubject.this);
                                                        successDialog.setTitle("Student Added Successfully")
                                                                .setMessage("The student has been successfully added to the subject.")
                                                                .setPositiveButton("OK", (dialogInterface, i) -> {
                                                                    // Dismiss the dialog when OK is clicked
                                                                    dialogInterface.dismiss();
                                                                });

                                                    // Show the dialog
                                                        successDialog.show();
                                                    });
                                        }
                                    });
                                });

                                btnCancel.setOnClickListener(v -> {
                                    // Handle "Cancel" button click
                                    dialog.dismiss();
                                });

                                // Show the dialog
                                dialog.show();
                            }
                            else{
                                MaterialAlertDialogBuilder invalidQRCodeDialog = new MaterialAlertDialogBuilder(AddStudentInSubject.this);
                                invalidQRCodeDialog.setTitle("QR Code not valid/No student found")
                                        .setMessage("Please try scanning a valid QR Code or check the student data.")
                                        .setPositiveButton("OK", (dialogInterface, i) -> {
                                            // Dismiss the dialog when OK is clicked
                                            dialogInterface.dismiss();
                                        });

// Show the dialog
                                invalidQRCodeDialog.show();
                            }
                            qrScanResult.create().show();
                        });
                    }
                });


        //Create a progress dialog feedback for searching student
        ProgressDialog progressDialog = new ProgressDialog(AddStudentInSubject.this);
        progressDialog.setTitle("Searching Student...");
        progressDialog.setMessage("This may take a while depending on your connection.");

        //We also need to create a feedback dialog for displaying query result if no student is found
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddStudentInSubject.this);
        alertDialogBuilder.setTitle("Student Search");
        alertDialogBuilder.setMessage("Sorry! Search returned no result. Please try again.");
        alertDialogBuilder.setPositiveButton("TRY AGAIN.",(dialogInterface, i) -> {
            dialogInterface.dismiss();
            //Then focus on the EDP number text field
            tvEDPNumber.setFocusableInTouchMode(true);
            tvEDPNumber.requestFocus();
        });

        btnSearchStudentByEDP.setOnClickListener(view -> {
            //Show the progress dialog here while we search for the student online
            progressDialog.show();
            db.collection("StudentAccount").where(Filter.equalTo("EDPNumber",tvEDPNumber.getText().toString())).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                            progressDialog.dismiss();
                        if (queryDocumentSnapshots.size() < 1){
                            //Show empty result dialog
                            alertDialogBuilder.create().show();
                        }
                        else{
                            alertDialogBuilder.setMessage("Student found!");
                            alertDialogBuilder.setPositiveButton("ADD THIS STUDENT",(dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                progressDialog.setMessage("Adding new student...");
                                progressDialog.show();
                                db.collection("Subjects").document(sectionCode).update("students", FieldValue.arrayUnion(tvEDPNumber.getText().toString())).addOnSuccessListener(unused -> {
                                    progressDialog.dismiss();
                                    MaterialAlertDialogBuilder alertDialogBuilderComplete = new MaterialAlertDialogBuilder(AddStudentInSubject.this);
                                    alertDialogBuilderComplete.setPositiveButton("OK",(dialogInterface1, i1) -> {
                                        dialogInterface1.dismiss();
                                        AddStudentInSubject.this.finish();
                                    });
                                    alertDialogBuilderComplete.setMessage("Student added to subject successfully!");
                                    alertDialogBuilderComplete.create().show();
                                });

                            });
                            alertDialogBuilder.create().show();
                        }
                    });

        });
        btnScanStudentQR.setOnClickListener(view -> {
            ScanOptions options = new ScanOptions();
            options.setOrientationLocked(false);
            options.setPrompt("Scan Student QR to add");

            barcodeLauncher.launch(options);
        });
    }

}