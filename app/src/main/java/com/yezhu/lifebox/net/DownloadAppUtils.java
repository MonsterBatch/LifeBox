package com.yezhu.lifebox.net;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;


/**
 * Created by yuyifei on 17-7-18.
 */

public class DownloadAppUtils {

    public static long downloadApk(Context context, String apkUrl, String packageName) {
        if (TextUtils.isEmpty(apkUrl) || TextUtils.isEmpty(packageName)) {
            return -1;
        }

        Uri apkUri = Uri.parse(apkUrl);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(apkUri);
        request.setVisibleInDownloadsUi(true);
        request.setTitle("下载应用");

        request.setDestinationInExternalFilesDir(context, "apkdata", packageName + ".apk");
        long downloadApkId = downloadManager.enqueue(request);
        return downloadApkId;
    }
}
