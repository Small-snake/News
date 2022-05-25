package com.example.test.register;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.data.SqliteManager;
import com.example.test.databinding.ActivityRegisterBinding;
import com.example.test.login.LoginActivity;
import com.example.test.ui.news.NewsAdapter;

import java.util.Optional;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterCheck registerCheck = new RegisterCheck();
    private SqliteManager sqliteManager = new SqliteManager(RegisterActivity.this);

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 123){
              String check = msg.obj.toString();
                if(check.equals("false")){
                    Toast toast = Toast.makeText(getApplicationContext(),"Register failed,Please check again",Toast.LENGTH_LONG);
                    toast.show();
                }
                else{
                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    Toast toast = Toast.makeText(getApplicationContext(),"Register Success!",Toast.LENGTH_LONG);
                    toast.show();
                    startActivity(i);
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final EditText passwordAnotherEditText = binding.password2;
        final Button registerButton = binding.resiger;




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
                registerCheck.registerDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(),passwordAnotherEditText.getText().toString(),sqliteManager);
                if(registerCheck.getErrorid() == 0){
                    registerButton.setEnabled(true);
                } else if(registerCheck.getErrorid() == 1){
                    usernameEditText.setError(getString(registerCheck.getErrorDetail()));
                } /*else if(registerCheck.getErrorid() == 4){
                    usernameEditText.setError(getString(registerCheck.getErrorDetail()));
                }*/else if(registerCheck.getErrorid() == 2){
                    passwordEditText.setError(getString(registerCheck.getErrorDetail()));
                } else if(registerCheck.getErrorid() == 3){
                    passwordAnotherEditText.setError(getString(registerCheck.getErrorDetail()));
                }
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordAnotherEditText.addTextChangedListener(afterTextChangedListener);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String check = registerCheck.register(usernameEditText.getText().toString(),
                                    passwordEditText.getText().toString(), sqliteManager);
                            Message message = new Message();
                            message.what = 123;
                            message.obj = check;
                            handler.sendMessage(message);
                        }
                    }).start();
                }
                return false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String check = registerCheck.register(usernameEditText.getText().toString(),
                                passwordEditText.getText().toString(), sqliteManager);
                        Message message = new Message();
                        message.what = 123;
                        message.obj = check;
                        handler.sendMessage(message);
                    }
                }).start();



            }
        });

    }
}