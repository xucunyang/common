package com.yang.me.lib.ui.vh;

import android.view.ViewGroup;

import com.yang.me.lib.R;
import com.yang.me.lib.data.bean.SingleSelectBean;
import com.yang.me.lib.databinding.ItemNameSelectBinding;

public class SingleSelectVH extends BaseAutoBindVH<ItemNameSelectBinding, SingleSelectBean> {
    public SingleSelectVH(ViewGroup parent) {
        super(R.layout.item_name_select, parent);
    }

    @Override
    public void onBindVH(int i, SingleSelectBean baseBean) {
        SingleSelectBean bean = castBean(baseBean);

        setText(mViewBinding.itemName, bean.getItemName());

        mViewBinding.select.setImageResource(bean.isDefaultSelect() ? android.R.color.background_dark : android.R.color.darker_gray);
    }
}
