package vn.lachongmedia.appnv.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import vn.lachongmedia.appnv.Common;
import vn.lachongmedia.appnv.KsmartSalesApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by tungda on 09/08/2017.
 */

public class NetContext {
    private static final String PREF_BASE_URL = "baseUrl";
    public static final NetContext instance = new NetContext();
    private SharedPreferences prefRetrofit;

    private SharedPreferences.Editor prefRetrofitEditor;
    Gson gson = new Gson();
    private String TAG = "NetContext";

    public static NetContext getInstance() {
        return instance;
    }

    private Retrofit retrofit;
    private long CONNECT_TIMEOUT = 60;
    private long READ_TIMEOUT = 60;
    public String BASE_URL = Common.URL_COMMON;

    public String getBASE_URL() {
        return BASE_URL;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public void setRetrofit(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public void setBASE_URL(String BASE_URL) {
        this.BASE_URL = BASE_URL;
    }

    private NetContext() {

    }

    public void init() {
        prefRetrofit = KsmartSalesApplication.getInstance().getSharedPreferences(PREF_BASE_URL, Context.MODE_PRIVATE);

        try {
            OkHttpClient client = new OkHttpClient
                    .Builder()
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                  /*  .addInterceptor(new HeaderInterceptor())*/
                    .addInterceptor(new LoggerInterceptor())
                    .build();
            Gson gson = new GsonBuilder().setLenient().create();

            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            //.addConverterFactory(GsonConverterFactory.create())
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.toString());
        }

        prefRetrofitEditor = prefRetrofit.edit();
        prefRetrofitEditor.putString(PREF_BASE_URL, BASE_URL);
        prefRetrofitEditor.apply();
    }

    public long getREAD_TIMEOUT() {
        return READ_TIMEOUT;
    }

    public void setREAD_TIMEOUT(long READ_TIMEOUT) {
        this.READ_TIMEOUT = READ_TIMEOUT;
    }

    public long getCONNECT_TIMEOUT() {
        return CONNECT_TIMEOUT;
    }

    public void setCONNECT_TIMEOUT(long CONNECT_TIMEOUT) {
        this.CONNECT_TIMEOUT = CONNECT_TIMEOUT;
    }

    public class LoggerInterceptor implements Interceptor {

        private static final String TAG = "LoggerInterceptor";

        @Override
        public Response intercept(Chain chain) throws IOException {
            try {
                //1 Get request
                Request request = chain.request();

                //2 Process request (print out)
                Log.d(TAG, String.format("url: %s", request.toString()));

                RequestBody body = request.body();
                if (body != null) {
                    Log.d(TAG, String.format("body: %s", body.toString()));
                }

                Headers headers = request.headers();
                if (headers != null) {
                    Log.d(TAG, String.format(String.format("headers: %s", headers.toString())));
                }
                //3 Proceed
                Response response = chain.proceed(request);

                //4 Process response
                Log.d(TAG, String.format("response: %s", response.toString()));

                Log.d(TAG, String.format("response body: %s", getResponseString(response)));

                return response;

            } catch (SocketTimeoutException e) {
                Log.d(TAG, "intercept: đã quá tgian");
                return chain.proceed(chain.request());
            }
        }
    }


    private String getResponseString(Response response) {
        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        try {
            source.request(Long.MAX_VALUE); // Buffer the entire body.
        } catch (IOException e) {
            e.printStackTrace();
        }
        Buffer buffer = source.buffer();
        return buffer.clone().readString(Charset.forName("UTF-8"));
    }

   /* class HeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            String token = SharedPrefs.getInstance().getSharedPreferences().getString(Common.SP_ACCESS_TOKEN, "");
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Authorization", token)
                    .build();
            return chain.proceed(request);
        }
    }*/

    public <T> T create(Class<T> classz) {
        return retrofit.create(classz);
    }
}