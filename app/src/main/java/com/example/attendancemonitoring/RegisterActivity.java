package com.example.attendancemonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {
    private EditText firstName, middleName, lastName, contactNo, address,guardianName,getGuardianContactNo, email,password,confirmPassword,tvEDPNumber;
    private Button createBtn;
    String imageUrl;
    ImageButton btnChooseProfile;
    Spinner spinnerTrackStrand, spinnerGrade;
    TextView tvSelectPhotoName;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    int SELECT_PICTURE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createBtn=findViewById(R.id.btnRegAccount);
        firstName=findViewById(R.id.txtFirstName);
        middleName=findViewById(R.id.txtMiddleName);
        lastName=findViewById(R.id.txtLastName);
        contactNo=findViewById(R.id.txtContactNo);
        btnChooseProfile=findViewById(R.id.btnChooseProfile);
        tvEDPNumber=findViewById(R.id.tvEDPNumber);
        spinnerTrackStrand=findViewById(R.id.spinnerTrackStrand);
        spinnerGrade=findViewById(R.id.spinnerGrade);
        address=findViewById(R.id.txtAddress);
        guardianName=findViewById(R.id.txtGuardianName);
        getGuardianContactNo=findViewById(R.id.txtGuardianContactNo);
        email=findViewById(R.id.txtEmailReg);
        password=findViewById(R.id.txtPasswordReg);
        confirmPassword=findViewById(R.id.txtConfirmPasswordReg);
        auth=FirebaseAuth.getInstance();
        tvSelectPhotoName = findViewById(R.id.tvSelectPhotoName);
        firestore =FirebaseFirestore.getInstance();

        ArrayAdapter<CharSequence> gradesAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.grades_selection,
                R.layout.custom_spinner_item
        );
        ArrayAdapter<CharSequence> track_strandAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.track_strand_selection,
                R.layout.custom_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        gradesAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        // Apply the adapter to the spinner.
        spinnerGrade.setAdapter(gradesAdapter);
        // Specify the layout to use when the list of choices appears.
        track_strandAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        // Apply the adapter to the spinner.s
        spinnerTrackStrand.setAdapter(track_strandAdapter);


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAccount();
            }
        });
        btnChooseProfile.setOnClickListener(view ->{
            imageChooser();
        });
    }

    // this function is triggered when
    // the Select Image Button is clicked
    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    btnChooseProfile.setImageURI(selectedImageUri);
                    tvSelectPhotoName.setText("Selected image: "+selectedImageUri.getLastPathSegment());
                   StorageReference usersRef = storageRef.child("users/"+selectedImageUri.getLastPathSegment());
                    ProgressDialog uploadPhotoProgress = new ProgressDialog(RegisterActivity.this);
                    uploadPhotoProgress.setCancelable(false);
                    uploadPhotoProgress.setMessage("Uploading your photo :)\nIndecent photos uploaded will be subject for school dismissal.");
                    uploadPhotoProgress.show();
                   usersRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                             taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   imageUrl = uri.toString();
                                   uploadPhotoProgress.dismiss();
                                   new AlertDialog.Builder(RegisterActivity.this).setMessage("Photo uploaded successfully!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialogInterface, int i) {
                                           dialogInterface.dismiss();
                                       }
                                   }).create().show();
                               }
                           });
                       }
                   });
                }
            }
        }
    }
    private void registerAccount()
    {
        String fName = firstName.getText().toString().trim();
        String mName = middleName.getText().toString().trim();
        String lName = lastName.getText().toString().trim();
        String contact= contactNo.getText().toString().trim();
        String uAddress= address.getText().toString().trim();
        String gName= guardianName.getText().toString().trim();
        String grade= spinnerGrade.getSelectedItem().toString();
        String EDPNumber= tvEDPNumber.getText().toString();
        String track_strand= spinnerTrackStrand.getSelectedItem().toString();
        String gContactNo= getGuardianContactNo.getText().toString().trim();
        String institutionalEmail = email.getText().toString().trim();
        String uPassword= password.getText().toString().trim();
        String conPassword= confirmPassword.getText().toString().trim();
        if (imageUrl == null){
            Toast.makeText(this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
            btnChooseProfile.setBackgroundColor(Color.parseColor("#FF2222"));
            return;

        }
        if(!institutionalEmail.endsWith("@sccpag.edu.ph"))
        {
            Toast.makeText(this, "We will only accept institutional E-Mail. If you don't have this email please go to MIS to ask for Institutional E-Mail", Toast.LENGTH_SHORT).show();
            return;
        }

        if (uPassword.equals(conPassword))
        {
            if(tvEDPNumber.getText().toString().isEmpty()){
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(RegisterActivity.this);
                alertDialogBuilder.setTitle("Registration Error");
                alertDialogBuilder.setMessage("Please include an EDP Number. You cannot proceed without this.");
                alertDialogBuilder.setPositiveButton("TRY AGAIN",(dialogInterface, i) -> {
                    dialogInterface.dismiss();

                    return;
                });
                alertDialogBuilder.setOnDismissListener(dialogInterface -> {
                    return;

                });

            }
            try
            {
                auth.createUserWithEmailAndPassword(institutionalEmail,uPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                                    progressDialog.setMessage("Saving user... Please wait.");
                                    progressDialog.setProgressDrawable(getDrawable(R.drawable.user));
                                    progressDialog.show();
                                    StudentClass studentClass = new StudentClass(fName,mName,lName,contact,uAddress,gName,gContactNo,
                                            institutionalEmail,uPassword,"Student",imageUrl,grade,track_strand,EDPNumber);
                                    firestore.collection("StudentAccount")
                                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .set(studentClass)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(RegisterActivity.this, "Create Account Successfully", Toast.LENGTH_SHORT).show();
                                                    Intent goToLogin = new Intent(RegisterActivity.this,LoginActivity.class);
                                                    startActivity(goToLogin);
                                                    finish();
                                                    auth.signOut();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(RegisterActivity.this, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
            }catch (Exception e)
            {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            Toast.makeText(this, "Password do not match. Please check your password", Toast.LENGTH_SHORT).show();
        }




    }
}