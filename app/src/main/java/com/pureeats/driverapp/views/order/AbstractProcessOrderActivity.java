package com.pureeats.driverapp.views.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;

import com.google.gson.Gson;
import com.pureeats.driverapp.R;
import com.pureeats.driverapp.commons.Actions;
import com.pureeats.driverapp.commons.Constants;
import com.pureeats.driverapp.commons.OrderStatus;
import com.pureeats.driverapp.models.Order;

public abstract class AbstractProcessOrderActivity extends AppCompatActivity {
    private static final String TAG = "ProcessOrderActivity";
    protected NavController navController;
    private String mUniqueOrderId;
    private int mOrderId;

    private static final Class<?> TARGET_ACTIVITY = DialogActivity.class;


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive........");
            Log.d(TAG, "mOrderID: " + mOrderId);
            int orderId = intent.getIntExtra(Constants.STR_ORDER_ID, -1);
            String uniqueOrderId = intent.getStringExtra(Constants.STR_UNIQUE_ORDER_ID);
            String actionName = intent.getAction();
            String title = intent.getStringExtra(Constants.STR_TITLE);
            String message = intent.getStringExtra(Constants.STR_MESSAGE);
            Log.d(TAG, "ORDER_ID: " + orderId + ", ACTION: "+ actionName +", UNIQUE_ORDER_ID: " + uniqueOrderId );
            Actions action = Actions.getAction(actionName);
            Log.d(TAG, "CURRENT_FRAGMENT_ACTION: " + getCurrentFragmentName());


            if(orderId != -1 && action != null && uniqueOrderId.equals(mUniqueOrderId) ){
                boolean canCloseActivity = false;
                Actions currentFragment = getCurrentFragmentName();

                switch (action){
                    case ORDER_TRANSFERRED:
                    case ORDER_CANCELLED:
                        /**
                         * If order transferred, from any screen it will forefully close the curren scree
                         */
                        showAlert(title, message, true);
                        break;
                    case DELIVERY_ASSIGNED:
                        if(currentFragment == Actions.ORDER_ARRIVED){
                            /**
                             * Delivery Already assigned from other channel like either Admin or By Other DeliveryGuy
                             */
                            showAlert(title, message, true);
                        }
                        break;

                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart......");
        IntentFilter[] intentFilters = {
                new IntentFilter(Actions.DELIVERY_ASSIGNED.name()),
                new IntentFilter(Actions.ORDER_CANCELLED.name()),
                new IntentFilter(Actions.ORDER_TRANSFERRED.name())
        };

        Log.d(TAG, "REGISTERING_LOCAL_BROADCAST...");
        for (IntentFilter intentFilter : intentFilters) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop......");
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiver);
    }

    /**
     * This intent is mainly use to open from Notification
     * @param context
     * @param orderId
     * @param uniqueOrderId
     * @return
     */
    public static Intent getIntent(Context context, int orderId, String uniqueOrderId){
        Intent intent = new Intent(context, TARGET_ACTIVITY);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent);

