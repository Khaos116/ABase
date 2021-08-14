package cc.abase.demo.component.web

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.net.http.SslError
import android.view.*
import android.webkit.*
import cc.ab.base.ext.*
import cc.ab.base.widget.engine.ImageEngine
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.config.HeaderManger
import cc.abase.demo.databinding.ActivityWebBinding
import cc.abase.demo.widget.LollipopFixedWebView
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.UriUtils
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import java.io.File

/**
 * Description: 如果需要js对接，参考添加BridgeWebView https://github.com/lzyzsd/JsBridge
 * @author: Khaos
 * @date: 2019/10/3 15:25
 */
class WebActivity : CommBindTitleActivity<ActivityWebBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    private const val WEB_URL = "INTENT_KEY_WEB_URL"
    fun startActivity(context: Context, url: String) {
      val intent = Intent(context, WebActivity::class.java)
      //"https://chatlink.mstatik.com/widget/standalone.html?eid=125038"
      if (url.isNotBlank()) intent.putExtra(WEB_URL, url)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //需要加载的web地址
  private var webUrl: String? = null

  //AgentWeb相关
  private var agentWeb: AgentWeb? = null
  private var agentBuilder: AgentWeb.CommonBuilder? = null

  //图片、视频选择回调
  private var mUploadCall: ValueCallback<Array<Uri>>? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  //初始化view
  @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
  override fun initContentView() {
    viewBinding.root.removeAllViews()
    initAgentBuilder()
    webUrl = intent.getStringExtra(WEB_URL) ?: "https://www.baidu.com" //获取加载地址
    agentWeb = agentBuilder?.createAgentWeb()?.ready()?.go(webUrl) //创建web并打开
    //agentWeb = agentBuilder?.createAgentWeb()?.ready()?.get()?.also {
    //  it.urlLoader?.loadData(htmlStr, "text/html", "UTF-8") //打开Html代码
    //}
    //设置适配
    val web = agentWeb?.webCreator?.webView
    web?.settings?.let { ws ->
      //支持javascript
      ws.javaScriptEnabled = true
      //设置可以支持缩放
      ws.setSupportZoom(true)
      //设置内置的缩放控件
      ws.builtInZoomControls = true
      //隐藏原生的缩放控件
      ws.displayZoomControls = false
      //扩大比例的缩放
      ws.useWideViewPort = true
      //允许WebView使用File协议
      ws.allowFileAccess = true
      //自适应屏幕
      ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
      ws.loadWithOverviewMode = true
    }
    //解决键盘不弹起的BUG
    web?.requestFocus(View.FOCUS_DOWN)
    web?.setOnTouchListener { v, event ->
      when (event.action) {
        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP -> if (!v.hasFocus()) {
          v.requestFocus()
        }
      }
      false
    }
    //有些页面不能自动设置，需要自己计算缩放比
    //web?.addJavascriptInterface(JavaScriptInterface(), "HTMLOUT")
    //agentWeb?.webCreator?.webView?.let { webView ->
    //    if (webView is BridgeWebView) {
    //        initJsBridge(webView)
    //    }
    //}
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化web">
  //初始化web
  private fun initAgentBuilder() {
    //为了解决安卓5.x的bug
    val webView = LollipopFixedWebView(this)
    webView.overScrollMode = View.OVER_SCROLL_NEVER
    webView.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
    agentBuilder = AgentWeb.with(this)
      .setAgentWebParent(viewBinding.root, ViewGroup.LayoutParams(-1, -1)) //添加到父容器
      .useDefaultIndicator(ColorUtils.getColor(R.color.colorPrimary)) //设置进度条颜色
      //.setWebViewClient(getWebViewClient())//监听结束，适配宽度
      .setWebViewClient(getWebViewClientSSL()) //SSL
      .setWebChromeClient(webChromeClient) //监听标题
      .setWebView(webView) //真正的webview
      .setMainFrameErrorView(R.layout.agentweb_error_page, -1) //失败的布局
      .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
      .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK) //打开其他应用时，弹窗咨询用户是否前往其他应用
      .interceptUnkownUrl() //拦截找不到相关页面的Scheme
    //给WebView添加Header
    val headers = HeaderManger.getStaticHeaders()
    agentBuilder?.additionalHttpHeader(webUrl, headers)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="标题获取、文件选择">
  //获取标题
  private val webChromeClient = object : com.just.agentweb.WebChromeClient() {
    override fun onReceivedTitle(view: WebView?, title: String?) {
      super.onReceivedTitle(view, title)
      title?.let { setTitleText(it) }
    }

    //Web调用图片、视频选择
    override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
      fileChooserParams?.acceptTypes?.firstOrNull()?.let { type ->
        mUploadCall = filePathCallback
        when {
          type.startsWith("image/") -> go2SelMedia(PictureMimeType.ofImage())
          type.startsWith("video/") -> go2SelMedia(PictureMimeType.ofVideo())
          type.startsWith("audio/") -> go2SelMedia(PictureMimeType.ofAudio())
          else -> {
            "暂不支持该类型选择".toast()
            filePathCallback?.onReceiveValue(null)
            mUploadCall = null
          }
        }
      }
      return true
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="JS交互">
  //private fun initJsBridge(webView: BridgeWebView) {
  //webView.setDefaultHandler(BridgeHandler { data, function ->
  //    "Js交互未指定方法data：$data".logE()
  //    function.onCallBack("reaponse 返回的数据")
  //})
  //webView.registerHandler(JsBridgeMethod.METHOD_NATIVE, object:BridgeHandler{})
  //}
  ////计算内容宽度和屏幕宽度设置缩放比
  //internal inner class JavaScriptInterface {
  //    @JavascriptInterface
  //    fun getContentWidth(value: String?) {
  //        if (value != null) {
  //            try {
  //                val width = Integer.parseInt(value)
  //                if (width > 0) {
  //                    runOnUiThread {
  //                        agentWeb?.webCreator?.webView?.setInitialScale((mScreenWidth * 100f / width).toInt())
  //                        webRootView.postDelayed(
  //                            { agentWeb?.webCreator?.webView?.visible() },
  //                            300
  //                        )
  //                    }
  //                } else {
  //                    agentWeb?.webCreator?.webView?.visible()
  //                }
  //            } catch (e: Exception) {
  //                e.printStackTrace()
  //                agentWeb?.webCreator?.webView?.visible()
  //            }
  //        }
  //    }
  //}
  //
  ////是否需要调用内容宽度自适应
  //private var needFitWidth = false
  ////自适应宽度使用 测试地址：http://file.aimymusic.com/privacyClause.html
  //private fun getWebViewClient(): com.just.agentweb.WebViewClient {
  //    return object : com.just.agentweb.WebViewClient() {
  //        override fun onPageFinished(view: WebView?, url: String?) {
  //            super.onPageFinished(view, url)
  //            if (needFitWidth) {
  //                agentWeb?.webCreator?.webView?.invisible()
  //                view?.loadUrl(
  //                    "javascript:window.HTMLOUT.getContentWidth(document.getElementsByTagName('html')[0].scrollWidth);"
  //                )
  //            }
  //        }
  //    }
  //}
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="解决SSL无法打开的问题+URL拦截">
  //解决SSL无法打开的问题
  private fun getWebViewClientSSL(): com.just.agentweb.WebViewClient {
    return object : com.just.agentweb.WebViewClient() {
      override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
        handler.proceed()
      }

      @Suppress("DEPRECATION")
      override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return shouldOverrideUrlLoading(view, request?.url?.toString())
      }

      override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        view?.settings?.cacheMode = WebSettings.LOAD_DEFAULT
        url?.let { u ->
          if (u.startsWith("Khaos://")) {
            //自己处理跳转拦截，需要自己处理返回true
            return true
          } else "Web内部跳转Url=$u".logI()
        }
        return super.shouldOverrideUrlLoading(view, url)
      }

      override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)
        error?.let { e ->
          if (e.errorCode == 330 && e.description.toString().uppercase().contains("ERR_CONTENT_DECODING_FAILED")) {
            "Android WebView暂不支持Br压缩方式，请联系H5相关人员".toast()
          }
        }
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="选择图片、视频">
  @SuppressLint("SourceLockedOrientationActivity")
  private fun go2SelMedia(chooseMode: Int) {
    //https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-Api%E8%AF%B4%E6%98%8E
    PictureSelector.create(this)
      .openGallery(chooseMode)
      .imageEngine(ImageEngine())
      .isGif(false)
      .isCamera(false)
      .isEnableCrop(true)
      .isPageStrategy(true, PictureConfig.MAX_PAGE_SIZE, true) //过滤掉已损坏的
      .maxSelectNum(1)
      .queryMaxFileSize(if (chooseMode == PictureMimeType.ofImage()) 5f else 500f)
      .isPreviewVideo(true)
      .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
      .forResult(PictureConfig.CHOOSE_REQUEST)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="图片选择回调">
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == PictureConfig.CHOOSE_REQUEST) { //处理文件选择
      var hasDeal = false
      if (resultCode == Activity.RESULT_OK) {
        // 图片、视频、音频选择结果回调
        PictureSelector.obtainMultipleResult(data)?.let { medias ->
          if (medias.isNotEmpty()) {
            try {
              hasDeal = true
              mUploadCall?.onReceiveValue(arrayOf(UriUtils.file2Uri(File(medias.first().cutPath ?: medias.first().realPath))))
            } catch (e: Exception) {
              e.logE()
            }
          }
        }
      }
      if (!hasDeal) mUploadCall?.onReceiveValue(null)
      mUploadCall = null
    }
  }
  //</editor-fold>
}