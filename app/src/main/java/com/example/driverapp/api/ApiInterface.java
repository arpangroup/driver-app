package com.example.driverapp.api;

import com.example.driverapp.models.ApiResponse;
import com.example.driverapp.models.request.DeliverOrderRequest;
import com.example.driverapp.models.request.DeliveryGuyGpsRequest;
import com.example.driverapp.models.request.LoginRequest;
import com.example.driverapp.models.LoginResponse;
import com.example.driverapp.models.Order;
import com.example.driverapp.models.request.RequestToken;
import com.example.driverapp.models.User;
import com.example.driverapp.models.request.ProcessOrderRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("/api/send-login-otp/{phone}")
    Call<ApiResponse> sendLoginOtp(@Path("phone") String phone);

    @POST("/api/delivery/login")
    Call<LoginResponse<User>> login(@Body LoginRequest loginRequest);


    @POST("/api/delivery/get-delivery-orders")
    Call<List<Order>> getAllDeliveryOrders(@Body RequestToken requestToken);

    @POST("/api/delivery/get-delivery-orders")
    Call<Boolean> saveDeliveryGuyGpsLocation(@Body DeliveryGuyGpsRequest deliveryGuyGpsRequest);

    @POST("/api/delivery/accept-to-deliver")
    Call<Order> acceptOrder(@Body ProcessOrderRequest processOrderRequest);

    @POST("/api/delivery/pickedup-order")
    Call<Order> pickedUpOrder(@Body ProcessOrderRequest processOrderRequest);


    @POST("/api/delivery/reached-to-pickup-location")
    Call<Order> reachToPickUpLocation(@Body ProcessOrderRequest processOrderRequest);

    @POST("/api/delivery/reached-to-deliver-location")
    Call<Order> reachToDeliverLocation(@Body ProcessOrderRequest processOrderRequest);

    @POST("/api/delivery/deliver-order")
    Call<Order> deliverOrder(@Body DeliverOrderRequest deliverOrderRequest);


}
