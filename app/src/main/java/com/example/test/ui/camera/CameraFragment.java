package com.example.test.ui.camera;

import static android.os.Build.VERSION_CODES.R;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.databinding.FragmentCameraBinding;
import com.example.test.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CameraFragment extends Fragment {
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private FragmentCameraBinding binding;
    private ImageButton imageButton;
    private String phoPath;
    private String sdPath;
    private String path;
    private Uri uri;
    private feedBack feedBack;
    private String imageURL;
    private LoginActivity loginActivity;
    private RadioButton aq;
    private RadioButton ws;
    private RadioButton zx;
    private RadioButton yb;
    private RadioButton zy;
    private Button button;
    private Button location;
    private TextView title;
    private TextView decribe;
    private JSONObject json;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(msg.what == 123){
                //Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_SHORT).show();
                initJSON();
                uploadJSON();


            }else if(msg.what == 456){
                tipDialog("?????????????????????");
                initall();
            } else{
                tipDialog("????????????,????????????: "+msg.what);
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        feedBack = new feedBack();
        loginActivity = new LoginActivity();
        // android 7.0???????????????????????????
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        title = binding.cameraTitle;
        decribe = binding.cameraDescribe;
        imageButton = binding.cameraImage;
        aq = binding.radioButtonAq;
        ws = binding.radioButtonWs;
        zx = binding.radioButtonZx;
        yb = binding.radioButtonYb;
        zy = binding.radioButtonZy;
        button = binding.cameraUpload;
        location = binding.cameraLocation;
        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, 1);


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationClient.setAgreePrivacy(true);
                getPermissionMethod();

                try {
                    mLocationClient = new LocationClient(getActivity().getApplicationContext());
                } catch (Exception e) {
                    System.out.println("qqqqqqq:");e.printStackTrace();
                }
                mLocationClient.registerLocationListener(myListener);
                getlocation();
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK){
            File file;
            Log.i("photoPath",path);
            try {
                file = new File(path);
                if(file.exists()){
                    int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if(permission != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, 1);

                    FileInputStream fs = new FileInputStream(path);
                    BufferedInputStream bf = new BufferedInputStream(fs);
                    Bitmap bitmap = BitmapFactory.decodeStream(bf);
                    imageButton.setImageBitmap(bitmap);
                    fs.close();
                    bf.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void openCamera(){
        File file = new File(Environment.getExternalStorageDirectory(),"Pictures");
        if(!file.exists()){
            file.getParentFile().mkdir();
        }
        sdPath = file.getAbsolutePath();
        phoPath = "/IMG_" + System.currentTimeMillis() + ".jpg";
        path = sdPath + phoPath;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        uri = Uri.fromFile(new File(path));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(intent,2);

    }

    private void uploadImage(){
        MediaType mediaType = MediaType.parse("image/jpeg");
        OkHttpClient okHttpClient = new OkHttpClient();
        File file = new File(path);
        RequestBody requestBody = RequestBody.create(mediaType,file);
        MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file",
                file.getName(),requestBody).build();
        Request request = new Request.Builder().post(multipartBody).url("http://49.235.134.191:8080/file/image/upload").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "onFailure: " + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("TAG", response.protocol() + " " +response.code() + " " + response.message());

                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Log.d("TAG", headers.name(i) + ":" + headers.value(i));
                }
                String onResponse = response.body().string();
                Log.d("TAG", "onResponse: " + onResponse);
                JSONObject imageJson;
                try {
                    imageJson = new JSONObject(onResponse);
                    imageURL = imageJson.optString("data");
                    Message message = new Message();
                    message.what = 123;
                    handler.sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void initJSON(){
        SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault());
        String myDate = format.format(new Date());
        Log.i("timeeee",myDate);

        feedBack.setAccount(loginActivity.getAccount());
        feedBack.setTitle(title.getText().toString());
        feedBack.setDesc(decribe.getText().toString());
        feedBack.setTime(myDate);
        feedBack.setImageUrl(imageURL);
        feedBack.setAddress(location.getText().toString());
        if(aq.isChecked())
            feedBack.setCategory("????????????");
        else if(ws.isChecked())
            feedBack.setCategory("????????????");
        else if(zx.isChecked())
            feedBack.setCategory("????????????");
        if(yb.isChecked())
            feedBack.setDegree(0);
        else if(zy.isChecked())
            feedBack.setDegree(1);
        json = feedBack.getJson();
        Log.i("timmeeer", json.toString());
    }
    private void uploadJSON(){
        MediaType mediaType = MediaType.parse("application/json");
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(mediaType, String.valueOf(json));
        //FormBody formBody = new FormBody.Builder().add("feedBack", String.valueOf(json)).build();
        Request request = new Request.Builder().post(requestBody).url("http://49.235.134.191:8080/feedback/save").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "onFailure: " + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("TAG", response.protocol() + " " +response.code() + " " + response.message());

                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Log.d("TAG", headers.name(i) + ":" + headers.value(i));
                }
                Message message = new Message();

                String onResponse = response.body().string();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(onResponse);
                    if(jsonObject.optInt("code") == 200){
                        message.what = 456;
                    }
                    else{
                        message.what = jsonObject.optInt("code");
                    }
                    handler.sendMessage(message);
                    Log.d("TAG", "onResponseJSON: " + onResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }


    /**
     * ???????????????
     */
    public void tipDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("?????????");
        builder.setMessage(message);
        builder.setCancelable(true);            //??????????????????????????????????????????????????????
        //??????????????????
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();      //??????AlertDialog??????
        //??????????????????????????????
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Log.e("TAG", "??????????????????");
            }
        });
        //??????????????????????????????
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.e("TAG", "??????????????????");
            }
        });
        dialog.show();                              //???????????????
    }

    private void getlocation(){
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);
        //?????????????????????????????????????????????????????????????????????false
        //?????????????????????????????????????????????????????????????????????true

        option.setAddrType("all");
        option.setNeedNewVersionRgc(true);
        //????????????????????????????????????????????????????????????????????????????????????true
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        //mLocationClient???????????????????????????LocationClient??????
        //??????????????????LocationClientOption???????????????setLocOption???????????????LocationClient????????????
        //??????LocationClientOption?????????????????????????????????LocationClientOption??????????????????
        mLocationClient.start();
        //for(int i=1;i<20;i++)
        //mLocationClient.start();
        myListener = new MyLocationListener();
        Log.i("qqqq","qwe");

    }

    private void getPermissionMethod() {
        List<String> permissionList = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity().getApplicationContext(),"????????????,???????????????????????????",Toast.LENGTH_SHORT).show();
            //?????????????????????????????????????????????????????????????????????????????????????????????
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        if (!permissionList.isEmpty()){
            //?????????????????????
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(),permissions,1);
        }
        LocationManager locManager = (LocationManager)getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.i("qqqq","sdddddd");
            // ????????????????????????????????????????????????????????????????????????????????????????????????
        }
    }


    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveVdrLocation(BDLocation bdLocation) {
            super.onReceiveVdrLocation(bdLocation);
        }
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Log.i("qqqqw",":"+bdLocation.getLocType()+bdLocation.getAddrStr());
            String addr = bdLocation.getAddrStr();    //????????????????????????
            String country = bdLocation.getCountry();    //????????????
            String province = bdLocation.getProvince();    //????????????
            String city = bdLocation.getCity();    //????????????
            String district = bdLocation.getDistrict();    //????????????
            String street = bdLocation.getStreet();    //??????????????????
            String town = bdLocation.getTown();    //??????????????????
            if(addr == null){
                location.setText("????????????????????????????????????");
            }
            else
                location.setText(addr);
        }
    }

    private void initall(){
        title.setText("");

        decribe.setText("");
        aq.setChecked(false);
        ws.setChecked(false);
        zx.setChecked(false);
        yb.setChecked(false);
        zy.setChecked(false);
        location.setText("????????????");

        imageButton.setImageResource(com.example.test.R.drawable.choose_pictures);
    }

}

