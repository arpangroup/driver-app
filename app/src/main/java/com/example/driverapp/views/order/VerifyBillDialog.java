package com.example.driverapp.views.order;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.driverapp.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class VerifyBillDialog extends BottomSheetDialogFragment {
    public interface VerifyBillDialogListener {
        public void onClickPhotoOfBill();
        public void onClickHaveNoBill();
    }

    private final String TAG = this.getClass().getSimpleName();
    private VerifyBillDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        final View rootView = View.inflate(getContext(), R.layout.layout_dialog_photo_of_bill, null);
        dialog.setContentView(rootView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View)rootView.getParent());
        //bottomSheetBehavior.setPeekHeight(screenUtils.getHeight());
        bottomSheetBehavior.setDraggable(false);

        /*
        final View view = View.inflate(getContext(), R.layout.layout_location_search, null);
        dialog.setContentView(view);
        BottomSheetBehavior bottomSheetBehavior  =  BottomSheetBehavior.from((View)view.getParent());
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(bottomSheetBehavior.STATE_EXPANDED == newState){
                    showView(appBarLayout, getActionBarSize());
                    hideView(linearLayout);
                }

                if(bottomSheetBehavior.STATE_COLLAPSED == newState){
                    hideView(appBarLayout);
                    showView(linearLayout, getActionBarSize());
                }

                if(bottomSheetBehavior.STATE_HIDDEN == newState){
                   dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
         */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_photo_of_bill, container, false);

        ImageButton btnClose = rootView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(view -> dismiss());

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mListener = (VerifyBillDialogListener)getParentFragment();
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement VerifyBillDialogListener");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();
            // ...customize your dim effect here

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }

}
