package com.pureeats.driverapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.pureeats.driverapp.databinding.ItemTripBinding;
import com.pureeats.driverapp.models.Earning;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.models.response.TripDetails;


public class TripListAdapter extends ListAdapter<TripDetails, TripListAdapter.OrderViewHolder> {


    public TripListAdapter() {
        super(TripDetails.itemCallback);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemTripBinding itemBinding = ItemTripBinding.inflate(layoutInflater, parent, false);
        //itemAcceptOrderBinding.setOrderInterface(orderInterface);
        return new OrderViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        TripDetails tripDetails = getItem(position);
        holder.itemBinding.setTripDetails(tripDetails);
        holder.itemBinding.executePendingBindings();




    }

    class OrderViewHolder extends RecyclerView.ViewHolder{
        ItemTripBinding itemBinding ;

        public OrderViewHolder(ItemTripBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;

            itemBinding.getRoot().setOnClickListener(view -> {
                //itemClickInterface.onItemVClick(null);
            });
        }
    }
    public interface OrderHistoryInterface {
        void onOrderHistoryClick(Order order);
    }

}
