package com.halove.sonicwebview.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.webkit.WebView
import com.halove.sonicwebview.core.IConfig
import com.halove.sonicwebview.ui.video.IRequestedOrientation
import com.halove.sonicwebview.R
import com.halove.sonicwebview.core.WebViewManage
import com.halove.sonicwebview.tools.AppUtils

/**
 * Created by yanglijun on 18-2-28.
 */
abstract class BaseWebActivity : AppCompatActivity(), IConfig {

    protected lateinit var webViewManager: WebViewManage
    protected var webView: WebView? = null

    override fun onContentChanged() {
        super.onContentChanged()
        webView = findViewById(R.id.webview)
        if (webView == null) {
            Log.e("BaseWebActivity", "WebView is null, not setContentView or layout does not include WebView?")
            return
        }
        webViewManager = WebViewManage(webView!!)
        webViewManager.onCreate(AppUtils.checkUrl(getUrl()), getSonicRuntime(applicationContext), getSonicConfig(),
                getSonicSessionConfig(), getSonicSessionClient(), getStateViewConfig())
        webViewManager.setRequestedOrientation(object: IRequestedOrientation {
            override fun requestedOrientation(orientation: Int) {
                requestedOrientation = orientation
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
    }

    override fun onDestroy() {
        webViewManager.onDestroy()
        webView?.destroy()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        webView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
    }

    override fun onBackPressed() {
        if (!webViewManager.goBack()) {
            super.onBackPressed()
        }
    }
}