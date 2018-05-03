package com.halove.sonicwebview

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.tencent.sonic.sdk.*

/**
 * Created by yanglijun on 18-2-28.
 */
class WebViewManager(webview: WebView?) {

    var sonicSession: SonicSession? = null
    val webView = webview
    var sonicSessionClient: DefaultSonicSessionClient? = null
    var webViewClientListener: WebViewClient? = null
    var webChromeClientListener: WebChromeClient? = null
    var errorRemind: View? = null
    var progressView: ProgressBar? = null
    lateinit var styleConfig: WebViewStyleConfig
    var isHideErrorLayout = false
    lateinit var btnRetry: Button

    fun onCreate(url: String) {
        onCreate(url, null, null, null, null)
    }

    fun onCreate(url: String, sonicRuntime: SonicRuntime?) {
        onCreate(url, sonicRuntime, null)
    }

    fun onCreate(url: String, sonicRuntime: SonicRuntime?, sonicConfig: SonicConfig?) {
        onCreate(url, sonicRuntime, sonicConfig, null, null)
    }

    fun onCreate(url: String, sonicSessionConfig: SonicSessionConfig?) {
        onCreate(url, sonicSessionConfig, null)
    }

    fun onCreate(url: String, sessionClient: SonicSessionClient?) {
        onCreate(url, null, sessionClient)
    }

    fun onCreate(url: String, sonicSessionConfig: SonicSessionConfig?, sessionClient: SonicSessionClient?) {
        onCreate(url, null, null, sonicSessionConfig, sessionClient)
    }

    fun onCreate(url: String, sonicRuntime: SonicRuntime?, sonicConfig: SonicConfig?, sonicSessionConfig: SonicSessionConfig?,
                 sessionClient: SonicSessionClient?) {
        if (webView == null) {
            throw UnknownError("webView is null!")
        }
        // step 1: Initialize sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            val runtime = sonicRuntime ?: DefaultSonicRuntime(webView.context.applicationContext)
            val config = sonicConfig ?: SonicConfig.Builder().build()
            SonicEngine.createInstance(runtime, config)
        }


        // step 2: Create SonicSession
        val sessionConfig = sonicSessionConfig ?: SonicSessionConfig.Builder().build()
        sonicSession = SonicEngine.getInstance().createSession(url, sessionConfig)
        if (null != sonicSession) {
            sonicSessionClient = sessionClient as? DefaultSonicSessionClient ?: DefaultSonicSessionClient()
            sonicSession?.bindClient(sonicSessionClient)
        } else {
            // this only happen when a same sonic session is already running,
            // u can comment following codes to feedback as a default mode.
//            throw UnknownError("create session fail!")
        }

        // step 3: BindWebView for sessionClient and bindClient for SonicSession
        // in the real world, the init flow may cost a long time as startup
        // runtime、init configs....
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                sonicSession?.getSessionClient()?.pageFinish(url)
                webViewClientListener?.onPageFinished(view, url)
            }

            @TargetApi(21)
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                webViewClientListener?.shouldInterceptRequest(view, request)
                return shouldInterceptRequest(view, request.url.toString())
            }

            override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                webViewClientListener?.shouldInterceptRequest(view, url)
                return sonicSession?.getSessionClient()?.requestResource(url) as? WebResourceResponse
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                webViewClientListener?.onReceivedError(view, request, error)
                //访问baidu时，子页面跳转过程中可能会触发，此错误回调不可靠
                Log.e("sonic", "onReceivedError  111  error==>" + error)
