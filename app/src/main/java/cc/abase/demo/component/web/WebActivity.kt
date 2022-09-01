package cc.abase.demo.component.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.net.http.SslError
import android.view.*
import android.webkit.*
import cc.ab.base.ext.*
import cc.ab.base.widget.engine.CoilEngine
import cc.ab.base.widget.engine.MyCompressEngine
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.config.HeaderManger
import cc.abase.demo.databinding.ActivityWebBinding
import cc.abase.demo.widget.LollipopFixedWebView
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.UriUtils
import com.hjq.permissions.*
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import java.io.File


/**
 * WebView加载Html 解决图片自适应屏幕宽度问题
 * Description: https://blog.csdn.net/oZhuiMeng123/article/details/120830455
 * val head = "<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> <style>*{margin:0;padding:0;}img{max-width: 100%; width:auto; height:auto;}</style></head>"
 * webview.loadDataWithBaseURL(null, "<html>$head<body>$content</body></html>", "text/html", "utf-8", null)
 *
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
    //解决部分HTML不自动跳转二级页面的BUG
    viewBinding.root.post { agentWeb?.webLifeCycle?.onResume() }
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
    override fun onShowFileChooser(
      webView: WebView?,
      filePathCallback: ValueCallback<Array<Uri>>?,
      fileChooserParams: FileChooserParams?
    ): Boolean {
      fileChooserParams?.acceptTypes?.firstOrNull()?.let { type ->
        mUploadCall = filePathCallback
        when {
          type.startsWith("image/") -> checkSD(SelectMimeType.ofImage())
          type.startsWith("video/") -> checkSD(SelectMimeType.ofVideo())
          type.startsWith("audio/") -> checkSD(SelectMimeType.ofAudio())
          else -> {
            R.string.暂不支持该类型选择.xmlToast()
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

      override fun onPageFinished(view: WebView?, url: String?) {
        //默认打开聊天
        //view?.loadUrl("javascript:window.HubSpotConversations.widget.open()")
        //view?.loadUrl(HUB_CODE)
        //默认关闭聊天窗口
        //view?.loadUrl("javascript:window.HubSpotConversations.widget.close()")
        super.onPageFinished(view, url)
      }

      private val HUB_CODE = "javascript:window.addEventListener(\"message\", function( event ) {\n" +
          "    if (event.origin === 'https://app.hubspot.com') {\n" +
          "        var data = JSON.parse(event.data);\n" +
          "        console.log(data);\n" +
          "        if (data.type === 'open-change' && data.data.isOpen === false) {\n" +
          "            window.location.href = 'js://webview?event=close';\n" +
          "        }\n" +
          "    }\n" +
          "});"

      @Suppress("DEPRECATION")
      override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return shouldOverrideUrlLoading(view, request?.url?.toString())
      }

      override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        view?.settings?.cacheMode = WebSettings.LOAD_DEFAULT
        url?.let { u ->
          if (url.startsWith("js://webview?event=close")) { // 用户关闭会话回调
            onBackPressed()
            return true
          } else if (u.startsWith("Khaos://")) {
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
            "Android WebView暂不支持Br压缩方式，请联系H5相关人员".logE()
          }
        }
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="选择图片、视频">
  private fun checkSD(chooseMode: Int) {
    //请求SD卡权限
    val hasSDPermission = XXPermissions.isGranted(mContext, Permission.MANAGE_EXTERNAL_STORAGE)
    if (!hasSDPermission) {
      XXPermissions.with(this)
        .permission(Permission.MANAGE_EXTERNAL_STORAGE)
        .request(object : OnPermissionCallback {
          override fun onGranted(permissions: MutableList<String>, all: Boolean) {
            go2SelMedia(chooseMode)
          }

          override fun onDenied(permissions: MutableList<String>, never: Boolean) {
            R.string.需要给予权限才能使用该功能.xmlToString()
          }
        })
    } else {
      go2SelMedia(chooseMode)
    }
  }

  @SuppressLint("SourceLockedOrientationActivity")
  private fun go2SelMedia(chooseMode: Int) {
    //https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-Api%E8%AF%B4%E6%98%8E
    PictureSelector.create(this)
      .openGallery(chooseMode)
      .setImageEngine(CoilEngine())
      .isGif(false)
      //.setCropEngine(MyCropEngine())//初步测试好像没办法实现先裁切后压缩(SD卡没有找到2个文件同时存在)，所以暂时只进行压缩
      .setCompressEngine(MyCompressEngine())//本来想先裁切后压缩的，但是同时设置后，只找的到裁切文件，没有压缩文件，所以只进行压缩
      .isPageStrategy(true, PictureConfig.MAX_PAGE_SIZE, true) //过滤掉已损坏的
      .setMaxSelectNum(1)
      .setFilterMaxFileSize((if (chooseMode == SelectMimeType.ofImage()) 5L else 500L) * MemoryConstants.MB)
      .isPreviewVideo(true)
      .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
      .forResult(object : OnResultCallbackListener<LocalMedia> {
        override fun onResult(result: ArrayList<LocalMedia>?) {
          var hasDeal = false
          if (!result.isNullOrEmpty()) {
            try {
              mUploadCall?.onReceiveValue(arrayOf(UriUtils.file2Uri(File(result.first().cutPath ?: result.first().realPath))))
              hasDeal = true
            } catch (e: Exception) {
              e.logE()
            }
          }
          if (!hasDeal) mUploadCall?.onReceiveValue(null)
          mUploadCall
        }

        override fun onCancel() {
          mUploadCall?.onReceiveValue(null)
          mUploadCall = null
        }
      })
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="生命周期">
  override fun onResume() {
    super.onResume()
    agentWeb?.webLifeCycle?.onResume()
  }

  override fun onPause() {
    super.onPause()
    agentWeb?.webLifeCycle?.onPause()
  }

  override fun onDestroy() {
    super.onDestroy()
    agentWeb?.webLifeCycle?.onDestroy()
  }
  //</editor-fold>
}