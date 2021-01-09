package cc.abase.demo.component.rxhttp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.bean.wan.IntegralBean
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.constants.WanUrls
import com.blankj.utilcode.util.StringUtils
import com.rxjava.rxlife.life
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_rxhttp.*
import kotlinx.android.synthetic.main.layout_comm_title.commTitleText
import rxhttp.RxHttp

/**
 * Description:https://github.com/liujingxing/okhttp-RxHttp
 * @see cc.abase.demo.app.MyApplication.initRxHttp 初始化
 * @see cc.abase.demo.component.rxhttp.parser.ResponseWanParser 自定义解析
 *
 * @author: CASE
 * @date: 2020/2/19 16:39
 */
class RxHttpActivity : CommTitleActivity() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, RxHttpActivity::class.java)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResContentId() = R.layout.activity_rxhttp
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_rxhttp))
    rxhttpRequest.pressEffectAlpha()
    rxhttpRequest.click { requestData() }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="请求测试">
  var isRequesting = false

  //执行请求
  private fun requestData() {
    showLoadingView()
    if (isRequesting) {
      mContext.toast("正在请求请稍等")
      return
    }
    isRequesting = true
    RxHttp.get(WanUrls.User.INTEGRAL)
        .setAssemblyEnabled(true) //添加公共参数/头部
        .asResponseWan(IntegralBean::class.java)
        .observeOn(AndroidSchedulers.mainThread()) //指定在主线程回调
        .doOnComplete { }
        .life(this) //自动销毁请求
        .subscribe({
          isRequesting = false
          dismissLoadingView()
          rxhttpResult.text = it.toString()
        }, {
          isRequesting = false
          dismissLoadingView()
          rxhttpResult.text = it.message
        })
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="重写loading相关位置和大小">
  override fun getHoldViewHeight() = rxhttpScroll.height

  override fun getHoldViewTransY() = (commTitleText.height + rxhttpRequest.height + mStatusBarHeight) / 2f

  override fun getHoldViewBgColor() = Color.WHITE
  //</editor-fold>
}