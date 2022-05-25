package com.example.test.login;

import android.util.Patterns;

import com.example.test.data.SqliteManager;

import com.example.test.R;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginCheck{
    private int errorid;
    private Integer errorDetail = null;
    LoginCheck(){};

    public String login(String username, String password, SqliteManager sqliteManager) {

        String path = "http://49.235.134.191:8080/user/login?account="+ URLEncoder.encode(username)+"&password="+URLEncoder.encode(password);
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            if(conn.getResponseCode() == 200){
                conn.disconnect();
                errorid = 0;
                return "true";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";

        /*tring anser = sqliteManager.select("USER", username,"account");
        if(anser.equals(password) == true){
            errorid = 0;
        }
        else{
            errorid = 4;
            errorDetail = R.string.password_error;
        }*/
    }



    public void loginDataChanged(String username, String password,SqliteManager sqliteManager) {

        if (!isUserNameValid(username)) {
            errorDetail = R.string.invalid_username;
            errorid = 1;

        } /*else if (sqliteManager.select("USER", username,"account") == null) {
            errorid = 3;
            errorDetail = R.string.account_error;

        } */else if (!isPasswordValid(password)) {
            errorDetail = R.string.invalid_password;
            errorid = 2;
        }
        else errorid = 0;
    }

    public int getErrorid() {
        return errorid;
    }

    public Integer getErrorDetail() {
        return errorDetail;
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

}