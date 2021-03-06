package com.pureeats.driverapp.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pureeats.driverapp.databinding.FragmentHomeBinding;
import com.pureeats.driverapp.databinding.FragmentOfferBinding;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.AuthRepositoryImpl;
import com.pureeats.driverapp.repositories.BaseRepository;
import com.pureeats.driverapp.viewmodels.BaseViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;


public class OfferFragment extends BaseDialogFragment<BaseViewModel, FragmentOfferBinding, BaseRepository> {
    private final String TAG = getClass().getName();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Class<BaseViewModel> getViewModel() {
        return BaseViewModel.class;
    }

    @Override
    public FragmentOfferBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentOfferBinding.inflate(inflater, container, false);
    }

    @Override
    public BaseRepository getRepository() {
        return new AuthRepositoryImpl(remoteDataSource.buildApi(Api.class), userSession);
    }
}