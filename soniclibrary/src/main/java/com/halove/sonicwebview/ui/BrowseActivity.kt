package com.halove.sonicwebview.ui

import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.halove.sonicwebview.Constants
import com.halove.sonicwebview.R
import com.halove.sonicwebview.ui.base.BaseWebActivity


/**
 * 默认浏览activity
 * Created by yanglijun on 18-2-9.
 */
open class BrowseActivity : BaseWebActivity() {

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
            Log.e("BrowseActivity", "url is null, finish activity")
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
            webViewManager?.setWebChromeClient(object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    if (title?.isNotBlank() == true) {
                        setTitle(title)
                    }
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

    override fun onConfigurationChanged(config: Configuration) {
        super.onConfigurationChanged(config)
        when (config.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
//                window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
//                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                supportActionBar?.hide()
            }
            Configuration.ORIENTATION_PORTRAIT -> {
//                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                supportActionBar?.show()
//                window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            }
        }
    }
}
