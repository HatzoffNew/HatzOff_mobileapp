package com.aliyahatzoff.Api;

import com.aliyahatzoff.utils.MyApplication;
import com.aliyahatzoff.utils.Sharedhelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
   // http://hatzapplication-env.eba-gsf4k9su.ap-south-1.elasticbeanstalk.com/
    public static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    //private static final String BASE_URL = "http://192.168.0.207:81/api/";
    //public static final String image_URL = "http://192.168.0.207:81/api/images/";


   //private static final String BASE_URL = "http://hatzofflatest-env.eba-3wa83htn.ap-south-1.elasticbeanstalk.com/api/";
    //public static final String image_URL = "http://Hatzofflatest-env.eba-3wa83htn.ap-south-1.elasticbeanstalk.com/public/images/";

    private static final String BASE_URL = "http://hatzoff-env.eba-7svxhd8x.ap-south-1.elasticbeanstalk.com/api/";
    public static final String image_URL = "http://Hatzoff-env.eba-7svxhd8x.ap-south-1.elasticbeanstalk.com/public/images/";

    private static Retrofit retrofit = null;
    private static Retrofit retrofitsms = null;
    private static Retrofit retrofitaws = null;

    public static Retrofit getRetrofit() {
        OkHttpClient client = getClient();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    //+91{user's_phone_no}/AUTOGEN
    public static Retrofit getRetrofitsms() {
        OkHttpClient client = getClient2();
        if (retrofitsms == null) {
            retrofitsms = new Retrofit.Builder()
                    .baseUrl("https://2factor.in/API/V1/0c49f41c-0f09-11ec-a13b-0200cd936042/SMS/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofitsms;
    }
    public static Retrofit getRetrofitgoogle() {
        final String u="http://hatzoff.s3.amazonaws.com";
        OkHttpClient client = getClient();
        if (retrofitaws == null) {
            retrofitaws = new Retrofit.Builder()
                    .baseUrl("https://googleapis.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofitaws;
    }
    private static OkHttpClient getClient2() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                // .addNetworkInterceptor(new AddHeaderInterceptor())
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .build();

        client.connectionPool().evictAll();

        return client;
    }

    private static OkHttpClient getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .addNetworkInterceptor(new AddHeaderInterceptor())
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .build();

        client.connectionPool().evictAll();

        return client;
    }

    public static class AddHeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
           // System.out.println(Sharedhelper.getKey(MyApplication.getAppContext(), "token"));
            Request.Builder builder = chain.request().newBuilder();
            builder.addHeader("Accept", "application/json");
            builder.addHeader("Authorization", "Bearer " + Sharedhelper.getKey(MyApplication.getAppContext(), "token"));
            return chain.proceed(builder.build());
        }
    }
}
