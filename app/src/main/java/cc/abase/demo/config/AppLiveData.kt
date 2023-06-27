package cc.abase.demo.config

import androidx.lifecycle.MutableLiveData
import cc.abase.demo.bean.net.IpBean

/**
 * Author:Khaos116
 * Date:2022/12/24
 * Time:18:50
 */
object AppLiveData {
  //监听ip
  val ipLiveData = MutableLiveData<IpBean>()

  //左右滑动监听
  val frameLayoutScrollLiveData = MutableLiveData<Pair<Int, Boolean>>()

  //滑动状态记录,多组滑动存在时使用
  val transStateMaps = hashMapOf<Int, Boolean>()
}