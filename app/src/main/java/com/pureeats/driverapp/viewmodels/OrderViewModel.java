package com.pureeats.driverapp.viewmodels;

import android.util.Log;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.models.response.ApiResponse;
import com.pureeats.driverapp.models.response.DeliveryOrderResponse;
import com.pureeats.driverapp.models.response.UpdateDeliveryUserInfoResponse;
import com.pureeats.driverapp.repositories.OrderRepository;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class OrderViewModel extends ViewModel implements LifecycleObserver {
    private final String TAG = this.getClass().getSimpleName();
    private OrderRepository orderRepository;
    //private MutableLiveData<ORDER_TYPE> mutableOrderType = new MutableLiveData<>(ORDER_TYPE.ALL);
    private MutableLiveData<List<Order>> mutableOrders = null;
    private MutableLiveData<Order> mutableOnGoingOrder = null;
    MutableLiveData<PolylineOptions> mutablePolyline = new MutableLiveData<>();

    // This is a temporary order, when a AcceptOrderDialog is shown, but not accepted
    private MutableLiveData<Order> mutableIncomingOrder = null;

    public OrderViewModel() {
    }

    public void init(){
        if(orderRepository == null){
            orderRepository = OrderRepository.getInstance();
        }

        if(mutableOnGoingOrder != null){

            Log.d(TAG, "ON_GOING_ORDER: "+mutableOnGoingOrder.getValue());
        }
    }


    public LiveData<Boolean> getIsLoading(){
        LiveData<Boolean> isLoading=orderRepository.getIsLoading();
        return isLoading;
    }


    public void setOnGoingOrder(Order order){
        Log.d(TAG, "setOnGoingOrder: "+ order);
        if(this.mutableOnGoingOrder == null){
            this.mutableOnGoingOrder = new MutableLiveData<>();
        }
        this.mutableOnGoingOrder.setValue(order);
    }
    public LiveData<Order> getOnGoingOrder(){
        if(mutableOnGoingOrder == null) {
            mutableOnGoingOrder = new MutableLiveData<>();
        }
        Log.d(TAG,  "ONGOING_ORDER:" +mutableOnGoingOrder.getValue());
        return mutableOnGoingOrder;
    }

    public void setIncomingOrder(Order order){
        if(mutableIncomingOrder == null){
            mutableIncomingOrder = new MutableLiveData<>();
        }
        mutableIncomingOrder.setValue(order);
    }
    public LiveData<Order> getIncomingOrder(){
        if(mutableIncomingOrder == null){
            mutableIncomingOrder = new MutableLiveData<>();
        }
        return mutableIncomingOrder;
    }

    public LiveData<ApiResponse> sendMessage(int orderId){
        return orderRepository.sendMessage(orderId);
    }

    public LiveData<ApiResponse> acceptOrder(Order order){
        return orderRepository.acceptOrder(order);
    }
    public LiveData<ApiResponse> pickedUpOrder(Order order){
        return orderRepository.pickedUpOrder(order);
    }
    public LiveData<Boolean> reachedPickupLocation(Order order){
        return orderRepository.reachedPickUpLocation(order);
    }
    public LiveData<Boolean> reachedDeliveryLocation(Order order){
        return orderRepository.reachedDeliveryLocation(order);
    }
    public LiveData<Boolean> deliverOrder(Order order, String deliveryPin){
        return orderRepository.deliverOrder(order, deliveryPin);
    }


    public LiveData<DeliveryOrderResponse> getDeliveryOrders(){
        return orderRepository.getAllDeliverableOrders();
    }
    public LiveData<UpdateDeliveryUserInfoResponse> getUsersOrderStatistics(){
        return orderRepository.getUsersOrderStatistics();
    }
    public LiveData<Order> getSingleDeliveryOrder(String uniqueOrderId){
        return orderRepository.getSingleDeliveryOrder(uniqueOrderId);
    }



    public void setPolyline(PolylineOptions polyline){
        mutablePolyline.setValue(polyline);
    }
    public LiveData<PolylineOptions> getPolyline(){
        if(mutablePolyline == null){
            mutablePolyline = new MutableLiveData<>();
        }
        return mutablePolyline;
    }

}
