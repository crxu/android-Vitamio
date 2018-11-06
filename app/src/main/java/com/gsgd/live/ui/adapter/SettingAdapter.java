package com.gsgd.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hdpfans.com.R;
import com.gsgd.live.data.listener.OnItemListener;

import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 设置列表
 */
public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolder> {

    private Context mContext;
    private LinkedHashMap<String, String> data;
    private List<String> list;
    private OnItemListener mListener;

    public SettingAdapter(Context context, OnItemListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public void setData(LinkedHashMap<String, String> data, List<String> list) {
        this.data = data;
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_setting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTvSetting.setText(list.get(position));
        holder.mTvMode.setText(data.get(list.get(position)));

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
                holder.itemView.setBackgroundResource(R.drawable.bg_setting_m_select);
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

        @BindView(R.id.tv_setting)
        TextView mTvSetting;
        @BindView(R.id.tv_mode)
        TextView mTvMode;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
