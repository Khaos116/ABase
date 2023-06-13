package cc.abase.demo.utils

import cc.ab.base.ext.launchError
import cc.abase.demo.rxhttp.repository.WanRepository
import rxhttp.awaitResult

/**
 * Author:CC
 * Date:2023/6/13
 * Time:14:26
 */
object TestUtils {
  //<editor-fold defaultstate="collapsed" desc="并发请求测试">
  fun testAsyncRequest() {
    launchError {
      WanRepository.banner(false)
        .awaitResult()
        .onSuccess { }
        .onFailure { }
    }
    launchError {
      WanRepository.article(0, false)
        .awaitResult()
        .onSuccess { }
        .onFailure { }
    }
  }
  //</editor-fold>
}