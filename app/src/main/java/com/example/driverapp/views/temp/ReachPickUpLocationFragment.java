package com.example.driverapp.views.temp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.Toast;

import com.example.driverapp.R;
import com.example.driverapp.commons.CommonUtils;
import com.example.driverapp.commons.Constants;
import com.example.driverapp.commons.DrawMarker;
import com.example.driverapp.databinding.FragmentAcceptOrderBinding;
import com.example.driverapp.databinding.FragmentReachPickUpLocationBinding;
import com.example.driverapp.directionhelpers.ConstructDirectionUrl;
import com.example.driverapp.directionhelpers.FetchURL;
import com.example.driverapp.directionhelpers.TaskLoadedCallback;
import com.example.driverapp.models.Address;
import com.example.driverapp.models.Order;
import com.example.driverapp.models.Restaurant;
import com.example.driverapp.viewmodels.AuthenticationViewModel;
import com.example.driverapp.viewmodels.OrderViewModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.ncorti.slidetoact.SlideToActView;

import java.util.Locale;

public class ReachPickUpLocationFragment extends Fragment implements OnMapReadyCallback {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentReachPickUpLocationBinding mBinding;
    OrderViewModel orderViewModel;
    NavController navController;
    Order mOrder;
    MarkerOptions mPlace1, mPlace2;


    private Polyline currentPolyline;
    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentReachPickUpLocationBinding.inflate(inflater, container, false);


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        orderViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);
        initClicks();

        mBinding.reachPickup.toolbar.title.setText("Reach Restaurant");

        orderViewModel.getAllAcceptedOrders().observe(requireActivity(), orders -> {
            mOrder = orders.get(0);
            mBinding.reachPickup.setOrder(mOrder);

            LatLng latLngRestaurant = CommonUtils.getRestaurantLocation(mOrder.getRestaurant());
            LatLng latLngUser = CommonUtils.getUserLocation(mOrder.getLocation());

            mPlace1 = new MarkerOptions().position(latLngRestaurant).title("Location 1");
            mPlace2 = new MarkerOptions().position(latLngUser).title("Location 2");

            String url = ConstructDirectionUrl.getUrl(mPlace1.getPosition(), mPlace2.getPosition(), "driving", Constants.GOOGLE_MAP_AUTH_KEY);
            new FetchURL(this.getContext(), FetchURL.POINT_PARSER).execute(url, "driving");

        });

        mBinding.reachPickup.btnAccept.setOnSlideCompleteListener(slideToActView -> {
            //navController.navigate(R.id.action_reachPickUpLocationFragment_to_pickOrderFragment);
        });


        orderViewModel.getPolyline().observe(requireActivity(), polylineOptions -> {
            if(mMap != null){
                Log.d(TAG, "POLYLINE_UPDATED.......................");
                if (currentPolyline != null)currentPolyline.remove();
                currentPolyline = mMap.addPolyline(polylineOptions);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mPlace1.getPosition(), 14.0f);
                mMap.moveCamera(cameraUpdate);
            }else{
                Log.d(TAG, "MAP IS NULL");
            }
        });



    }

    private void initClicks() {
        mBinding.reachPickup.layoutCallRestaurant.setOnClickListener(view -> {
            CommonUtils.makePhoneCall(requireActivity(), mOrder.getRestaurant().getContactNumber());
        });
        mBinding.reachPickup.layoutCallCustomer.setOnClickListener(view -> {
            CommonUtils.makePhoneCall(requireActivity(), mOrder.getUser().getPhone());
        });
        mBinding.reachPickup.layoutDirection.setOnClickListener(view -> {
            double destinationLatitude = Double.parseDouble(mOrder.getRestaurant().getLatitude());
            double destinationLongitude = Double.parseDouble(mOrder.getRestaurant().getLongitude());
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", destinationLatitude, destinationLongitude, "Where the party is at");
            Intent intentDirection = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            intentDirection.setPackage("com.google.android.apps.maps");
            startActivity(intentDirection);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //BitmapDescriptor iconRestaurant = BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant);
        Drawable circleDrawable = getResources().getDrawable(R.drawable.circle_shape);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

        mMap = googleMap;
        DrawMarker.getInstance(requireActivity()).draw(mMap, mPlace1.getPosition(), R.drawable.ic_restaurant, "Restaurant");
        DrawMarker.getInstance(requireActivity()).draw(mMap, mPlace2.getPosition(), R.drawable.ic_home_location, "Your Location");

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mPlace1.getPosition(), 14.0f);
        mMap.moveCamera(cameraUpdate);
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}