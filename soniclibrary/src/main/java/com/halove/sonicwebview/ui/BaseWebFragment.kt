package com.halove.sonicwebview.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import android.webkit.WebView
import com.halove.sonicwebview.IConfig
import com.halove.sonicwebview.IFragmentBackHandler
import com.halove.sonicwebview.R
import com.halove.sonicwebview.WebViewManager
import com.halove.sonicwebview.tools.AppUtils
import com.halove.sonicwebview.tools.BackHandlerHelper

/**
 * Created by yanglijun on 18-3-7.
 */
abstract class BaseWebFragment : Fragment(), IFragmentBackHandler, IConfig {

    protected var webViewManager: WebViewManager? = null
    protected var webView: WebView? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        webView = view?.findViewById(R.id.webview) as WebView
        if (webView == null) {
            Log.e("BaseWebFragment", "WebView is null, not setContentView or layout does not include WebView?")
            return
        }
        if (webViewManager == null) {
            webViewManager = WebViewManager(webView)
            webViewManager!!.setWebViewStyleConfig(getWebViewStyleConfig())
            webViewManager!!.onCreate(AppUtils.checkUrl(getUrl()), getSonicRuntime(context), getSonicConfig(),
                    getSonicSessionConfig(), getSonicSessionClient())
        }
    }

    override fun onDestroyView() {
        webViewManager?.onDestroy()
        webView?.destroy()
        super.onDestroyView()
    }

    override fun onPause() {
        super.onPause()
        webView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
    }

    override fun onBackPressed(): Boolean {
        if (webViewManager?.goBack()?:false) {
            return true
        }
//        if (webView?.canGoBack() ?: false) {
//            webView?.goBack()
//            return true
//        }
        return BackHandlerHelper.handleBackPress(this)
    }
}