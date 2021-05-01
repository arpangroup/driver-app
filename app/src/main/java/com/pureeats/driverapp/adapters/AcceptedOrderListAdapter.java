package com.pureeats.driverapp.adapters;

import androidx.annotation.NonNull;

import com.pureeats.driverapp.R;
import com.pureeats.driverapp.databinding.ItemAcceptedOrderBinding;
import com.pureeats.driverapp.models.Order;

import java.util.List;

public class AcceptedOrderListAdapter extends BaseAdapter<Order, ItemAcceptedOrderBinding>{

    public AcceptedOrderListAdapter(List<Order> items) {
        super(items);
    }

    @Override
    public int getLayout() {
        return R.layout.item_accepted_order;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemAcceptedOrderBinding> holder, int position) {
        holder.binding.setOrder(items.get(position));
    }
}
