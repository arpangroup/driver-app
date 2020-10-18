package com.example.driverapp.views.order;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.example.driverapp.directionhelpers.TaskLoadedCallback;
import com.example.driverapp.models.Direction;
import com.example.driverapp.models.Location;
import com.example.driverapp.models.Order;
import com.example.driverapp.services.FetchOrderService;
import com.example.driverapp.viewmodels.LocationViewModel;
import com.example.driverapp.viewmodels.OrderViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.util.Locale;

public class ReachDirectionFragment extends Fragment implements OnMapReadyCallback, TaskLoadedCallback {
    private final String TAG = this.getClass().getSimpleName();
    private FusedLocationProviderClient mFusedLocationClient;

    private FragmentReachDirectionBinding mBinding;
    OrderViewModel orderViewModel;
    LocationViewModel locationViewModel;
    NavController navController;
    Order mOrder;
    MarkerOptions mPlace1, mPlace2;


    private Polyline currentPolyline;
    private GoogleMap mMap;
    Marker userLocationMarker;
    private boolean  isPolyLineShowed = false;
    LatLng currentLatLng = null;
    private PolylineOptions mPolylineOptions;



    public ReachDirectionFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Inside onCreateView()...................................");
        mBinding = FragmentReachDirectionBinding.inflate(inflater, container, false);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        /*
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
         */

        return mBinding.getRoot();
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
        isPolyLineShowed = false;

        orderViewModel.getOnGoingOrder().observe(requireActivity(), order -> {
            mOrder = order;
            mBinding.setOrder(mOrder);
            Log.d(TAG, "##############################################");
            Log.d(TAG, "ORDER: " + order);
            Location orderLocation = new Gson().fromJson(mOrder.getLocation(), Location.class);
            mOrder.setAddress(orderLocation.getHouse() + ", " + orderLocation.getAddress());
//            LatLng latLngRestaurant = CommonUtils.getRestaurantLocation(mOrder.getRestaurant());
//            LatLng latLngCustomer = CommonUtils.getUserLocation(mOrder.getLocation());
//            LatLng latLngDriver = locationViewModel.getCurrentLocation().getValue();
//            if (latLngDriver == null) latLngDriver = new LatLng(0.0, 0.0);
//            Log.d(TAG, "latLngRestaurant:  " + latLngRestaurant);
//            Log.d(TAG, "latLngCustomer:  " + latLngCustomer);
//            Log.d(TAG, "latLngDriver:  " + latLngDriver);
            Log.d(TAG, "##############################################");

            if (mOrder.getOrderStatusId() == 4) {
                //Reach to the customers location
                mBinding.toolbar.title.setText("Reach Drop Location");
//                mPlace1 = new MarkerOptions().position(latLngRestaurant).title("Restaurant Location");
//                mPlace2 = new MarkerOptions().position(latLngCustomer).title("Customer Location");

            } else {
                // Reach to the restaurant
                mBinding.toolbar.title.setText("Reach Restaurant");
//                mPlace1 = new MarkerOptions().position(latLngDriver).title("Driver Location");
//                mPlace2 = new MarkerOptions().position(latLngRestaurant).title("Restaurant Location");
            }
//            String url = ConstructDirectionUrl.getUrl(mPlace1.getPosition(), mPlace2.getPosition(), "driving", Constants.GOOGLE_MAP_AUTH_KEY);
//            new FetchURL(this.getContext(), FetchURL.POINT_PARSER).execute(url, "driving");
        });

        mBinding.btnAccept.setOnSlideCompleteListener(slideToActView -> {
            if (mOrder.getOrderStatusId() == 4) {
                navController.navigate(R.id.action_reachDirectionFragment_to_deliverOrderFragment);
            } else if (mOrder.getOrderStatusId() == 3 || mOrder.getOrderStatusId() == 7 || mOrder.getOrderStatusId() == 10) {
                navController.navigate(R.id.action_reachDirectionFragment_to_pickOrderFragment);
            }
        });


