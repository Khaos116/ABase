package cc.ab.base.widget.discretescrollview.adapter;

import android.view.*;
import androidx.recyclerview.widget.RecyclerView;
import cc.ab.base.ext.ViewExtKt;
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder;
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator;
import cc.ab.base.widget.discretescrollview.listener.OnItemClickListener;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 *
 * @author: CASE
 * @date: 2019/10/14 12:09
 */
public class DiscretePageAdapter<T> extends RecyclerView.Adapter<DiscreteHolder<T>> {
  private List<T> datas;
  private DiscreteHolderCreator creator;
  private OnItemClickListener onItemClickListener;

  public DiscretePageAdapter(DiscreteHolderCreator creator, List<T> datas) {
    this.creator = creator;
    this.datas = datas;
  }

  @SuppressWarnings("unchecked")
  @NotNull @Override
  public DiscreteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    int layoutId = creator.getLayoutId();
    View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    return creator.createHolder(itemView);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onBindViewHolder(DiscreteHolder holder, final int position) {
    holder.updateUI(datas.get(position), position, getItemCount());

    if (onItemClickListener != null) {
      holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(position, datas.get(position)));
      ViewExtKt.pressEffectAlpha(holder.itemView, 0.9f);
    } else {
      ViewExtKt.pressEffectDisable(holder.itemView);
    }
  }

  @Override
  public int getItemCount() {
    return datas == null ? 0 : datas.size();
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }
}
