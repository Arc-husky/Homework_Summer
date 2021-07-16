package com.bytedance.practice5;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.practice5.model.UploadResponse;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity {
    private static final String TAG = "chapter5";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private IApi api;
    private Uri coverImageUri;
    private SimpleDraweeView coverSD;
    private EditText toEditText;
    private EditText contentEditText ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNetwork();
        setContentView(R.layout.activity_upload);
        coverSD = findViewById(R.id.sd_cover);
        toEditText = findViewById(R.id.et_to);
        contentEditText = findViewById(R.id.et_content);
        findViewById(R.id.btn_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });


        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                coverSD.setImageURI(coverImageUri);

                if (coverImageUri != null) {
                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }
    }

    private void initNetwork() {
        //TODO 3
        // 创建Retrofit实例
        // 生成api对象
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(IApi.class);
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    private void submit() {
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String to = toEditText.getText().toString();
        if (TextUtils.isEmpty(to)) {
            Toast.makeText(this, "请输入TA的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入想要对TA说的话", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( coverImageData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO 5
        // 使用api.submitMessage()方法提交留言
        // 如果提交成功则关闭activity，否则弹出toast

        MultipartBody.Part from_part = MultipartBody.Part.createFormData("from", Constants.USER_NAME);
        MultipartBody.Part to_part = MultipartBody.Part.createFormData("to",to);
        MultipartBody.Part content_part = MultipartBody.Part.createFormData("content",content);
        MultipartBody.Part image_part = MultipartBody.Part.createFormData("image", "picture.jpg",
                RequestBody.create(MediaType.parse("multipart/form_data"), coverImageData));

        Call<UploadResponse> call = api.submitMessage(Constants.STUDENT_ID, "",
                from_part, to_part,content_part, image_part, Constants.token);
        try {
            call.enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    if(!response.isSuccessful()) {
                        Toast.makeText(getBaseContext(),"提交错误",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!response.body().success) {
                        Toast.makeText(getBaseContext(),"提交失败:"+response.body().error,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent i = new Intent(UploadActivity.this,MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Toast.makeText(getBaseContext(),"提交失败",Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        }catch (Exception e) {
            Toast.makeText(this,"提交失败"+e.toString(),Toast.LENGTH_SHORT).show();
        }
    }


    // TODO 7 选做 用URLConnection的方式实现提交
    private void submitMessageWithURLConnection(){
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String to = toEditText.getText().toString();
        if (TextUtils.isEmpty(to)) {
            Toast.makeText(this, "请输入TA的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入想要对TA说的话", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( coverImageData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlStr = String.format(Constants.BASE_URL+"messages?student_id=%s&extra_value=%s"
                            ,Constants.STUDENT_ID,"\"\"");
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(6000);
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setRequestProperty("token",Constants.token);
                    conn.setRequestProperty("connection", "Keep-Alive");
                    conn.setRequestProperty("Charsert", "UTF-8");
                    final String Boundary = "====WebKitFormBoundary";
                    conn.setRequestProperty("Content-Type","multipart/form-data; boundary="+Boundary);
                    final String SEP = "--"+Boundary+"\r\n";
                    StringBuilder builder = new StringBuilder();

//                    builder.append("\r\n");

                    builder.append(SEP);
                    builder.append("Content-Disposition: form-data; name=\"from\"\r\n\r\n");
                    builder.append(Constants.USER_NAME);
                    builder.append("\r\n");

                    builder.append(SEP);
                    builder.append("Content-Disposition: form-data; name=\"to\"\r\n\r\n");
                    builder.append(to);
                    builder.append("\r\n");

                    builder.append(SEP);
                    builder.append("Content-Disposition: form-data; name=\"content\"\r\n\r\n");
                    builder.append(content);
                    builder.append("\r\n");

                    builder.append(SEP);
                    builder.append("Content-Disposition: form-data; name=\"image\"; " +
                            "filename=\"picture.jpg\"\r\n");
                    builder.append("Content-Type: image/jpeg\r\n\r\n");

                    String result =builder.toString();
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.write(builder.toString().getBytes());
                    os.write(coverImageData);
                    os.write(("\r\n--"+Boundary+"--\r\n").getBytes());
                    os.flush();

                    if(conn.getResponseCode() == 200) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                        UploadResponse response = new Gson().fromJson(reader, new TypeToken<UploadResponse>() {
                        }.getType());
                        if(response.success) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UploadActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(UploadActivity.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                }
                            });
                        }else {
                            Toast.makeText(UploadActivity.this,"上传失败:"+response.error,Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UploadActivity.this,"上传失败！",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    conn.disconnect();
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(UploadActivity.this,"上传失败！",Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }


    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


}
