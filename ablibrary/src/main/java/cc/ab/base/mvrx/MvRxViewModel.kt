package cc.ab.base.mvrx

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/24 11:20
 */
abstract class MvRxViewModel<S : MvRxState>(initialState: S) : BaseMvRxViewModel<S>(initialState)