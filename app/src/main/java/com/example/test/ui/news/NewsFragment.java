package com.example.test.ui.news;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test.R;
import com.example.test.databinding.FragmentNewsBinding;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsFragment extends Fragment {
    private NewsModel newsModel = new NewsModel();
    NewsAdapter newsAdapter;
    ListView listView;

    private ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 123){
                String json = msg.obj.toString();

                    listItem = newsModel.getNews(json);
                    newsAdapter = new NewsAdapter(getActivity(), listItem);
                    listView.setAdapter(newsAdapter);


            }
        }
    };


    private FragmentNewsBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //取消不能在主线程请求网络的限制
        //StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
       // StrictMode.setThreadPolicy(policy);

        binding = FragmentNewsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        listView = binding.lv;


        new Thread(new Runnable() {
            @Override
            public void run() {

                    while(true){
                        String json = newsModel.getJson();
                        if(json != null){
                            Message msg = new Message();
                            msg.what = 123;
                            msg.obj = json;

                            handler.sendMessage(msg);
                            break;
                        }
                    }




            }
        }).start();

        if(listItem == null)
        Log.i("qweee",newsModel.getJson());
        //SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),listItem,R.layout.lv_item,new String[]{"item_image","item_title","item_news","item_time"},new int[]{R.id.item_image,R.id.item_title,R.id.item_news,R.id.item_time});

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}