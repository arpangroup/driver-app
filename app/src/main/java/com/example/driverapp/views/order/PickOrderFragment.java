package com.example.driverapp.views.order;

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
import android.widget.Toast;

import com.example.driverapp.R;
import com.example.driverapp.commons.CommonUtils;
import com.example.driverapp.databinding.FragmentPickOrderBinding;
import com.example.driverapp.models.Order;
import com.example.driverapp.viewmodels.OrderViewModel;

public class PickOrderFragment extends Fragment{
    private final String TAG = this.getClass().getSimpleName();

    private FragmentPickOrderBinding mBinding;
    OrderViewModel orderViewModel;
    NavController navController;
    Order mOrder = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentPickOrderBinding.inflate(inflater, container, false);
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
        mBinding.pickOrder.btnClickPhoto.setVisibility(View.GONE);
        initClicks();

        orderViewModel.getOnGoingOrder().observe(requireActivity(), order -> {
            mOrder = order;
            mBinding.pickOrder.toolbar.title.setText("PICK ORDER");
            mBinding.pickOrder.setOrder(mOrder);
        });


        mBinding.pickOrder.btnAccept.setOnSlideCompleteListener(slideToActView -> {
            orderViewModel.pickedUpOrder(mOrder).observe(requireActivity(), isPickedUp -> {
                if(isPickedUp){
                    mOrder.setOrderStatusId(4);
                    orderViewModel.setOnGoingOrder(mOrder);
                    navController.navigate(R.id.action_pickOrderFragment_to_reachDirectionFragment);
                }else{
                    mBinding.pickOrder.btnAccept.resetSlider();
                    Toast.makeText(requireActivity(), "Order is not ready yet", Toast.LENGTH_LONG).show();
                }
            });
        });





    }

    private void initClicks() {
        mBinding.pickOrder.toggleRestaurantDetails.setOnClickListener(view -> {

        });
        mBinding.pickOrder.toggleItems.setOnClickListener(view -> {
            if(mBinding.pickOrder.dishRecycler.getVisibility() ==  View.GONE){
                mBinding.pickOrder.dishRecycler.setVisibility(View.VISIBLE);
                mBinding.pickOrder.imgToggleItem.setRotation(-90);
            }else{
                mBinding.pickOrder.dishRecycler.setVisibility(View.GONE);
                mBinding.pickOrder.imgToggleItem.setRotation(270);
            }

        });
        mBinding.pickOrder.callToRestaurant.setOnClickListener(view -> {
            CommonUtils.makePhoneCall(requireActivity(), mOrder.getRestaurant().getContactNumber());
        });

        mBinding.pickOrder.callToCustomer.setOnClickListener(view -> {
            CommonUtils.makePhoneCall(requireActivity(), mOrder.getUser().getPhone());
        });

        mBinding.pickOrder.btnClickPhoto.setOnClickListener(view -> {
            VerifyBillDialog dialog = new VerifyBillDialog();
            dialog.show(requireActivity().getSupportFragmentManager(), "View Order Id From Bill");

        });
        mBinding.pickOrder.radioConfirm.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                mBinding.pickOrder.btnAccept.setEnabled(true);
                mBinding.pickOrder.btnAccept.setOuterColor(R.color.orange);
            }else{
                mBinding.pickOrder.btnAccept.setEnabled(false);
                mBinding.pickOrder.btnAccept.setOuterColor(R.color.gray);
            }
        });
    }



}