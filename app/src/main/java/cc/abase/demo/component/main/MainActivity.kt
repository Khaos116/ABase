package cc.abase.demo.component.main

import android.content.Context
import android.content.Intent
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity

class MainActivity : CommActivity() {

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, MainActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun layoutResId() = R.layout.activity_main

  override fun initView() {

  }

  override fun initData() {

  }

}
