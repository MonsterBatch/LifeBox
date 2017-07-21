package com.yezhu.lifebox.apkmanager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.morgoo.droidplugin.pm.PluginManager;
import com.yezhu.lifebox.R;

import java.util.List;

/**
 * Created by yuyifei on 17-7-6.
 */

class ApkAdapter extends RecyclerView.Adapter<ApkAdapter.ApkViewHolder> {
    private final Context mContext;
    private final LayoutInflater mInflater;

    private List<ApkInfo> mApkInfoList;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


    public ApkAdapter(Context context, List<ApkInfo> apkInfoList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mApkInfoList = apkInfoList;
    }

    @Override
    public ApkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ApkAdapter.ApkViewHolder holder = new ApkAdapter.ApkViewHolder(mInflater.inflate(
                R.layout.apk_item, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(final ApkViewHolder holder, int position) {
        ApkInfo item = mApkInfoList.get(position);

        holder.tv.setText(item.getmAppName());
        holder.iv.setImageDrawable(item.getmIcon());

        try {
            boolean installed = PluginManager.getInstance().isPluginPackage(item.getmPackageName());
            if (installed) {
                holder.flag.setText("已安装");
                ColorStateList csl = mContext.getResources()
                        .getColorStateList(R.color.color_flag);
                holder.flag.setTextColor(csl);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mApkInfoList.size();
    }


    public class ApkViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tv;
        TextView flag;

        public ApkViewHolder(View view) {
            super(view);

            iv = (ImageView) view.findViewById(R.id.id_apk_icon);
            tv = (TextView) view.findViewById(R.id.id_apk_name);
            flag = (TextView) view.findViewById(R.id.id_flag);
        }
    }
}

