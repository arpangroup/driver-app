package com.pureeats.driverapp.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.databinding.FragmentAcceptOrderBinding;
import com.pureeats.driverapp.databinding.FragmentProfileBinding;
import com.pureeats.driverapp.models.User;
import com.pureeats.driverapp.sharedprefs.UserSession;
import com.pureeats.driverapp.viewmodels.LocationViewModel;
import com.pureeats.driverapp.viewmodels.OrderViewModel;
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