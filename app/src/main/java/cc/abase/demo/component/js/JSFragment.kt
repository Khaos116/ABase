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
import com.blankj.utilcode.util.Utils
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
      //字体大小不跟随系统
      ws.textZoom = 100
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
      //NORMAL（默认值）：在没有任何缩放的情况下，按照网页的原始宽度进行布局。这种布局适用于大部分网页，但可能出现一些水平滚动条
      //SINGLE_COLUMN：把所有内容放在 WebView 的一列中，并使其适应屏幕宽度。这种布局适用于移动端，可以避免水平滚动条出现，但可能导致部分内容的缩放
      //NARROW_COLUMNS：将页面分为更窄的列，以适应屏幕宽度。这种布局适用于在较窄屏幕上显示内容，可以更好地利用屏幕空间
      //ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
      ws.loadWithOverviewMode = true
      //Cannot call method ‘getItem’ of null
      ws.domStorageEnabled = true
      //ws.setAppCacheMaxSize(1024 * 1024 * 8)
      //val appCachePath: String = mContext.application.cacheDir.absolutePath
      //ws.setAppCachePath(appCachePath)
      //ws.setAppCacheEnabled(true)
      //DOM Storage 允许网页应用在客户端存储数据
      ws.domStorageEnabled = true
      //请求头，可以自行修改，这里使用系统默认的请求头(如果没有可能会导致页面加载到一半无法加载)
      ws.userAgentString = WebSettings.getDefaultUserAgent(Utils.getApp())
      //混合内容(在https连接中加载http连接的情况,默认WebView会阻止加载此类混合内容，可能会导致页面加载到一半无法加载)
      ws.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
      //是否禁止加载网络图片(把图片加载放在最后来加载渲染)
      ws.blockNetworkImage = false
    }
    bridgeWebView.webViewClient = object : BridgeWebViewClient(bridgeWebView) {
      @SuppressLint("WebViewClientOnReceivedSslError")
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