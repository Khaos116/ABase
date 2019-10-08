package cc.ab.base.widget.spedit.gif.drawable;

import cc.ab.base.widget.spedit.gif.listener.RefreshListener;

/**
 * Created by sunhapper on 2019/1/25 .
 */
public interface InvalidateDrawable {
    void addRefreshListener(RefreshListener callback);

    void removeRefreshListener(RefreshListener callback);
}
