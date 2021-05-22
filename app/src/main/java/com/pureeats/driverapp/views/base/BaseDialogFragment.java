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

import com.pureeats.driverapp.network.datasource.RemoteDataSource;
import com.pureeats.driverapp.repositories.BaseRepository;
import com.pureeats.driverapp.sharedprefs.UserSession;
import com.pureeats.driverapp.viewmodels.BaseViewModel;
import com.pureeats.driverapp.viewmodels.ViewModelFactory;
import com.pureeats.driverapp.views.App;
import com.pureeats.driverapp.views.order.DialogActivity;


public abstract class BaseDialogFragment<VM extends BaseViewModel, B extends ViewBinding, R extends BaseRepository> extends DialogFragment {
    private static String TAG = "BaseDialogFragment";
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

    public void disableBackButton(){
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button even
                Log.d(TAG, "Back button clicks");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

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

}
