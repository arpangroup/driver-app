package com.example.driverapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.driverapp.databinding.ItemOrderBinding;
import com.example.driverapp.databinding.ItemOrderHistoryBinding;
import com.example.driverapp.models.Order;
import com.example.driverapp.models.response.OrderDetailsView;


public class OrderListAdapter extends ListAdapter<OrderDetailsView, OrderListAdapter.OrderViewHolder> {
    ItemClickInterface itemClickInterface;


    public OrderListAdapter(ItemClickInterface itemClickInterface) {
        super(OrderDetailsView.itemCallback);
        this.itemClickInterface = itemClickInterface;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemOrderHistoryBinding itemBinding = ItemOrderHistoryBinding.inflate(layoutInflater, parent, false);
        //itemAcceptOrderBinding.setOrderInterface(orderInterface);
        return new OrderViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderDetailsView order = getItem(position);
        holder.itemBinding.setOrderDetails(order);

        holder.itemBinding.executePendingBindings();




    }

    class OrderViewHolder extends RecyclerView.ViewHolder{
        ItemOrderHistoryBinding itemBinding ;

        public OrderViewHolder(ItemOrderHistoryBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;

            itemBinding.getRoot().setOnClickListener(view -> {
                itemClickInterface.onItemVClick(null);
            });
        }
    }
    public interface OrderHistoryInterface {
        void onOrderHistoryClick(Order order);
    }

}
