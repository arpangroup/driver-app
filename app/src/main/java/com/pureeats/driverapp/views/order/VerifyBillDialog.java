package com.pureeats.driverapp.views.order;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.pureeats.driverapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pureeats.driverapp.utils.CommonUtils;

import static android.app.Activity.RESULT_OK;

public class VerifyBillDialog extends BottomSheetDialogFragment {
    private final String TAG = this.getClass().getSimpleName();
    private static final int CAMERA_REQUEST = 600;
    private static final int GALLERY_REQUEST = 601;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private ImageButton btnClose;
    private Button btnClickNoBill, btnClickPhoto;
    private ImageView imageview;

    private OnPhotoClickListener mPhotoClickedListener;
    private OnClickListener mNoBillClickedListener;

   public void setPhotoClickListener(final OnPhotoClickListener listener){
       mPhotoClickedListener = listener;
   }
    public void setNoBillClickListener(final OnClickListener listener){
        mNoBillClickedListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        final View rootView = View.inflate(getContext(), R.layout.layout_dialog_photo_of_bill, null);
        dialog.setContentView(rootView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View)rootView.getParent());
        //bottomSheetBehavior.setPeekHeight(screenUtils.getHeight());
        bottomSheetBehavior.setDraggable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_dialog_photo_of_bill, container, false);

        btnClose = rootView.findViewById(R.id.btnClose);
        btnClickNoBill = rootView.findViewById(R.id.btnClickNoBill);
        btnClickPhoto = rootView.findViewById(R.id.btnClickPhoto);
        imageview = rootView.findViewById(R.id.bill_photo);



        btnClose.setOnClickListener(view -> dismiss());
        btnClickNoBill.setOnClickListener(view -> {
            //openGallery();
            if(mNoBillClickedListener != null) mNoBillClickedListener.onClick(this, 1);
            dismiss();
        });
        btnClickPhoto.setOnClickListener(view -> openCamera());

        return rootView;
    }



    private void openCamera(){
        if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            return;
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void openGallery(){
        if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            return;
        }
        // Choose Imgae from Gallery:
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , GALLERY_REQUEST);//one can be replaced with any action code
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        System.out.println("################Inside onActivityResult");
        Log.d(TAG, "onActivityResult........");
        switch (requestCode){
            case CAMERA_REQUEST:
                if(resultCode == RESULT_OK){
                    //Uri selectedImage = imageReturnedIntent.getData();
                    //imageview.setImageURI(selectedImage);

                    Bitmap selectedImage = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    imageview.setImageBitmap(selectedImage);

                    String image64Str = CommonUtils.convertToBase64(selectedImage);

                    if(mPhotoClickedListener != null){
                        mPhotoClickedListener.onClick(this, image64Str);
                        dismiss();
                    }
                }
                break;
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageview.setImageURI(selectedImage);

                    /*
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    if (selectedImage != null) {
                        Cursor cursor = requireActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            imageview.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                            cursor.close();
                        }
                    }
                    */
                }
                break;
        }
    }


    public static class Builder {
        private Context context;
        private String title;
        private OnPhotoClickListener mPhotoClickedListener;
        private OnClickListener mNoBillClickedListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title){
            this.setTitle(title);
            return this;
        }

        public Builder setPhotoClickListener(final OnPhotoClickListener listener){
            this.mPhotoClickedListener = listener;
            return this;
        }
        public Builder setNoBillClickListener(final OnClickListener listener){
            this.mNoBillClickedListener = listener;
            return this;
        }

        public void show(){
            VerifyBillDialog dialog = new VerifyBillDialog();
            dialog.setPhotoClickListener(mPhotoClickedListener);
            dialog.setNoBillClickListener(mNoBillClickedListener);
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            dialog.show(fragmentManager, getClass().getName());
        }
    }


    public interface OnPhotoClickListener {
        void onClick(VerifyBillDialog dialog, String base64EnodedText);
    }
    public interface OnClickListener {
        void onClick(VerifyBillDialog dialog, int res);
    }
}
