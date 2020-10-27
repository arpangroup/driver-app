package com.pureeats.driverapp.views.order;

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

import com.pureeats.driverapp.commons.CommonUtils;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.databinding.FragmentPickOrderBinding;
import com.pureeats.driverapp.databinding.FragmentTripDetailsBinding;
import com.pureeats.driverapp.directionhelpers.ConstructDirectionUrl;
import com.pureeats.driverapp.directionhelpers.FetchURL;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.viewmodels.LocationViewModel;
import com.pureeats.driverapp.viewmodels.OrderViewModel;
import com.google.android.gms.maps.model.LatLng;

public class TripDetailsFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentTripDetailsBinding mBinding;
    OrderViewModel orderViewModel;
    LocationViewModel locationViewModel;
    NavController navController;
    Order mOrder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentTripDetailsBinding.inflate(inflater, container, false);
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
        mBinding.tripDetails.toolbar.title.setText("Trip Summary");


        orderViewModel.getOnGoingOrder().observe(requireActivity(), order -> {
            mOrder =  order;
            mBinding.tripDetails.setOrder(mOrder);

            Log.d(TAG, "ORDER: " + order);
            LatLng place1  = CommonUtils.getRestaurantLocation(order.getRestaurant());
            LatLng place2  = CommonUtils.getUserLocation(order.getLocation());
            String url = ConstructDirectionUrl.getUrl(place1, place2, "driving", Constants.GOOGLE_MAP_AUTH_KEY);
            Log.d(TAG, "REQUEST FOR DISTANCE CALCULATION");
            Log.d(TAG, "URL: "+ url);
            new FetchURL(requireActivity(), FetchURL.DISTANCE_PARSER).execute(url, "driving");
        });

        locationViewModel.getDirection().observe(requireActivity(), direction -> {
            Log.d(TAG, "DIRECTION: "+direction);
            Log.d(TAG, "DISTANCE: "+direction.getDistance());
            Log.d(TAG, "DURATION: "+direction.getDuration());
            mBinding.tripDetails.setDirection(direction);
        });

        mBinding.tripDetails.btnFinish.setOnClickListener(view -> requireActivity().finish());

    }
}