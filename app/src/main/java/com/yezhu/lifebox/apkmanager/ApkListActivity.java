package com.yezhu.lifebox.apkmanager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.morgoo.droidplugin.pm.PluginManager;
import com.yezhu.lifebox.Constant;
import com.yezhu.lifebox.R;
import com.yezhu.lifebox.net.RemoteApkInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by yuyifei on 17-7-6.
 */

public class ApkListActivity extends AppCompatActivity {
    private static final String APK_STORE_DIR = "apkdata";
    private static final String PIC_SUFFIX = "ic_launcher.png";
    private static final String APK_SUFFIX = "base.apk";
    private RecyclerView mRecyclerView;
    private List<ApkInfo> mApkInfoList;
    private ApkAdapter mAdapter;
    private List<com.yezhu.lifebox.net.ApkInfo> list = null;
    private boolean dataFlag = false;
    private AlertDialog.Builder normalDia = null;
    private AlertDialog parentNormalDia = null;
    private ProgressDialog myDialog = null;

    public static final String UPDATE_MAIN_UI_ACTION = "update.ui.broadcast.action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_list);
        initData();
    }

    private void initData() {
        myDialog=ProgressDialog.show(ApkListActivity.this,"友情提示","加载中......",true);
        new GetData().execute();
    }


    class GetData extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... params) {
            String response = "";
            while ((response = RemoteApkInfo.getRemoteApkInfo(RemoteApkInfo.BASE_URL + "ApkServlet", "")).equals("")){
                continue;
            }
            myDialog.dismiss();
            dataFlag = true;
            return response;

        }

        @Override
        protected void onPostExecute(String s) {
            mApkInfoList = new ArrayList<>();
            String dirPath = getFilesDir().getAbsolutePath() + File.separator + APK_STORE_DIR;
            File dirFile = new File(dirPath);
            // create apkdata dir if not
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            Gson gson = new Gson();
            list = gson.fromJson(s, new TypeToken<List<com.yezhu.lifebox.net.ApkInfo>>() {}.getType());
            // TODO: 17-7-18
            for (com.yezhu.lifebox.net.ApkInfo apk : list) {
                String apkDir = dirPath + File.separator + apk.getApk_name() + "-" + apk.getApk_info();
                String picPath = apkDir + File.separator + PIC_SUFFIX;

                File apkDirFile = new File(apkDir);
                if (!apkDirFile.exists()) {
                    apkDirFile.mkdir();
                }

                File picFile = new File(picPath);
                if (!picFile.exists()) {
                    try {
                        picFile.createNewFile();
                        RemoteApkInfo.downloadFile(RemoteApkInfo.BASE_URL + apk.getApk_pic(), picPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                Bitmap bitmap = BitmapFactory.decodeFile(picPath);
                Drawable pic = new BitmapDrawable(bitmap);
                ApkInfo apkInfo = new ApkInfo(apk.getApk_name(), apk.getApk_info(), pic, apk.getApk_path());
                mApkInfoList.add(apkInfo);
            }

            mRecyclerView = (RecyclerView) findViewById(R.id.id_apk_list_recyclerview);
            mAdapter = new ApkAdapter(ApkListActivity.this, mApkInfoList);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(ApkListActivity.this));
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(new DividerGridItemDecoration(ApkListActivity.this));
            initEvent();

        }

        private void initEvent() {
            mAdapter.setOnItemClickLitener(new ApkAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(ApkListActivity.this, "安装应用可长点击该处！",
                            Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onItemLongClick(View view, final int position) {
                    // 如果已经安装了，则不允许再次那装
                    try {
                        boolean installed = PluginManager.getInstance()
                                .isPluginPackage(mApkInfoList.get(position).getmPackageName());
                        if (installed) {
                            Toast.makeText(ApkListActivity.this,
                                    "该应用已经安装了，不能再次安装", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    DialogInterface.OnClickListener dialogOnclicListener =
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case Dialog.BUTTON_POSITIVE:
                                            String apkDir = ApkListActivity.this.getFilesDir()
                                                    + File.separator + APK_STORE_DIR
                                                    + File.separator + mApkInfoList.get(position).getmAppName()
                                                    + "-" + mApkInfoList.get(position).getmPackageName();
                                            String apkPath = apkDir + File.separator + APK_SUFFIX;
                                            String apkUrl = RemoteApkInfo.BASE_URL + mApkInfoList.get(position).getApkPath();

                                            Intent data = new Intent(UPDATE_MAIN_UI_ACTION);
                                            data.putExtra("apk_path", apkPath);
                                            data.putExtra("apk_url", apkUrl);
                                            data.putExtra("package_name", mApkInfoList.get(position).getmPackageName());

                                            sendBroadcast(data);
                                            ApkListActivity.this.finish();
                                    }
                                }
                            };
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ApkListActivity.this);
                    builder.setTitle("提示")
                            .setMessage("是否安装应用?")
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("安装", dialogOnclicListener)
                            .setNegativeButton("取消", dialogOnclicListener)
                            .create().show();
                }
            });
        }
    }
}
