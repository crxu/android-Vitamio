package com.gsgd.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hdpfans.com.R;
import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.model.ChannelType;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 栏目列表
 */
public class ChannelTypeAdapter extends RecyclerView.Adapter<ChannelTypeAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ChannelType> data;
    private OnItemListener mListener;

    public ChannelTypeAdapter(Context context, OnItemListener listener) {
        mContext = context;
        this.mListener = listener;
    }

    public void setData(ArrayList<ChannelType> data) {
        this.data = data;
        try {
            notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_channel_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvChannelName.setText(data.get(position).type);

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
                holder.itemView.setBackgroundResource(R.drawable.bg_channel_select);
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

        @BindView(R.id.tv_channel_name)
        TextView tvChannelName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
