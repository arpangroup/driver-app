package com.example.driverapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.driverapp.models.Direction;
import com.example.driverapp.models.Order;
import com.example.driverapp.repositories.OrderRepository;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class LocationViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private MutableLiveData<LatLng> mutableCurrentLocation  = new MutableLiveData<>();
    MutableLiveData<Direction> mutableDirection;

    public void setCurrentLocation(LatLng latLng){
        if(mutableCurrentLocation == null){
            mutableCurrentLocation  = new MutableLiveData<>();
        }
        mutableCurrentLocation.setValue(latLng);
    }
    public LiveData<LatLng>  getCurrentLocation(){
        return mutableCurrentLocation;
    }


    public void setDirection(Direction direction){
        if(mutableDirection == null){
            mutableDirection = new MutableLiveData<>();
        }
        mutableDirection.setValue(direction);
    }
    public LiveData<Direction> getDirection(){
        if(mutableDirection == null){
            mutableDirection = new MutableLiveData<>();
        }
        return mutableDirection;
    }
}
