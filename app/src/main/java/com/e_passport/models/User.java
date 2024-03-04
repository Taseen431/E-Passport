package com.e_passport.models;

public class User {
    private String profileImg64bit;
    private String name;
    private String email;
    private String dateOfBirth;
    private String age;
    private String gender;
    private String phoneNumber;
    private String address;
    private String nidNumber;
    private String nidFront64bit;
    private String nidBack64bit;
    private String passportStatus;

    public User(String profileImg64bit, String name, String email, String dateOfBirth, String age, String gender, String phoneNumber, String address, String nidNumber, String nidFront64bit, String nidBack64bit, String passportStatus) {
        this.profileImg64bit = profileImg64bit;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.nidNumber = nidNumber;
        this.nidFront64bit = nidFront64bit;
        this.nidBack64bit = nidBack64bit;
        this.passportStatus = passportStatus;
    }
    public String getProfileImg64bit() {
        return profileImg64bit;
    }
    public void setProfileImg64bit(String profileImg64bit) {
        this.profileImg64bit = profileImg64bit;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getAge() {
        return age;
    }
    public void setAge(String age) {
        this.age = age;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getNidNumber() {
        return nidNumber;
    }
    public void setNidNumber(String nidNumber) {
        this.nidNumber = nidNumber;
    }
    public String getNidFront64bit() {
        return nidFront64bit;
    }
    public void setNidFront64bit(String nidFront64bit) {
        this.nidFront64bit = nidFront64bit;
    }
    public String getNidBack64bit() {
        return nidBack64bit;
    }
    public void setNidBack64bit(String nidBack64bit) {
        this.nidBack64bit = nidBack64bit;
    }
    public String getPassportStatus() {
        return passportStatus;
    }

    public void setPassportStatus(String passportStatus) {
        this.passportStatus = passportStatus;
    }
}