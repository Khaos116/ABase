package cc.abase.demo.component.login

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import cc.ab.base.ext.*
import cc.ab.base.utils.CcInputHelper
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.main.MainActivity
import cc.abase.demo.constants.UiConstants
import cc.abase.demo.repository.UserRepository
import kotlinx.android.synthetic.main.activity_register.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/10 21:03
 */
class RegisterActivity : CommTitleActivity() {
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, RegisterActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun layoutResContentId() = R.layout.activity_register

  override fun needKeyListener() = true
  override fun initContentView() {
    setTitleText(mContext.getString(R.string.login_register_hint))
    checkSubmit()
    CcInputHelper.wrapCommCountLimit(registerEditAccount, 30, 0)
    CcInputHelper.wrapCommCountLimit(registerEditPassword1, 30, 0)
    CcInputHelper.wrapCommCountLimit(registerEditPassword2, 30, 0)
    registerEditAccount.addTextWatcher { checkSubmit() }
    registerEditPassword1.addTextWatcher { checkSubmit() }
    registerEditPassword2.addTextWatcher { checkSubmit() }
    registerSubmit.click {
      showActionLoading()
      UserRepository.instance.register(
          registerEditAccount.text.toString(),
          registerEditPassword1.text.toString(),
          registerEditPassword2.text.toString()
      )
          .subscribe { suc, error ->
            dismissActionLoading()
            if (suc) {
              MainActivity.startActivity(mContext)
            } else {
              mContext.toast(error.message)
            }
          }
    }
  }

  private fun checkSubmit() {
    val textAcc = registerEditAccount.text
    val textPass1 = registerEditPassword1.text
    val textPass2 = registerEditPassword2.text
    val enable = textAcc.length >= 3 && textPass1.length >= 6 &&
        textPass1.length == textPass2.length &&
        TextUtils.equals(registerEditPassword1.text, registerEditPassword2.text)
    registerSubmit.isEnabled = enable
    registerSubmit.alpha = if (enable) 1f else UiConstants.disable_alpha
    if (enable) {
      registerSubmit.pressEffectAlpha()
    } else {
      registerSubmit.pressEffectDisable()
    }
  }

  override fun initData() {
  }
}