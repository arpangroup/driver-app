package com.pureeats.driverapp.adapters;

import androidx.annotation.NonNull;

import com.pureeats.driverapp.R;
import com.pureeats.driverapp.databinding.ItemLoginSessionBinding;
import com.pureeats.driverapp.models.response.LoginHistory;

import java.util.List;

public class LoginHistoryAdapter extends BaseAdapter<LoginHistory, ItemLoginSessionBinding> {
    public LoginHistoryAdapter(List<LoginHistory> items) {
        super(items);
    }

    @Override
    public int getLayout() {
        return R.layout.item_login_session;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<ItemLoginSessionBinding> holder, int position) {
        holder.binding.setLoginHistory(items.get(position));
    }
}
