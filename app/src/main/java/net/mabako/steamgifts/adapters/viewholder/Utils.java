package net.mabako.steamgifts.adapters.viewholder;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import net.mabako.steamgifts.R;
import net.mabako.steamgifts.activities.UrlHandlingActivity;
import net.mabako.steamgifts.activities.WebViewActivity;

public final class Utils {
    public static CharSequence fromHtml(Context context, String source) {
        return fromHtml(context, source, true);
    }

    public static CharSequence fromHtml(Context context, String source, boolean useCustomViewHandler) {
        if (TextUtils.isEmpty(source))
            return source;

        if (useCustomViewHandler) {
            try {
                CharSequence cs = Html.fromHtml(source, null, new CustomHtmlTagHandler());
                cs = cs.subSequence(0, cs.length() - 2);
                return addProperLinks(context, cs);
            } catch (Exception e) {
                Log.e(Utils.class.getSimpleName(), "Failed to parse HTML with custom parser", e);
            }
        }

        CharSequence cs = Html.fromHtml(source);
        cs = cs.subSequence(0, cs.length() - 2);
        return addProperLinks(context, cs);
    }

    /**
     * Convert all {@link URLSpan} (which uses the default browser) to use our custom {@link ClickableSpan} instead.
     *
     * @param context
     * @param charSequence
     * @return
     */
    private static CharSequence addProperLinks(final Context context, CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence))
            return charSequence;

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(charSequence);
        URLSpan[] urls = stringBuilder.getSpans(0, charSequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            int start = stringBuilder.getSpanStart(span);
            int end = stringBuilder.getSpanEnd(span);
            int flags = stringBuilder.getSpanFlags(span);

            final String url = span.getURL();
            if (url.startsWith("http://") || url.startsWith("https://")) {
                // Custom Span for clicking
                stringBuilder.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        // Do we have anything in the app we can open with that url?
                        Intent intent = UrlHandlingActivity.getIntentForUri(context, Uri.parse(url));
                        if (intent == null) {
                            // Nope, just open a normal webview.
                            intent = new Intent(context, WebViewActivity.class);
                            intent.putExtra(WebViewActivity.ARG_URL, url);
                        }
                        context.startActivity(intent);
                    }
                }, start, end, flags);
                stringBuilder.removeSpan(span);
            }
        }

        return stringBuilder;
    }

    public static void setBackgroundDrawable(Context context, View view, boolean highlighted) {
        if (highlighted) {
            int attrs[] = new int[]{R.attr.colorHighlightBackground};
            TypedArray ta = context.getTheme().obtainStyledAttributes(attrs);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(ta.getDrawable(0));
            } else {
                view.setBackground(ta.getDrawable(0));
            }
        } else {
            view.setBackgroundResource(R.color.colorTransparent);
        }
    }
}
