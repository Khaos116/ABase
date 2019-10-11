package cc.ab.base.widget.discretescrollview.transform;

import android.view.View;

public interface DiscreteScrollItemTransformer {
  void transformItem(View item, float position);
}