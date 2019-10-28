package cc.abase.demo.component.login

import android.content.Context
import android.content.Intent
import cc.ab.base.ext.*
import cc.ab.base.utils.CcInputHelper
import cc.ab.base.utils.PressEffectHelper
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.main.MainActivity
import cc.abase.demo.constants.LengthConstants
import cc.abase.demo.constants.UiConstants
import cc.abase.demo.repository.UserRepository
import com.blankj.utilcode.util.ActivityUtils
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
    CcInputHelper.wrapCommCountLimit(loginEditAccount, LengthConstants.MAX_LEN_ACC, 0)
    CcInputHelper.wrapCommCountLimit(loginEditPassword, LengthConstants.MAX_LEN_PASS, 0)
    loginEditAccount.addTextWatcher { checkSubmit() }
    loginEditPassword.addTextWatcher { checkSubmit() }
    loginSubmit.click {
      showActionLoading()
      UserRepository.instance.login(
          loginEditAccount.text.toString(),
          loginEditPassword.text.toString()
      )
        .subscribe({ suc ->
          dismissActionLoading()
          if (suc) {
            MainActivity.startActivity(mContext)
          } else {
            mContext.toast(R.string.login_fail)
          }
        }, { error ->
          dismissActionLoading()
          mContext.toast(error.message)
        })
    }
    loginRegister.click { RegisterActivity.startActivity(mContext) }
    extKeyBoard { statusHeight, navigationHeight, keyBoardHeight -> }
  }

  override fun initData() {
    //来到登录页默认需要清除数据
    UserRepository.instance.clearUserInfo()
    //关闭其他所有页面
    ActivityUtils.finishOtherActivities(javaClass)
  }

  private fun checkSubmit() {
    val textAcc = loginEditAccount.text
    val textPass = loginEditPassword.text
    if (textAcc.isEmpty()) {
      loginInputAccount.hint = mContext.getString(R.string.login_account_hint)
    } else if (textAcc.isNotEmpty() && textAcc.length < LengthConstants.MIN_LEN_ACC) {
      loginInputAccount.hint = mContext.getString(R.string.login_account_short)
    } else {
      loginInputAccount.hint = ""
    }
    if (textPass.isEmpty()) {
      loginInputPassword.hint = mContext.getString(R.string.login_password_hint)
    } else if (textPass.isNotEmpty() && textPass.length < LengthConstants.MIN_LEN_PASS) {
      loginInputPassword.hint = mContext.getString(R.string.login_password_short)
    } else {
      loginInputPassword.hint = ""
    }
    val enable = textAcc.length >= LengthConstants.MIN_LEN_ACC &&
        textPass.length >= LengthConstants.MIN_LEN_PASS
    loginSubmit.isEnabled = enable
    loginSubmit.alpha = if (enable) 1f else UiConstants.disable_alpha
    if (enable) {
      loginSubmit.pressEffectAlpha()
    } else {
      loginSubmit.pressEffectDisable()
    }
  }
}