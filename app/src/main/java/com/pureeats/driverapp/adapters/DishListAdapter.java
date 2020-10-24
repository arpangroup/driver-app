package com.pureeats.driverapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.pureeats.driverapp.databinding.ItemDishBinding;
import com.pureeats.driverapp.models.Dish;


public class DishListAdapter extends ListAdapter<Dish, DishListAdapter.DishViewHolder> {

    public DishListAdapter() {
        super(Dish.itemCallback);
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemDishBinding itemDish1Binding = ItemDishBinding.inflate(layoutInflater, parent, false);
        return new DishViewHolder(itemDish1Binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        Dish dish = getItem(position);
        holder.itemDish1Binding.setDish(dish);
    }

    class DishViewHolder extends RecyclerView.ViewHolder{
        ItemDishBinding itemDish1Binding ;

        public DishViewHolder(ItemDishBinding binding) {
            super(binding.getRoot());
            this.itemDish1Binding = binding;
        }
    }


}
