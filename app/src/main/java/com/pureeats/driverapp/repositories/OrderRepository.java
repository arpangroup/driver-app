package com.pureeats.driverapp.repositories;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pureeats.driverapp.api.ApiInterface;
import com.pureeats.driverapp.api.ApiService;
import com.pureeats.driverapp.commons.ErrorCode;
import com.pureeats.driverapp.commons.OrderStatus;
import com.pureeats.driverapp.models.Direction;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.models.request.DeliverOrderRequest;
import com.pureeats.driverapp.models.request.ProcessOrderRequest;
import com.pureeats.driverapp.models.request.RequestToken;
import com.pureeats.driverapp.models.response.ApiResponse;
import com.pureeats.driverapp.models.response.Dashboard;
import com.pureeats.driverapp.models.response.DeliveryOrderResponse;
import com.pureeats.driverapp.models.response.TripDetails;
import com.pureeats.driverapp.models.response.UpdateDeliveryUserInfoResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
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

    public LiveData<DeliveryOrderResponse> getAllDeliverableOrders(){
        return getDeliveryOrdersApi();
    }

    public LiveData<ApiResponse> acceptOrder(Order order){
        //mutableAcceptedOrders.setValue(Collections.singletonList(order));
        order.setOrderStatus(OrderStatus.DELIVERY_GUY_ASSIGNED);
        mutableAcceptedOrders.setValue(Collections.singletonList(order));
        return acceptOrderApi(order);
    }
    public LiveData<Boolean> reachedPickUpLocation(Order order){
//        order.setOrderStatus(OrderStatus.REACHED_PICKUP_LOCATION);
//        mutableAcceptedOrders.setValue(Collections.singletonList(order));
//        return reachPickupLocationApi(order);
        return new MutableLiveData<>(false);
    }
    public LiveData<ApiResponse> pickedUpOrder(Order order){
        order.setOrderStatus(OrderStatus.ON_THE_WAY);
        mutableAcceptedOrders.setValue(Collections.singletonList(order));
        return pickUpOrderApi(order);
    }
    public LiveData<Boolean> reachedDeliveryLocation(Order order){
//        order.setOrderStatus(OrderStatus.REACHED_DELIVERY_LOCATION);
//        mutableAcceptedOrders.setValue(Collections.singletonList(order));
//        return reachDeliveryLocationApi(order);
        return new MutableLiveData<>(false);
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

    public LiveData<UpdateDeliveryUserInfoResponse> getUsersOrderStatistics(){
        return updateUserInfoApi();
    }

    public LiveData<Order> getSingleDeliveryOrder(String uniqueOrderId){
        return getSingleDeliveryOrderApi(uniqueOrderId);
    }

    public LiveData<ApiResponse> getDashboard(RequestToken requestToken){
        return getDashboardApi(requestToken);
    }

    public LiveData<ApiResponse> sendMessage(int orderId){
        return sendMessageApi(orderId);
    }

    public LiveData<ApiResponse<TripDetails>> getTripDetails(int orderId){
        return getTripDetailsApi(orderId+"");
    }
    public LiveData<ApiResponse<List<TripDetails>>> getTripSummary(RequestToken requestToken){
        return getTripSummaryApi(requestToken);
    }





    /*========================================================API_CALLS==============================================*/
    private LiveData<ApiResponse> acceptOrderApi(Order order){
        ProcessOrderRequest request = new ProcessOrderRequest(order.getId());
        MutableLiveData<ApiResponse> apiResponseMutableLiveData = new MutableLiveData<>();
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
                    Order orderResp = response.body();
                    ApiResponse apiResponse = new ApiResponse(true, "order accepted");
                    if(orderResp.isMaxOrder()){
                        apiResponse.setSuccess(false);
                        apiResponse.setMesssage(ErrorCode.MAX_ORDER_REACHED.name());
                    }
                    apiResponseMutableLiveData.setValue(apiResponse);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                isLoading.setValue(false);
                Log.d(TAG, "FAIL");
                apiResponseMutableLiveData.setValue(new ApiResponse(false, "FAIL"));
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
    private LiveData<ApiResponse> pickUpOrderApi(Order order){
        ProcessOrderRequest request = new ProcessOrderRequest(order.getId());
        MutableLiveData<ApiResponse> apiResponseMutableLiveData = new MutableLiveData<>();
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
                        ApiResponse apiResponse = new ApiResponse(true, "order pickedup");
                        apiResponseMutableLiveData.setValue(apiResponse);
                    }else{
                        ApiResponse apiResponse = new ApiResponse(false, "Order is not ready yet");
                        apiResponseMutableLiveData.setValue(apiResponse);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "Error in pickUpOrderApi()......");
                    ApiResponse apiResponse = new ApiResponse(false, "Order is not ready yet");
                    apiResponseMutableLiveData.setValue(apiResponse);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                isLoading.setValue(false);
                Log.d(TAG, "FAIL");
                apiResponseMutableLiveData.setValue(new ApiResponse(false, "FAIL"));
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
        apiInterface.reachToDeliverLocation(request).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "Setting accepted orders to the list...");
                    isLoading.setValue(false);
                    apiResponseMutableLiveData.setValue(true);
                }else{
                    Log.d(TAG, "Invalid response");
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
        Direction direction  = order.getDirection();
        DeliverOrderRequest request = new DeliverOrderRequest(order.getId(), deliveryPin);
        request.setDistanceTravelled(direction.getDistance().getValue());
        request.setDistanceTravelledText(direction.getDistance().getText());
        request.setDurationVal(direction.getDuration().getValue());
        request.setDurationText(direction.getDuration().getText());
        MutableLiveData<Boolean> apiResponseMutableLiveData = new MutableLiveData<>();
        Log.d(TAG, "Inside deliverOrderApi()....");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(request));
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.deliverOrder(request).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                isLoading.setValue(false);
                if(response.isSuccessful()){
                    Log.d(TAG, "RESPONSE: "+ response.body());
                    apiResponseMutableLiveData.setValue(true);
                    //Log.d(TAG, "Setting accepted orders to the list...");
                    //apiResponseMutableLiveData.setValue(true);
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


    private LiveData<DeliveryOrderResponse> getDeliveryOrdersApi(){
        RequestToken requestToken = new RequestToken();
        MutableLiveData<DeliveryOrderResponse> mutableResponse = new MutableLiveData<>();
        Log.d(TAG, "Inside getDeliveryOrders()....");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(requestToken));
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.getAllDeliveryOrders(requestToken).enqueue(new Callback<DeliveryOrderResponse>() {
            @Override
            public void onResponse(Call<DeliveryOrderResponse> call, Response<DeliveryOrderResponse> response) {
                isLoading.setValue(false);
                if(response.isSuccessful()){
                    try{
                        DeliveryOrderResponse  deliveryOrderResponse =  response.body();
                        mutableResponse.setValue(deliveryOrderResponse);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<DeliveryOrderResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
        return mutableResponse;
    }


    private LiveData<UpdateDeliveryUserInfoResponse> updateUserInfoApi(){
        RequestToken requestToken = new RequestToken();
        MutableLiveData<UpdateDeliveryUserInfoResponse> mutableResponse = new MutableLiveData<>();
        Log.d(TAG, "Inside updateUserInfo()....");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(requestToken));
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.updateUserInfo(requestToken).enqueue(new Callback<ApiResponse<UpdateDeliveryUserInfoResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<UpdateDeliveryUserInfoResponse>> call, Response<ApiResponse<UpdateDeliveryUserInfoResponse>> response) {
                isLoading.setValue(false);
                if(response.isSuccessful()){
                    ApiResponse<UpdateDeliveryUserInfoResponse> apiResponse  = response.body();
                    if(apiResponse.isSuccess()){
                        mutableResponse.setValue(apiResponse.getData());
                    }else{
                        //...
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UpdateDeliveryUserInfoResponse>> call, Throwable t) {
                isLoading.setValue(false);
                Log.d(TAG, "FAIL");
            }
        });
        return mutableResponse;
    }

    private LiveData<Order> getSingleDeliveryOrderApi(String uniqueOrderId){
        ProcessOrderRequest processOrderRequest = new ProcessOrderRequest(uniqueOrderId);
        MutableLiveData<Order> mutableResponse = new MutableLiveData<>();
        Log.d(TAG, "Inside getSingleDeliveryOrder()....");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(processOrderRequest));
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.getSingleDeliveryOrder(processOrderRequest).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                isLoading.setValue(false);
                if(response.isSuccessful()){
                    Order  order = response.body();
                    mutableResponse.setValue(order);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                isLoading.setValue(false);
                Log.d(TAG, "FAIL");
            }
        });
        return mutableResponse;
    }


    private LiveData<ApiResponse> sendMessageApi(int orderId){
        ProcessOrderRequest processOrderRequest = new ProcessOrderRequest(orderId);
        MutableLiveData<ApiResponse> mutableResponse = new MutableLiveData<>();
        Log.d(TAG, "Inside sendMessageApi()....");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(processOrderRequest));
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.sendMessage(processOrderRequest).enqueue(new Callback<com.pureeats.driverapp.models.ApiResponse>() {
            @Override
            public void onResponse(Call<com.pureeats.driverapp.models.ApiResponse> call, Response<com.pureeats.driverapp.models.ApiResponse> response) {
                mutableResponse.setValue(new ApiResponse(true, "Message has been send"));
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<com.pureeats.driverapp.models.ApiResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
        return mutableResponse;
    }



    private LiveData<ApiResponse> getDashboardApi(RequestToken requestToken){
        MutableLiveData<ApiResponse> mutableResponse = new MutableLiveData<>();
        Log.d(TAG, "Inside getDashboardApi()....");
        Log.d(TAG, "REQUEST: "+ new Gson().toJson(requestToken));
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.getDashboard(requestToken).enqueue(new Callback<ApiResponse<Dashboard>>() {
            @Override
            public void onResponse(Call<ApiResponse<Dashboard>> call, Response<ApiResponse<Dashboard>> response) {
                isLoading.setValue(false);
                ApiResponse<Dashboard> apiResponse = response.body();
                mutableResponse.postValue(apiResponse);
            }

            @Override
            public void onFailure(Call<ApiResponse<Dashboard>> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
        return mutableResponse;
    }


    private LiveData<ApiResponse<TripDetails>> getTripDetailsApi(String orderId){
        MutableLiveData<ApiResponse<TripDetails>> mutableResponse = new MutableLiveData<>();
        Log.d(TAG, "Inside getTripDetails()....");
        Log.d(TAG, "REQUEST: orderId :"+ orderId);
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.getTripDetails(orderId).enqueue(new Callback<ApiResponse<TripDetails>>() {
            @Override
            public void onResponse(Call<ApiResponse<TripDetails>> call, Response<ApiResponse<TripDetails>> response) {
                isLoading.setValue(false);
                mutableResponse.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse<TripDetails>> call, Throwable t) {
                isLoading.setValue(false);
                ApiResponse<TripDetails> apiResponse = new ApiResponse<>(false, "FAIL", null);
                mutableResponse.setValue(apiResponse);
            }
        });
        return mutableResponse;
    }
    private LiveData<ApiResponse<List<TripDetails>>> getTripSummaryApi(RequestToken requestToken){
        MutableLiveData<ApiResponse<List<TripDetails>>> mutableResponse = new MutableLiveData<>();
        String riderId  = requestToken.getDeliveryGuyId()+"";
        Log.d(TAG, "Inside getTripSummary()....");
        Log.d(TAG, "REQUEST: RiderId :"+ riderId);
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.getTripSummary(riderId).enqueue(new Callback<ApiResponse<List<TripDetails>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TripDetails>>> call, Response<ApiResponse<List<TripDetails>>> response) {
                isLoading.setValue(false);
                Log.d(TAG, "RESPONSE: " + response.body().getData());
                mutableResponse.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TripDetails>>> call, Throwable t) {
                isLoading.setValue(false);
                Log.d(TAG, "FAIL");
                mutableResponse.setValue(new ApiResponse<>(false, "FAIL", new ArrayList<>()));
            }
        });
        return mutableResponse;
    }

}
