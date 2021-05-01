package com.pureeats.driverapp.adapters;

import androidx.annotation.NonNull;

import com.pureeats.driverapp.R;
import com.pureeats.driverapp.databinding.ItemOrderHistoryBinding;
import com.pureeats.driverapp.databinding.ItemTripBinding;
import com.pureeats.driverapp.models.response.OrderDetailsView;
import com.pureeats.driverapp.models.response.TripDetails;

import java.util.List;

public class TripListAdapter extends BaseAdapter<TripDetails, ItemTripBinding> {
    public TripListAdapter(List<TripDetails> items) {
        super(items);
    }

    @Override
    public int getLayout() {
        return R.layout.item_login_session;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemTripBinding> holder, int position) {
        holder.binding.setTripDetails(items.get(position));
    }
}