//                addErrorView()
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                webViewClientListener?.onReceivedError(view, errorCode, description, failingUrl)
                Log.e("sonic", "onReceivedError  2222")
                isHideErrorLayout = false
                errorRemind?.visibility = View.VISIBLE
                btnRetry.isEnabled = true
            }

            override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
                return webViewClientListener?.shouldOverrideKeyEvent(view, event) ?: super.shouldOverrideKeyEvent(view, event)
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)
                webViewClientListener?.doUpdateVisitedHistory(view, url, isReload)
            }

            override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                return webViewClientListener?.onRenderProcessGone(view, detail) ?: super.onRenderProcessGone(view, detail)
            }

            override fun onReceivedLoginRequest(view: WebView?, realm: String?, account: String?, args: String?) {
                super.onReceivedLoginRequest(view, realm, account, args)
                webViewClientListener?.onReceivedLoginRequest(view, realm, account, args)
            }

            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                super.onReceivedHttpError(view, request, errorResponse)
                webViewClientListener?.onReceivedHttpError(view, request, errorResponse)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                webViewClientListener?.onPageStarted(view, url, favicon)
            }

            override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
                super.onScaleChanged(view, oldScale, newScale)
                webViewClientListener?.onScaleChanged(view, oldScale, newScale)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return webViewClientListener?.shouldOverrideUrlLoading(view, url) ?: super.shouldOverrideUrlLoading(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return webViewClientListener?.shouldOverrideUrlLoading(view, request) ?: super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                webViewClientListener?.onPageCommitVisible(view, url)
            }

            override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
                super.onUnhandledKeyEvent(view, event)
                webViewClientListener?.onUnhandledKeyEvent(view, event)
            }

            override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest?) {
                super.onReceivedClientCertRequest(view, request)
                webViewClientListener?.onReceivedClientCertRequest(view, request)
            }

            override fun onReceivedHttpAuthRequest(view: WebView?, handler: HttpAuthHandler?, host: String?, realm: String?) {
                super.onReceivedHttpAuthRequest(view, handler, host, realm)
                webViewClientListener?.onReceivedHttpAuthRequest(view, handler, host, realm)
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                super.onReceivedSslError(view, handler, error)
                webViewClientListener?.onReceivedSslError(view, handler, error)
            }

            override fun onTooManyRedirects(view: WebView?, cancelMsg: Message?, continueMsg: Message?) {
                super.onTooManyRedirects(view, cancelMsg, continueMsg)
                webViewClientListener?.onTooManyRedirects(view, cancelMsg, continueMsg)
            }

            override fun onFormResubmission(view: WebView?, dontResend: Message?, resend: Message?) {
                super.onFormResubmission(view, dontResend, resend)
                webViewClientListener?.onFormResubmission(view, dontResend, resend)
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                webViewClientListener?.onLoadResource(view, url)
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onRequestFocus(view: WebView?) {
                super.onRequestFocus(view)
                webChromeClientListener?.onRequestFocus(view)
            }

            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                return webChromeClientListener?.onJsAlert(view, url, message, result) ?: super.onJsAlert(view, url, message, result)
            }

            override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
                return webChromeClientListener?.onJsPrompt(view, url, message, defaultValue, result)
                        ?: super.onJsPrompt(view, url, message, defaultValue, result)
            }

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                super.onShowCustomView(view, callback)
                webChromeClientListener?.onShowCustomView(view, callback)
            }

            override fun onHideCustomView() {
                super.onHideCustomView()
                webChromeClientListener?.onHideCustomView()
            }

            override fun onShowCustomView(view: View?, requestedOrientation: Int, callback: CustomViewCallback?) {
                super.onShowCustomView(view, requestedOrientation, callback)
                webChromeClientListener?.onShowCustomView(view, requestedOrientation, callback)
            }

            override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
                super.onGeolocationPermissionsShowPrompt(origin, callback)
                webChromeClientListener?.onGeolocationPermissionsShowPrompt(origin, callback)
            }

            override fun onPermissionRequest(request: PermissionRequest?) {
                super.onPermissionRequest(request)
                webChromeClientListener?.onPermissionRequest(request)
            }

            override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
                super.onConsoleMessage(message, lineNumber, sourceID)
                webChromeClientListener?.onConsoleMessage(message, lineNumber, sourceID)
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                return webChromeClientListener?.onConsoleMessage(consoleMessage)
                        ?: super.onConsoleMessage(consoleMessage)
            }

            override fun onPermissionRequestCanceled(request: PermissionRequest?) {
                super.onPermissionRequestCanceled(request)
                webChromeClientListener?.onPermissionRequestCanceled(request)
            }

            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                return webChromeClientListener?.onShowFileChooser(webView, filePathCallback, fileChooserParams)
                        ?: super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
            }

            override fun onReceivedTouchIconUrl(view: WebView?, url: String?, precomposed: Boolean) {
                super.onReceivedTouchIconUrl(view, url, precomposed)
                webChromeClientListener?.onReceivedTouchIconUrl(view, url, precomposed)
            }

            override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                super.onReceivedIcon(view, icon)
                webChromeClientListener?.onReceivedIcon(view, icon)
            }

            override fun onExceededDatabaseQuota(url: String?, databaseIdentifier: String?, quota: Long, estimatedDatabaseSize: Long, totalQuota: Long, quotaUpdater: WebStorage.QuotaUpdater?) {
                super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater)
                webChromeClientListener?.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                Log.e("sonic", "onReceivedTitle  title:" + title)
                webChromeClientListener?.onReceivedTitle(view, title)
            }

            override fun onReachedMaxAppCacheSize(requiredStorage: Long, quota: Long, quotaUpdater: WebStorage.QuotaUpdater?) {
                super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater)
                webChromeClientListener?.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater)
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                webChromeClientListener?.onProgressChanged(view, newProgress)
                progressView?.progress = newProgress
                Log.e("sonic", "newProgress:" + newProgress)
                if (newProgress >= 100) {
                    btnRetry.isEnabled = true
                    progressView?.visibility = View.GONE
                    if (isHideErrorLayout) {
                        errorRemind?.visibility = View.GONE
                    }
                } else {
                    if (progressView?.visibility == View.GONE) {
                        progressView?.visibility = View.VISIBLE
                    }
                }
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                return webChromeClientListener?.onJsConfirm(view, url, message, result)
                        ?: super.onJsConfirm(view, url, message, result)
            }

            override fun getVisitedHistory(callback: ValueCallback<Array<String>>?) {
                super.getVisitedHistory(callback)
                webChromeClientListener?.getVisitedHistory(callback)
            }

            override fun getVideoLoadingProgressView(): View? {
                return webChromeClientListener?.getVideoLoadingProgressView()
                        ?: super.getVideoLoadingProgressView()
            }

            override fun onGeolocationPermissionsHidePrompt() {
                super.onGeolocationPermissionsHidePrompt()
                webChromeClientListener?.onGeolocationPermissionsHidePrompt()
            }

            override fun getDefaultVideoPoster(): Bitmap? {
                return webChromeClientListener?.getDefaultVideoPoster()
                        ?: super.getDefaultVideoPoster()
            }

            override fun onJsBeforeUnload(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                return webChromeClientListener?.onJsBeforeUnload(view, url, message, result)
                        ?: super.onJsBeforeUnload(view, url, message, result)
            }

            override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                return webChromeClientListener?.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
                        ?: super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
            }

            override fun onCloseWindow(window: WebView?) {
                super.onCloseWindow(window)
                webChromeClientListener?.onCloseWindow(window)
            }

            override fun onJsTimeout(): Boolean {
                return webChromeClientListener?.onJsTimeout() ?: super.onJsTimeout()
            }
        }

        val webSettings = webView.settings

        // step 4: bind javascript
        // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
        // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
        // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
        webSettings.javaScriptEnabled = true
        webView.removeJavascriptInterface("searchBoxJavaBridge_")
