package com.krunal.example;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.krunal.example.ClsGlobal.CreateProgressDialog;
import static com.krunal.example.ClsGlobal.hideProgress;
import static com.krunal.example.ClsGlobal.showProgress;
import static com.krunal.example.ClsGlobal.updateProgress;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION = 1;
    private final int REQUEST_FILE_CODE = 34;
    private TextView txt_file_Name;
    private Button choose_file, Upload_file;
    String FilePath = "";
//    private static final String URL = "http://192.168.1.55:92/api/Service/saveFile";
//    OkHttpClient client;
//    private static Retrofit retrofit;
//    private static final String BASE_URL = "http://192.168.1.55:92/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_file_Name = findViewById(R.id.txt_file_Name);
        choose_file = findViewById(R.id.choose_file);
        Upload_file = findViewById(R.id.Upload_file);

        requestLocationPermission();

//
//        if (retrofit == null) {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//        }

        choose_file.setOnClickListener(v -> {

            String manufacturer = Build.MANUFACTURER;
            Log.e("manufacturer", manufacturer);

            if (manufacturer.contains("samsung")) {
                Log.e("manufacturer", "samsung call");
                Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
                intent.putExtra("CONTENT_TYPE", "*/*");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(intent, REQUEST_FILE_CODE);
            } else {
                Intent chooser = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getDownloadCacheDirectory().getPath().toString());
                chooser.addCategory(Intent.CATEGORY_OPENABLE);
                chooser.setDataAndType(uri, "*/*");

                try {
                    PackageManager pm = getPackageManager();
                    if (chooser.resolveActivity(pm) != null) {
                        startActivityForResult(chooser, REQUEST_FILE_CODE);
                    }

                } catch (android.content.ActivityNotFoundException ex) {

                    Toast.makeText(MainActivity.this, "Please install a File Manager."
                            , Toast.LENGTH_SHORT).show();
                }
            }

        });

        Upload_file.setOnClickListener(v -> {

            if (FilePath != null
                    && !FilePath.equalsIgnoreCase("")){
//                UploadFile(new File(FilePath));
//                new UploadAsyncTask().execute();

                CreateProgressDialog(MainActivity.this);
                showProgress("Uploading Backup");
                FileUploader fileUploader = new FileUploader(new File(FilePath),MainActivity.this);
                fileUploader.SetCallBack(new FileUploader.FileUploaderCallback() {
                    @Override
                    public void onError() {
                        hideProgress();
                    }

                    @Override
                    public void onFinish(String responses) {
                        hideProgress();
                    }

                    @Override
                    public void onProgressUpdate(int currentpercent, int totalpercent,String msg) {
                        updateProgress(totalpercent,"Uploading Backup",msg);
                    }
                });


                FilePath = "";
                txt_file_Name.setText("File Path");
            }else {
                Toast.makeText(MainActivity.this,"Select File",Toast.LENGTH_SHORT).show();
            }

        });


    }


    @AfterPermissionGranted(REQUEST_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = new String[0];
        perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission",
                    REQUEST_PERMISSION, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions,
                grantResults, this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_FILE_CODE && resultCode == Activity.RESULT_OK) {
            if (EasyPermissions.hasPermissions(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {


                Uri getUri = data.getData();
                if (getUri != null) {
                    try {
                        String getpathFromFileManager = data.getData().getPath();

                        Log.e("contains", getpathFromFileManager);

                        if ("content".equals(getUri.getScheme())) {
                            Log.e("contains", "getUri.getScheme()");
                            FilePath = ClsGlobal.getPathFromUri(MainActivity.this, getUri);
                            txt_file_Name.setText(FilePath);
                        } else {

                            FilePath = getUri.getPath();

                            txt_file_Name.setText(FilePath);
                        }

                        Log.e("contains", FilePath);

                        if (getpathFromFileManager != null && getpathFromFileManager.contains("primary:")) {
                            Log.e("contains", "contains inside");

                            if (FilePath != null) {

//                                UploadFile(new File(FilePath));
//                                new LocalRestoreAsyncTask(this, FilePath).execute();
                                Toast.makeText(this, FilePath, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Incorrect file", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Log.e("contains", "contains outside");
                            if (getpathFromFileManager != null && FilePath != null) {
//                                UploadFile(new File(FilePath));
//                                new LocalRestoreAsyncTask(this, FilePath).execute();
                                Toast.makeText(this, FilePath, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Incorrect file", Toast.LENGTH_LONG).show();
                            }

                        }


                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }

                }


            } else {
                EasyPermissions.requestPermissions(this, "This app needs access to your file storage",
                        REQUEST_PERMISSION, Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        }

    }


    private void UploadFile(File file) {
        InterfaceFileUpload interfaceFileUpload = ApiClient.getRetrofitInstance().create(InterfaceFileUpload.class);
//        InterfaceFileUpload interfaceFileUpload = retrofit.create(InterfaceFileUpload.class);
        Log.e("--URL--", "interfaceDesignation: " + interfaceFileUpload.toString());

        RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file",
                file.getName(), mFile);

        ///RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        RequestBody CustomerID = RequestBody.create(
                        okhttp3.MultipartBody.FORM, "CIK002");

        RequestBody BackupType = RequestBody.create(
                okhttp3.MultipartBody.FORM, "Document");

        RequestBody ProductName = RequestBody.create(
                okhttp3.MultipartBody.FORM, "Retail");

        RequestBody AppVersionAppVersion = RequestBody.create(
                okhttp3.MultipartBody.FORM, "1.22.3");

        RequestBody AppType = RequestBody.create(
                okhttp3.MultipartBody.FORM, "Mobile");

        RequestBody IMEINumber = RequestBody.create(
                okhttp3.MultipartBody.FORM, "23234234");

        RequestBody DeviceInfo = RequestBody.create(
                okhttp3.MultipartBody.FORM, "Motorola");

        RequestBody Remark = RequestBody.create(
                okhttp3.MultipartBody.FORM, "@Backup");

        RequestBody FileName = RequestBody.create(
                okhttp3.MultipartBody.FORM, "@SHAP_Backup");

        RequestBody Extentsion = RequestBody.create(
                okhttp3.MultipartBody.FORM, ".zip");


        Call<UploadResponse> fileUpload = interfaceFileUpload.UploadFile(fileToUpload,CustomerID,BackupType,
                ProductName,AppVersionAppVersion,AppType,IMEINumber,DeviceInfo,Remark,FileName,Extentsion);

        Log.e("--URL--", "************************  before call : " +
                fileUpload.request().url());

        fileUpload.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call,
                                   Response<UploadResponse> response) {

                if (response != null && response.code() ==200){
                    Log.e("getResponse","--Response:-"+response.message());
                    Log.e("getResponse","--Response:-"+response.body().toString());

                    if (response.body().getSuccess().equalsIgnoreCase("1")){
                        Toast.makeText(MainActivity.this, response.body().getMessage(),Toast.LENGTH_LONG).show();
                    }

                }


            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {

            }
        });


    }

//    @SuppressLint("StaticFieldLeak")
//    class UploadAsyncTask extends AsyncTask<Void, Void, Void> {
//
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            ClsGlobal.sendFileToServer(FilePath,URL);
////            if (!FilePath.equalsIgnoreCase("")){
////                Log.e("test","doInBackground call");
////
////            }
//
//            return null;
//        }
    }








