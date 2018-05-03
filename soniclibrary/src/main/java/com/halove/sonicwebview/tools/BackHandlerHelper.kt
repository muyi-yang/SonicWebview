package com.halove.sonicwebview.tools

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import com.halove.sonicwebview.IFragmentBackHandler

/**
 * 工具类，用于实现分发back事件,Fragment和Activity的外理逻辑是一样，所以两者都需要调用该类的方法
 * Created by yanglijun on 18-3-21.
 */

object BackHandlerHelper {
    /**
     * 将back事件分发给 FragmentManager 中管理的子Fragment，如果该 FragmentManager 中的所有Fragment都
     * 没有处理back事件，则尝试 FragmentManager.popBackStack()
     *
     * @return 如果处理了back键则返回 **true**
     * @see .handleBackPress
     * @see .handleBackPress
     */
    fun handleBackPress(fragmentManager: FragmentManager): Boolean {
        val fragments = fragmentManager.fragments ?: return false

        for (i in fragments.indices.reversed()) {
            val child = fragments[i]

            if (isFragmentBackHandled(child)) {
                return true
            }
        }

        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            return true
        }
        return false
    }

    fun handleBackPress(fragment: Fragment): Boolean {
        return handleBackPress(fragment.childFragmentManager)
    }

    fun handleBackPress(fragmentActivity: FragmentActivity): Boolean {
        return handleBackPress(fragmentActivity.supportFragmentManager)
    }

    /**
     * 判断Fragment是否处理了Back键
     *
     * @return 如果处理了back键则返回 **true**
     */
    fun isFragmentBackHandled(fragment: Fragment?): Boolean {
        return (fragment != null
                && fragment.isVisible
                && fragment.userVisibleHint //for ViewPager

                && fragment is IFragmentBackHandler
                && (fragment as IFragmentBackHandler).onBackPressed())
    }

}
