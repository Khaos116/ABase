package cc.abase.demo.component.login

import android.content.Context
import android.content.Intent
import android.util.Log
import cc.ab.base.ext.*
import cc.ab.base.utils.CcInputHelper
import cc.ab.base.utils.PressEffectHelper
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.constants.UiConstants
import cc.abase.demo.repository.UserRepository
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/10 14:58
 */
class LoginActivity : CommActivity() {
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, LoginActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun layoutResId() = R.layout.activity_login
  override fun initView() {
    checkSubmit()
    PressEffectHelper.alphaEffect(loginRegister)
    CcInputHelper.wrapCommCountLimit(loginEditAccount, 30, 0)
    CcInputHelper.wrapCommCountLimit(loginEditPassword, 30, 0)
    loginEditAccount.addTextWatcher { checkSubmit() }
    loginEditPassword.addTextWatcher { checkSubmit() }
    loginSubmit.click {
      showActionLoading()
      UserRepository.instance.login(
          loginEditAccount.text.toString(),
          loginEditPassword.text.toString()
      )
          .subscribe { result, error ->
            dismissActionLoading()
            Log.e("CASE", "result=${result}")
            Log.e("CASE", "error=${error}")
          }
    }
    loginRegister.click { RegisterActivity.startActivity(mContext) }
  }

  override fun needKeyListener() = true

  override fun initData() {

  }

  private fun checkSubmit() {
    val textAcc = loginEditAccount.text
    val textPass = loginEditPassword.text
    val enable = textAcc.length >= 3 && textPass.length >= 6
    loginSubmit.isEnabled = enable
    loginSubmit.alpha = if (enable) 1f else UiConstants.disable_alpha
    if (enable) {
      loginSubmit.pressEffectAlpha()
    } else {
      loginSubmit.pressEffectDisable()
    }
  }
}