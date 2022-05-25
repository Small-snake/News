package com.example.test.register;

import android.content.ContentValues;
import android.util.Patterns;
import com.example.test.R;
import com.example.test.data.SqliteManager;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterCheck{
    private int errorid = 100;//0->true  1->username  2->password  3->password2
    private Integer errorDetail = null;
    RegisterCheck(){};



    public String register(String username, String password, SqliteManager sqliteManager) {
        ContentValues qwe = new ContentValues();
        qwe.put("account",username);
        qwe.put("password",password);

       sqliteManager.insert("USER",null, qwe);

        String path = "http://49.235.134.191:8080/user/save?account="+ URLEncoder.encode(username)+"&password="+URLEncoder.encode(password);
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            if(conn.getResponseCode() == 200){
                conn.disconnect();
                return "true";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }



    public void registerDataChanged(String username, String password, String password2, SqliteManager sqliteManager) {

        if (!isUserNameValid(username)) {
            errorDetail = R.string.invalid_username;
            errorid = 1;

        } else if (!isPasswordValid(password)) {
            errorDetail = R.string.invalid_password;
            errorid = 2;
        } else if (password.equals(password2) == false) {
            errorDetail = R.string.unequal_password;
            errorid = 3;
        }  else{
            errorid = 5;
        }
    }

    public int getErrorid(){
        return errorid;
    }
    public Integer getErrorDetail(){
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