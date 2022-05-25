package com.example.test.ui.news;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class NewsModel {

    NewsModel() {}

    public String getJson(){
        try {
            URL url = new URL("http://49.235.134.191:8080/news/get");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            InputStream is = conn.getInputStream();
            conn.disconnect();

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            String json = stringBuilder.toString();
            Log.i("qweee","json:"+json);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<HashMap<String, Object>> getNews(String json) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.optInt("code");
            if (code != 200) {
                //Log.i("qweeew",Integer.toString(code));
                return null;
            }
            JSONArray jsonArray = new JSONArray(jsonObject.optString("data"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("item_image",item.optString("imageUrl"));
                map.put("item_title", item.optString("title"));
                map.put("item_news", item.optString("desc"));
                map.put("item_time", item.optString("publishTime"));
                list.add(map);
            }
            return list;
        } catch (JSONException e) {
            System.out.println("异常为：");e.printStackTrace();
        }
        return null;
    }

}
