package com.pureeats.driverapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.pureeats.driverapp.databinding.ItemLoginSessionBinding;
import com.pureeats.driverapp.databinding.ItemTripBinding;
import com.pureeats.driverapp.models.Order;
import com.pureeats.driverapp.models.response.LoginHistory;
import com.pureeats.driverapp.models.response.TripDetails;


public class LoginHistoryAdapter extends ListAdapter<LoginHistory, LoginHistoryAdapter.OrderViewHolder> {


    public LoginHistoryAdapter() {
        super(LoginHistory.itemCallback);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemLoginSessionBinding itemBinding = ItemLoginSessionBinding.inflate(layoutInflater, parent, false);
        //itemAcceptOrderBinding.setOrderInterface(orderInterface);
        return new OrderViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        LoginHistory loginHistory = getItem(position);
        holder.itemBinding.setLoginHistory(loginHistory);
        holder.itemBinding.executePendingBindings();




    }

    class OrderViewHolder extends RecyclerView.ViewHolder{
        ItemLoginSessionBinding itemBinding ;

        public OrderViewHolder(ItemLoginSessionBinding binding) {
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
