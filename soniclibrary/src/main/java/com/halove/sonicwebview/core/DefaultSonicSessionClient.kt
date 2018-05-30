package com.halove.sonicwebview.core

import android.os.Bundle
import android.webkit.WebView

import com.tencent.sonic.sdk.SonicSessionClient

import java.util.HashMap

/**
 * Created by yanglijun on 18-2-9.
 */

class DefaultSonicSessionClient : SonicSessionClient() {

    var webView: WebView ?= null
        private set

    fun bindWebView(webView: WebView?) {
        this.webView = webView
    }

    override fun loadUrl(url: String?, extraData: Bundle?) {
        webView?.loadUrl(url)
    }

    override fun loadDataWithBaseUrl(baseUrl: String?, data: String, mimeType: String, encoding: String, historyUrl: String) {
        webView?.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }

    override fun loadDataWithBaseUrlAndHeader(baseUrl: String?, data: String, mimeType: String, encoding: String, historyUrl: String, headers: HashMap<String, String>) {
        loadDataWithBaseUrl(baseUrl, data, mimeType, encoding, historyUrl)
    }
}
