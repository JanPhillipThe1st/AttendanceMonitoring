package com.example.attendancemonitoring;

public class StudentClass {
public String firstname,middleName,lastName,contactNo,address,guardianName,guardianContactNo,email,password,userType,imageUrl,track_strand,grade,EDPNumber;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public StudentClass(String firstname, String middleName, String lastName, String contactNo, String address, String guardianName,
                        String guardianContactNo,
                        String email,
                        String password,
                        String userType,
                        String imageUrl,
                        String grade,
                        String track_strand,
                        String EDPNumber) {
        this.firstname = firstname;
        this.middleName = middleName;
        this.EDPNumber = EDPNumber;
        this.lastName = lastName;
        this.contactNo = contactNo;
        this.address = address;
        this.guardianName = guardianName;
        this.guardianContactNo = guardianContactNo;
        this.imageUrl = imageUrl;
        this.email = email;
        this.password = password;
        this.grade=grade;
        this.track_strand=track_strand;
        this.userType=userType;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }

    public String getGuardianContactNo() {
        return guardianContactNo;
    }

    public void setGuardianContactNo(String guardianContactNo) {
        this.guardianContactNo = guardianContactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
