package com.pureeats.driverapp.views.base;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.google.gson.Gson;
import com.pureeats.driverapp.commons.OrderStatus;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.network.datasource.RemoteDataSource;
import com.pureeats.driverapp.repositories.BaseRepository;
import com.pureeats.driverapp.sharedprefs.UserSession;
import com.pureeats.driverapp.viewmodels.BaseViewModel;
import com.pureeats.driverapp.viewmodels.ViewModelFactory;
import com.pureeats.driverapp.views.App;
import com.pureeats.driverapp.views.order.AcceptOrderDialog;
import com.pureeats.driverapp.views.order.DeliverOrderFragment;
import com.pureeats.driverapp.views.order.DialogActivity;
import com.pureeats.driverapp.views.order.PickOrderFragment;
import com.pureeats.driverapp.views.order.ReachDirectionFragment;
import com.pureeats.driverapp.views.order.TripDetailsFragment;


public abstract class BaseDialogFragment<VM extends BaseViewModel, B extends ViewBinding, R extends BaseRepository> extends DialogFragment {
    private static String TAG = "BaseBottomSheetDialogFragment";
    protected FragmentActivity mContext;
    protected UserSession userSession;
    protected B mBinding;
    protected VM viewModel;
    protected RemoteDataSource remoteDataSource = new RemoteDataSource();
    private ViewModelFactory factory;

    protected App app;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button even
                Log.d("BACKBUTTON", "Back button clicks");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DetailsFragmentSlideAnimation;
        dialog.getWindow().setWindowAnimations(com.pureeats.driverapp.R.style.DetailsFragmentSlideAnimation);
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        //mBinding = FragmentLoginVerifyPhoneBinding.inflate(inflater, container, false);
        //return mBinding.getRoot();

        mContext = requireActivity();
        userSession = new UserSession(mContext);
        mBinding = getBinding(inflater, container);
        factory = new ViewModelFactory(getRepository());
        //viewModel = new ViewModelProvider(this, factory).get(getViewModel());
        viewModel = new ViewModelProvider(requireActivity(), factory).get(getViewModel());
        app = App.getInstance();

        return mBinding.getRoot();
    }

    public abstract Class<VM> getViewModel();
    public abstract B getBinding(LayoutInflater inflater, ViewGroup container);
    public abstract R getRepository();

    public void logout(){

    }

    public void removeCurrentFragment(String tag){
        FragmentTransaction ft = mContext.getSupportFragmentManager().beginTransaction();
        Fragment prev = mContext.getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            Log.d(TAG, "showFragment: remove prev...." + prev.getClass().getSimpleName());
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        ft.commit(); // or ft.commitAllowingStateLoss()
    }


    public void onBackPressed(){
        if(getDialog() != null)dismiss();
        else mContext.onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;// to prevent memory leak
    }

    public void dismissOrderDialog(){
        if(requireActivity() instanceof DialogActivity){
            requireActivity().finish();
        }else{
            dismiss();
        }
    }

    public void gotoNextActivity(Order order){
        OrderStatus orderStatus = OrderStatus.getStatus(order.getOrderStatusId());
        if(orderStatus == null) return;
        switch (orderStatus){
            case ORDER_RECEIVED:
            case ORDER_READY:
                AcceptOrderDialog.newInstance(new Gson().toJson(order)).show(mContext.getSupportFragmentManager(), AcceptOrderDialog.class.getName());
                removeCurrentFragment(AcceptOrderDialog.class.getName());
                break;
            case DELIVERY_GUY_ASSIGNED://On the way to pickup you order from restaurant
            case ORDER_READY_AND_DELIVERY_ASSIGNED://On the way to pickup you order from restaurant
                ReachDirectionFragment.newInstance(order, false).show(mContext.getSupportFragmentManager(), ReachDirectionFragment.class.getName());
                removeCurrentFragment(ReachDirectionFragment.class.getName());
                break;
            case ORDER_READY_AND_DELIVERY_REACHED_TO_PICKUP:
            case REACHED_PICKUP_LOCATION:
                PickOrderFragment.newInstance(order).show(mContext.getSupportFragmentManager(), PickOrderFragment.class.getName());
                removeCurrentFragment(PickOrderFragment.class.getName());
                break;
            case ON_THE_WAY:// Order is pickedUp and is on its way to deliver [PICKED]
                ReachDirectionFragment.newInstance(order, true).show(mContext.getSupportFragmentManager(), ReachDirectionFragment.class.getName());
                removeCurrentFragment(ReachDirectionFragment.class.getName());
                break;
            case REACHED_DROP_LOCATION:
                DeliverOrderFragment.newInstance(order).show(mContext.getSupportFragmentManager(), DeliverOrderFragment.class.getName());
                removeCurrentFragment(DeliverOrderFragment.class.getName());
                break;
            case DELIVERED:
                TripDetailsFragment.newInstance(order).show(mContext.getSupportFragmentManager(), TripDetailsFragment.class.getName());
                removeCurrentFragment(TripDetailsFragment.class.getName());
                break;
            case CANCELED:
                break;
        }

    }


}
