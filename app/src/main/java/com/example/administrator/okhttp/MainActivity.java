package com.example.administrator.okhttp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button send,postsend,sendfile,downloadfile;
    private OkHttpClient mOkHttpClient;
    private URL url;

    /*public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mOkHttpClient=initHttpClient();

        send=(Button)findViewById(R.id.send);
        postsend=(Button)findViewById(R.id.postsend);
        sendfile=(Button)findViewById(R.id.sendfile);
        downloadfile=(Button)findViewById(R.id.downloadfile);

        // 用activity 直接实现OnClickListener， 必须要有的设置
        send.setOnClickListener(this);
        postsend.setOnClickListener(this);
        sendfile.setOnClickListener(this);
        downloadfile.setOnClickListener(this);
        try {
            url = new URL("http://www.baidu.com");
        } catch(MalformedURLException e){
            e.printStackTrace();
        }
    }
    public void onClick(View v){

        switch(v.getId()){
            case R.id.send:
                send(url);
                break;
            case R.id.postsend:
                post();
                break;
            case  R.id.sendfile:
                //postAsynFile();
                break;
            case R.id.downloadfile:
                downAsynFile();
                break;
        }

    }

    private OkHttpClient initHttpClient(){
        File sdcache = getExternalCacheDir();
        int cacheSize = 10 * 1024 * 1024;
        OkHttpClient client =null;
        //推荐使用OkHttpClient.Builder() 去建立client.
        client=new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(5,TimeUnit.SECONDS).writeTimeout(5,TimeUnit.SECONDS)
                .cache(new Cache(sdcache,cacheSize)).build();
        return client;
    }

    /*
    *执行get 命令
     */
    private void send(URL url){

        Request request=new Request.Builder().url(url).get()
                //.cacheControl(CacheControl.FORCE_NETWORK)   //缓存设置为每次都从网络重新获取
                .build();
        Call call=mOkHttpClient.newCall(request);
        call.enqueue(new Callback(){
            public void onFailure(Call var1, IOException var2){

            }

            public void onResponse(Call var1, Response var2) throws IOException{
                if (null != var2.cacheResponse()) {
                    String str = var2.cacheResponse().toString();
                    Log.i("wangshu", "cache---" + str);
                } else {
                    var2.body().string();
                    String str = var2.networkResponse().toString();
                    Log.i("wangshu", "network---" + str);
                }

            }
        });
    }
    /*
    * 执行post 命令
    */
    private void post(){
        //FormBody 继承了RequestBody
        FormBody foramBody=new FormBody.Builder().add("user","Li")
                .add("pass","123").build();
        Request request=new Request.Builder().url("http://www.baidu.com")
                .post(foramBody).build();
        Call call=mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.i("OKHttp", str);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "请求成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /*
    * 异步上传文件
    */
   /* private void postAsynFile(){
        File file=new File("/sdcard/download.txt");
        Request request=new Request.Builder()
                .url("https://api.github.com/markdown/raw")
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN,file))
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("wangshu", response.body().string());
            }
        });
    }*/

   /*
   * 异步下载文件
   */
    private void downAsynFile(){
        String url="http://img.my.csdn.net/uploads/201603/26/1458988468_5804.jpg";
        Request request=new Request.Builder().url(url).build();
        Call call=mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream=response.body().byteStream();
                FileOutputStream fileOutputStream = null;

                try {
                    fileOutputStream=new FileOutputStream(new File("/sdcard/liyang.jpg"));
                    byte[ ] buffer=new byte[2018];
                    int len;
                    while((len= inputStream.read(buffer))!= -1){
                        fileOutputStream.write(buffer,0,len);
                    }
                    fileOutputStream.flush();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
