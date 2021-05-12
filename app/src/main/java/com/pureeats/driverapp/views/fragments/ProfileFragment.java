package com.pureeats.driverapp.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.databinding.FragmentOfferBinding;
import com.pureeats.driverapp.databinding.FragmentProfileBinding;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.AuthRepositoryImpl;
import com.pureeats.driverapp.repositories.BaseRepository;
import com.pureeats.driverapp.repositories.OrderRepositoryImpl;
import com.pureeats.driverapp.viewmodels.BaseViewModel;
import com.pureeats.driverapp.viewmodels.OrderViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;


public class ProfileFragment extends BaseDialogFragment<OrderViewModel, FragmentProfileBinding, OrderRepositoryImpl> {
    private final String TAG = getClass().getName();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.setUser(userSession.getUserData());
        Glide.with(mBinding.profileImage).load(Constants.DELIVERY_IMAGE_URL + userSession.getUserData().getPhoto()).into(mBinding.profileImage);
    }

    @Override
    public Class<OrderViewModel> getViewModel() {
        return OrderViewModel.class;
    }

    @Override
    public FragmentProfileBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentProfileBinding.inflate(inflater, container, false);
    }

    @Override
    public OrderRepositoryImpl getRepository() {
        return new OrderRepositoryImpl(remoteDataSource.buildApi(Api.class), userSession);
    }
}