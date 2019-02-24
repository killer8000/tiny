package com.tiny.component.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View

abstract class IPluginActivity {


    /**
     * 在oncreate前被执行，这里可以设置一些activity的基础信息，例如设置全屏
     */
    abstract fun init(activity: Activity)

    abstract fun getActivity(): Activity

    abstract fun getRootView(): View

    abstract fun onCreate(savedInstanceState: Bundle?): View

    abstract fun onResume()

    abstract fun onPause()

    abstract fun onDestroy()

    open fun onNewIntent(intent: Intent?) {}

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {}

     fun setResult(requestCode: Int, intent: Intent) {
        getActivity().setResult(requestCode, intent)
    }

     fun setResult(requestCode: Int) {
        getActivity().setResult(requestCode)
    }

     fun <T : View> findViewById(resource: Int): T? {
        return getRootView()?.findViewById(resource)
    }

    fun getResource(): Resources? {
        return getActivity()?.resources
    }
}
