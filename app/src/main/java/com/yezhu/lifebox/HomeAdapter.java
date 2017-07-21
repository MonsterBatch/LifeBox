package com.yezhu.lifebox;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.morgoo.droidplugin.pm.PluginManager;

import java.util.List;

/**
 * Created by yuyifei on 17-7-6.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private final Context mContext;
    private List<AppInfo> mAppInfoList;
    private LayoutInflater mInflater;

    private final int MAX_APP_NUMBER = 9;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


    public HomeAdapter(Context context, List<AppInfo> appInfoList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mAppInfoList = appInfoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(mInflater.inflate(
                R.layout.item_home, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        AppInfo item = mAppInfoList.get(position);

        holder.tv.setText(item.getmAppName());
        holder.iv.setImageDrawable(item.getmIcon());

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
                    removeData(pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mAppInfoList.size();
    }

    public void addData(int position, AppInfo appInfo) {
        if (mAppInfoList.size() >= MAX_APP_NUMBER) {
            Toast.makeText(mContext, "警告！目前仅能安装"+ MAX_APP_NUMBER + "个应用！",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAppInfoList.add(position, appInfo);
        notifyItemInserted(position);
    }


    public void removeData(final int position) {
        DialogInterface.OnClickListener dialogOnclicListener =
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case Dialog.BUTTON_POSITIVE:

                                try {
                                    PluginManager.getInstance().deletePackage(
                                            mAppInfoList.get(position).getmPackageName(), 0);
                                    mAppInfoList.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(mContext, "删除应用成功", Toast.LENGTH_SHORT).show();
                                } catch (RemoteException e) {
                                    Toast.makeText(mContext, "删除应用失败", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("警告");
        builder.setMessage("是否删除应用?");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("删除", dialogOnclicListener);
        builder.setNegativeButton("取消", dialogOnclicListener);
        builder.create().show();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tv;

        public MyViewHolder(View view) {
            super(view);

            iv = (ImageView) view.findViewById(R.id.id_app_icon);
            tv = (TextView) view.findViewById(R.id.id_app_name);
        }
    }
}