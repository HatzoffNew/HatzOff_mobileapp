package com.aliyahatzoff.Api;

public class Common {
    public  static ApiInterface getGoogleApi()
    {
        return ApiClient.getRetrofitgoogle().create(ApiInterface.class);
    }
    public  static ApiInterface getHatzoffApi()
    {
        return ApiClient.getRetrofit().create(ApiInterface.class);
    }
    public  static ApiInterface getOtpApi()
    {
        return ApiClient.getRetrofitsms().create(ApiInterface.class);
    }
}
