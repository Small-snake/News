package com.example.test.ui.news;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.example.test.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<HashMap<String,Object>> data;
    private Context context;
    public NewsAdapter(Context context,ArrayList<HashMap<String,Object>> data){
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null){
            holder = new ViewHolder();
            view = View.inflate(context,R.layout.lv_item,null);
            holder.image = view.findViewById(R.id.item_image);
            holder.news = view.findViewById(R.id.item_news);
            holder.time = view.findViewById(R.id.item_time);
            holder.title = view.findViewById(R.id.item_title);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        Map<String,Object> item = data.get(i);
        holder.news.setText(item.get("item_news").toString());
        holder.time.setText(item.get("item_time").toString());
        holder.title.setText(item.get("item_title").toString());
        Glide.with(context).load(item.get("item_image").toString()).into(holder.image);

        return view;
    }


    static class ViewHolder{
        TextView news;
        TextView time;
        TextView title;
        ImageView image;
    }
}
