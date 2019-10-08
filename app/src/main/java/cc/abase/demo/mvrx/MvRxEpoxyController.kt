package cc.abase.demo.mvrx

import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.EpoxyController

/**
 *description: Epoxy的Controller创建的工具类.
 *@date 2019/3/26 14:10.
 *@author: YangYang.
 */
open class MvRxEpoxyController<T>(
  val buildModelsCallback: EpoxyController.(data: T) -> Unit = { state -> }
) : AsyncEpoxyController() {
  //数据
  var data: T? = null
    set(value) {
      field = value
      if (value != null) requestModelBuild()
    }

  override fun buildModels() {
    data?.let { buildModelsCallback(it) }
  }
}
//
///**
// * Create a [MvRxEpoxyController] that builds models with the given callback.
// */
//fun CommMvRxEpoxyFragment.simpleController(
//    buildModels: EpoxyController.() -> Unit
//) = MvRxEpoxyController {
//  // Models are built asynchronously, so it is possible that this is called after the fragment
//  // is detached under certain race conditions.
//  if (view == null || isRemoving) return@MvRxEpoxyController
//  buildModels()
//}
//
///**
// * Create a [MvRxEpoxyController] that builds models with the given callback.
// * When models are built the current state of the viewmodel will be provided.
// */
//fun <S : MvRxState, A : MvRxViewModel<S>> CommMvRxEpoxyFragment.simpleController(
//    viewModel: A,
//    buildModels: EpoxyController.(state: S) -> Unit
//) = MvRxEpoxyController {
//  if (view == null || isRemoving) return@MvRxEpoxyController
//  com.airbnb.mvrx.withState(viewModel) { state1 ->
//    buildModels(state1)
//  }
//}
//
///**
// * Create a [MvRxEpoxyController] that builds models with the given callback.
// * When models are built the current state of the viewmodels will be provided.
// */
//fun <A : BaseMvRxViewModel<B>, B : MvRxState, C : BaseMvRxViewModel<D>, D : MvRxState> CommMvRxEpoxyFragment.simpleController(
//    viewModel1: A,
//    viewModel2: C,
//    buildModels: EpoxyController.(state1: B, state2: D) -> Unit
//) = MvRxEpoxyController {
//  if (view == null || isRemoving) return@MvRxEpoxyController
//  com.airbnb.mvrx.withState(viewModel1, viewModel2) { state1, state2 ->
//    buildModels(state1, state2)
//  }
//}
//
///**
// * Create a [MvRxEpoxyController] that builds models with the given callback.
// * When models are built the current state of the viewmodels will be provided.
// */
//fun <A : BaseMvRxViewModel<B>, B : MvRxState, C : BaseMvRxViewModel<D>, D : MvRxState, E : BaseMvRxViewModel<F>, F : MvRxState> CommMvRxEpoxyFragment.simpleController(
//    viewModel1: A,
//    viewModel2: C,
//    viewModel3: E,
//    buildModels: EpoxyController.(state1: B, state2: D, state3: F) -> Unit
//) = MvRxEpoxyController {
//  if (view == null || isRemoving) return@MvRxEpoxyController
//  com.airbnb.mvrx.withState(viewModel1, viewModel2, viewModel3) { state1, state2, state3 ->
//    buildModels(state1, state2, state3)
//  }
//}
//
//
///**
// * Create a [MvRxEpoxyController] that builds models with the given callback.
// */
//fun CommMvRxEpoxyActivity.simpleController(
//    buildModels: EpoxyController.() -> Unit
//) = MvRxEpoxyController {
//  // Models are built asynchronously, so it is possible that this is called after the fragment
//  // is detached under certain race conditions.
//  if (isDestroyed) return@MvRxEpoxyController
//  buildModels()
//}
//
///**
// * Create a [MvRxEpoxyController] that builds models with the given callback.
// * When models are built the current state of the viewmodel will be provided.
// */
//fun <S : MvRxState, A : MvRxViewModel<S>> CommMvRxEpoxyActivity.simpleController(
//    viewModel: A,
//    buildModels: EpoxyController.(state: S) -> Unit
//) = MvRxEpoxyController {
//  //    if (isDestroyed) return@MvRxEpoxyController
//  com.airbnb.mvrx.withState(viewModel) { state ->
//    buildModels(state)
//  }
//}
//
///**
// * Create a [MvRxEpoxyController] that builds models with the given callback.
// * When models are built the current state of the viewmodels will be provided.
// */
//fun <A : BaseMvRxViewModel<B>, B : MvRxState, C : BaseMvRxViewModel<D>, D : MvRxState> CommMvRxEpoxyActivity.simpleController(
//    viewModel1: A,
//    viewModel2: C,
//    buildModels: EpoxyController.(state1: B, state2: D) -> Unit
//) = MvRxEpoxyController {
//  if (isDestroyed) return@MvRxEpoxyController
//  com.airbnb.mvrx.withState(viewModel1, viewModel2) { state1, state2 ->
//    buildModels(state1, state2)
//  }
//}
//
///**
// * Create a [MvRxEpoxyController] that builds models with the given callback.
// * When models are built the current state of the viewmodels will be provided.
// */
//fun <A : BaseMvRxViewModel<B>, B : MvRxState, C : BaseMvRxViewModel<D>, D : MvRxState, E : BaseMvRxViewModel<F>, F : MvRxState> CommMvRxEpoxyActivity.simpleController(
//    viewModel1: A,
//    viewModel2: C,
//    viewModel3: E,
//    buildModels: EpoxyController.(state1: B, state2: D, state3: F) -> Unit
//) = MvRxEpoxyController {
//  if (isDestroyed) return@MvRxEpoxyController
//  com.airbnb.mvrx.withState(viewModel1, viewModel2, viewModel3) { state1, state2, state3 ->
//    buildModels(state1, state2, state3)
//  }
//}