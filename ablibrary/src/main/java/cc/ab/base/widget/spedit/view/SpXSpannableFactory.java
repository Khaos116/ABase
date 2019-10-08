package cc.ab.base.widget.spedit.view;

import android.text.*;
import androidx.annotation.NonNull;
import java.util.List;

/**
 * Created by sunhapper on 2019/1/25 .
 */
public class SpXSpannableFactory extends Spannable.Factory {
    private List<NoCopySpan> mNoCopySpans;
    public SpXSpannableFactory(List<NoCopySpan> watchers) {
        mNoCopySpans = watchers;
    }

    public Spannable newSpannable(@NonNull CharSequence source) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        for (NoCopySpan span : mNoCopySpans) {
            spannableStringBuilder.setSpan(span, 0,0,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE | Spanned.SPAN_PRIORITY);
        }
        spannableStringBuilder.append(source);
        return spannableStringBuilder;
    }
}
