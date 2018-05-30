package com.halove.sonicwebview.ui.video

import android.content.pm.ActivityInfo
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout

/**
 * 视频视图管理
 * Created by yanglijun on 18-5-30.
 */
class VideoViewManage(webView: WebView) {

    private val TAG = "VideoViewManage"

    private val webView = webView
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var orientationCallback: IRequestedOrientation? = null

    fun onShowCustomView(view: View?, callback: WebChromeClient.CustomViewCallback?) {
        orientationCallback?.requestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        // 如果一个视图已经存在，那么立刻终止并新建一个
        if (customView != null) {
            callback?.onCustomViewHidden()
            return
        }
        customView = view
        var rootView: FrameLayout? = null
        if (webView.parent is FrameLayout) {
            rootView = webView.parent as FrameLayout
            rootView.addView(customView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT))
            if(customView is FrameLayout){
                val focusedChild = (customView as FrameLayout).getFocusedChild()
                Log.e(TAG, "onShowCustomView focusedChild==>" + focusedChild)
            }
        } else {
            webView.addView(customView)
            customViewCallback = callback
        }
        Log.e(TAG, "onShowCustomView view width==>" + view?.width + "  height==>" + view?.height +
                "  webView width==>" + webView.width + "  height==>" + webView.height + "  webView.parent==>" + webView.parent)
    }

    fun onHideCustomView() {
        Log.e(TAG, "onHideCustomView")

        if (customView == null)
        // 不是全屏播放状态
            return
        var rootView: FrameLayout? = null
        if (webView.parent is FrameLayout) {
            rootView = webView.parent as FrameLayout
            rootView.removeView(customView)
        } else {
            webView.removeView(customView)
        }
        customView = null
        orientationCallback?.requestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    fun setRequestedOrientation(callback: IRequestedOrientation) {
        orientationCallback = callback
    }

    fun isFullScreen(): Boolean {
        var back = false
        if (customView != null) {
            back = true
            customViewCallback?.onCustomViewHidden()
        }
        return back
    }
}