package com.halove.sonicwebview.core

import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import com.halove.sonicwebview.ui.state.StateViewConfig
import com.halove.sonicwebview.ui.state.StateViewManage
import com.halove.sonicwebview.ui.video.IRequestedOrientation
import com.halove.sonicwebview.ui.video.VideoViewManage
import com.tencent.sonic.sdk.*

/**
 * webview管理类
 * Created by yanglijun on 18-2-28.
 */
class WebViewManage(webview: WebView) {

    private var sonicSession: SonicSession? = null
    private val webView = webview
    private var sonicSessionClient: SonicSessionClient? = null
    private var webViewClientListener: WebViewClient? = null
    private var webChromeClientListener: WebChromeClient? = null
    private lateinit var stateViewManage: StateViewManage
    private lateinit var videoViewManage: VideoViewManage

    fun onCreate(url: String,
                 sonicRuntime: SonicRuntime = DefaultSonicRuntime(webView.context.applicationContext),
                 sonicConfig: SonicConfig = SonicConfig.Builder().build(),
                 sonicSessionConfig: SonicSessionConfig = SonicSessionConfig.Builder().build(),
                 sessionClient: SonicSessionClient = DefaultSonicSessionClient(),
                 stateViewConfig: StateViewConfig = StateViewConfig()) {
        // step 1: Initialize sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(sonicRuntime, sonicConfig)
        }

        // step 2: Create SonicSession
        sonicSession = SonicEngine.getInstance().createSession(url, sonicSessionConfig)
        if (null != sonicSession) {
            sonicSessionClient = sessionClient
            sonicSession?.bindClient(sonicSessionClient)
        } else {
            // this only happen when a same sonic session is already running,
            // u can comment following codes to feedback as a default mode.
//            throw UnknownError("create session fail!")
        }

        // step 3: BindWebView for sessionClient and bindClient for SonicSession
        // in the real world, the init flow may cost a long time as startup
        // runtime、init configs....
        webView.webViewClient = webViewClient
        webView.webChromeClient = webChromeClient

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
        webSettings.pluginState = WebSettings.PluginState.ON
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)  //提高渲染的优先级
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webSettings.allowFileAccess = true  //设置可以访问文件
        webSettings.useWideViewPort = true  //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        webSettings.setSupportMultipleWindows(true)


        // step 5: webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            if (sonicSessionClient is DefaultSonicSessionClient) {
                (sonicSessionClient as DefaultSonicSessionClient).bindWebView(webView)
            }
            sonicSessionClient?.clientReady()
        } else { // default mode
            webView.loadUrl(url)
        }
        stateViewManage = StateViewManage(webView, stateViewConfig)
        videoViewManage = VideoViewManage(webView)
    }

    private val webViewClient: WebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            sonicSession?.getSessionClient()?.pageFinish(url)
            webViewClientListener?.onPageFinished(view, url)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
            webViewClientListener?.shouldInterceptRequest(view, request)
            return shouldInterceptRequest(view, request.url.toString())
        }

        override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
            webViewClientListener?.shouldInterceptRequest(view, url)
            return sonicSession?.getSessionClient()?.requestResource(url) as? WebResourceResponse
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            webViewClientListener?.onReceivedError(view, request, error)
            //访问baidu时，子页面跳转过程中可能会触发，此错误回调不可靠
//            Log.e("sonic", "onReceivedError  111  error==>" + error)
        }

        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            webViewClientListener?.onReceivedError(view, errorCode, description, failingUrl)
//            Log.e("sonic", "onReceivedError  2222")
            stateViewManage.onReceivedError()
        }

        override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
            return webViewClientListener?.shouldOverrideKeyEvent(view, event)
                    ?: super.shouldOverrideKeyEvent(view, event)
        }

        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
            super.doUpdateVisitedHistory(view, url, isReload)
            webViewClientListener?.doUpdateVisitedHistory(view, url, isReload)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
            return webViewClientListener?.onRenderProcessGone(view, detail)
                    ?: super.onRenderProcessGone(view, detail)
        }

        override fun onReceivedLoginRequest(view: WebView?, realm: String?, account: String?, args: String?) {
            super.onReceivedLoginRequest(view, realm, account, args)
            webViewClientListener?.onReceivedLoginRequest(view, realm, account, args)
        }

        @RequiresApi(Build.VERSION_CODES.M)
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
            return webViewClientListener?.shouldOverrideUrlLoading(view, url)
                    ?: super.shouldOverrideUrlLoading(view, url)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return webViewClientListener?.shouldOverrideUrlLoading(view, request)
                    ?: super.shouldOverrideUrlLoading(view, request)
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            webViewClientListener?.onPageCommitVisible(view, url)
        }

        override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
            super.onUnhandledKeyEvent(view, event)
            webViewClientListener?.onUnhandledKeyEvent(view, event)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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

    private val webChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onRequestFocus(view: WebView?) {
            super.onRequestFocus(view)
            webChromeClientListener?.onRequestFocus(view)
        }

        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
            return webChromeClientListener?.onJsAlert(view, url, message, result)
                    ?: super.onJsAlert(view, url, message, result)
        }

        override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
            return webChromeClientListener?.onJsPrompt(view, url, message, defaultValue, result)
                    ?: super.onJsPrompt(view, url, message, defaultValue, result)
        }

        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            super.onShowCustomView(view, callback)
            webChromeClientListener?.onShowCustomView(view, callback)
            videoViewManage.onShowCustomView(view, callback)
        }

        override fun onHideCustomView() {
            super.onHideCustomView()
            webChromeClientListener?.onHideCustomView()
            videoViewManage.onHideCustomView()
        }

        override fun onShowCustomView(view: View?, requestedOrientation: Int, callback: CustomViewCallback?) {
            super.onShowCustomView(view, requestedOrientation, callback)
            webChromeClientListener?.onShowCustomView(view, requestedOrientation, callback)
        }

        override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
            super.onGeolocationPermissionsShowPrompt(origin, callback)
            webChromeClientListener?.onGeolocationPermissionsShowPrompt(origin, callback)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onPermissionRequestCanceled(request: PermissionRequest?) {
            super.onPermissionRequestCanceled(request)
            webChromeClientListener?.onPermissionRequestCanceled(request)
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
            stateViewManage.onProgressChanged(newProgress)
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

    fun goBack(): Boolean {
        var back = webView.canGoBack() ?: false
        if (back) {
            webView.goBack()
            stateViewManage.setHideErrorLayout(true)
        }
        val fullScreen = videoViewManage.isFullScreen()
        if (fullScreen) {
            back = true
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

    fun onDestroy() {
        sonicSession?.destroy()
        sonicSession = null
    }

    fun setRequestedOrientation(callback: IRequestedOrientation) {
        videoViewManage.setRequestedOrientation(callback)
    }
}