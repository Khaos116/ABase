package cc.abase.demo.component.set

import android.content.Context
import android.content.Intent
import cc.ab.base.ext.*
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.databinding.ActivitySettingBinding
import cc.abase.demo.rxhttp.repository.UserRepository
import cc.abase.demo.utils.AppInfoUtils
import cc.abase.demo.utils.CacheUtils
import com.blankj.utilcode.util.AppUtils
import kotlinx.coroutines.*

/**
 * Description:
 * @author: Khaos
 * @date: 2020/3/5 17:04
 */
class SettingActivity : CommBindTitleActivity<ActivitySettingBinding>() {
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, SettingActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun initContentView() {
    setTitleText("设置")
    viewBinding.settingLogout.pressEffectAlpha()
    viewBinding.setCacheLayout.click {
      if (mJob2?.isCompleted == false) return@click
      mJob2 = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch(Dispatchers.Main) {
        showActionLoading("缓存清理中")
        val size = CacheUtils.clearCache()
        delay(1000)
        if (isActive) {
          viewBinding.setCacheSize.text = size
          dismissActionLoading()
        }
      }
    }
    viewBinding.settingLogout.click {
      UserRepository.logOut()
      LoginActivity.startActivity(mContext)
    }
    viewBinding.setVersionTv.text = AppUtils.getAppVersionName()
    viewBinding.setVersionLayout.setOnClickListener { if (++clickCount == 10) viewBinding.settingAppInfo.text = AppInfoUtils.getAppInfo() }
    //读取缓存大小
    mJob1 = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch(Dispatchers.Main) {
      val size = CacheUtils.getCacheSize()
      if (isActive) viewBinding.setCacheSize.text = size
    }
  }

  private var clickCount = 0

  private var mJob1: Job? = null
  private var mJob2: Job? = null

  override fun finish() {
    super.finish()
    mJob1?.cancel()
    mJob2?.cancel()
  }

  override fun onDestroy() {
    super.onDestroy()
    mJob1?.cancel()
    mJob2?.cancel()
  }
}