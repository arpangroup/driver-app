package com.example.driverapp.views.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.driverapp.R;
import com.example.driverapp.commons.CommonUtils;
import com.example.driverapp.databinding.FragmentDeliverOrderBinding;
import com.example.driverapp.databinding.FragmentReachPickUpLocationBinding;
import com.example.driverapp.models.Order;
import com.example.driverapp.utils.FormatPrice;
import com.example.driverapp.viewmodels.AuthenticationViewModel;
import com.example.driverapp.viewmodels.OrderViewModel;

public class DeliverOrderFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentDeliverOrderBinding mBinding;
    OrderViewModel orderViewModel;
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
        orderViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);
        initClicks();
        mBinding.deliverOrder.btnItemGivenToCustomer.setVisibility(View.GONE);
        mBinding.deliverOrder.btnAccept.setLocked(true);


        orderViewModel.getOnGoingOrder().observe(requireActivity(), order -> {
            mOrder = order;
            Log.d(TAG, "###################################################################");
            Log.d(TAG, "ORDER: " + mOrder);
            Log.d(TAG, "###################################################################");
            mBinding.deliverOrder.toolbar.title.setText("Deliver Order");
            mBinding.deliverOrder.setOrder(mOrder);
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
        });

        mBinding.deliverOrder.radioConfirm.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                confirmAmountToBeCollect();
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
            orderViewModel.deliverOrder(mOrder, mOrder.getDeliveryPin()).observe(requireActivity(), isDelivered -> {
                if(isDelivered){
                    mOrder.setOrderStatusId(5);
                    navController.navigate(R.id.action_deliverOrderFragment_to_tripDetailsFragment);
                }else{
                    mBinding.deliverOrder.btnAccept.resetSlider();
                    Toast.makeText(requireActivity(), "Something error happened", Toast.LENGTH_SHORT).show();
                }
            });
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