package com.halove.sonicwebview.ui.state

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.halove.sonicwebview.R

/**
 * 状态视图管理（加载，错误等状态）
 * Created by yanglijun on 18-5-30.
 */
class StateViewManage(webview: WebView, stateConfig: StateViewConfig) {

    private var errorRemind: View? = null
    private var progressView: ProgressBar? = null
    private lateinit var btnRetry: Button
    private var stateConfig = stateConfig
    private val webView = webview
    private var isHideErrorLayout = false

    init {
        addErrorView()
        addProgressView()
    }

    fun isHideErrorLayout(): Boolean {
        return isHideErrorLayout
    }

    fun setHideErrorLayout(hide: Boolean) {
        isHideErrorLayout = hide
    }

    private fun addProgressView() {
        if (stateConfig.isShowLoadProgress) {
            progressView = LayoutInflater.from(webView!!.context).inflate(R.layout.sonic_layout_load_progress, webView, false) as ProgressBar
            webView.addView(progressView)
        }
    }

    private fun addErrorView() {
        if (stateConfig.isShowLoadErrorLayout) {
            if (errorRemind == null) {
                errorRemind = LayoutInflater.from(webView!!.context).inflate(R.layout.sonic_layout_load_error, webView, false)
                webView.addView(errorRemind)
                errorRemind!!.visibility = View.GONE
            } else {
                errorRemind!!.visibility = View.VISIBLE
            }
            var iconImg = errorRemind!!.findViewById<ImageView>(R.id.iv_icon)
            if (stateConfig.isShowErrorIcon) {
                if (stateConfig.loadErrorIconRes > 0) {
                    iconImg.setImageResource(stateConfig.loadErrorIconRes)
                } else if (stateConfig.loadErrorIconBitmap != null) {
                    iconImg.setImageBitmap(stateConfig.loadErrorIconBitmap)
                }
                iconImg.visibility = View.VISIBLE
            } else {
                iconImg.visibility = View.GONE
            }

            var remindTxt = errorRemind!!.findViewById<TextView>(R.id.tv_remind)
            if (stateConfig.isShowErrorText) {
                if (stateConfig.loadErrorTextColor != 0) {
                    remindTxt.setTextColor(stateConfig.loadErrorTextColor)
                }
                if (!stateConfig.loadErrorText.isNullOrEmpty()) {
                    remindTxt.setText(stateConfig.loadErrorText)
                }
                remindTxt.visibility = View.VISIBLE
            } else {
                remindTxt.visibility = View.GONE
            }

            btnRetry = errorRemind!!.findViewById<Button>(R.id.btn_retry)
            if (stateConfig.isShowErrorBtn && !stateConfig.isFullScreenRefreshMode) {
                btnRetry.visibility = View.VISIBLE
                if (stateConfig.loadErrorBtnTextColor != 0) {
                    btnRetry.setTextColor(stateConfig.loadErrorBtnTextColor)
                }
                if (stateConfig.loadErrorBtnBgRes != 0) {
                    btnRetry.setBackgroundResource(stateConfig.loadErrorBtnBgRes)
                }
                if (!stateConfig.loadErrorBtnText.isNullOrEmpty()) {
                    btnRetry.setText(stateConfig.loadErrorBtnText)
                }
            } else {
                btnRetry.visibility = View.GONE
            }
            btnRetry.setOnClickListener {
                reload()
            }
            if (stateConfig.isFullScreenRefreshMode) {
                errorRemind!!.setOnClickListener {
                    reload()
                }
            }
        }
    }

    fun onReceivedError() {
        isHideErrorLayout = false
        errorRemind?.visibility = View.VISIBLE
        btnRetry.isEnabled = true
    }

    fun onProgressChanged(newProgress: Int) {
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

    private fun reload() {
        webView!!.reload()
        btnRetry.isEnabled = false
        isHideErrorLayout = true
    }
}