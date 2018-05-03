package com.halove.sonicwebview.tools

import android.content.Context
import android.util.Log
import android.webkit.URLUtil


/**
 * Created by yanglijun on 18-3-29.
 */
object AppUtils {
    /**
     * 获取应用程序名称
     */
    fun getAppName(context: Context): String? {
        try {
            val packageManager = context.getPackageManager()
            val packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0)
            val labelRes = packageInfo.applicationInfo.labelRes
            return context.getResources().getString(labelRes)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 获取应用程序版本名称信息
     *
     * @param context
     * @return 当前应用的版本名称
     */
    fun getVersionName(context: Context): String? {
        try {
            val packageManager = context.getPackageManager()
            val packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0)
            return packageInfo.versionName

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    fun getSystemVersion(): String {
        return android.os.Build.VERSION.RELEASE
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    fun getSystemModel(): String {
        return android.os.Build.MODEL
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商
     */
    fun getDeviceBrand(): String {
        return android.os.Build.BRAND
    }

    /**
     * 检查url是否完整
     */
    fun checkUrl(url:String): String{
        if(URLUtil.isNetworkUrl(url)){
            return url
        }else{
            Log.e("SonicWebview", "error: url not Scheme '$url', default use Http://")
        }
        val lastUrl = "http://"+url
        return lastUrl
    }
}