package com.pureeats.driverapp.adapters;

import androidx.annotation.NonNull;

import com.pureeats.driverapp.R;
import com.pureeats.driverapp.databinding.ItemDishBinding;
import com.pureeats.driverapp.databinding.ItemEarningBinding;
import com.pureeats.driverapp.models.Dish;
import com.pureeats.driverapp.models.Earning;

import java.util.List;

public class EarningListAdapter extends BaseAdapter<Earning, ItemEarningBinding> {
    public EarningListAdapter(List<Earning> items) {
        super(items);
    }

    @Override
    public int getLayout() {
        return R.layout.item_dish;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemEarningBinding> holder, int position) {
        holder.binding.setEarning(items.get(position));
    }
}
