package com.example.test.ui.camera;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class feedBack {
    private long id;  //无需指定
    private String imageUrl; //图片url
    private String title;   //标题
    private String desc;  //描述
    private String account;  //账号
    private String address = "qqq"; //地址，定位
    private String category; //类别：安全隐患、卫生问题、秩序问题
    private int degree;  //级别：0-一般，  1-重要
    private String time;  //时间: 2021-11-06T13:14:25.909+00:00
    private String process = "已提交"; //当前状态："已提交"

    public void setId(long id){
        this.id = id;
    }
    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setDesc(String desc){
        this.desc = desc;
    }
    public void setAccount(String account){
        this.account = account;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public void setCategory(String category){
        this.category = category;
    }
    public void setDegree(int degree){
        this.degree = degree;
    }
    public void setTime(String time){
        this.time = time;
    }
    public void setProcess(String process){
        this.process = process;
    }
    public JSONObject getJson(){
        JSONObject json = new JSONObject();
        try {
            json.put("id",id);
            json.put("imageUrl",imageUrl);
            json.put("title",title);
            json.put("desc",desc);
            json.put("account",account);
            json.put("address","address");
            json.put("category",category);
            json.put("degree",degree);
            json.put("time",time);
            json.put("process",process);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
