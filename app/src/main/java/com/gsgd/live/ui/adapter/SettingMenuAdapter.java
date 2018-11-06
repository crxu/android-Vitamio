package com.gsgd.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hdpfans.com.R;
import com.gsgd.live.data.listener.OnItemListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 设置菜单列表
 */
public class SettingMenuAdapter extends RecyclerView.Adapter<SettingMenuAdapter.ViewHolder> {

    private Context mContext;
    private List<String> data;
    private OnItemListener mListener;
    private int type;

    public SettingMenuAdapter(Context context, OnItemListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public void setData(List<String> data) {
        this.data = data;
        try {
            notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_setting_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTvMenu.setText(data.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onItemClick(position);
                }
            }
        });

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (null != mListener) {
                    mListener.onItemFocus(position, hasFocus);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == data ? 0 : data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_menu)
        TextView mTvMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
