package com.pureeats.driverapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


/**
 * Created by Arpan Jana on 21/12/2020
 * Base adapter to be extended by all the recycler view adapters.
 */

public abstract class BaseAdapter<T, VB extends ViewDataBinding> extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder<VB>>{
    protected List<T> items;

    public BaseAdapter(List<T> items){
        this.items = items;
    }

    public void updateAll(List<T> items){
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items == null || items.size() ==0? 0 : items.size();
    }

    public abstract int getLayout();

    @NonNull
    @Override
    public BaseViewHolder<VB> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder<VB>(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        getLayout(),
                        parent,
                        false
                )
        );
    }

    public static class BaseViewHolder<VB extends ViewDataBinding> extends RecyclerView.ViewHolder{
        public VB binding;
        public BaseViewHolder(VB binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}

