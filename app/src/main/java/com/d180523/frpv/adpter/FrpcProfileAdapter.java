package com.d180523.frpv.adpter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.d180523.frpv.R;
import com.d180523.frpv.net.bean.FrpcBean;
import com.d180523.frpv.utils.AppUtils;

import java.util.List;

/**
 * @author mxy
 * @time 2018-05-28
 */
public class FrpcProfileAdapter extends RecyclerView.Adapter<FrpcProfileAdapter.MyViewHolder> {

    private static final String TAG = "FrpcProfileAdapter";

    private final List<FrpcBean> mListData;

    private final Fragment mFragment;

    //是否是在线
    private boolean mIsOnline = false;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FrpcProfileAdapter(Fragment fragment, List<FrpcBean> mListData, String type) {
        this.mFragment = fragment;
        this.mListData = mListData;
        this.mIsOnline = "在线".equals(type);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext().getApplicationContext();
        if (mIsOnline) {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_online_datas, parent, false));
        } else {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_local_datas, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final FrpcBean item = mListData.get(position);
        holder.item_name.setText(item.getName());

        if (mIsOnline) {
            holder.item_note.setText(item.getNote());
            final boolean isExist = AppUtils.isExist(mFragment.getActivity(), item.getName());
            if (!isExist) {
                holder.item_exist.setText("下载");
                holder.item_exist.setTextColor(mFragment.getActivity().getResources().getColor(R.color.colorAccent));
            } else {
                holder.item_exist.setText("已下载");
                holder.item_exist.setTextColor(mFragment.getActivity().getResources().getColor(R.color.gray));
            }

            holder.item_exist.setOnClickListener(new View.OnClickListener() {//item监听
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && !isExist) {
                        itemClickListener.onItemClick(item);
                    }
                }
            });
        } else {
            holder.item_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(item);
                    }
                }
            });
            holder.item_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.delClick(item);
                    }
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return mListData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView item_name;
        TextView item_note;
        TextView item_exist;
        TextView item_del;

        public MyViewHolder(final View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            item_note = itemView.findViewById(R.id.item_note);
            item_exist = itemView.findViewById(R.id.item_exist);
            item_del = itemView.findViewById(R.id.tv_del);
        }
    }

    //点击事件监听
    public interface ItemClickListener {
        void onItemClick(FrpcBean frpcBean);

        void delClick(FrpcBean frpcBean);
    }
}
