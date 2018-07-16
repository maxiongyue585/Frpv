package com.d180523.frpv.net;

import android.webkit.WebSettings;

import com.d180523.frpv.AppContext;
import com.d180523.frpv.BuildConfig;
import com.d180523.frpv.utils.SPUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.d180523.frpv.common.UrlConstainer.HOST_URL;
import static com.d180523.frpv.utils.Logger.logD;


public class NetUtils {

    public static MediaType MT_JSON = MediaType.parse("application/json; charset=utf-8");


    public static final OkHttpClient okhc = new OkHttpClient.Builder()

            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                    Request.Builder b = chain.request().newBuilder();

                    if (AppContext.session_keys.size() > 0) {

                        for (Map.Entry<String, String> e : AppContext.session_keys.entrySet()) {

                            b.addHeader(e.getKey(), e.getValue());
                        }
                    }
                    return chain.proceed(b.build());
                }
            })
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES).build();

    public static Retrofit retrofit = new Retrofit.Builder()
            //.client(okhc)
            .baseUrl(HOST_URL)
            .client(genericClient())
            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    private static OkHttpClient genericClient() {

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()

                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        String token = AppContext.getInstance().spc.getProperty(SPUtils.TOKEN);
                        if (token != null) {
                            AppContext.session_keys.put("token", token);
                        } else {
                            Response response = chain.proceed(chain.request());
                            if (response.header("token") != null) {
                                AppContext.getInstance().spc.setProperty(SPUtils.TOKEN, response.header("token"));
                                AppContext.session_keys.put("token", response.header("token"));
                            }
                        }

                        Request.Builder b = chain.request().newBuilder();
                        b.addHeader("User-Agent", WebSettings.getDefaultUserAgent(AppContext.getInstance()));//添加真正的头部

                        if (AppContext.session_keys.size() > 0) {

                            for (Map.Entry<String, String> e : AppContext.session_keys.entrySet()) {

                                b.addHeader(e.getKey(), e.getValue());
                            }
                        }
                        return chain.proceed(b.build());
                    }

                });
        //DEBUG模式下 添加日志拦截器
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(interceptor);
        }
        return httpClientBuilder.build();
    }

    public static String put(String url, String data) throws Exception {

        RequestBody rb = RequestBody.create(MT_JSON, data);
        Request request = new Request.Builder().url(url).put(rb).build();
        final Call call = okhc.newCall(request);
        final Response response = call.execute();
        return response.body().string();
    }

    public static String post(String url, String data) throws Exception {

        RequestBody rb = RequestBody.create(MT_JSON, data);
        Request request = new Request.Builder().url(url).post(rb).build();
        final Call call = okhc.newCall(request);
        final Response response = call.execute();
        return response.body().string();
    }

    public static String get(String url) throws Exception {

        logD("get url: " + url);
        final Call call = okhc.newCall(new Request.Builder().url(url).get().build());
        final Response response = call.execute();
        return response.body().string();
    }

    public static void muteDownload(String url, File saveTo) throws Exception {

        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setConnectTimeout(5000);

        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
        FileOutputStream fos = new FileOutputStream(saveTo);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = bis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        fos.flush();
        fos.close();
        bis.close();
    }

    public static ObjectMapper jsonParser = new ObjectMapper();
}
