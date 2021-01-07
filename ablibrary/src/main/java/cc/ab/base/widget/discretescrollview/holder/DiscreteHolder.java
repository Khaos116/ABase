package cc.ab.base.widget.discretescrollview.holder;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public abstract class DiscreteHolder<T> extends RecyclerView.ViewHolder {
  public DiscreteHolder(View itemView) {
    super(itemView);
  }

  public abstract void updateUI(T data, int position, int count);
}
