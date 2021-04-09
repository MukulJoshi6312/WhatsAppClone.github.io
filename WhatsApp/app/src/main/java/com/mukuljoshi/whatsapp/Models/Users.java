package com.mukuljoshi.whatsapp.Models;

public class Users {

    String porfilepic, userName , mail , password, userID , lastMessage,status;

    public Users(String porfilepic, String userName, String mail, String password, String userID, String lastMessage, String status) {
        this.porfilepic = porfilepic;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.userID = userID;
        this.lastMessage = lastMessage;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Users(){}

    // Sign up constructor

    public Users(String userName, String mail, String password) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;

    }



    public String getPorfilepic() {
        return porfilepic;
    }

    public void setPorfilepic(String porfilepic) {
        this.porfilepic = porfilepic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
