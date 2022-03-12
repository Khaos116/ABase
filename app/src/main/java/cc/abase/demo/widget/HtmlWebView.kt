package cc.abase.demo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.http.SslError
import android.os.Build
import android.util.AttributeSet
import android.webkit.*
import androidx.lifecycle.*
import cc.ab.base.ext.*
import org.jsoup.Jsoup

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
    settings.setSupportZoom(false) //支持放大缩小

    settings.builtInZoomControls = false //不显示缩放按钮

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
      @SuppressLint("WebViewClientOnReceivedSslError")
      override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
        handler.proceed()
      }

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
  fun loadHtml(divHtml: String) {
    val document = Jsoup.parse(divHtml)

    //解决宽度+缩放问题
    val headElements = document.select("head")
    for (element in headElements) {
      //文字缩放
      element.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">")
      //去除外面边距
      element.append("<style>*{margin:0;padding:0;}</style>")
    }

    //解决特定情况下，图片外层宽度导致图片无法适配屏幕宽度问题
    val divElements = document.select("div")
    for (element in divElements) {
      val str = element.attributes().toString()
      if (str.contains("style") && str.contains("width")) {
        element.attr("style", "width: 100%; height: auto")
      }
    }

    //图片宽度适配1
    val pImgElements = document.select("p:has(img)")
    for (element in pImgElements) {
      element.attr("style", "max-width:100%;height:auto")
    }

    //图片宽度适配2
    val imgElements = document.select("img")
    for (element in imgElements) {
      //重新设置宽高
      element.attr("style", "max-width:100%;height:auto")
    }

    //适配文字间距
    val pElements = document.select("p")
    for (element in pElements) {
      val tags = element.getElementsByTag("strong")
      if (tags.isNullOrEmpty()) {//设置段间距+左右间距
        element.attr("style", "margin-bottom:10px;margin-left:10px;margin-right:10px")
        if (element.childrenSize() == 0) {
          if (element.text().isBlank()) {
            element.attr("style", "margin-bottom:0px;margin-left:10px;margin-right:10px")
          } else if (!element.text().startsWith(" ")) {
            element.text("\u3000\u3000${element.text()}")
          }
        }
      } else {//设置段间距
        val hasImg = !element.getElementsByTag("img").isNullOrEmpty()
        if (hasImg) {
          element.attr("style", "margin-bottom:10px")
        } else {
          element.attr("style", "margin-bottom:10px;margin-left:10px;margin-right:10px")
        }
      }
    }
    val result = document.toString()
    "\n$result".logD()
    this.loadDataWithBaseURL(null, result, "text/html", "utf-8", null)
  }

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
    "HtmlWebView:onPause".logD()
  }

  override fun onResume(owner: LifecycleOwner) {
    this.onResume()
    this.resumeTimers()
    "HtmlWebView:onResume".logD()
  }

  override fun onDestroy(owner: LifecycleOwner) {
    this.clearHistory()
    this.removeParent()
    this.loadUrl("about:blank")
    this.stopLoading()
    this.webChromeClient = null
    this.destroy()
    "HtmlWebView:onDestroy".logD()
  }
  //</editor-fold>
}