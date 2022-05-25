package com.example.test.ui.home;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.test.ButtonActivity;
import com.example.test.data.SqliteManager;
import com.example.test.databinding.FragmentHomeBinding;
import com.example.test.login.LoginActivity;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SqliteManager sqliteManager;
    private LoginActivity loginActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sqliteManager = new SqliteManager(getActivity());
        loginActivity = new LoginActivity();
        final TextView textView = binding.username;
        textView.setText(loginActivity.getAccount());

        final Button button = binding.exit;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues value = new ContentValues();
                value.put("password","2");
                sqliteManager.update("USER",value,"account = ?",new String[]{"@@"});
                sqliteManager.update("USER",value,"account = ?",new String[]{"@"});
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}