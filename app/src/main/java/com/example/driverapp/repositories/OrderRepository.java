package com.example.driverapp.repositories;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.driverapp.api.ApiInterface;
import com.example.driverapp.api.ApiService;
import com.example.driverapp.commons.OrderStatus;
import com.example.driverapp.models.ApiResponse;
import com.example.driverapp.models.Order;
import com.example.driverapp.models.request.DeliverOrderRequest;
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

    public LiveData<List<Order>> getAllAcceptedOrders(){
        if(mutableAcceptedOrders == null){
            mutableAcceptedOrders = new MutableLiveData<>(new ArrayList<>());
        }
        return mutableAcceptedOrders;
    }

    public LiveData<Boolean> acceptOrder(Order order){
        //mutableAcceptedOrders.setValue(Collections.singletonList(order));
        order.setOrderStatus(OrderStatus.DELIVERY_GUY_ASSIGNED);
        mutableAcceptedOrders.setValue(Collections.singletonList(order));
        return acceptOrderApi(order);
    }
    public LiveData<Boolean> reachedPickUpLocation(Order order){
        order.setOrderStatus(OrderStatus.REACHED_PICKUP_LOCATION);
        mutableAcceptedOrders.setValue(Collections.singletonList(order));
        return reachPickupLocationApi(order);
    }
    public LiveData<Boolean> pickedUpOrder(Order order){
        order.setOrderStatus(OrderStatus.ON_THE_WAY);
        mutableAcceptedOrders.setValue(Collections.singletonList(order));
        return pickUpOrderApi(order);
    }
    public LiveData<Boolean> reachedDeliveryLocation(Order order){
        order.setOrderStatus(OrderStatus.REACHED_DELIVERY_LOCATION);
        mutableAcceptedOrders.setValue(Collections.singletonList(order));
        return reachDeliveryLocationApi(order);
    }

    public LiveData<Boolean> deliverOrder(Order order, String deliveryPin){
        order.setOrderStatus(OrderStatus.DELIVERED);
        mutableAcceptedOrders.setValue(Collections.singletonList(order));
        return deliverOrderApi(order, deliveryPin);
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
    private LiveData<Boolean> acceptOrderApi(Order order){
        ProcessOrderRequest request = new ProcessOrderRequest(order.getId());
        MutableLiveData<Boolean> apiResponseMutableLiveData = new MutableLiveData<>();
        Log.d(TAG, "Inside acceptOrderApi()....");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(request));
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.acceptOrder(request).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "Setting accepted orders to the list...");
                    isLoading.setValue(false);
                    apiResponseMutableLiveData.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                isLoading.setValue(false);
                Log.d(TAG, "FAIL");
                apiResponseMutableLiveData.setValue(false);
            }
        });
        return apiResponseMutableLiveData;
    }
    private LiveData<Boolean> reachPickupLocationApi(Order order){
        ProcessOrderRequest request = new ProcessOrderRequest(order.getId());
        MutableLiveData<Boolean> apiResponseMutableLiveData = new MutableLiveData<>();
        Log.d(TAG, "Inside reachPickupLocationApi()....");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(request));
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.reachToPickUpLocation(request).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "Setting accepted orders to the list...");
                    isLoading.setValue(false);
                    apiResponseMutableLiveData.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                isLoading.setValue(false);
                Log.d(TAG, "FAIL");
                apiResponseMutableLiveData.setValue(false);
            }
        });
        return apiResponseMutableLiveData;
    }
    private LiveData<Boolean> pickUpOrderApi(Order order){
        ProcessOrderRequest request = new ProcessOrderRequest(order.getId());
        MutableLiveData<Boolean> apiResponseMutableLiveData = new MutableLiveData<>(false);
        Log.d(TAG, "Inside pickUpOrderApi()....");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(request));
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.pickedUpOrder(request).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                isLoading.setValue(false);
                try{
                    Log.d(TAG, "RESPONSE: "+response.body());
                    if(response.isSuccessful()){
                        apiResponseMutableLiveData.setValue(true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "Error in pickUpOrderApi()......");
                    apiResponseMutableLiveData.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                isLoading.setValue(false);
                Log.d(TAG, "FAIL");
                apiResponseMutableLiveData.setValue(false);
            }
        });
        return apiResponseMutableLiveData;
    }
    private LiveData<Boolean> reachDeliveryLocationApi(Order order){
        ProcessOrderRequest request = new ProcessOrderRequest(order.getId());
        MutableLiveData<Boolean> apiResponseMutableLiveData = new MutableLiveData<>();
        Log.d(TAG, "Inside reachDeliveryLocationApi()....");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(request));
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.reachToPickUpLocation(request).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "Setting accepted orders to the list...");
                    isLoading.setValue(false);
                    apiResponseMutableLiveData.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                isLoading.setValue(false);
                Log.d(TAG, "FAIL");
                apiResponseMutableLiveData.setValue(false);
            }
        });
        return apiResponseMutableLiveData;
    }

    private LiveData<Boolean> deliverOrderApi(Order order, String deliveryPin){
        DeliverOrderRequest request = new DeliverOrderRequest(order.getId(), deliveryPin);
        MutableLiveData<Boolean> apiResponseMutableLiveData = new MutableLiveData<>();
        Log.d(TAG, "Inside deliverOrderApi()....");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(request));
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.deliverOrder(request).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "Setting accepted orders to the list...");
                    isLoading.setValue(false);
                    apiResponseMutableLiveData.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                isLoading.setValue(false);
                Log.d(TAG, "FAIL");
                apiResponseMutableLiveData.setValue(false);
            }
        });
        return apiResponseMutableLiveData;
    }
}
