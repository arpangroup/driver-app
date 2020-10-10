package com.example.driverapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.driverapp.models.Direction;
import com.example.driverapp.models.Duration;
import com.example.driverapp.models.Order;
import com.example.driverapp.repositories.OrderRepository;
import com.example.driverapp.sharedprefs.UserSession;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class OrderViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private OrderRepository orderRepository;
    //private MutableLiveData<ORDER_TYPE> mutableOrderType = new MutableLiveData<>(ORDER_TYPE.ALL);
    private MutableLiveData<List<Order>> mutableOrders = null;
    private MutableLiveData<Order> mutableRunningOrder = null;
    MutableLiveData<PolylineOptions> mutablePolyline;
    private Order order = null;

    public void init(){
        if (mutableOrders != null){
            return;
        }
        orderRepository = OrderRepository.getInstance();
    }


    public LiveData<Boolean> getIsLoading(){
        LiveData<Boolean> isLoading=orderRepository.getIsLoading();
        return isLoading;
    }
    public void setOnGoingOrder(Order order){
        if(mutableRunningOrder  == null){
            mutableRunningOrder = new MutableLiveData<>();
        }
        mutableRunningOrder.setValue(order);
        this.order = order;
    }
    public LiveData<Order> getRunningOrder(){
        if(mutableRunningOrder == null) mutableRunningOrder = new MutableLiveData<>();
        return mutableRunningOrder;
    }
    public Order getOnGoingOrder(){
        return this.order;
    }



    public LiveData<Boolean> acceptOrder(Order order){
        return orderRepository.acceptOrder(order);
    }
    public LiveData<Boolean> pickedUpOrder(Order order){
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


    public LiveData<List<Order>> getAllAcceptedOrders(){
        return orderRepository.getAllAcceptedOrders();
    }


    public void setPolyline(PolylineOptions polyline){
        if(mutablePolyline == null){
            mutablePolyline = new MutableLiveData<>();
        }
        mutablePolyline.setValue(polyline);
    }
    public LiveData<PolylineOptions> getPolyline(){
        if(mutablePolyline == null){
            mutablePolyline = new MutableLiveData<>();
        }
        return mutablePolyline;
    }

}
