package com.example.driverapp.repositories;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.driverapp.api.ApiInterface;
import com.example.driverapp.api.ApiService;
import com.example.driverapp.models.ApiResponse;
import com.example.driverapp.models.Order;
import com.example.driverapp.models.request.ProcessOrderRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private final String TAG = this.getClass().getSimpleName();
    private static OrderRepository orderRepository;
    private MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private MutableLiveData<List<Order>> mutableAcceptedOrders = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<String>> mutableCancelOrders;
    MutableLiveData<List<Order>> mutableNewOrderList;

    public static OrderRepository getInstance(){
        if (orderRepository == null){
            orderRepository = new OrderRepository();
        }
        return orderRepository;
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    public void acceptOrder(Order order){
        mutableAcceptedOrders.setValue(Collections.singletonList(order));
    }
    public LiveData<List<Order>> getAllAcceptedOrders(){
        if(mutableAcceptedOrders == null){
            mutableAcceptedOrders = new MutableLiveData<>(new ArrayList<>());
        }
        return mutableAcceptedOrders;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean setStatusChanged(Order order){
        //Update accepted orders list:
        Log.d(TAG, "Inside assignDeliveryPerson...");
       if(mutableAcceptedOrders == null) return false;
       List<Order> orderList = new ArrayList<>(mutableAcceptedOrders.getValue());
       Log.d(TAG, "ACCEPTED_ORDER_SIZE: "+orderList.size());
       Log.d(TAG, "ACCEPTED_ORDERS: "+ orderList);

       orderList.forEach(orderObj ->{
           if(orderObj.getId() == order.getId()){
               orderObj.setDeliveryDetails(order.getDeliveryDetails());
               orderObj.setOrderStatusId(order.getOrderStatusId());
               orderList.set(orderList.indexOf(orderObj), orderObj);
           }
       });
        mutableAcceptedOrders.setValue(orderList);
        return true;
    }





    /*========================================================API_CALLS==============================================*/
    private LiveData<ApiResponse> acceptOrderApi(ProcessOrderRequest processOrderRequest){
        MutableLiveData<ApiResponse> apiResponseMutableLiveData = new MutableLiveData<>();
        Log.d(TAG, "Inside acceptOrderApi()....");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(processOrderRequest));
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.acceptOrder(processOrderRequest).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {

            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {

            }
        });
        return apiResponseMutableLiveData;
    }
}