        orderViewModel.getPolyline().observe(requireActivity(), polylineOptions -> {

            if (mMap != null && polylineOptions != null) {
                Log.d(TAG, "#########################PolyLine Observer###################################");
                if (currentPolyline != null) currentPolyline.remove();
                currentPolyline = mMap.addPolyline(polylineOptions);
                //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 14.0f);
                //mMap.moveCamera(cameraUpdate);
                Log.d(TAG, "POLYLINE_UPDATED.......................");
            } else {
                Log.d(TAG, "MAP IS NULL");
            }

        });

        FetchOrderService.mutableLocations.observe(requireActivity(), locations -> {
            int lastLocation = locations.size() - 1;
            if(mMap != null){
                Log.d(TAG, "................LOCATION...............................");
                android.location.Location location = locations.get(lastLocation);
                Log.d(TAG, location.getLatitude() + ", " + location.getLongitude());
                setUserLocationMarker(location);
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
            if (mOrder.getOrderStatus() == OrderStatus.DELIVERY_GUY_ASSIGNED) {
                // Need to travel to restaurant location
                destinationLatitude = Double.parseDouble(mOrder.getRestaurant().getLatitude());
                destinationLongitude = Double.parseDouble(mOrder.getRestaurant().getLongitude());
            } else if (mOrder.getOrderStatus() == OrderStatus.ON_THE_WAY) {
                // Need to travel to customers location
                LatLng latLngCustomer = CommonUtils.getUserLocation(mOrder.getLocation());
                destinationLatitude = latLngCustomer.latitude;
                destinationLongitude = latLngCustomer.longitude;
            } else {
                //....
            }
            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", destinationLatitude, destinationLongitude, "Where the party is at");
            Intent intentDirection = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            intentDirection.setPackage("com.google.android.apps.maps");
            startActivity(intentDirection);
        });
    }


    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<android.location.Location> locationTask = mFusedLocationClient.getLastLocation();
        locationTask.addOnSuccessListener(location -> {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
            //mMap.addMarker(new MarkerOptions().position(latLng));
        });

    }

    private void setUserLocationMarker(android.location.Location location){
        LatLng latLng =  new LatLng(location.getLatitude(), location.getLongitude());
        currentLatLng = latLng;
        if(userLocationMarker == null){
            // Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);

            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.rider));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float)0.5);

            userLocationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));

        }else{
            // Use the previously created marker
            userLocationMarker.setPosition(latLng);
            userLocationMarker.setRotation(location.getBearing());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));

            LatLng latLngDestination = null;
            if(mOrder != null && latLng != null){
                if(mOrder.getOrderStatusId() == 4){
                    // Need to travel to restaurant location
                    double lat = Double.parseDouble(mOrder.getRestaurant().getLatitude());
                    double lng = Double.parseDouble(mOrder.getRestaurant().getLongitude());
                    latLngDestination = new LatLng(lat, lng);
                }else{
                    latLngDestination = CommonUtils.getUserLocation(mOrder.getLocation());
                }
            }
            String url = ConstructDirectionUrl.getUrl(latLng, latLngDestination, "driving", Constants.GOOGLE_MAP_AUTH_KEY);
            new FetchURL(this.getContext(), FetchURL.POINT_PARSER).execute(url, "driving");


        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Inside onMapReady()...................................");
        mMap = googleMap;
        if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            enableUserLocation();
            //zoomToUserLocation();
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

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Permission granted
                //getLastLocation();
                //checkSettingsAndStartLocationUpdate();
                enableUserLocation();
                zoomToUserLocation();
            }else{
                // We can show a dialog that permission is not granted
            }
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        Log.d(TAG, "Inside onTaskDone()......");
        PolylineOptions polylineOptions = (PolylineOptions) values[0];

        Log.d(TAG, "#########################PolyLine Observer###################################");
        if (currentPolyline != null) currentPolyline.remove();
        currentPolyline = mMap.addPolyline(polylineOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 14.0f);
        mMap.moveCamera(cameraUpdate);
        Log.d(TAG, "POLYLINE_UPDATED.......................");
    }
}