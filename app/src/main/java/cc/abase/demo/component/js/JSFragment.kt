package cc.abase.demo.component.js

import android.annotation.SuppressLint
import android.net.http.SslError
import android.webkit.*
import android.widget.FrameLayout
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindFragment
import cc.abase.demo.databinding.FragmentJsBinding
import com.blankj.utilcode.util.TimeUtils
import com.github.lzyzsd.jsbridge.*


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
    bridgeWebView.settings.let { ws ->
      //支持javascript
      ws.javaScriptEnabled = true
      ////设置可以支持缩放
      //ws.setSupportZoom(true)
      ////设置内置的缩放控件
      //ws.builtInZoomControls = true
      ////隐藏原生的缩放控件
      //ws.displayZoomControls = false
      ////扩大比例的缩放
      //ws.useWideViewPort = true
      //允许WebView使用File协议
      ws.allowFileAccess = true
      ////设置网页字体不跟随系统字体发生改变
      //ws.textZoom = 100
      ////自适应屏幕
      //ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
      ws.loadWithOverviewMode = true
      //Cannot call method ‘getItem’ of null
      ws.domStorageEnabled = true
      ws.setAppCacheMaxSize(1024 * 1024 * 8)
      val appCachePath: String = mContext.application.cacheDir.absolutePath
      ws.setAppCachePath(appCachePath)
      ws.setAppCacheEnabled(true)
    }
    bridgeWebView.webViewClient = object : BridgeWebViewClient(bridgeWebView) {
      override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        handler?.proceed()
      }
    }
    bridgeWebView.webChromeClient = object : WebChromeClient() {
      override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
      }
    }
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
    bridgeWebView.loadUrl(htmlUrl)
  }
  //</editor-fold>
}