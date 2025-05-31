package com.example.attendancemonitoring;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.squareup.picasso.Picasso;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentInformationAdapter extends RecyclerView.Adapter<StudentInformationAdapter.StudentInformationHolder> {

    Context context;
    List<Map<String, Object>> list;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String sectionCode;
    FirebaseAuth firebaseAuth;


    public StudentInformationAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public StudentInformationAdapter.StudentInformationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_information_card,parent,false);
        return new StudentInformationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentInformationAdapter.StudentInformationHolder holder, int position) {
        Map<String,Object> studentData = list.get(position);
        String studentName = studentData.get("firstname").toString() +" " + studentData.get("lastName").toString();
        if (studentName.length()>20) studentName = studentName.substring(0,20);
        holder.tvStudentName.setText(studentName);
        holder.tvStudentGrade.setText(studentData.get("grade").toString());
        holder.tvStudentStrand.setText(studentData.get("track_strand").toString());
        holder.tvAbsences.setText("Absences: "+studentData.get("absences").toString());
        Picasso.get().load(studentData.get("imageUrl").toString()).into(holder.civStudentImage);
        if (Integer.parseInt(studentData.get("absences").toString())>=5){
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.rounded_card_danger));
        }
        holder.itemView.setOnLongClickListener(view -> {
            db.collection("Subjects").document(studentData.get("section_code").toString()).get().addOnSuccessListener(documentSnapshot -> {
                Map<String, Object> subjectData = documentSnapshot.getData();
                String messageString = "FROM SCC-SHS ATTENDANCE MONITORING APP:\n";
                messageString += "Hello, Mr./Mrs "+ studentData.get("guardianName").toString()+"!";
                messageString += "This is to inform that your son/daughter "+ studentData.get("firstname").toString()+" "+ studentData.get("lastName").toString();
                messageString += "\nhas been missing "+studentData.get("absences").toString()+" attendance for "+ subjectData.get("descriptive_title").toString();
                messageString += ".\nPlease settle this matter immediately.";

                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);


                smsIntent.setData(Uri.parse("smsto:"));
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address"  , new String ("+63"+studentData.get("guardianContactNo").toString()));
                smsIntent.putExtra("sms_body"  , messageString);
                smsIntent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
                ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
                PendingIntent sentPI = PendingIntent.getBroadcast(holder.itemView.getContext(), 0,
                        new Intent(holder.itemView.getContext(), SmsSentReceiver.class), PendingIntent.FLAG_IMMUTABLE);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(holder.itemView.getContext(), 0,
                        new Intent(holder.itemView.getContext(), SmsDeliveredReceiver.class), PendingIntent.FLAG_IMMUTABLE);
                try {
                    SmsManager sms = SmsManager.getDefault();
                    ArrayList<String> mSMSMessage = sms.divideMessage(messageString);
                    for (int i = 0; i < mSMSMessage.size(); i++) {
                        sentPendingIntents.add(i, sentPI);
                        deliveredPendingIntents.add(i, deliveredPI);
                    }
                    sms.sendMultipartTextMessage( new String ("+63"+studentData.get("guardianContactNo").toString()), null, mSMSMessage,
                            sentPendingIntents, deliveredPendingIntents);
                    Toast.makeText(holder.itemView.getContext(), "SMS sent to "+"+63"+studentData.get("guardianContactNo").toString()+".",Toast.LENGTH_LONG).show();

                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(holder.itemView.getContext(), "SMS sending failed...",Toast.LENGTH_SHORT).show();
                }
//                 String ACCOUNT_SID = "AC9d9f2accb15ae21d44727c2991c1cfcf";
//                 String AUTH_TOKEN = "37972eaa20c8171718c12c34a6bb6f9c";
//                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//                Message message = Message.creator(
//                        new com.twilio.type.PhoneNumber(new String ("+63"+studentData.get("guardianContactNo").toString())),
//                        new com.twilio.type.PhoneNumber("+12764008180"),
//                        "Hello").create();
//                try {
//                    ((Activity) holder.itemView.getContext()).startActivity(smsIntent);
//                    ((Activity) holder.itemView.getContext()).finish();
//                    Toast.makeText(holder.itemView.getContext(), "SMS sent to "+"+63"+studentData.get("guardianContactNo").toString()+".",
//                            Toast.LENGTH_LONG).show();
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText( holder.itemView.getContext(),
//                            "Error: "+ex.getMessage(), Toast.LENGTH_SHORT).show();
//                }

            });
            return  true;
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public static class StudentInformationHolder extends RecyclerView.ViewHolder{

        CircleImageView civStudentImage;
        TextView tvStudentName;
        TextView tvStudentGrade;
        TextView tvStudentStrand;
        TextView tvAbsences;
        LinearProgressIndicator lpiPunctuality;
        Button btnGenerateQRCode,btnScan, btnAttendee;
        public StudentInformationHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentGrade = itemView.findViewById(R.id.tvStudentGrade);
            tvStudentStrand = itemView.findViewById(R.id.tvStudentStrand);
            tvAbsences = itemView.findViewById(R.id.tvAbsences);
            lpiPunctuality = itemView.findViewById(R.id.lpiPunctuality);
            civStudentImage = itemView.findViewById(R.id.civStudentImage);
        }

    }
}


