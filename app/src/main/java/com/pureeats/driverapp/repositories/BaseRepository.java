package com.pureeats.driverapp.repositories;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.pureeats.driverapp.network.Api;
import com.pureeats.driverapp.network.Resource;
import com.pureeats.driverapp.network.datasource.RemoteDataSource;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public abstract class BaseRepository {
    private final String TAG = this.getClass().getSimpleName();
    public final MutableLiveData<Boolean> isLoading=new MutableLiveData<>(false);
    Api api = RemoteDataSource.buildApi(Api.class);

    public <T> LiveData<Resource<T>> safeApiCall(Call<T> call) {
        MutableLiveData<Resource<T>> mutableResponse = new MutableLiveData<>();
        mutableResponse.setValue(Resource.loading());
       // mutableResponse.setValue();

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if(response.isSuccessful()){
                    mutableResponse.setValue(Resource.success(response.body()));
                    Log.d(TAG, "SUCCESS_RESPONSE: "+mutableResponse.getValue().data.toString());
                }else{
                    try{
                        String errorData = response.errorBody().string();
                        Log.d(TAG, "ERROR_RESPONSE: "+ errorData);
                        try{
                            JSONObject obj = new JSONObject(errorData);
                            Log.d(TAG, "API_RESPONSE: " + obj);
                            if(obj != null && obj.getString("message") != null){
                                Log.d(TAG, "ERROR_RESPONSE_API_RESPONSE: " + obj);
                                String error = obj.getString("message");
                                String errorCode = TextUtils.isEmpty(obj.getString("code")) ? null : obj.getString("code");
                                Log.d(TAG, "ERROR_CODE: "+ errorCode);
                                Log.d(TAG, "ERROR_MESSAGE: "+ error);
                                mutableResponse.setValue(Resource.error(errorCode, error));
                            }
                        }catch (Throwable t){
                            Log.d(TAG, "ApiResponse Parsing error: "+ t.getMessage());
                            mutableResponse.setValue(Resource.error(null, errorData));
                        }

                    }catch (Throwable t){
                        Log.d(TAG, "ERROR_RESPONSE: "+ t.getMessage());
                        mutableResponse.setValue(Resource.error(null, "Something error happened"));
                    }
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t.getMessage());
                try{
                    if(t instanceof HttpException){
                        Log.d(TAG, "Exception is of type HttpException ");
                        ((HttpException) t).response();
                        int code = ((HttpException) t).code();
                        String errorBody = String.valueOf(((HttpException) t).response().errorBody());
                        mutableResponse.setValue(Resource.error( String.valueOf(code), errorBody));
                    }else{
                        Log.d(TAG, "NetworkFail Exception ");
                        mutableResponse.setValue(Resource.networkError());
                    }
                }catch (Throwable throwable){
                    Log.d(TAG, "Throwing error: " +throwable.getMessage());
                    mutableResponse.setValue(Resource.error( null, null ));
                }
            }
        });

        return mutableResponse;
    }


    public LiveData<Boolean> logout(Api api){
        //return safeApiCallOld(api.logout());
        return new MutableLiveData<>(true);
    }

}
