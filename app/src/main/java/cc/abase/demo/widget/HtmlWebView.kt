package cc.abase.demo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.webkit.*
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import cc.ab.base.ext.getMyLifecycleOwner
import cc.ab.base.ext.logE
import cc.ab.base.ext.removeParent

/**
 * 替换解析抱歉，可以使用Jsoup; https://www.jianshu.com/p/d2acd79c3d32
 * WebView全面解析：https://blog.csdn.net/vicwudi/article/details/81990467
 *
 * http://t.zoukankan.com/renhui-p-11401033.html
 * Author:Khaos
 * Date:2022/1/12
 * Time:9:38
 */
@SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
class HtmlWebView @JvmOverloads constructor(
  con: Context,
  attrs: AttributeSet? = null,
  defStyle: Int = 0
) : WebView(con, attrs, defStyle), DefaultLifecycleObserver {
  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    // 设置WebView支持JavaScript
    settings.javaScriptEnabled = true
    //支持自动适配
    settings.useWideViewPort = true
    settings.loadWithOverviewMode = true
    settings.setSupportZoom(true) //支持放大缩小

    settings.builtInZoomControls = true //显示缩放按钮

    settings.blockNetworkImage = true // 把图片加载放在最后来加载渲染

    settings.allowFileAccess = false // 不允许访问文件

    settings.saveFormData = true
    settings.setGeolocationEnabled(true)
    settings.domStorageEnabled = true
    settings.javaScriptCanOpenWindowsAutomatically = true /// 支持通过JS打开新窗口

    settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
    settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

    settings.defaultTextEncodingName = "utf-8"
    settings.loadsImagesAutomatically = true

    //设置不让其跳转浏览器
    this.webViewClient = object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        url?.let { u -> view?.loadUrl(u) }
        return false
      }
    }
    // 添加客户端支持
    this.webChromeClient = WebChromeClient()
    //不加这个图片显示不出来
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
      settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }
    settings.blockNetworkImage = false
    //允许cookie 不然有的网站无法登陆
    val mCookieManager: CookieManager = CookieManager.getInstance()
    mCookieManager.setAcceptCookie(true)
    mCookieManager.setAcceptThirdPartyCookies(this, true)
    setBackgroundColor(Color.TRANSPARENT)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="加载HTML">
  fun loadHtml(html: String, textColor: String = "#FFFFFF") {
    //修改默认文字大小+颜色 https://www.jianshu.com/p/dff75027fbfc
    //解决图片自适应屏幕宽度问题 https://blog.csdn.net/oZhuiMeng123/article/details/120830455
    val head = "<head>" +
        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
        "<style>*" +
        //去除外面边距
        "{margin:0;padding:0;}" +
        //设置默认文字大小 + 行高
        "{font-size:14px;line-height:20px;}" +
        //设置默认文字颜色
        "p{color:${textColor};}" +
        //设置段间距
        "p{margin: 10px auto}" +
        //设置图片最大宽度
        "img{max-width: 100%; width:auto; height:auto;}" +
        "</style>" +
        "</head>"
    this.loadDataWithBaseURL(null, "<html>$head<body>${html}</body></html>", "text/html", "utf-8", null)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="自感应生命周期">
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    setLifecycleOwner(getMyLifecycleOwner())
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    setLifecycleOwner(null)
    this.onPause()
    this.destroy()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Lifecycle生命周期">
  private var mLifecycle: Lifecycle? = null

  //通过Lifecycle内部自动管理暂停和播放(如果不需要后台播放)
  private fun setLifecycleOwner(owner: LifecycleOwner?) {
    if (owner == null) {
      mLifecycle?.removeObserver(this)
      mLifecycle = null
    } else {
      mLifecycle?.removeObserver(this)
      mLifecycle = owner.lifecycle
      mLifecycle?.addObserver(this)
    }
  }

  override fun onPause(owner: LifecycleOwner) {
    this.onPause()
    this.pauseTimers() //小心这个！！！暂停整个 WebView 所有布局、解析、JS
    "HtmlWebView:onPause".logE()
  }

  override fun onResume(owner: LifecycleOwner) {
    this.onResume()
    this.resumeTimers()
    "HtmlWebView:onResume".logE()
  }

  override fun onDestroy(owner: LifecycleOwner) {
    this.clearHistory()
    this.removeParent()
    this.loadUrl("about:blank")
    this.stopLoading()
    this.webChromeClient = null
    this.destroy()
    "HtmlWebView:onDestroy".logE()
  }
  //</editor-fold>
}