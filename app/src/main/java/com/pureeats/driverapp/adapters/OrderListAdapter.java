package com.pureeats.driverapp.adapters;

import androidx.annotation.NonNull;

import com.pureeats.driverapp.R;
import com.pureeats.driverapp.databinding.ItemLoginSessionBinding;
import com.pureeats.driverapp.databinding.ItemOrderHistoryBinding;
import com.pureeats.driverapp.models.response.LoginHistory;
import com.pureeats.driverapp.models.response.OrderDetailsView;

import java.util.List;

public class OrderListAdapter extends BaseAdapter<OrderDetailsView, ItemOrderHistoryBinding> {
    public OrderListAdapter(List<OrderDetailsView> items) {
        super(items);
    }

    @Override
    public int getLayout() {
        return R.layout.item_order_history;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemOrderHistoryBinding> holder, int position) {
        holder.binding.setOrderDetails(items.get(position));
    }
}
