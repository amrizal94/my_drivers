package com.example.mydrivers.Model;

public class userModel {
    private String userName, userEmail, userNumber, userPassword, userId;

    public userModel(){

    }

    public userModel(String userName, String userEmail, String userNumber, String userPassword, String userId){
        this.userName = userName;
        this.userEmail = userEmail;
        this.userNumber = userNumber;
        this.userPassword = userPassword;
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
