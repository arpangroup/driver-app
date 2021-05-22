package com.pureeats.driverapp.views.order;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.gson.Gson;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.adapters.DishListAdapter;
import com.pureeats.driverapp.databinding.FragmentDeliverOrderBinding;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.network.api.Api;
import com.pureeats.driverapp.repositories.OrderRepositoryImpl;
import com.pureeats.driverapp.utils.CommonUiUtils;
import com.pureeats.driverapp.utils.CommonUtils;
import com.pureeats.driverapp.viewmodels.OrderViewModel;

import java.util.ArrayList;


public class DeliverOrderFragment extends AbstractOrderFragment<OrderViewModel, FragmentDeliverOrderBinding, OrderRepositoryImpl> {
    private final String TAG = getClass().getName();
    private Order mOrder;
    private boolean toggleItems = false;

    public static DeliverOrderFragment newInstance(Order order){
        DeliverOrderFragment dialog = new DeliverOrderFragment();
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
        Bundle args = new Bundle();
        args.putString("order_json", new Gson().toJson(order));
        dialog.setArguments(args);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        disableBackButton();
        mBinding.setLifecycleOwner(this);
        navController = Navigation.findNavController(rootView);
        mOrder = new Gson().fromJson(getArguments().getString("order_json"), Order.class);
        mOrderId = mOrder.getId(); //important
        mUniqueOrderId = mOrder.getUniqueOrderId();//important
        mBinding.setOrder(mOrder);
        mBinding.btnAccept.setLocked(true);
        mBinding.setToggleItems(toggleItems);
        mBinding.dishRecycler.setAdapter(new DishListAdapter(CollectionUtils.isEmpty(mOrder.getOrderitems()) ? new ArrayList<>() : mOrder.getOrderitems()));
        mBinding.toolbar.back.setOnClickListener(view -> dismissOrderDialog());
        mBinding.btnToggleItems.setOnClickListener(view -> {
            toggleItems = !toggleItems;
            mBinding.setToggleItems(toggleItems);
        });
        mBinding.btnSendMessage.setOnClickListener(view -> sendMessage());
        mBinding.btnCallToCustomer.setOnClickListener(view -> CommonUtils.makePhoneCall(requireActivity(), mOrder.getCustomerPhone()));
        mBinding.radioConfirm.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked)mBinding.btnItemGivenToCustomer.setEnabled(true);
            else mBinding.btnItemGivenToCustomer.setEnabled(false);
        });
        mBinding.btnAccept.setOnSlideCompleteListener(slideToActView -> processOrder(mOrder));
        mBinding.btnItemGivenToCustomer.setOnClickListener(view -> {
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
                        mBinding.btnAccept.setEnabled(true);
                        mBinding.btnAccept.setLocked(false);
                        mBinding.btnItemGivenToCustomer.setEnabled(false);
                        mBinding.btnAccept.setOuterColor(R.color.orange);
                    }else{
                        Log.d(TAG, "ORIGINAL_OTP: " + mOrder.getDeliveryPin());
                        Log.d(TAG, "PROVIDE_OTP : " + otp);
                        CommonUiUtils.showSnackBar(getView(), "Invalid Delivery PIN");
                    }
                }
            });
            alert.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
        });
    }

    private void sendMessage(){
        viewModel.sendMessage(mOrder).observe(this, resource ->{
            if(mBinding == null) return;
            switch (resource.status){
                case LOADING:
                    mBinding.setIsMessageSending(true);
                    break;
                case ERROR:
                    mBinding.setIsMessageSending(false);
                    CommonUiUtils.showSnackBar(getView(), resource.message);
                    break;
                case SUCCESS:
                    mBinding.setIsMessageSending(false);
                    mOrder.setIsOrderReachedMessageSend(1);
                    mBinding.setOrder(mOrder);
                    CommonUiUtils.showSnackBar(getView(), "Message Send Successfully");
                    break;
            }
        });
    }

    private void processOrder(Order order){
        viewModel.deliverOrder(order, order.getDeliveryPin()).observe(mContext, resource -> {
            switch (resource.status){
                case LOADING:
                    break;
                case ERROR:
                    break;
                case SUCCESS:
                    gotoNextFragment(R.id.action_deliverOrrderFragment_to_tripDetailsFragment, order);
                    break;
            }
        });
    }





    @Override
    public Class<OrderViewModel> getViewModel() {
        return OrderViewModel.class;
    }

    @Override
    public FragmentDeliverOrderBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentDeliverOrderBinding.inflate(inflater, container, false);
    }

    @Override
    public OrderRepositoryImpl getRepository() {
        return new OrderRepositoryImpl(remoteDataSource.buildApi(Api.class), userSession);
    }
}