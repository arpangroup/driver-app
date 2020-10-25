package com.pureeats.driverapp.api;

import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.models.request.DeliverOrderRequest;
import com.pureeats.driverapp.models.request.DeliveryGuyGetGpsRequest;
import com.pureeats.driverapp.models.request.DeliveryGuySetGpsRequest;
import com.pureeats.driverapp.models.request.LoginRequest;
import com.pureeats.driverapp.models.LoginResponse;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.models.request.RequestToken;
import com.pureeats.driverapp.models.request.ProcessOrderRequest;
import com.pureeats.driverapp.models.response.Dashboard;
import com.pureeats.driverapp.models.response.DeliveryOrderResponse;
import com.pureeats.driverapp.models.response.UpdateDeliveryUserInfoResponse;

import java.util.ArrayList;
import java.util.Map;

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
    Call<DeliveryOrderResponse> getAllDeliveryOrders(@Body RequestToken requestToken);

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

    @POST("/api/delivery/send-message")
    Call<ApiResponse> sendMessage(@Body ProcessOrderRequest orderRequest);




    @POST("/api/delivery/update-user-info")
    Call<com.pureeats.driverapp.models.response.ApiResponse<UpdateDeliveryUserInfoResponse>> updateUserInfo(@Body RequestToken requestToken);

    @POST("/api/delivery/set-delivery-guy-gps-location")
    Call<Boolean> setDeliveryGuyGpsLocation(@Body DeliveryGuySetGpsRequest deliveryGuyGpsRequest);

    @POST("/api/delivery/get-delivery-guy-gps-location")
    Call<DeliveryOrderResponse> getDeliveryGuyGpsLocation(@Body DeliveryGuyGetGpsRequest deliveryGuyGetGpsRequest);

    @POST("/api/delivery/get-single-delivery-order")
    Call<Order> getSingleDeliveryOrder(@Body ProcessOrderRequest processOrderRequest);


    @POST("/api/delivery/dashboard")
    Call<com.pureeats.driverapp.models.response.ApiResponse<Dashboard>> getDashboard(@Body RequestToken requestToken);



}
