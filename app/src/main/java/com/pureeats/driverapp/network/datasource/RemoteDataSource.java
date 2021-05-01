package com.pureeats.driverapp.network.datasource;

import com.pureeats.driverapp.BuildConfig;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.models.request.RequestToken;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RemoteDataSource {
    private static final String BASE_URL = Constants.WEBSITE_URL;

    private static HttpLoggingInterceptor getLogger(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            // development build
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            // production build
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }
        return logging;
    }
    private static OkHttpClient getOkHttpClient(){
        return new OkHttpClient.Builder()
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(getLogger())
                /*=================AVOID SSL [START]======================*/
                // remove this section for production
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                })
                /*=================AVOID SSL[END]======================*/
                .build();
    }
    private static OkHttpClient getOkHttpClient(RequestToken requestToken){
        return new OkHttpClient.Builder()
                .addInterceptor(getLogger())
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Request newRequest;

                    newRequest = request.newBuilder()
                            .addHeader("Authorization", "Bearer "+requestToken.getToken())
                            .build();
                    return chain.proceed(newRequest);
                })
                /*=================AVOID SSL [START]======================*/
                // remove this section for production
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                })
                /*=================AVOID SSL[END]======================*/
                .build();
    }


    private static Retrofit getRetrofitInstance(){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public static <T> T buildApi(Class<T> api){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(api);
    }


    public static <T> T buildApi(Class<T> api, RequestToken requestToken){ //overloaded function without authtoken
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                //.client(getOkHttpClient(requestToken))
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(api);
    }

//    public static <T> T invoke(Class<T> api, NetworkConnectionInterceptor networkConnectionInterceptor){ //overloaded function without authtoken
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .readTimeout(120, TimeUnit.SECONDS)
//                .connectTimeout(120, TimeUnit.SECONDS)
//                .addInterceptor(getLogger())
//                .addInterceptor(networkConnectionInterceptor)
//                .hostnameVerifier((HostnameVerifier) (s, sslSession) -> true)
//                .build();
//
//        return new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                //.client(getOkHttpClient(requestToken))
//                .client(okHttpClient)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//                .create(api);
//    }
public static <T> T invoke(Class<T> api){ //overloaded function without authtoken
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(getLogger())
            .hostnameVerifier((HostnameVerifier) (s, sslSession) -> true)
            .build();

    return new Retrofit.Builder()
            .baseUrl(BASE_URL)
            //.client(getOkHttpClient(requestToken))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api);
}
}
