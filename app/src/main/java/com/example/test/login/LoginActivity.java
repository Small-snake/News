package com.example.test.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.data.SqliteManager;
import com.example.test.databinding.ActivityLoginBinding;
import com.example.test.databinding.ActivityRegisterBinding;
import com.example.test.register.RegisterActivity;
import com.example.test.register.RegisterCheck;

public class LoginActivity extends AppCompatActivity {
//@ = 1 ->记住密码||@@ = 1 ->自动登录||@@@ = 账号 -> 账号
    private static String account = "qwe";
    private ActivityLoginBinding binding;
    private LoginCheck loginCheck = new LoginCheck();
    private SqliteManager sqliteManager = new SqliteManager(LoginActivity.this);

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 123){
                String check = msg.obj.toString();
                if(check.equals("false")){
                    Toast toast = Toast.makeText(getApplicationContext(),"Login failed,Please check again",Toast.LENGTH_LONG);
                    toast.show();
                }
                else{
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    Toast toast = Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_LONG);
                    toast.show();

                    startActivity(i);
                }

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //sqliteManager.dropTable();



        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final Button registerButton = binding.register;
        final CheckBox remember = binding.remember;
        final CheckBox autologin = binding.autologin;
        init(sqliteManager);
       // checkLogin(autologin,remember,usernameEditText,passwordEditText);



        if(sqliteManager.select("USER", "@@", "account").equals("1") == true){
            autologin.setChecked(true);
            loginButton.setEnabled(true);
            remember.setChecked(true);
            String username = sqliteManager.select("USER", "@@@", "account");
            usernameEditText.setText(username);
            passwordEditText.setText(sqliteManager.select("USER", username, "account"));
            loginButton.post(new Runnable() {
                @Override
                public void run() {
                    loginButton.performClick();
                }
            });

           // loginButton.performClick();
        } else if(sqliteManager.select("USER", "@", "account").equals("1") == true){
            remember.setChecked(true);
            loginButton.setEnabled(true);
            String username = sqliteManager.select("USER", "@@@", "account");
            usernameEditText.setText(username);
            passwordEditText.setText(sqliteManager.select("USER", username, "account"));

        }




        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginCheck.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),sqliteManager);
                if(loginCheck.getErrorid() == 0){
                    loginButton.setEnabled(true);
                } else if(loginCheck.getErrorid() == 1){
                    usernameEditText.setError(getString(loginCheck.getErrorDetail()));
                } else if(loginCheck.getErrorid() == 2){
                    passwordEditText.setError(getString(loginCheck.getErrorDetail()));
                }  else if(loginCheck.getErrorid() == 3){
                    usernameEditText.setError(getString(loginCheck.getErrorDetail()));
                }
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(loginCheck.getErrorid() == 4){
                        Toast toast = Toast.makeText(getApplicationContext(),"Login failed,Password uncorrect",Toast.LENGTH_LONG);
                        toast.show();
                    }
                    else if(loginCheck.getErrorid() != 0){
                        Toast toast = Toast.makeText(getApplicationContext(),"Login failed,Please check again",Toast.LENGTH_LONG);
                        toast.show();
                    }
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_LONG);
                        toast.show();
                        loginCheck.login(usernameEditText.getText().toString(),
                                passwordEditText.getText().toString(), sqliteManager);
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                }
                return false;
            }
        });

        autologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(autologin.isChecked() == true)
                remember.setChecked(true);
            }
        });

        remember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(remember.isChecked() == false && autologin.isChecked() == true){
                    autologin.setChecked(false);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        account = usernameEditText.getText().toString();
                        String check = loginCheck.login(usernameEditText.getText().toString(),
                                passwordEditText.getText().toString(), sqliteManager);
                        Log.i("loginqqq",check);
                        Message message = new Message();
                        message.what = 123;
                        message.obj = check;
                        handler.sendMessage(message);
                        if(check.equals("true")){
                            beforeLogin(autologin,remember,usernameEditText.getText().toString(),sqliteManager);
                        }
                    }
                }).start();

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });


    }

    private void init(SqliteManager sqliteManager){
        ContentValues value = new ContentValues();
        if(sqliteManager.select("USER", "@", "account") == null){
            value.put("account","@");
            value.put("password","2");
            sqliteManager.insert("USER",null,value);
        }
        value.clear();
        if(sqliteManager.select("USER", "@@", "account") == null){

            value.put("account","@@");
            value.put("password","2");
            sqliteManager.insert("USER",null,value);
        }
        value.clear();
        if(sqliteManager.select("USER", "@@@", "account") == null){
            value.put("account","@@@");
            value.put("password","2");
            sqliteManager.insert("USER",null,value);
        }



    }



    private void checkLogin(CheckBox autologin,CheckBox remember,EditText usernameEditText,EditText passwordEditText){
        String username = new String();
        if(sqliteManager.select("USER", "@@", "account").equals("1") == true){
            autologin.setChecked(true);
            username = sqliteManager.select("USER", "@@@", "account");
            usernameEditText.setText(username);
            passwordEditText.setText(sqliteManager.select("USER", username, "account"));
        } else if(sqliteManager.select("USER", "@", "account").equals("1") == true){
            remember.setChecked(true);
            username = sqliteManager.select("USER", "@@@", "account");
            passwordEditText.setText(sqliteManager.select("USER", username, "account"));
        }
    }

    private void beforeLogin(CheckBox autologin,CheckBox remember,String username,SqliteManager sqliteManager){
        ContentValues value = new ContentValues();
        value.put("password",username);
        sqliteManager.update("USER",value,"account = ?",new String[]{"@@@"});

        if(remember.isChecked() == true){
            if(autologin.isChecked() == true){
                value.clear();
                value.put("password","1");
                sqliteManager.update("USER",value,"account = ?",new String[]{"@@"});
            } else{
                value.clear();
                value.put("password","2");
                sqliteManager.update("USER",value,"account = ?",new String[]{"@@"});
            }
                value.clear();
                value.put("password","1");
                sqliteManager.update("USER",value,"account = ?",new String[]{"@"});
        } else{
            value.clear();
            value.put("password","2");
            sqliteManager.update("USER",value,"account = ?",new String[]{"@"});
        }
    }

public String getAccount(){
        return account;
}


}