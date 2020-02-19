package cc.abase.demo.component.rxhttp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.constants.WanUrls
import cc.abase.demo.repository.bean.wan.IntegralBean
import com.blankj.utilcode.util.StringUtils
import com.rxjava.rxlife.life
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_rxhttp.*
import kotlinx.android.synthetic.main.layout_comm_title.commTitleText
import rxhttp.wrapper.param.RxHttp

/**
 * Description:https://github.com/liujingxing/okhttp-RxHttp
 * @author: caiyoufei
 * @date: 2020/2/19 16:39
 */
class RxHttpActivity : CommTitleActivity() {

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, RxHttpActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun layoutResContentId() = R.layout.activity_rxhttp

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_rxhttp))
    rxhttpRequest.pressEffectAlpha()
    rxhttpRequest.click { requestData() }
  }

  override fun initData() {
  }

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
        .subscribeOnIo()
        .setAssemblyEnabled(true)//添加公共参数/头部
        .asResponseWan(IntegralBean::class.java)
        .observeOn(AndroidSchedulers.mainThread()) //指定在主线程回调
        .life(this)//自动销毁请求
        .subscribe({ bean ->
          dismissLoadingView()
          rxhttpResult.text = bean.toString()
        },
            { e -> rxhttpResult.text = e.message },
            { isRequesting = false })
  }

  override fun getLoadingViewHeight() = rxhttpScroll.height

  override fun getLoadingViewTransY() =
    (commTitleText.height + rxhttpRequest.height + mStatusBarHeight) / 2f

  override fun getLoadingViewBgColor() = Color.WHITE
}