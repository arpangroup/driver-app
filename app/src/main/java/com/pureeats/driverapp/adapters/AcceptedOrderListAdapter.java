package com.pureeats.driverapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.pureeats.driverapp.databinding.ItemOrderBinding;
import com.pureeats.driverapp.models.Order;


public class AcceptedOrderListAdapter extends ListAdapter<Order, AcceptedOrderListAdapter.OrderViewHolder> {
    ItemClickInterface itemClickInterface;


    public AcceptedOrderListAdapter(ItemClickInterface itemClickInterface) {
        super(Order.itemCallback);
        this.itemClickInterface = itemClickInterface;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemOrderBinding itemBinding = ItemOrderBinding.inflate(layoutInflater, parent, false);
        //itemAcceptOrderBinding.setOrderInterface(orderInterface);
        return new OrderViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = getItem(position);
        holder.itemBinding.setOrder(order);

        holder.itemBinding.executePendingBindings();




    }

    class OrderViewHolder extends RecyclerView.ViewHolder{
        ItemOrderBinding itemBinding ;

        public OrderViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;

            itemBinding.getRoot().setOnClickListener(view -> {
                itemClickInterface.onItemVClick(getItem(getAdapterPosition()));
            });
        }
    }
    public interface OrderHistoryInterface {
        void onOrderHistoryClick(Order order);
    }

}
