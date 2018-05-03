package com.halove.sonicwebview.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.halove.sonicwebview.Constants
import com.halove.sonicwebview.R

/**
 * Created by yanglijun on 18-3-21.
 */
class BrowseFragment : BaseWebFragment(){

    private var url: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        url = arguments.getString(Constants.PARAM_URL)
        if (TextUtils.isEmpty(url)) {
            return null
        }
        var view = LayoutInflater.from(context).inflate(R.layout.sonic_activity_default_browse, null)
        return view
    }

    override fun getUrl(): String {
        return url!!
    }

}