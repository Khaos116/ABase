package cc.abase.demo.component.spedit

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.lifecycle.Observer
import cc.ab.base.ext.*
import cc.ab.base.utils.CcInputHelper
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.sticky.StickyActivity
import cc.abase.demo.constants.EventKeys
import cc.abase.demo.repository.bean.local.AtBean
import cc.abase.demo.repository.bean.local.CityBean
import cc.abase.demo.widget.spedit.MySelectDeleteKeyEventProxy
import cc.abase.demo.widget.spedit.SpeditUtil
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.StringUtils
import com.google.gson.reflect.TypeToken
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.android.synthetic.main.activity_spedit.*

/**
 * Description:@或者#变色效果
 * @author: caiyoufei
 * @date: 2019/11/30 20:49
 */
class SpeditActivity : CommTitleActivity() {
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, SpeditActivity::class.java)
      context.startActivity(intent)
    }
  }

  //最大输入数量
  private val MAX_LENGTH = 200

  override fun layoutResContentId() = R.layout.activity_spedit

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_spedit))
    //点击效果
    speditChoose.pressEffectAlpha()
    //删除效果
    speditInput.setKeyEventProxy(MySelectDeleteKeyEventProxy())
    //最大输入长度
    SpeditUtil.instance.setInputFilter(speditInput, MAX_LENGTH)
    //点击事件
    speditChoose.click { StickyActivity.startActivity(mContext, true) }
    speditShow.click { getSubmitInfo() }
    speditResult.click { mContext.toast(speditResult.text.toString()) }
    //监听长度输入
    speditLen.text = String.format("0/%s", MAX_LENGTH)
    speditInput.addTextWatcher {
      speditLen.text = String.format(
          "%s/$MAX_LENGTH", CcInputHelper.getRealLength(it?.toString() ?: "")
      )
    }
  }

  override fun initData() {
    LiveEventBus.get(EventKeys.CHOOSE_STICKY, CityBean::class.java)
        .observe(this, Observer {
          if (TextUtils.equals(mContext::class.java.name, it.fromTag)) {
            SpeditUtil.instance.insertUser(speditInput, it.regionFullName ?: "", it.id, MAX_LENGTH)
          }
        })
  }

  //获取需要提交的信息
  private fun getSubmitInfo() {
    val result = SpeditUtil.instance.getEditTextEnter1(speditInput)
    val atList = SpeditUtil.instance.getAtList1Enter(speditInput)
    val atJson = GsonUtils.toJson(atList)
    showSubmitInfo(result.toString(), atJson)
  }

  //显示@效果
  private fun showSubmitInfo(
    content: String,
    atJson: String
  ) {
    //模仿从服务器返回的描述和at字段
    val atList = GsonUtils.fromJson<MutableList<AtBean>>(
        atJson, object : TypeToken<MutableList<AtBean>>() {}.type
    )
    //处理span点击效果
    val span = SpeditUtil.instance.getAtSpan(content, atList, click = {
      mContext.toast(it.name)
    })
    //设置展示
    speditResult.text = span
  }
}