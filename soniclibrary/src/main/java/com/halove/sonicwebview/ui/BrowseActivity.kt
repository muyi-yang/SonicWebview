package com.halove.sonicwebview.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.halove.sonicwebview.Constants
import com.halove.sonicwebview.R

/**
 * Created by yanglijun on 18-2-9.
 */

class BrowseActivity : BaseWebActivity() {

    private var url: String? = null
    private var title: String? = null
    private var showActionBar: Boolean = false
    private var forceFinish: Boolean = false
    private var showBack: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = intent.getStringExtra(Constants.PARAM_URL)
        title = intent.getStringExtra(Constants.PARAM_TITLE)
        showActionBar = intent.getBooleanExtra(Constants.PARAM_ACTIONBAR_SHOW, true)
        forceFinish = intent.getBooleanExtra(Constants.PARAM_BACK_FORCE_FINISH, false)
        showBack = intent.getBooleanExtra(Constants.PARAM_ACTIONBAR_BACK_SHOW, true)
        if (TextUtils.isEmpty(url)) {
            finish()
            return
        }
        if (!showActionBar) {
            supportActionBar?.hide()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(showBack)
        setContentView(R.layout.sonic_activity_default_browse)
        if (title != null) {
            setTitle(title)
        } else {
            webViewManager!!.setWebChromeClient(object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    setTitle(title)
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home// 点击返回图标事件
            -> {
                if (webView!!.canGoBack() && !forceFinish) {
                    webView!!.goBack()
                } else {
                    this.finish()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun getUrl(): String {
        return url!!
    }
}
