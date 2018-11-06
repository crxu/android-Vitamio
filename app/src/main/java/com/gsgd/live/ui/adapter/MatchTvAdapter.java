package com.gsgd.live.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gsgd.live.data.listener.OnItemListener;
import com.gsgd.live.data.model.Channel;
import com.gsgd.live.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import hdpfans.com.R;

/**
 * 列表
 */
public class MatchTvAdapter extends RecyclerView.Adapter<MatchTvAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Channel> data;
    private OnItemListener mListener;

    public MatchTvAdapter(Context context, ArrayList<Channel> data, OnItemListener listener) {
        this.mContext = context;
        this.data = data;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_match_tv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Channel channel = data.get(position);

        holder.mTvChannelNum.setText(Utils.getChannelId(channel.id));
        holder.tvChannelName.setText(channel.channel);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == data ? 0 : data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_channel_num)
        TextView mTvChannelNum;
        @BindView(R.id.tv_channel_name)
        TextView tvChannelName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
