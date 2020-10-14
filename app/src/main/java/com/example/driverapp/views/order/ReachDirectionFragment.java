package com.example.driverapp.views.order;

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
import com.example.driverapp.commons.OrderStatus;
import com.example.driverapp.databinding.FragmentReachDirectionBinding;
import com.example.driverapp.databinding.FragmentReachPickUpLocationBinding;
import com.example.driverapp.directionhelpers.ConstructDirectionUrl;
import com.example.driverapp.directionhelpers.FetchURL;
import com.example.driverapp.models.Location;
import com.example.driverapp.models.Order;
import com.example.driverapp.viewmodels.LocationViewModel;
import com.example.driverapp.viewmodels.OrderViewModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.gson.Gson;

import java.util.Locale;

public class ReachDirectionFragment extends Fragment implements OnMapReadyCallback {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentReachDirectionBinding mBinding;
    OrderViewModel orderViewModel;
    LocationViewModel locationViewModel;
    NavController navController;
    Order mOrder;
    MarkerOptions mPlace1, mPlace2;


    private Polyline currentPolyline;
    private GoogleMap mMap;


    public ReachDirectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBinding.mapView.onCreate(savedInstanceState);
        mBinding.mapView.onResume();

        mBinding.mapView.getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentReachDirectionBinding.inflate(inflater, container, false);

        /*
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
         */

        return mBinding.getRoot();
    }

    private void setMapView(){
        Drawable circleDrawable = getResources().getDrawable(R.drawable.circle_shape);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

        try{
            DrawMarker.getInstance(requireActivity()).draw(mMap, mPlace1.getPosition(), R.drawable.ic_restaurant, "Restaurant");
            DrawMarker.getInstance(requireActivity()).draw(mMap, mPlace2.getPosition(), R.drawable.ic_home_location, "Your Location");

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mPlace1.getPosition(), 14.0f);
            mMap.moveCamera(cameraUpdate);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        Log.d(TAG, "Inside onViewCreated...................");

        // Initialize ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        //orderViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);
        initClicks();


        orderViewModel.getOnGoingOrder().observe(requireActivity(),  order -> {
            mOrder = order;
            mBinding.setOrder(mOrder);
            Log.d(TAG, "##############################################");
            Log.d(TAG, "ORDER: "+order);
            Location orderLocation = new Gson().fromJson(mOrder.getLocation(), Location.class);
            mOrder.setAddress(orderLocation.getHouse() + ", "+ orderLocation.getAddress());
            Log.d(TAG, "##############################################");
            LatLng latLngRestaurant = CommonUtils.getRestaurantLocation(mOrder.getRestaurant());
            LatLng latLngCustomer = CommonUtils.getUserLocation(mOrder.getLocation());
            LatLng latLngDriver = locationViewModel.getCurrentLocation().getValue();
            if(latLngDriver == null) latLngDriver = new LatLng(0.0, 0.0);

            if(mOrder.getOrderStatusId() == OrderStatus.ON_THE_WAY.value()){
                //Reach to the customers location
                mBinding.toolbar.title.setText("Reach Drop Location");
                mPlace1 = new MarkerOptions().position(latLngRestaurant).title("Restaurant Location");
                mPlace2 = new MarkerOptions().position(latLngCustomer).title("Customer Location");

            }else {
                // Reach to the restaurant
                mBinding.toolbar.title.setText("Reach Restaurant");
                mPlace1 = new MarkerOptions().position(latLngDriver).title("Driver Location");
                mPlace2 = new MarkerOptions().position(latLngRestaurant).title("Restaurant Location");
            }
            String url = ConstructDirectionUrl.getUrl(mPlace1.getPosition(), mPlace2.getPosition(), "driving", Constants.GOOGLE_MAP_AUTH_KEY);
            new FetchURL(this.getContext(), FetchURL.POINT_PARSER).execute(url, "driving");
        });

        mBinding.btnAccept.setOnSlideCompleteListener(slideToActView -> {
            if(mOrder.getOrderStatusId() == 4){
                navController.navigate(R.id.action_reachDirectionFragment_to_deliverOrderFragment);
            }else if(mOrder.getOrderStatusId() == 3 || mOrder.getOrderStatusId() == 7 || mOrder.getOrderStatusId() == 10){
                navController.navigate(R.id.action_reachDirectionFragment_to_pickOrderFragment);
            }
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
        mBinding.layoutCallRestaurant.setOnClickListener(view -> {
            CommonUtils.makePhoneCall(requireActivity(), mOrder.getRestaurant().getContactNumber());
        });
        mBinding.layoutCallCustomer.setOnClickListener(view -> {
            CommonUtils.makePhoneCall(requireActivity(), mOrder.getUser().getPhone());
        });
        mBinding.layoutDirection.setOnClickListener(view -> {
            double destinationLatitude = 0.0;
            double destinationLongitude = 0.0;
            if(mOrder.getOrderStatus() == OrderStatus.DELIVERY_GUY_ASSIGNED){
                // Need to travel to restaurant location
                destinationLatitude = Double.parseDouble(mOrder.getRestaurant().getLatitude());
                destinationLongitude = Double.parseDouble(mOrder.getRestaurant().getLongitude());
            }else if(mOrder.getOrderStatus() == OrderStatus.ON_THE_WAY){
                // Need to travel to customers location
                LatLng latLngCustomer = CommonUtils.getUserLocation(mOrder.getLocation());
                destinationLatitude = latLngCustomer.latitude;
                destinationLongitude = latLngCustomer.longitude;
            }else{
                //....
            }
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", destinationLatitude, destinationLongitude, "Where the party is at");
            Intent intentDirection = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            intentDirection.setPackage("com.google.android.apps.maps");
            startActivity(intentDirection);
        });
    }



    public void onMapReady(GoogleMap googleMap) {
        //BitmapDescriptor iconRestaurant = BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant);
        Drawable circleDrawable = getResources().getDrawable(R.drawable.circle_shape);
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

        try{
            mMap = googleMap;
//            DrawMarker.getInstance(requireActivity()).draw(mMap, mPlace1.getPosition(), R.drawable.ic_restaurant, "Restaurant");
//            DrawMarker.getInstance(requireActivity()).draw(mMap, mPlace2.getPosition(), R.drawable.ic_home_location, "Your Location");
//
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mPlace1.getPosition(), 14.0f);
//            mMap.moveCamera(cameraUpdate);
        }catch (Exception e){
            e.printStackTrace();
        }

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