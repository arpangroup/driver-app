package com.pureeats.driverapp.adapters;

import androidx.annotation.NonNull;

import com.pureeats.driverapp.R;
import com.pureeats.driverapp.databinding.ItemDishBinding;
import com.pureeats.driverapp.databinding.ItemOrderHistoryBinding;
import com.pureeats.driverapp.models.Dish;
import com.pureeats.driverapp.models.response.OrderDetailsView;

import java.util.List;

public class DishListAdapter extends BaseAdapter<Dish, ItemDishBinding> {
    public DishListAdapter(List<Dish> items) {
        super(items);
    }

    @Override
    public int getLayout() {
        return R.layout.item_dish;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemDishBinding> holder, int position) {
        holder.binding.setDish(items.get(position));
    }
}
