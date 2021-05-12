package com.pureeats.driverapp.adapters;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.pureeats.driverapp.R;
import com.pureeats.driverapp.databinding.ItemAcceptedOrderBinding;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.utils.SafeClickListener;
import com.pureeats.driverapp.views.order.DialogActivity;

import java.util.List;

public class AcceptedOrderListAdapter extends BaseAdapter<Order, ItemAcceptedOrderBinding>{
    private String TAG = getClass().getSimpleName();
    private ItemClickListener listener;

    public AcceptedOrderListAdapter(List<Order> items) {
        super(items);
    }
    public AcceptedOrderListAdapter(List<Order> items, ItemClickListener listener) {
        super(items);
        this.listener = listener;
    }

    @Override
    public int getLayout() {
        return R.layout.item_accepted_order;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemAcceptedOrderBinding> holder, int position) {
        holder.binding.setOrder(items.get(position));

        if (listener != null){
            Log.d(TAG, "Goto Process Order Activity...................");
            holder.binding.cardView.setOnClickListener(new SafeClickListener(2000) {
                @Override
                public void onSafeClick(View view) {
                    listener.OnClickItem(items.get(position));
                }
            });
        }


    }

    private void processOrder(){
        /*
        int orderStatus = order.getOrderStatusId();
        Log.d(TAG, "ORDER: " + order.getId() + ", "+ order.getUniqueOrderId());
        Log.d(TAG, "ORDER_STATUS: " + order.getOrderStatusId());

        String orderJson = new Gson().toJson(order);

        Intent intent = new Intent(getActivity(), ProcessOrderActivityDialog.class);
        intent.putExtra(ProcessOrderActivityDialog.ORDER_REQUEST, orderJson);

        // Order Accepted(3); Restaurant Mark the order Ready(7); ReadyToPickUp(10)
        if(orderStatus == OrderStatus.DELIVERY_GUY_ASSIGNED.value() || orderStatus == OrderStatus.ORDER_READY.value() || orderStatus == OrderStatus.ORDER_READY_TO_PICKUP.value()){
            // Driver want to go to PickupLocation OR Driver want to go to Deliver location
            // start ReachDirectionFragment
            intent.setAction(Actions.REACH_DIRECTION_FRAGMENT.name());
            startActivity(intent);

        }else if(orderStatus == OrderStatus.ON_THE_WAY.value()){
            // start DeliverOrderFragment
            intent.setAction(Actions.DELIVER_ORDER_FRAGMENT.name());
            startActivity(intent);
        }
        */

    }

    public interface ItemClickListener{
        void OnClickItem(Order order);
    }

}
