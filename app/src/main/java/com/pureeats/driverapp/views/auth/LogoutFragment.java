package com.pureeats.driverapp.views.auth;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.databinding.FragmentOfferBinding;
import com.pureeats.driverapp.services.FetchOrderService;
import com.pureeats.driverapp.sharedprefs.ServiceTracker;
import com.pureeats.driverapp.viewmodels.AuthenticationViewModelOld;
import com.pureeats.driverapp.viewmodels.LocationViewModel;

public class LogoutFragment extends AppCompatDialogFragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentOfferBinding mBinding;
    AuthenticationViewModelOld authViewModel;
    LocationViewModel locationViewModel;
    NavController navController;
    private Button btnYes, btnNo;

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//
//        return dialog;
//    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_logout, container, false);


        // Initialize ViewModel
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModelOld.class);
        authViewModel.init();

        btnYes = rootView.findViewById(R.id.btnYes);
        btnNo = rootView.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(view -> {
            authViewModel.logout();
            try {
                Intent intentService = new Intent(requireActivity(), FetchOrderService.class);
                intentService.setAction(Actions.STOP.name());
                System.out.println("Trying to run the service");
                ContextCompat.startForegroundService(requireActivity(), intentService);
            }catch (Exception e){
                e.printStackTrace();
            }

            ServiceTracker.setServiceState(requireActivity(), ServiceTracker.ServiceState.STOPPED);
            Intent intent = new Intent(requireActivity(), AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityCompat.finishAffinity(requireActivity());


            startActivity(intent);
        });
        btnNo.setOnClickListener(view -> {
            dismiss();
        });

        return rootView;

    }
}