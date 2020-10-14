package com.example.driverapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.driverapp.databinding.ItemEarningBinding;
import com.example.driverapp.databinding.ItemOrderBinding;
import com.example.driverapp.models.Earning;
import com.example.driverapp.models.Order;
import com.example.driverapp.models.response.OrderDetailsView;


public class EarningListAdapter extends ListAdapter<Earning, EarningListAdapter.OrderViewHolder> {


    public EarningListAdapter() {
        super(Earning.itemCallback);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemEarningBinding itemBinding = ItemEarningBinding.inflate(layoutInflater, parent, false);
        //itemAcceptOrderBinding.setOrderInterface(orderInterface);
        return new OrderViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Earning earning = getItem(position);
        holder.itemBinding.setEarning(earning);

        holder.itemBinding.executePendingBindings();




    }

    class OrderViewHolder extends RecyclerView.ViewHolder{
        ItemEarningBinding itemBinding ;

        public OrderViewHolder(ItemEarningBinding binding) {
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
