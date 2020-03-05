package cc.abase.demo.component.set

import android.content.Context
import android.content.Intent
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.config.NetConfig
import cc.abase.demo.fuel.repository.UserRepositoryFuel
import cc.abase.demo.rxhttp.repository.UserRepository
import kotlinx.android.synthetic.main.activity_setting.settingLogout

/**
 * Description:
 * @author: caiyoufei
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
    settingLogout.click {
      if (NetConfig.USE_RXHTTP) UserRepository.instance.logOut()
      else UserRepositoryFuel.instance.logOut()
      LoginActivity.startActivity(mContext)
    }
  }

  override fun initData() {
  }
}