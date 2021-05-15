package com.pureeats.driverapp.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pureeats.driverapp.models.ApiResponse;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.models.response.Dashboard;
import com.pureeats.driverapp.models.response.DeliveryOrderResponse;
import com.pureeats.driverapp.models.response.TripDetails;
import com.pureeats.driverapp.models.response.UpdateDeliveryUserInfoResponse;
import com.pureeats.driverapp.network.Resource;
import com.pureeats.driverapp.repositories.AuthRepositoryImpl;
import com.pureeats.driverapp.repositories.OrderRepositoryImpl;
import com.pureeats.driverapp.utils.CommonUtils;

import java.util.List;
import java.util.Map;


public class OrderViewModel extends BaseViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private OrderRepositoryImpl orderRepository;
    private MutableLiveData<Order> mutableOrder = new MutableLiveData<>();

    public OrderViewModel(OrderRepositoryImpl repository) {
        super(repository);
        this.orderRepository = repository;
    }

    public LiveData<Resource<ApiResponse>> sendMessage(Order order){
        return orderRepository.sendMessage(order);
    }
    public LiveData<Resource<Order>> acceptOrder(Order order){
        return orderRepository.acceptOrder(order);
    }
    public LiveData<Resource<Order>> pickedUpOrder(Order order, String billPhoto){
        return orderRepository.pickedUpOrder(order, billPhoto);
    }
    public LiveData<Resource<Order>> reachedPickupLocation(Order order){
        return orderRepository.reachedPickUpLocation(order);
    }
    public LiveData<Resource<Order>> reachedDropLocation(Order order){
        return orderRepository.reachedDropLocation(order);
    }
    public LiveData<Resource<Order>> deliverOrder(Order order, String deliveryPin){
        return orderRepository.deliverOrder(order, deliveryPin);
    }



    public LiveData<Resource<ApiResponse<Dashboard>>> getDashboard(){
        return orderRepository.getDashboard();
    }
    public LiveData<Resource<DeliveryOrderResponse>> getDeliveryOrders(){
        return orderRepository.getAllDeliverableOrders();
    }
    public LiveData<Resource<Order>> getSingleDeliveryOrder(Order order){
        return orderRepository.getSingleDeliveryOrder(order.getUniqueOrderId());
    }
    public LiveData<Resource<ApiResponse<UpdateDeliveryUserInfoResponse>>> getUsersOrderStatistics(){
        return orderRepository.getUsersOrderStatistics();
    }
    public LiveData<Resource<ApiResponse<List<TripDetails>>>> getTripSummary(){
        return orderRepository.getTripSummary();
    }



    /*############################## GETTE______SETTER[START] ##################################*/
    public LiveData<Order> getOrder(){
        return mutableOrder;
    }
    public void setOrder(Order order){
        mutableOrder.setValue(order);
    }
    /*############################## GETTE______SETTER[END]  ##################################*/
}