package com.yezhu.lifebox;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.compat.PackageManagerCompat;
import com.yezhu.lifebox.ad.AdActivity;
import com.yezhu.lifebox.apkmanager.ApkListActivity;
import com.yezhu.lifebox.fileutils.FileOperation;
import com.yezhu.lifebox.net.DownloadAppUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private HomeAdapter mAdapter;
    private List<AppInfo> mAppInfoList;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();

        initData();

        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        mAdapter = new HomeAdapter(this, mAppInfoList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        mRecyclerView.setAdapter(mAdapter);

//        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        initEvent();

        mBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                final String apkPath = bundle.getString("apk_path");
                String apkUrl = bundle.getString("apk_url");
                final String packageName = bundle.getString("package_name");

                // download apk
                Toast.makeText(HomeActivity.this, "正在后台下载应用，请等候...", Toast.LENGTH_SHORT).show();
                final long downloadApkId = DownloadAppUtils.downloadApk(HomeActivity.this, apkUrl, packageName);

                BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                        if (completeDownloadId == downloadApkId) {
                            String srcPath = getExternalFilesDir("apkdata").getAbsolutePath()
                                    + File.separator + packageName + ".apk";
                            FileOperation.copyFile(srcPath, apkPath);
                            Toast.makeText(HomeActivity.this, "应用下载完成", Toast.LENGTH_SHORT).show();
                        }

                        try {
                            int ret = PluginManager.getInstance().installPackage(
                                    apkPath, PackageManagerCompat.INSTALL_REPLACE_EXISTING);
                            if (ret != -1) {
                                ApplicationInfo applicationInfo = PluginManager
                                        .getInstance().getApplicationInfo(packageName, 0);
                                PackageManager pm = HomeActivity.this.getPackageManager();
                                Drawable icon = applicationInfo.loadIcon(pm);
                                CharSequence label = applicationInfo.loadLabel(pm);
                                AppInfo appInfo = new AppInfo(label, packageName, icon);
                                mAdapter.addData(mAppInfoList.size(), appInfo);
                                Toast.makeText(HomeActivity.this, "安装成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this, "安装应用失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                };

                IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                registerReceiver(downloadCompleteReceiver, filter);
            }
        };

        IntentFilter filter = new IntentFilter(ApkListActivity.UPDATE_MAIN_UI_ACTION);
        registerReceiver(mBroadcastReceiver, filter);
    }


    private void initEvent() {
        mAdapter.setOnItemClickLitener(new HomeAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {

                PackageManager pm = getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(mAppInfoList.get(position).getmPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                // TODO: 17-6-29
                // do nothing
            }
        });
    }

    // TODO: 17-6-28
    protected void initData() {
        mAppInfoList = new ArrayList<>();

        try {
            List<ApplicationInfo> installedApps = PluginManager
                    .getInstance().getInstalledApplications(0);
            if (installedApps == null || installedApps.size() <= 0) {
                return;
            }

            for (int i=0; i < installedApps.size(); ++i) {
                ApplicationInfo applicationInfo = installedApps.get(i);
                PackageManager pm = this.getPackageManager();
                Drawable icon = applicationInfo.loadIcon(pm);
                CharSequence label = applicationInfo.loadLabel(pm);
                String packageName = applicationInfo.packageName;

                AppInfo appInfo = new AppInfo(label, packageName, icon);

                mAppInfoList.add(appInfo);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        ImageView adImage = (ImageView) findViewById(R.id.id_ad_image);
        adImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AdActivity.class);
                startActivity(intent);
            }
        });

        ImageView mainAdd = (ImageView) findViewById(R.id.btd_add);
        mainAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ApkListActivity.class);
                startActivity(intent);
//                startActivityForResult(intent, Constant.REQUEST_APP_APP);
            }
        });
    }
}
