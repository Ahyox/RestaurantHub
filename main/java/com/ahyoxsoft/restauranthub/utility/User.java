package com.ahyoxsoft.restauranthub.utility;

/**
 * Created by dejiogunnubi on 12/25/15.
 */
public class User {
    private String userID;
    private int response;
    private String username;
    private String email;
    private int login_medium;
    private String timestamp;
    private String last_login;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLogin_medium() {
        return login_medium;
    }

    public void setLogin_medium(int login_medium) {
        this.login_medium = login_medium;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }
}
