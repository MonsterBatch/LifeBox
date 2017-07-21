package com.yezhu.lifebox.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

/**
 * Created by Aven on 2017/7/11.
 */

public class RemoteApkInfo {
    public static final String BASE_URL = "http://139.196.138.247:8080/App/";
    private static String url = "";

    public static String getRemoteApkInfo(String urlPath,String params){
        return request(urlPath,params,"json");
    }

    private static String request(String urlPath,String param,String type){
        System.out.println(urlPath);
        URL url = null;
        HttpURLConnection httpConn = null;
        int resultCode = -1;
        String responseStr = "";
        try {
            url = new URL(urlPath);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod("POST");
            if ("json".equals(type)){
                httpConn.setRequestProperty("Content-Type", "application/json");
                httpConn.setRequestProperty("Accept", "application/json");
            }else{
                httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }
            httpConn.setRequestProperty("Charset", "UTF-8");
            OutputStreamWriter dos = null;
            if (!"".equals(param) && null != param) {
                dos = new OutputStreamWriter(httpConn.getOutputStream());
                dos.write(param);
                dos.flush();
                dos.close();
            }
            resultCode = httpConn.getResponseCode();
            System.out.println(resultCode);
            if (resultCode == HttpURLConnection.HTTP_OK){
                StringBuffer sb = new StringBuffer();
                String readLine = "";
                BufferedReader responseReader = new BufferedReader(
                        new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine).append("\n");
                }
                responseStr = sb.toString();
                responseReader.close();
            }
        } catch (Exception e){
           System.out.println(e.getMessage());
        }

        return responseStr;

    }


    private static HttpURLConnection request(String urlPath,String param){
        System.out.println(urlPath);
        URL url = null;
        HttpURLConnection httpConn = null;
        int resultCode = -1;
        String responseStr = "";
        try {
            url = new URL(urlPath);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("Charset", "UTF-8");
            OutputStreamWriter dos = null;
            if (!"".equals(param) && null != param) {
                dos = new OutputStreamWriter(httpConn.getOutputStream());
                dos.write(param);
                dos.flush();
                dos.close();
            }
            resultCode = httpConn.getResponseCode();
            System.out.println(resultCode);
            if (resultCode == HttpURLConnection.HTTP_OK){
              return httpConn;
            }else{
                httpConn = null;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return httpConn;

    }

    /**
     *下载远程服务器的文件
     *
     * @param downloadFileUrl
     * @param destination 下载的文件存放的位置
     *
     * @return boolean
     */
    public static boolean downloadFile(String downloadFileUrl, String destination) {
        boolean           downloadFlag  = false;
        try {
            System.out.println("downloading");
            HttpURLConnection httpURLConnection = request(downloadFileUrl,"");
            if(httpURLConnection != null){
                writeFile(httpURLConnection.getInputStream(),destination);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return downloadFlag;
    }

    /**
     * 写文件
     *
     * @param inputStream
     * @param destination
     */
    public static void writeFile(InputStream inputStream, String destination) {
        ByteArrayOutputStream out    = new ByteArrayOutputStream();
        FileOutputStream      fops   = null;
        byte[]                buffer = new byte[100];
        int                   len    = 0;

        try {
            while ((len = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            out.flush();

            byte[] result = out.toByteArray();

            fops = new FileOutputStream(new File(destination));
            fops.write(result);
            fops.flush();
            fops.close();
            inputStream.close();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
        String json_info = getRemoteApkInfo(BASE_URL+"ApkServlet","");
        Gson gson = new Gson();
        ApkInfo apk = null;
        List<ApkInfo> list = gson.fromJson(json_info, new TypeToken<List<ApkInfo>>(){}.getType());
		for(int i = 0; i < list.size(); i++)
		    apk = list.get(i);
			System.out.println(apk.getApk_info());
			System.out.println(apk.getApk_name());
			System.out.println(apk.getApk_path());
			System.out.println(apk.getApk_pic());
		}
    }

