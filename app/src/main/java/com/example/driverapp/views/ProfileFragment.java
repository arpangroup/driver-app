package com.example.driverapp.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.driverapp.R;
import com.example.driverapp.commons.Constants;
import com.example.driverapp.databinding.FragmentAcceptOrderBinding;
import com.example.driverapp.databinding.FragmentProfileBinding;
import com.example.driverapp.models.User;
import com.example.driverapp.sharedprefs.UserSession;
import com.example.driverapp.viewmodels.LocationViewModel;
import com.example.driverapp.viewmodels.OrderViewModel;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentProfileBinding mBinding;
    OrderViewModel orderViewModel;
    LocationViewModel locationViewModel;
    NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentProfileBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        orderViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);


        User user = UserSession.getUserData(requireActivity());
        mBinding.setUser(user);
        Picasso.get().load(Constants.DELIVERY_IMAGE_URL + user.getPhoto()).into(mBinding.profileImage);
    }
}