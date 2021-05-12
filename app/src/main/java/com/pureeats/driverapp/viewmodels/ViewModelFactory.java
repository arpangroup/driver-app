package com.pureeats.driverapp.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.pureeats.driverapp.repositories.AuthRepositoryImpl;
import com.pureeats.driverapp.repositories.BaseRepository;
import com.pureeats.driverapp.repositories.OrderRepositoryImpl;


public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private BaseRepository repository;

    public ViewModelFactory(BaseRepository repository){
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(AuthViewModel.class)){
            return (T) new AuthViewModel((AuthRepositoryImpl)repository);
        }if(modelClass.isAssignableFrom(OrderViewModel.class)){
            return (T) new OrderViewModel((OrderRepositoryImpl)repository);
        }else{
            throw new IllegalArgumentException("ViewModel class not found");
        }

    }
}
