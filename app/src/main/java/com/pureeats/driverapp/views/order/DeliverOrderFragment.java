package com.pureeats.driverapp.views.order;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.CommonUtils;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.databinding.FragmentDeliverOrderBinding;
import com.pureeats.driverapp.databinding.FragmentReachPickUpLocationBinding;
import com.pureeats.driverapp.directionhelpers.ConstructDirectionUrl;
import com.pureeats.driverapp.directionhelpers.FetchURL;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.utils.FormatPrice;
import com.pureeats.driverapp.viewmodels.LocationViewModel;
import com.pureeats.driverapp.viewmodels.OrderViewModel;

public class DeliverOrderFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentDeliverOrderBinding mBinding;
    OrderViewModel orderViewModel;
    LocationViewModel locationViewModel;
    NavController navController;
    Order mOrder = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentDeliverOrderBinding.inflate(inflater, container, false);
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
        initClicks();
        mBinding.deliverOrder.btnItemGivenToCustomer.setVisibility(View.GONE);
        mBinding.deliverOrder.btnAccept.setLocked(true);


//        orderViewModel.getOnGoingOrder().observe(requireActivity(), order -> {
//            mOrder = order;
//            Log.d(TAG, "###################################################################");
//            Log.d(TAG, "ORDER: " + mOrder);
//            Log.d(TAG, "###################################################################");
//            mBinding.deliverOrder.toolbar.title.setText("Deliver Order");
//            mBinding.deliverOrder.setOrder(mOrder);
//        });

        orderViewModel.getIsLoading().observe(requireActivity(), aBoolean -> {
            if(aBoolean){
                mBinding.deliverOrder.progress.setVisibility(View.GONE);
            }else {
                mBinding.deliverOrder.progress.setVisibility(View.GONE);
            }
        });

        orderViewModel.getSingleDeliveryOrder(orderViewModel.getOnGoingOrder().getValue().getUniqueOrderId()).observe(requireActivity(), order -> {
            mOrder = order;
            Log.d(TAG, "###################################################################");
            Log.d(TAG, "ORDER: " + mOrder);
            Log.d(TAG, "###################################################################");
            LatLng  latLngRestaurant = CommonUtils.getRestaurantLocation(mOrder.getRestaurant());
            LatLng  latLngCustomer = CommonUtils.getUserLocation(mOrder.getLocation());
            String url = ConstructDirectionUrl.getUrl(latLngRestaurant, latLngCustomer, "driving", Constants.GOOGLE_MAP_AUTH_KEY);
            new FetchURL(this.getContext(), FetchURL.DISTANCE_PARSER).execute(url, "driving");

            mBinding.deliverOrder.toolbar.title.setText("Deliver Order");
            mOrder.setDeliveryPin(order.getDeliveryPin());
            mBinding.deliverOrder.setOrder(mOrder);
        });

        locationViewModel.getDirection().observe(requireActivity(), direction -> {
            Log.d(TAG, "#########################");
            Log.d(TAG, "DIRECTION: "+ direction);
            mOrder.setDirection(direction);
        });



    }

    private void initClicks() {
        mBinding.deliverOrder.callToCustomer.setOnClickListener(view -> {
            CommonUtils.makePhoneCall(requireActivity(), mOrder.getUser().getPhone());
        });
        mBinding.deliverOrder.toggleItems.setOnClickListener(view -> {
            if(mBinding.deliverOrder.dishRecycler.getVisibility() ==  View.GONE){
                mBinding.deliverOrder.dishRecycler.setVisibility(View.VISIBLE);
                mBinding.deliverOrder.imgToggleItem.setRotation(-90);
            }else{
                mBinding.deliverOrder.dishRecycler.setVisibility(View.GONE);
                mBinding.deliverOrder.imgToggleItem.setRotation(270);
            }

        });
        mBinding.deliverOrder.sendMessage.setOnClickListener(view -> {
            Log.d(TAG, "Send message to customer.....Not implement yet");
            orderViewModel.sendMessage(mOrder.getId()).observe(requireActivity(), apiResponse -> {
                mBinding.deliverOrder.sendMessage.setVisibility(View.GONE);
                mBinding.deliverOrder.messageSendSuccess.setVisibility(View.VISIBLE);
            });
        });

        mBinding.deliverOrder.radioConfirm.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                if(mOrder.getPaymentMode().equalsIgnoreCase("COD")){
                    confirmAmountToBeCollect();
                }else{
                    mBinding.deliverOrder.radioConfirm.setChecked(true);
                    mBinding.deliverOrder.radioConfirm.setEnabled(false);
                    mBinding.deliverOrder.btnItemGivenToCustomer.setVisibility(View.VISIBLE);
                    mBinding.deliverOrder.btnItemGivenToCustomer.setEnabled(true);
                }
                //mBinding.deliverOrder.btnAccept.setEnabled(true);
                //mBinding.deliverOrder.btnAccept.setOuterColor(R.color.orange);
            }else{
                //mBinding.deliverOrder.btnAccept.setEnabled(false);
                //mBinding.deliverOrder.btnAccept.setOuterColor(R.color.gray);
            }
        });

        mBinding.deliverOrder.btnItemGivenToCustomer.setOnClickListener(view -> {
            final EditText input = new EditText(requireActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(24, 60, 24, 24);
            input.setLayoutParams(layoutParams);

            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(requireActivity());
            alert.setView(input);
            alert.setTitle("Delivery PIN");
            alert.setMessage("Please enter the delivery OTP to confirm reaching delivery");
            alert.setCancelable(false);
            alert.setPositiveButton("PROCEED",  (dialogInterface, i) -> {
                String otp = "";
                if(input.getText() != null){
                    otp = input.getText().toString();
                    if(otp.equals(mOrder.getDeliveryPin())){
                        mBinding.deliverOrder.btnAccept.setEnabled(true);
                        mBinding.deliverOrder.btnAccept.setLocked(false);
                        mBinding.deliverOrder.btnItemGivenToCustomer.setEnabled(false);
                        mBinding.deliverOrder.btnAccept.setOuterColor(R.color.orange);
                    }else{
                        Log.d(TAG, "ORIGINAL_OTP: " + mOrder.getDeliveryPin());
                        Log.d(TAG, "PROVIDE_OTP : " + otp);
                        Toast.makeText(requireActivity(), "Invalid Delivery PIN", Toast.LENGTH_LONG).show();
                    }
                }
            });
            alert.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
        });

        mBinding.deliverOrder.btnAccept.setOnSlideCompleteListener(slideToActView -> {
            if(mOrder.getDirection() != null){
                orderViewModel.deliverOrder(mOrder, mOrder.getDeliveryPin()).observe(requireActivity(), isDelivered -> {
                    if(isDelivered){
                        mOrder.setOrderStatusId(5);
                        orderViewModel.setOnGoingOrder(mOrder);
                        navController.navigate(R.id.action_deliverOrderFragment_to_tripDetailsFragment);
                    }else{
                        mBinding.deliverOrder.btnAccept.resetSlider();
                        Toast.makeText(requireActivity(), "Something error happened", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(requireActivity(), "Distance not calculated", Toast.LENGTH_SHORT).show();
                mBinding.deliverOrder.btnAccept.resetSlider();
            }
        });
    }

    private void confirmAmountToBeCollect(){
        String totalAmount = FormatPrice.format(mOrder.getTotal());
        new AlertDialog.Builder(requireActivity())
                .setTitle("Payment Confirmation")
                .setMessage("Have you collected cash amount of Rs." +  totalAmount +"from customer?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("YES, CONFIRM", (dialog, whichButton) -> {
                    mBinding.deliverOrder.radioConfirm.setChecked(true);
                    mBinding.deliverOrder.radioConfirm.setEnabled(false);
                    mBinding.deliverOrder.btnItemGivenToCustomer.setVisibility(View.VISIBLE);
                    mBinding.deliverOrder.btnItemGivenToCustomer.setEnabled(true);
                    //mBinding.deliverOrder.btnAccept.setEnabled(true);
                    //mBinding.deliverOrder.btnAccept.setOuterColor(R.color.orange);
                })
                .setNegativeButton("NO", (dialogInterface, i) -> mBinding.deliverOrder.radioConfirm.setChecked(false)).show();
    }
}