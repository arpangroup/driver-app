package com.pureeats.driverapp.viewmodels;

import androidx.lifecycle.ViewModel;

import com.pureeats.driverapp.repositories.BaseRepository;

import io.reactivex.disposables.CompositeDisposable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseViewModel extends ViewModel {
    private static final String TAG = "BaseViewModel";
    protected CompositeDisposable compositeDisposable;
    private BaseRepository repository;

    //constructor
    public BaseViewModel(BaseRepository baseRepository){
        this.repository = baseRepository;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
        super.onCleared();
    }

}
