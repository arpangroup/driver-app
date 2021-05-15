package com.pureeats.driverapp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.Direction;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.models.request.DeliverOrderRequest;
import com.pureeats.driverapp.models.request.LoginRequest;
import com.pureeats.driverapp.models.request.ProcessOrderRequest;
import com.pureeats.driverapp.models.request.RequestToken;
import com.pureeats.driverapp.models.response.Dashboard;
import com.pureeats.driverapp.models.response.DeliveryOrderResponse;
import com.pureeats.driverapp.models.response.LoginHistory;
import com.pureeats.driverapp.models.response.TripDetails;
import com.pureeats.driverapp.models.response.UpdateDeliveryUserInfoResponse;
import com.pureeats.driverapp.network.Resource;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.sharedprefs.UserSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepositoryImpl extends AuthRepositoryImpl implements OrderRepository{
    private final String TAG = this.getClass().getSimpleName();
    private Api api;
    private UserSession userSession;
    private RequestToken requestToken;


    public OrderRepositoryImpl(Api api, UserSession userSession) {
        super(api, userSession);
        this.api = api;
        this.userSession = userSession;
        this.requestToken = userSession.getRequestToken();
    }


    public LiveData<Resource<ApiResponse<Dashboard>>> getDashboard(){
        return safeApiCall(api.getDashboard(requestToken));
    }
    public LiveData<Resource<DeliveryOrderResponse>> getAllDeliverableOrders(){
        return safeApiCall(api.getAllDeliveryOrders(requestToken));
    }
    public LiveData<Resource<Order>> getSingleDeliveryOrder(String uniqueOrderId){
        Map<String, String> map = new HashMap<>();
        map.put("unique_order_id", uniqueOrderId);
        map.put("token", requestToken.getToken());
        map.put("user_id", requestToken.getUserId());
        return safeApiCall(api.getSingleDeliveryOrder(map));
    }
    public LiveData<Resource<ApiResponse<UpdateDeliveryUserInfoResponse>>> getUsersOrderStatistics(){
        return safeApiCall(api.updateUserInfo(requestToken));
    }
    public LiveData<Resource<ApiResponse<List<TripDetails>>>> getTripSummary(){
        return safeApiCall(api.getTripSummary(requestToken.getUserId()));
    }


    public LiveData<Resource<Order>> acceptOrder(Order order){
        return safeApiCall(api.acceptOrder(new ProcessOrderRequest(requestToken, order.getId())));
    }
    public LiveData<Resource<Order>> pickedUpOrder(Order order, String billPhoto){
        ProcessOrderRequest processOrderRequest = new ProcessOrderRequest(requestToken, order.getId());
        processOrderRequest.setBillPhoto(billPhoto);
        Log.d(TAG, "PICKUP_ORDER_REQUEST: " + new Gson().toJson(processOrderRequest));
        return safeApiCall(api.pickedUpOrder(processOrderRequest));
    }
    public LiveData<Resource<Order>> reachedPickUpLocation(Order order){
        return safeApiCall(api.reachedPickUpLocation(new ProcessOrderRequest(requestToken, order.getId())));
    }
    public LiveData<Resource<Order>> reachedDropLocation(Order order){
        return safeApiCall(api.reachedDropLocation(new ProcessOrderRequest(requestToken, order.getId())));
    }
    public LiveData<Resource<ApiResponse>> sendMessage(Order order){
        return safeApiCall(api.sendMessage(new ProcessOrderRequest(requestToken, order.getId())));
    }
    public LiveData<Resource<Order>> deliverOrder(Order order, String deliveryPin){
        DeliverOrderRequest request = new DeliverOrderRequest(requestToken, order.getId(), deliveryPin);
        Direction direction  = order.getDirection();
        if(direction != null && direction.getDistance() != null && direction.getDuration() != null){
            request.setDistanceTravelled(direction.getDistance().getValue());
            request.setDistanceTravelledText(direction.getDistance().getText());
            request.setDurationVal(direction.getDuration().getValue());
            request.setDurationText(direction.getDuration().getText());
        }
        Log.d(TAG, "DELIVER_ORDER_REQUEST: " + new Gson().toJson(request));
        return safeApiCall(api.deliverOrder(request));
    }



}
