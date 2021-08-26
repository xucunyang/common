package com.yang.me.lib.ui.vh;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yang.me.lib.data.bean.BaseBean;

public abstract class BaseViewHolder<T extends BaseBean> extends RecyclerView.ViewHolder {
    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void onBindVH(int position, T bean);

}
