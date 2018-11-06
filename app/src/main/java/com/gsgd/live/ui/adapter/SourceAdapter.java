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
 * 源列表
 */
public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.ViewHolder> {

    private Context mContext;
    private List<String> sources;
    private int selectPosition;//选择的位置
    private OnItemListener mListener;

    public SourceAdapter(Context context, List<String> sources, int selectPosition, OnItemListener listener) {
        this.mContext = context;
        this.sources = sources;
        this.selectPosition = selectPosition;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_source, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTvSource.setText("视频源" + (position + 1));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onItemClick(position);
                }
            }
        });

        if (position == selectPosition) {
            //是当前的
//            holder.mTvSource.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.mipmap.ic_gou), null, null, null);
//            holder.itemView.setBackgroundResource(R.drawable.bg_source_s_select);

        } else {
            //不是当前的
//            holder.mTvSource.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//            holder.itemView.setBackgroundResource(R.drawable.bg_source_select);
        }
    }

    @Override
    public int getItemCount() {
        return null == sources ? 0 : sources.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_source)
        TextView mTvSource;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