//        intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
//        webView.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient, intent), "sonic");

        // init webview settings
        webSettings.allowContentAccess = true
        webSettings.databaseEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.savePassword = false
        webSettings.saveFormData = false
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.pluginState = WebSettings.PluginState.ON
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)  //提高渲染的优先级
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.allowFileAccess = true  //设置可以访问文件
        webSettings.useWideViewPort = true  //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小


        // step 5: webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient?.bindWebView(webView)
            sonicSessionClient?.clientReady()
        } else { // default mode
            webView.loadUrl(url)
        }
        addErrorView()
        addProgressView()
    }

    private fun addProgressView() {
        if (styleConfig.isShowLoadProgress) {
            progressView = LayoutInflater.from(webView!!.context).inflate(R.layout.sonic_layout_load_progress, webView, false) as ProgressBar
            webView.addView(progressView)
        }
    }

    private fun addErrorView() {
        if (styleConfig.isShowLoadErrorLayout) {
            if (errorRemind == null) {
                errorRemind = LayoutInflater.from(webView!!.context).inflate(R.layout.sonic_layout_load_error, webView, false)
                webView.addView(errorRemind)
                errorRemind!!.visibility = View.GONE
            } else {
                errorRemind!!.visibility = View.VISIBLE
            }
            var iconImg = errorRemind!!.findViewById<ImageView>(R.id.iv_icon)
            if (styleConfig.isShowErrorIcon) {
                if (styleConfig.loadErrorIconRes > 0) {
                    iconImg.setImageResource(styleConfig.loadErrorIconRes)
                } else if (styleConfig.loadErrorIconBitmap != null) {
                    iconImg.setImageBitmap(styleConfig.loadErrorIconBitmap)
                }
                iconImg.visibility = View.VISIBLE
            } else {
                iconImg.visibility = View.GONE
            }

            var remindTxt = errorRemind!!.findViewById<TextView>(R.id.tv_remind)
            if (styleConfig.isShowErrorText) {
                if (styleConfig.loadErrorTextColor != 0) {
                    remindTxt.setTextColor(styleConfig.loadErrorTextColor)
                }
                if (!styleConfig.loadErrorText.isNullOrEmpty()) {
                    remindTxt.setText(styleConfig.loadErrorText)
                }
                remindTxt.visibility = View.VISIBLE
            } else {
                remindTxt.visibility = View.GONE
            }

            btnRetry = errorRemind!!.findViewById<Button>(R.id.btn_retry)
            if (styleConfig.isShowErrorBtn && !styleConfig.isFullScreenRefreshMode) {
                btnRetry.visibility = View.VISIBLE
                if (styleConfig.loadErrorBtnTextColor != 0) {
                    btnRetry.setTextColor(styleConfig.loadErrorBtnTextColor)
                }
                if (styleConfig.loadErrorBtnBgRes != 0) {
                    btnRetry.setBackgroundResource(styleConfig.loadErrorBtnBgRes)
                }
                if (!styleConfig.loadErrorBtnText.isNullOrEmpty()) {
                    btnRetry.setText(styleConfig.loadErrorBtnText)
                }
            } else {
                btnRetry.visibility = View.GONE
            }
            btnRetry.setOnClickListener {
                reload()
            }
            if(styleConfig.isFullScreenRefreshMode) {
                errorRemind!!.setOnClickListener {
                    reload()
                }
            }
        }
    }

    private fun reload(){
        webView!!.reload()
        btnRetry.isEnabled = false
        isHideErrorLayout = true
    }

    fun goBack():Boolean{
        var back = webView?.canGoBack() ?: false
        if (back) {
            webView?.goBack()
            isHideErrorLayout = true
        }
        return back
    }

    fun loadUrl(url: String) {
        sonicSessionClient?.loadUrl(url, null)
    }

    fun setWebViewClient(webViewClient: WebViewClient) {
        webViewClientListener = webViewClient
    }

    fun setWebChromeClient(webChromeClient: WebChromeClient) {
        webChromeClientListener = webChromeClient
    }

    fun setWebViewStyleConfig(config: WebViewStyleConfig) {
        styleConfig = config
    }

    fun onDestroy() {
        sonicSession?.destroy()
        sonicSession = null
    }
}