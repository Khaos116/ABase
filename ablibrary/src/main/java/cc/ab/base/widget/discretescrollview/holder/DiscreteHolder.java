package cc.ab.base.widget.discretescrollview.holder;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

public abstract class DiscreteHolder<T> extends RecyclerView.ViewHolder {
  public DiscreteHolder(View itemView) {
    super(itemView);
    initView(itemView);
  }

  protected abstract void initView(@NotNull View itemView);

  public abstract void updateUI(T data, int position, int count);
}
