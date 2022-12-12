package cc.abase.demo.component.js

import android.annotation.SuppressLint
import android.widget.FrameLayout
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindFragment
import cc.abase.demo.databinding.FragmentJsBinding
import com.blankj.utilcode.util.TimeUtils
import com.github.lzyzsd.jsbridge.BridgeWebView
import com.github.lzyzsd.jsbridge.CallBackFunction

/**
 * JS交互
 * Author:Khaos116
 * Date:2022/12/12
 * Time:15:30
 */
class JSFragment : CommBindFragment<FragmentJsBinding>() {
  //<editor-fold defaultstate="collapsed" desc="初始化">
  @SuppressLint("SetJavaScriptEnabled")
  override fun lazyInit() {
    viewBinding.tvCallJS.pressEffectAlpha()
    viewBinding.flTitle.commTitleBack.pressEffectAlpha()
    viewBinding.flTitle.commTitleText.text = R.string.JS交互调用.xmlToString()
    viewBinding.flTitle.commTitleBack.click { mActivity.onBackPressed() }
    val bridgeWebView = BridgeWebView(mContext)
    viewBinding.flWebView.addView(bridgeWebView, FrameLayout.LayoutParams(-1, -1))
    viewBinding.tvCallJS.click {
      bridgeWebView.callHandler("functionInJs", "这是APP调用JS的传入参数") { s: String? ->
        "APP收到HTML调用回调:\n${s ?: "null"}".toast()
      }
    }
    bridgeWebView.setDefaultHandler { data: String?, function: CallBackFunction? ->
      "APP收到HTML默认调用:\n${data ?: "null"}".toast()
      function?.onCallBack("HTML默认调用APP成功 ${TimeUtils.millis2String(System.currentTimeMillis())}")
    }
    bridgeWebView.registerHandler("submitFromWeb") { data: String?, function: CallBackFunction? ->
      "APP收到HTML专用调用:\n${data ?: "null"}".toast()
      function?.onCallBack("HTML专用调用APP成功 ${TimeUtils.millis2String(System.currentTimeMillis())}")
    }
    val htmlUrl = "file:///android_asset/jscript.html"
    bridgeWebView.settings.javaScriptEnabled = true
    bridgeWebView.loadUrl(htmlUrl)
  }
  //</editor-fold>
}