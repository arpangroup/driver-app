package com.pureeats.driverapp.network.api;

import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.models.request.DeliverOrderRequest;
import com.pureeats.driverapp.models.request.DeliveryGuyGetGpsRequest;
import com.pureeats.driverapp.models.request.DeliveryGuySetGpsRequest;
import com.pureeats.driverapp.models.request.HeartbeatRequest;
import com.pureeats.driverapp.models.request.LoginRequest;
import com.pureeats.driverapp.models.LoginResponse;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.models.request.RequestToken;
import com.pureeats.driverapp.models.request.ProcessOrderRequest;
import com.pureeats.driverapp.models.response.Dashboard;
import com.pureeats.driverapp.models.response.DeliveryOrderResponse;
import com.pureeats.driverapp.models.response.HeartBeatResponse;
import com.pureeats.driverapp.models.response.LoginHistory;
import com.pureeats.driverapp.models.response.TripDetails;
import com.pureeats.driverapp.models.response.UpdateDeliveryUserInfoResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {

    @GET("/api/send-login-otp/{phone}")
    Call<ApiResponse<Object>> sendLoginOtp(@Path("phone") String phone);

    @POST("/api/verify-phone")
    Call<ApiResponse<Object>> verifyPhone(@Body HashMap<String, String> map);

    @POST("/api/delivery/login")
    Call<ApiResponse<User>> loginUsingOtp(@Body LoginRequest loginRequest);

    @POST("/api/delivery/dashboard")
    Call<ApiResponse<Dashboard>> getDashboard(@Body RequestToken requestToken);


    @POST("/api/delivery/get-delivery-orders")
    Call<DeliveryOrderResponse> getAllDeliveryOrders(@Body RequestToken requestToken);

    @POST("/api/delivery/get-single-delivery-order")
    Call<Order> getSingleDeliveryOrder(@Body Map<String, String> uniqueOrderId);

    @POST("/api/delivery/update-user-info")
    Call<ApiResponse<UpdateDeliveryUserInfoResponse>> updateUserInfo(@Body RequestToken requestToken);

    @POST("/api/delivery/get-login-history")
    Call<List<LoginHistory>> loginHistory(@Body RequestToken requestToken);

    @GET("/api/delivery/get-trip-summary/{rider_id}")
    Call<ApiResponse<List<TripDetails>>> getTripSummary(@Path("rider_id") String riderId);


//    @POST("/api/delivery/heartbeat")
//    Call<Map<String, List<Object>>> scheduleHeartbeat(@Body DeliveryGuySetGpsRequest requestToken);

    @POST("/api/delivery/heartbeat")
    Call<HeartBeatResponse> scheduleHeartbeat(@Body HeartbeatRequest heartbeatRequest);








    /*#################################################################*/
    @POST("/api/delivery/login")
    Call<LoginResponse<User>> login(@Body LoginRequest loginRequest);

    @POST("/api/delivery/logout")
    Call<ApiResponse> logoutSession(@Body RequestToken requestToken);

    @POST("/api/delivery/accept-to-deliver")
    Call<Order> acceptOrder(@Body ProcessOrderRequest processOrderRequest);

    @POST("/api/delivery/pickedup-order")
    Call<Order> pickedUpOrder(@Body ProcessOrderRequest processOrderRequest);

    @POST("/api/delivery/reached-to-pickup-location")
    Call<Order> reachedPickUpLocation(@Body ProcessOrderRequest processOrderRequest);

    @POST("/api/delivery/reached-to-drop-location")
    Call<Order> reachedDropLocation(@Body ProcessOrderRequest processOrderRequest);

    @POST("/api/delivery/deliver-order")
    Call<Order> deliverOrder(@Body DeliverOrderRequest deliverOrderRequest);

    @POST("/api/delivery/send-message")
    Call<ApiResponse> sendMessage(@Body ProcessOrderRequest orderRequest);





    @POST("/api/delivery/set-delivery-guy-gps-location")
    Call<Boolean> setDeliveryGuyGpsLocation(@Body DeliveryGuySetGpsRequest deliveryGuyGpsRequest);

    @POST("/api/delivery/get-delivery-guy-gps-location")
    Call<DeliveryOrderResponse> getDeliveryGuyGpsLocation(@Body DeliveryGuyGetGpsRequest deliveryGuyGetGpsRequest);




    @GET("/api/delivery/get-trip-details/{order_id}")
    Call<com.pureeats.driverapp.models.response.ApiResponse<TripDetails>> getTripDetails(@Path("order_id") String orderId);




}
