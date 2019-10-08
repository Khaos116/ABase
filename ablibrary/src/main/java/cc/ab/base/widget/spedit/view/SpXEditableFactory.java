package cc.ab.base.widget.spedit.view;

import android.annotation.SuppressLint;
import android.text.*;
import android.text.Editable.Factory;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/**
 * Created by sunhapper on 2019/1/25 .
 */
public class SpXEditableFactory extends Factory {
  private List<NoCopySpan> mNoCopySpans;
  @Nullable
  private static Class<?> sWatcherClass;

  @SuppressLint("PrivateApi") SpXEditableFactory(List<NoCopySpan> watchers) {
    mNoCopySpans = watchers;
    try {
      String className = "android.text.DynamicLayout$ChangeWatcher";
      ClassLoader loader = this.getClass().getClassLoader();
      if (loader != null) sWatcherClass = loader.loadClass(className);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Editable newEditable(@NonNull CharSequence source) {
    SpannableStringBuilder
        spannableStringBuilder = sWatcherClass != null ? EmojiSpannableStringBuilder.create(
        sWatcherClass, source)
        : SpannableStringBuilder.valueOf(source);
    for (NoCopySpan span : mNoCopySpans) {
      spannableStringBuilder.setSpan(span, 0, source.length(),
          Spanned.SPAN_INCLUSIVE_INCLUSIVE | Spanned.SPAN_PRIORITY);
    }
    return spannableStringBuilder;
  }
}
