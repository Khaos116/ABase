package cc.abase.demo.component.spedit

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import cc.ab.base.ext.*
import cc.ab.base.utils.CcInputHelper
import cc.abase.demo.R
import cc.abase.demo.bean.local.AtBean
import cc.abase.demo.bean.local.CityBean
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.component.sticky.StickyActivity
import cc.abase.demo.constants.EventKeys
import cc.abase.demo.databinding.ActivitySpeditBinding
import cc.abase.demo.widget.spedit.MySelectDeleteKeyEventProxy
import cc.abase.demo.widget.spedit.SpeditUtil
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.StringUtils
import com.google.gson.reflect.TypeToken
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * Description:@或者#变色效果
 * @author: CASE
 * @date: 2019/11/30 20:49
 */
class SpeditActivity : CommBindTitleActivity<ActivitySpeditBinding>() {
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, SpeditActivity::class.java)
      context.startActivity(intent)
    }
  }

  //最大输入数量
  private val MAX_LENGTH = 200

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_spedit))
    //点击效果
    viewBinding.speditChoose.pressEffectAlpha()
    //删除效果
    viewBinding.speditInput.setKeyEventProxy(MySelectDeleteKeyEventProxy())
    //最大输入长度
    SpeditUtil.setInputFilter(viewBinding.speditInput, MAX_LENGTH)
    //点击事件
    viewBinding.speditChoose.click { StickyActivity.startActivity(mContext, true) }
    viewBinding.speditShow.click { getSubmitInfo() }
    viewBinding.speditResult.click { mContext.toast(viewBinding.speditResult.text.toString()) }
    //监听长度输入
    viewBinding.speditLen.text = String.format("0/%s", MAX_LENGTH)
    viewBinding.speditInput.addTextWatcher {
      viewBinding.speditLen.text = String.format(
          "%s/$MAX_LENGTH", CcInputHelper.getRealLength(it?.toString() ?: "")
      )
    }
    LiveEventBus.get(EventKeys.CHOOSE_STICKY, CityBean::class.java).observe(this) {
      if (TextUtils.equals(mContext::class.java.name, it.fromTag)) {
        SpeditUtil.insertUser(viewBinding.speditInput, it.regionFullName ?: "", it.id, MAX_LENGTH)
      }
    }
  }

  //获取需要提交的信息
  private fun getSubmitInfo() {
    val result = SpeditUtil.getEditTextEnter1(viewBinding.speditInput)
    val atList = SpeditUtil.getAtList1Enter(viewBinding.speditInput)
    val atJson = GsonUtils.toJson(atList)
    showSubmitInfo(result.toString(), atJson)
  }

  //显示@效果
  private fun showSubmitInfo(content: String, atJson: String) {
    //模仿从服务器返回的描述和at字段
    val atList = GsonUtils.fromJson<MutableList<AtBean>>(
        atJson, object : TypeToken<MutableList<AtBean>>() {}.type
    )
    //处理span点击效果
    val span = SpeditUtil.getAtSpan(content, atList, click = {
      mContext.toast(it.name)
    })
    //设置展示
    viewBinding.speditResult.text = span
  }
}