        intent.setAction(Actions.ACCEPT_ORDER_FRAGMENT.name()); //mandatory
        intent.putExtra(Constants.STR_ORDER_ID, orderId);
        intent.putExtra(Constants.STR_UNIQUE_ORDER_ID, uniqueOrderId);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static void start(Context context, Order order){
        Log.d(TAG, "Inside start()...." + order.getId());
        Intent intent = new Intent(context, TARGET_ACTIVITY);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(intent);

        Actions action = Actions.REACH_PICKUP_LOCATION;
        switch (OrderStatus.getStatus(order.getOrderStatusId())){
            case ORDER_RECEIVED:
            case ORDER_READY:
                action = Actions.ACCEPT_ORDER_FRAGMENT;
                //AcceptOrderDialog.newInstance(order.getId(), order.getUniqueOrderId()).show(mContext.getSupportFragmentManager(), AcceptOrderDialog.class.getName());
                //removeCurrentFragment(AcceptOrderDialog.class.getName());
                break;
            case DELIVERY_GUY_ASSIGNED://On the way to pickup you order from restaurant
            case ORDER_READY_AND_DELIVERY_ASSIGNED://On the way to pickup you order from restaurant
                action = Actions.REACH_PICKUP_LOCATION;
                //ReachDirectionFragment.newInstance(order, false).show(mContext.getSupportFragmentManager(), ReachDirectionFragment.class.getName());
                //removeCurrentFragment(ReachDirectionFragment.class.getName());
                //removeCurrentFragment(AcceptOrderDialog.class.getName());
                break;
            case ORDER_READY_AND_DELIVERY_REACHED_TO_PICKUP:
            case REACHED_PICKUP_LOCATION:
                action = Actions.PICK_ORDER_FRAGMENT;
                //PickOrderFragment.newInstance(order).show(mContext.getSupportFragmentManager(), PickOrderFragment.class.getName());
                //removeCurrentFragment(PickOrderFragment.class.getName());
                //removeCurrentFragment(ReachDirectionFragment.class.getName());
                break;
            case ON_THE_WAY:// Order is pickedUp and is on its way to deliver [PICKED]
                action = Actions.REACH_DROP_LOCATION;
                //ReachDirectionFragment.newInstance(order, true).show(mContext.getSupportFragmentManager(), ReachDirectionFragment.class.getName());
                //removeCurrentFragment(PickOrderFragment.class.getName());
                break;
            case REACHED_DROP_LOCATION:
                action = Actions.DELIVER_ORDER_FRAGMENT;
                //DeliverOrderFragment.newInstance(order).show(mContext.getSupportFragmentManager(), DeliverOrderFragment.class.getName());
                //removeCurrentFragment(ReachDirectionFragment.class.getName());
                break;
            case DELIVERED:
                action = Actions.TRIP_DETAILS_FRAGMENT;
                //TripDetailsFragment.newInstance(order).show(mContext.getSupportFragmentManager(), TripDetailsFragment.class.getName());
                //removeCurrentFragment(DeliverOrderFragment.class.getName());
                break;
            case CANCELED:
                break;
        }

        intent.setAction(action.name());// mandatory
        intent.putExtra(Constants.STR_ORDER_JSON, new Gson().toJson(order));//mandatory
        intent.putExtra(Constants.STR_ORDER_ID, order.getId());//optional
        intent.putExtra(Constants.STR_UNIQUE_ORDER_ID, order.getUniqueOrderId());//optional



        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public void resolveDestination(Intent intent){
        Log.d(TAG, "######InsideResolveDestination...");
        Log.d(TAG, "ACTION: " + intent.getAction());
        Actions action = Actions.getAction(intent.getAction());
        if(action == null || navController == null) return;

        mOrderId = intent.getIntExtra(Constants.STR_ORDER_ID, -1);
        mUniqueOrderId = intent.getStringExtra(Constants.STR_UNIQUE_ORDER_ID);
        Log.d(TAG, "ORDER_ID: " + mOrderId);
        Log.d(TAG, "UNIQUE_ORDER_ID: " + mUniqueOrderId);

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.STR_ORDER_ID, mOrderId);
        bundle.putString(Constants.STR_UNIQUE_ORDER_ID,  mUniqueOrderId);
        bundle.putString(Constants.STR_ORDER_JSON, intent.getStringExtra(Constants.STR_ORDER_JSON));

        switch (action){
            case ACCEPT_ORDER_FRAGMENT:
                navController.navigate(R.id.action_loaderFragment_to_acceptOrderFragment, bundle);
                break;
            case REACH_PICKUP_LOCATION:
                bundle.putBoolean(Constants.STR_IS_ORDER_PICKED, false);
                navController.navigate(R.id.action_loaderFragment_to_reachDirectionFragment, bundle);
                break;
            case PICK_ORDER_FRAGMENT:
                navController.navigate(R.id.action_loaderFragment_to_pickOrderFragment, bundle);
                break;
            case REACH_DROP_LOCATION:
                bundle.putBoolean(Constants.STR_IS_ORDER_PICKED, true);
                navController.navigate(R.id.action_loaderFragment_to_reachDirectionFragment, bundle);
                break;
            case DELIVER_ORDER_FRAGMENT:
                navController.navigate(R.id.action_loaderFragment_to_deliverOrrderFragment, bundle);
                break;
            default:
                navController.navigate(R.id.action_loaderFragment_to_acceptOrderFragment, bundle);
        }
    }
    private void showAlert(String title, String message, boolean shouldCloseActivity){
        new android.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                   if (shouldCloseActivity) finish();
                }).show();
    }

    private Fragment getForegroundFragment(){
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragmentProcessOrder);
        return navHostFragment == null ? null : navHostFragment.getChildFragmentManager().getFragments().get(0);
    }

    private Actions getCurrentFragmentName(){
        Fragment fragment = getForegroundFragment();
        Actions action = null;
        if(fragment != null){
            String fragmentName = getForegroundFragment().getClass().getName();
            Log.d(TAG, "FRAGMENT_NAME: " + fragmentName);
            String packageName = this.getPackageName();
            Log.d(TAG, "PACKAGE_NAME: " + packageName);

            Log.d(TAG, "ORDER_ARRIVE_FRAGMENT: " + Constants.ORDER_ARRIVE_FRAGMENT);
            Log.d(TAG, "REACH_DIRECTION_FRAGMENT: " + Constants.REACH_DIRECTION_FRAGMENT);
            Log.d(TAG, "PICK_ORDER_FRAGMENT: " + Constants.PICK_ORDER_FRAGMENT);
            Log.d(TAG, "DELIVER_ORDER_FRAGMENT: " + Constants.DELIVER_ORDER_FRAGMENT);
            Log.d(TAG, "TRIP_DETAILS_FRAGMENT: " + Constants.TRIP_DETAILS_FRAGMENT);

            if(Constants.ORDER_ARRIVE_FRAGMENT.equalsIgnoreCase(fragmentName)){
                action = Actions.ORDER_ARRIVED;
            }else if(Constants.REACH_DIRECTION_FRAGMENT.equalsIgnoreCase(fragmentName)){
                action = Actions.REACH_PICKUP_LOCATION;
            }else if(Constants.PICK_ORDER_FRAGMENT.equalsIgnoreCase(fragmentName)){
                action = Actions.PICK_ORDER_FRAGMENT;
            }else if(Constants.DELIVER_ORDER_FRAGMENT.equalsIgnoreCase(fragmentName)){
                action = Actions.DELIVER_ORDER_FRAGMENT;
            }else if(Constants.TRIP_DETAILS_FRAGMENT.equalsIgnoreCase(fragmentName)){
                action = Actions.TRIP_DETAILS_FRAGMENT;
            }else{
                action = null;
            }
        }
        return action;
    }


}
