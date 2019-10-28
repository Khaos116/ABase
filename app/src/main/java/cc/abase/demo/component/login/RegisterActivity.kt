package cc.abase.demo.component.login

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import cc.ab.base.ext.*
import cc.ab.base.utils.CcInputHelper
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.component.main.MainActivity
import cc.abase.demo.constants.LengthConstants
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
  override fun keyBoardChange(
    isPopup: Boolean,
    keyboardHeight: Int
  ) {
    if (!isPopup) {
      registerEditAccount.clearFocus()
      registerEditPassword1.clearFocus()
      registerEditPassword2.clearFocus()
    }
  }

  override fun initContentView() {
    setTitleText(mContext.getString(R.string.login_register_hint))
    checkSubmit()
    CcInputHelper.wrapCommCountLimit(registerEditAccount, LengthConstants.MAX_LEN_ACC, 0)
    CcInputHelper.wrapCommCountLimit(registerEditPassword1, LengthConstants.MAX_LEN_PASS, 0)
    CcInputHelper.wrapCommCountLimit(registerEditPassword2, LengthConstants.MAX_LEN_PASS, 0)
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
            if (suc == true) {
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
    if (textAcc.isEmpty()) {
      registerInputAccount.hint = mContext.getString(R.string.login_account_hint)
    } else if (textAcc.isNotEmpty() && textAcc.length < LengthConstants.MIN_LEN_ACC) {
      registerInputAccount.hint = mContext.getString(R.string.login_account_short)
    } else {
      registerInputAccount.hint = ""
    }
    if (textPass1.isEmpty()) {
      registerInputPassword1.hint = mContext.getString(R.string.login_password_hint)
    } else if (textPass1.isNotEmpty() && textPass1.length < LengthConstants.MIN_LEN_PASS) {
      registerInputPassword1.hint = mContext.getString(R.string.login_password_short)
    } else {
      registerInputPassword1.hint = ""
    }
    if (textPass2.isEmpty()) {
      registerInputPassword2.hint = mContext.getString(R.string.login_password_again_hint)
    } else if (!TextUtils.equals(textPass1, textPass2)) {
      registerInputPassword2.hint = mContext.getString(R.string.login_password_not_same)
    } else {
      registerInputPassword2.hint = ""
    }
    val enable = textAcc.length >= LengthConstants.MIN_LEN_ACC &&
        textPass1.length >= LengthConstants.MIN_LEN_PASS &&
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