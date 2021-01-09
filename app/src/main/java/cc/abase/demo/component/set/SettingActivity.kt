package cc.abase.demo.component.set

import android.content.Context
import android.content.Intent
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.rxhttp.repository.UserRepository
import cc.abase.demo.utils.CacheUtils
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.coroutines.*

/**
 * Description:
 * @author: CASE
 * @date: 2020/3/5 17:04
 */
class SettingActivity : CommTitleActivity() {
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, SettingActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun layoutResContentId() = R.layout.activity_setting

  override fun initContentView() {
    setTitleText("设置")
    settingLogout.pressEffectAlpha()
    setCacheLayout.pressEffectBgColor()
    setCacheLayout.click {
      if (mJob2?.isCompleted == false) return@click
      mJob2 = GlobalScope.launch(Dispatchers.Main) {
        showActionLoading("缓存清理中")
        val size = CacheUtils.clearCache()
        delay(1000)
        if (isActive) {
          setCacheSize.text = size
          dismissActionLoading()
        }
      }
    }
    settingLogout.click {
      UserRepository.logOut()
      LoginActivity.startActivity(mContext)
    }
  }

  private var mJob1: Job? = null
  private var mJob2: Job? = null
  override fun initData() {
    //读取缓存大小
    mJob1 = GlobalScope.launch(Dispatchers.Main) {
      val size = CacheUtils.getCacheSize()
      if (isActive) setCacheSize.text = size
    }
  }

  override fun finish() {
    super.finish()
    mJob1?.cancel()
    mJob2?.cancel()
  }
}