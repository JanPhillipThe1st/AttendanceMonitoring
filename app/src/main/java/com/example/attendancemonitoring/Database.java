package com.example.attendancemonitoring;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class Database {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public List<Map<String,Object>> getStudents(){
        ArrayList<Map<String,Object>> result = new ArrayList<>();
        db.collection("StudentAccount").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot studentAccounts:queryDocumentSnapshots){
                result.add(studentAccounts.getData());
            }
        });
        return result;
    }
    public List<Map<String,Object>> getStudents(Filter filter){
        ArrayList<Map<String,Object>> result = new ArrayList<>();
        db.collection("StudentAccount").where(filter).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(DocumentSnapshot studentAccounts:queryDocumentSnapshots){
                result.add(studentAccounts.getData());
            }
        });
        return result;
    }
}
