package com.pureeats.driverapp.views.order;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.commons.DrawMarker;
import com.pureeats.driverapp.commons.OrderStatus;
import com.pureeats.driverapp.databinding.FragmentReachDirectionBinding;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.network.Resource;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.OrderRepositoryImpl;
import com.pureeats.driverapp.services.EndlessService;
import com.pureeats.driverapp.utils.AnimationUtils;
import com.pureeats.driverapp.utils.CommonUiUtils;
import com.pureeats.driverapp.utils.CommonUtils;
import com.pureeats.driverapp.utils.MapUtils;
import com.pureeats.driverapp.viewmodels.OrderViewModel;
import com.pureeats.driverapp.views.base.BaseDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ReachDirectionFragment extends BaseDialogFragment<OrderViewModel, FragmentReachDirectionBinding, OrderRepositoryImpl> implements OnMapReadyCallback {
    private final String TAG = getClass().getName();
    private FusedLocationProviderClient mFusedLocationClient;
    private Order mOrder;
    private boolean isOrderPicked;
    LatLng latLngRestaurant, latLngCustomer, latLngDeliveryGuy;

    private Polyline currentPolyline;
    private GoogleMap mMap;
    private boolean  isPolyLineShowed = false;
    private PolylineOptions mPolylineOptions;

    private Marker userLocationMarker;
    private Marker originMarker = null;
    private Marker destinationMarker = null;
    private Polyline grayPolyline = null;
    private Polyline blackPolyline = null;
    private Marker movingCabMarker = null;
    private LatLng previousLatLng = null;
    private LatLng currentLatLng= null;
    private Handler handler;
    private Runnable runnable;


    public static ReachDirectionFragment newInstance(Order order, boolean isOrderPicked){
        ReachDirectionFragment dialog = new ReachDirectionFragment();
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
        Bundle args = new Bundle();
        args.putString("order_json", new Gson().toJson(order));
        args.putBoolean("is_order_picked", isOrderPicked);
        dialog.setArguments(args);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "Inside onActivityCreated()...................................");

        mBinding.mapView.onCreate(savedInstanceState);
        mBinding.mapView.onResume();

        mBinding.mapView.getMapAsync(this);
    }


    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        mBinding.setLifecycleOwner(this);
        mOrder = new Gson().fromJson(getArguments().getString("order_json"), Order.class);
        isOrderPicked = getArguments().getBoolean("is_order_picked");
        //isOrderPicked = mOrder.getOrderStatusId() > OrderStatus.DELIVERY_GUY_ASSIGNED.status();
        mBinding.setOrder(mOrder);
        latLngRestaurant = CommonUtils.getRestaurantLocation(mOrder.getRestaurant());
        latLngCustomer = CommonUtils.getUserLocation(mOrder.getLocation());
        latLngDeliveryGuy = EndlessService.getLastLocation();
        Log.d(TAG, "LATLNG_RESTAURANT: " + latLngRestaurant);
        Log.d(TAG, "LATLNG_CUSTOMER: " + latLngCustomer);
        Log.d(TAG, "LATLNG_DELIVERY_GUY: " + latLngDeliveryGuy);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
       // mBinding.progressBar.setVisibility(View.VISIBLE);
        mBinding.toolbar.back.setOnClickListener(view -> dismissOrderDialog());
        mBinding.layoutCallRestaurant.setOnClickListener(view -> CommonUtils.makePhoneCall(mContext, mOrder.getRestaurant().getContactNumber()));
        mBinding.layoutCallCustomer.setOnClickListener(view -> CommonUtils.makePhoneCall(requireActivity(), mOrder.getUser().getPhone()));
        mBinding.layoutDirection.setOnClickListener(view -> CommonUtils.openNavigationActivity(mContext, isOrderPicked ? latLngCustomer : latLngRestaurant));
        mBinding.btnAccept.setOnSlideCompleteListener(slideToActView -> processOrder(mOrder));
    }

    private void processOrder(Order order){
        if(isOrderPicked){
            viewModel.reachedDropLocation(order).observe(mContext, this::handleStatus);
        }else{
            viewModel.reachedPickupLocation(order).observe(mContext, this::handleStatus);
        }
    }

    private void handleStatus(Resource<Order> resource){
        switch (resource.status){
            case LOADING:
                break;
            case ERROR:
                CommonUiUtils.showSnackBar(getView(), resource.message);
                break;
            case SUCCESS:
                gotoNextActivity(resource.data);
                break;
        }
    }




    @Override
    public Class<OrderViewModel> getViewModel() {
        return OrderViewModel.class;
    }

    @Override
    public FragmentReachDirectionBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentReachDirectionBinding.inflate(inflater, container, false);
    }

    @Override
    public OrderRepositoryImpl getRepository() {
        return new OrderRepositoryImpl(remoteDataSource.buildApi(Api.class), userSession);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Inside onMapReady()...................................");
        mMap = googleMap;
        enableMyLocationIcon();
        showDefaultLocationOnMap(latLngDeliveryGuy);
        addCarMarkerAndGet(latLngDeliveryGuy);

        List<LatLng> path;
        if(isOrderPicked){// show path between restaurant and Customer
            path = Arrays.asList(latLngRestaurant, latLngCustomer);
            // Add destination location marker(Customer's Address)
            DrawMarker.getInstance(mContext).draw(mMap, path.get(1), R.drawable.ic_home_location, mOrder.getAddress());
        }else{ // show path between deliveryGuy and Restaurant
            path = Arrays.asList(latLngDeliveryGuy, latLngRestaurant);
            // Add destination location marker(Restaurants's Address)
            DrawMarker.getInstance(mContext).draw(mMap, path.get(1), R.drawable.ic_restaurant, mOrder.getRestaurant().getName());
        }
        showPath(new ArrayList<>(path));
    }


    /*##########################################################################################*/
    /*                                 GOOGLE_MAP[START]                                        */
    /*##########################################################################################*/
    private void enableMyLocationIcon(){
        if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }else{
            //Ask for the permission
            if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
                    Log.d(TAG, "You should show a alert dialog");
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST_CODE);
                }else{
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST_CODE);
                }
            }
        }
    }
    private void moveCamera(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
    private void animateCamera(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15.5f).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    private Marker addCarMarkerAndGet(LatLng latLng) {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtils.getCarBitmap(mContext));
        return mMap.addMarker(new MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor));
    }
    private void showDefaultLocationOnMap(LatLng latLng) {
        moveCamera(latLng);
        animateCamera(latLng);
    }
    /**
     * this function will be used to draw a path between origin and destination by using the addPolyline() method.
     * @param latLngList
     * we can draw a path between the Origin and the Destination. We will use Polyline,
     * which is a list of points, where line segments are drawn between consecutive points.
     * Also, we will be drawing two polylines, one of grey color and another black color.
     * The basic idea is to draw the grey line first and then draw the black line by sowing some animation.
     * So, create a function named showPath() that will take a list of LatLng and this function
     * will be used to draw a path between origin and destination by using the addPolyline() method.
     */
    private void showPath(ArrayList<LatLng> latLngList){
        Log.d(TAG, "Inside showPath().....");
        Log.d(TAG, "PATH: " + latLngList);
        LatLngBounds.Builder builder  = new LatLngBounds.Builder();
        for (LatLng latLng : latLngList) {
            builder.include(latLng);
        }

        // this is used to set the bound of the Map
        //we have used bound in the Google Map. It is an area that is created by using the LatLng we have.
        LatLngBounds bounds = builder.build();//
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2));

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.GRAY);
        polylineOptions.width(5f);
        polylineOptions.addAll(latLngList);
        grayPolyline = mMap.addPolyline(polylineOptions);

        PolylineOptions blackPolylineOptions = new PolylineOptions();
        blackPolylineOptions.color(Color.BLACK);
        polylineOptions.width(5f);
        blackPolyline = mMap.addPolyline(blackPolylineOptions);

        originMarker = addOriginDestinationMarkerAndGet(latLngList.get(0));
        if(originMarker != null) originMarker.setAnchor(0.5f, 0.5f);
        destinationMarker = addOriginDestinationMarkerAndGet(latLngList.get(latLngList.size()-1));
        if(destinationMarker != null) destinationMarker.setAnchor(0.5f, 0.5f);

        ValueAnimator polylineAnimator = AnimationUtils.polylineAnimator();
        polylineAnimator.addUpdateListener(valueAnimator -> {
            int percentValue = (int) valueAnimator.getAnimatedValue();
            int index = 0;
            if(grayPolyline != null){
                index = (int) ((grayPolyline.getPoints().size()) * (percentValue / 100.0f));
            }
            if(blackPolyline != null){
                blackPolyline.setPoints(grayPolyline.getPoints().subList(0, index));
            }
        });
        polylineAnimator.start();
    }
    private Marker addOriginDestinationMarkerAndGet(LatLng latLng) {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtils.getOriginDestinationMarkerBitmap());
        return mMap.addMarker(new MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor));
    }
    private void updateCarLocation(LatLng latLng){
        if (movingCabMarker == null) {
            movingCabMarker = addCarMarkerAndGet(latLng);
        }
        if (previousLatLng == null) {
            currentLatLng = latLng;
            previousLatLng = currentLatLng;
            if(movingCabMarker != null) movingCabMarker.setPosition(currentLatLng);
            if(movingCabMarker != null) movingCabMarker.setAnchor(0.5f, 0.5f);
            if(currentLatLng != null) animateCamera(currentLatLng);
        }else{
            previousLatLng = currentLatLng;
            currentLatLng = latLng;
            ValueAnimator valueAnimator = AnimationUtils.carAnimator();
            valueAnimator.addUpdateListener(va  -> {
                if (currentLatLng != null && previousLatLng != null) {
                    float multiplier = va.getAnimatedFraction();
                    LatLng nextLocation = new LatLng(
                            multiplier * currentLatLng.latitude + (1 - multiplier) * previousLatLng.latitude,
                            multiplier * currentLatLng.longitude + (1 - multiplier) * previousLatLng.longitude
                    );
                    if(movingCabMarker != null) movingCabMarker.setPosition(nextLocation);
                    Float rotation = MapUtils.getRotation(previousLatLng, nextLocation);
                    if (!rotation.isNaN()) {
                        movingCabMarker.setRotation(rotation);
                    }
                    movingCabMarker.setAnchor(0.5f, 0.5f);
                    animateCamera(nextLocation);
                }
            });
            valueAnimator.start();
        }
    }
    private void showMovingCab(ArrayList<LatLng> cabLatLngList) {
        handler = new Handler();
        final int[] index = {0};
        runnable = () -> {
            if (index[0] < 10) {
                try {
                    updateCarLocation(cabLatLngList.get(index[0]));
                    handler.postDelayed(runnable, 3000);
                    ++index[0];
                }catch (Exception e){}
            } else {
                handler.removeCallbacks(runnable);
                Toast.makeText(mContext, "Trip Ends", Toast.LENGTH_SHORT).show();
            }
        };

        handler.postDelayed(runnable, 5000);
    }
    private void setUserLocationMarker(LatLng latLng){
        //LatLng latLng =  new LatLng(location.getLatitude(), location.getLongitude());
        if(userLocationMarker == null){
            // Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.delivery_guy));
            //markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float)0.5);

            userLocationMarker = mMap.addMarker(markerOptions);
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));

        }else{
            // Use the previously created marker
            userLocationMarker.setPosition(latLng);
            //userLocationMarker.setRotation(location.getBearing());
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }

    }
    /*##########################################################################################*/
    /*                                 GOOGLE_MAP[END]                                        */
    /*##########################################################################################*/

}