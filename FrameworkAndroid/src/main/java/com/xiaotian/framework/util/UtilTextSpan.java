package com.xiaotian.framework.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.LocaleSpan;
import android.text.style.MaskFilterSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.util.Property;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilTextSpan {
    private Context mContext;

    public UtilTextSpan() {}

    public UtilTextSpan(Context context) {
        mContext = context;
    }

    public CharSequence text(Object text) {
        if (text == null) return "";
        return text.toString();
    }

    // 对Text应用Span
    // 直接子类(背景,点击,前景,覆盖过滤,)
    // BackgroundColorSpan, ClickableSpan, ForegroundColorSpan, MaskFilterSpan,
    // MetricAffectingSpan, RasterizerSpan, StrikethroughSpan, SuggestionSpan,
    // UnderlineSpan
    // 间接子类
    // AbsoluteSizeSpan, DynamicDrawableSpan, ImageSpan, LocaleSpan,
    // RelativeSizeSpan, ReplacementSpan, ScaleXSpan, StyleSpan, SubscriptSpan,
    // SuperscriptSpan, TextAppearanceSpan, TypefaceSpan, URLSpan
    // Flag
    // Spanned.SPAN_EXCLUSIVE_EXCLUSIVE --- 不包含两端start和end所在的端点              (a,b)
    // Spanned.SPAN_EXCLUSIVE_INCLUSIVE --- 不包含端start，但包含end所在的端点       (a,b]
    // Spanned.SPAN_INCLUSIVE_EXCLUSIVE --- 包含两端start，但不包含end所在的端点   [a,b)
    // Spanned.SPAN_INCLUSIVE_INCLUSIVE--- 包含两端start和end所在的端点                     [a,b]
    //Matcher
    // 设置背景色
    public CharSequence genBackgroundColorSpan(String text, int spanColor) {
        SpannableString spannableString = new SpannableString(text);
        BackgroundColorSpan sp = new BackgroundColorSpan(spanColor);
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public CharSequence genBackgroundColorSpan(String text, String patten, int spanColor) {
        SpannableString spannableString = new SpannableString(text);
        BackgroundColorSpan sp = new BackgroundColorSpan(spanColor);
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 设置前景色
    public CharSequence genForegroundColorSpan(String text, int spanColor) {
        SpannableString spannableString = new SpannableString(text);
        ForegroundColorSpan sp = new ForegroundColorSpan(spanColor);
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public CharSequence genForegroundColorSpan(String text, String patten, int spanColor) {
        SpannableString spannableString = new SpannableString(text);
        ForegroundColorSpan sp = new ForegroundColorSpan(spanColor);
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 点击
    public void setClickableSpan(TextView textView, String text, String patten, final int spanColor, final ClickableSpan clickableSpan) {
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan sp = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                // 设置高亮颜色
                ds.setColor(spanColor);
                ds.setUnderlineText(true); // 是否有下划线
            }

            @Override
            public void onClick(View widget) {
                if (clickableSpan != null) {
                    clickableSpan.onClick(widget); // 点击事件
                }
            }
        };
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString);
    }

    // 分段符号(圆点)
    public CharSequence genBulletSpan(String text, int bulletSize, int bulletColor) {
        SpannableString spannableString = new SpannableString(text);
        BulletSpan sp = new BulletSpan(bulletSize, bulletColor);// wide(px),color
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    // 引用线
    public CharSequence genQuoteSpan(String text, int quoteSpanColor) {
        SpannableString spannableString = new SpannableString(text);
        QuoteSpan sp = new QuoteSpan(quoteSpanColor);
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    // 下划线
    public CharSequence genUnderlineSpan(CharSequence text, int spanColor) {
        SpannableString spannableString = new SpannableString(text);
        UnderlineSpan sp = new UnderlineSpan();
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public void setUnderLineFlag(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public CharSequence genUnderlineSpan(CharSequence text, String patten, int spanColor) {
        SpannableString spannableString = new SpannableString(text);
        UnderlineSpan sp = new UnderlineSpan();
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 删除线
    public CharSequence genStrikethroughSpan(CharSequence text, int spanColor) {
        SpannableString spannableString = new SpannableString(text);
        StrikethroughSpan sp = new StrikethroughSpan();
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public CharSequence genStrikethroughSpan(CharSequence text, String patten, int spanColor) {
        SpannableString spannableString = new SpannableString(text);
        StrikethroughSpan sp = new StrikethroughSpan();
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 过滤-模糊
    public CharSequence genMaskFilterSpanBlur(CharSequence text, String patten, float radius) {
        SpannableString spannableString = new SpannableString(text);
        MaskFilterSpan sp = new MaskFilterSpan(new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL));
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 过滤-浮雕
    public CharSequence genMaskFilterSpanEmboss(CharSequence text, String patten) {
        SpannableString spannableString = new SpannableString(text);
        MaskFilterSpan sp = new MaskFilterSpan(new EmbossMaskFilter(new float[]{1, 1, 1}, 0.4f, 6, 3.5f));
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 下标(下沉文字)
    public CharSequence genSubscriptSpan(CharSequence text) {
        SpannableString spannableString = new SpannableString(text);
        SubscriptSpan sp = new SubscriptSpan();
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public CharSequence genSubscriptSpan(CharSequence text, String patten) {
        SpannableString spannableString = new SpannableString(text);
        SubscriptSpan sp = new SubscriptSpan();
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 上标(上浮文字)
    public CharSequence genSuperscriptSpan(CharSequence text) {
        SpannableString spannableString = new SpannableString(text);
        SuperscriptSpan sp = new SuperscriptSpan();
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public CharSequence genSuperscriptSpan(CharSequence text, String patten) {
        SpannableString spannableString = new SpannableString(text);
        SuperscriptSpan sp = new SuperscriptSpan();
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 应用Style样式
    public CharSequence genStyleSpan(CharSequence text, int styles) {
        SpannableString spannableString = new SpannableString(text);
        StyleSpan sp = new StyleSpan(styles);// styles: Typeface.BOLD | Typeface.ITALIC
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public CharSequence genStyleSpan(CharSequence text, String patten, int styles) {
        SpannableString spannableString = new SpannableString(text);
        StyleSpan sp = new StyleSpan(styles);// styles: Typeface.BOLD | Typeface.ITALIC
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 指定大小
    public CharSequence genAbsoluteSizeSpan(CharSequence text, int textSize) {
        SpannableString spannableString = new SpannableString(text);
        AbsoluteSizeSpan sp = new AbsoluteSizeSpan(textSize, true);// true:dip
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public CharSequence genAbsoluteSizeSpan(CharSequence text, String patten, int textSize) {
        SpannableString spannableString = new SpannableString(text);
        AbsoluteSizeSpan sp = new AbsoluteSizeSpan(textSize, true);// true:dip
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 相对大小
    public CharSequence genRelativeSizeSpan(CharSequence text, float relativeSize) {
        SpannableString spannableString = new SpannableString(text);
        RelativeSizeSpan sp = new RelativeSizeSpan(relativeSize);// true:dip
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public CharSequence genRelativeSizeSpan(CharSequence text, String patten, float relativeSize) {
        SpannableString spannableString = new SpannableString(text);
        RelativeSizeSpan sp = new RelativeSizeSpan(relativeSize);// true:dip
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 应用XML声明的Style
    public CharSequence genTextAppearanceSpan(CharSequence text, Context context, int styleId) {
        SpannableString spannableString = new SpannableString(text);
        TextAppearanceSpan sp = new TextAppearanceSpan(context, styleId);
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public CharSequence genTextAppearanceSpan(CharSequence text, String patten, Context context, int styleId) {
        SpannableString spannableString = new SpannableString(text);
        TextAppearanceSpan sp = new TextAppearanceSpan(context, styleId);
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 地域
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public CharSequence genLocaleSpan(CharSequence text, String patten, Locale locale) {
        SpannableString spannableString = new SpannableString(text);
        LocaleSpan sp = new LocaleSpan(locale);
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // X方向拉伸
    public CharSequence genScaleXSpan(CharSequence text, float scale) {
        SpannableString spannableString = new SpannableString(text);
        ScaleXSpan sp = new ScaleXSpan(scale);
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public CharSequence genScaleXSpan(CharSequence text, String patten, float scale) {
        SpannableString spannableString = new SpannableString(text);
        ScaleXSpan sp = new ScaleXSpan(scale);
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 字体样式
    public CharSequence genTypefaceSpan(CharSequence text, String typeFace) {
        SpannableString spannableString = new SpannableString(text);
        TypefaceSpan sp = new TypefaceSpan(typeFace);// serif
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public CharSequence genTypefaceSpan(CharSequence text, String patten, String typeFace) {
        SpannableString spannableString = new SpannableString(text);
        TypefaceSpan sp = new TypefaceSpan(typeFace);// serif
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 图片
    public CharSequence genImageSpan(CharSequence text, String patten, int imageResource) {
        SpannableString spannableString = new SpannableString(text);
        ImageSpan sp = new ImageSpan(mContext, imageResource);
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    private static final Property<MutableForegroundColorSpan, Integer> MUTABLE_FOREGROUND_COLOR_SPAN_FC_PROPERTY = new Property<MutableForegroundColorSpan, Integer>(Integer.class,
            "MUTABLE_FOREGROUND_COLOR_SPAN_FC_PROPERTY") {

        @Override
        public void set(MutableForegroundColorSpan alphaForegroundColorSpanGroup, Integer value) {
            alphaForegroundColorSpanGroup.setForegroundColor(value);
        }

        @Override
        public Integer get(MutableForegroundColorSpan span) {
            return span.getForegroundColor();
        }
    };

    // 动态前景色
    public void animateColorSpan(final TextView textView, CharSequence text, String patten, int color) {
        final SpannableString spannableString = new SpannableString(text);
        MutableForegroundColorSpan sp = new MutableForegroundColorSpan(255, color);
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        // Animation
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(sp, MUTABLE_FOREGROUND_COLOR_SPAN_FC_PROPERTY, Color.BLACK, Color.RED);
        objectAnimator.setEvaluator(new ArgbEvaluator());
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // refresh
                textView.setText(spannableString);
            }
        });
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.setDuration(2000);
        objectAnimator.start();
    }

    // 气泡
    public CharSequence genBubbleSpan(CharSequence text, String patten) {
        final SpannableString spannableString = new SpannableString(text);
        BubbleSpan sp = new BubbleSpan();
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    // 字符高亮背景
    public CharSequence genLetterLineBackgroundSpan(CharSequence text) {
        final SpannableString spannableString = new SpannableString(text);
        LetterLineBackgroundSpan sp = new LetterLineBackgroundSpan();// 'a', 'e', 'i', 'o', 'u', 'y'
        spannableString.setSpan(sp, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    // 小方框替代
    public CharSequence genFrameSpan(CharSequence text, String patten) {
        final SpannableString spannableString = new SpannableString(text);
        FrameSpan sp = new FrameSpan();
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(text);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(sp, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    private static final Property<TypeWriterSpanGroup, Float> TYPE_WRITER_GROUP_ALPHA_PROPERTY = new Property<TypeWriterSpanGroup, Float>(Float.class, "TYPE_WRITER_GROUP_ALPHA_PROPERTY") {
        @Override
        public void set(TypeWriterSpanGroup spanGroup, Float value) {
            spanGroup.setAlpha(value);
        }

        @Override
        public Float get(TypeWriterSpanGroup spanGroup) {
            return spanGroup.getAlpha();
        }
    };

    // 打字显示
    public void animateTypeWriter(final TextView textView) {
        animateTypeWriter(textView, Color.BLACK, 100);
    }

    public void animateTypeWriter(final TextView textView, final int textColor, final int durationCharactor) {
        final SpannableString spannableString = new SpannableString(textView.getText());
        // 打字机(0 ~ length-1 字符)
        final TypeWriterSpanGroup spanGroup = new TypeWriterSpanGroup(0);
        for (int index = 0; index <= spannableString.length() - 1; index++) {
            MutableForegroundColorSpan span = new MutableForegroundColorSpan(0, textColor);
            spanGroup.addSpan(span);
            spannableString.setSpan(span, index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        // Animation
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(spanGroup, TYPE_WRITER_GROUP_ALPHA_PROPERTY, 0.0f, 1.0f);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //refresh
                textView.setText(spannableString);
            }
        });
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(spannableString.length() * durationCharactor);
        objectAnimator.start();
    }

    private static final Property<FireworksSpanGroup, Float> FIREWORKS_GROUP_PROGRESS_PROPERTY = new Property<FireworksSpanGroup, Float>(Float.class, "FIREWORKS_GROUP_PROGRESS_PROPERTY") {
        @Override
        public void set(FireworksSpanGroup spanGroup, Float value) {
            spanGroup.setProgress(value);
        }

        @Override
        public Float get(FireworksSpanGroup spanGroup) {
            return spanGroup.getProgress();
        }
    };

    // 动态烟火
    public void animateFireworks(final TextView textView, int fireColor) {
        final SpannableString spannableString = new SpannableString(textView.getText());
        // 烟火
        FireworksSpanGroup spanGroup = new FireworksSpanGroup();
        for (int index = 0; index <= spannableString.length() - 1; index++) {
            MutableForegroundColorSpan span = new MutableForegroundColorSpan(0, fireColor);
            spanGroup.addSpan(span);
            spannableString.setSpan(span, index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        spanGroup.init();
        // Animation
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(spanGroup, FIREWORKS_GROUP_PROGRESS_PROPERTY, 0.0f, 1.0f);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //refresh
                textView.setText(spannableString);
            }
        });
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.setDuration(2000);
        objectAnimator.start();
    }

    private static final Property<MutableBlurMaskFilterSpan, Float> BLUR_RADIUS_PROPERTY = new Property<MutableBlurMaskFilterSpan, Float>(Float.class, "BLUR_RADIUS_PROPERTY") {

        @Override
        public void set(MutableBlurMaskFilterSpan span, Float value) {
            span.setRadius(value);
        }

        @Override
        public Float get(MutableBlurMaskFilterSpan span) {
            return span.getRadius();
        }
    };

    // 动态模糊清晰
    public void animateBlur(final TextView textView, String patten) {
        float density = textView.getResources().getDisplayMetrics().density;
        float maxRadius = density * 8;
        final SpannableString spannableString = new SpannableString(textView.getText());
        final MutableBlurMaskFilterSpan span = new MutableBlurMaskFilterSpan(maxRadius);
        Pattern p = Pattern.compile(patten);
        Matcher m = p.matcher(spannableString.toString());
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            spannableString.setSpan(span, mr.start(), mr.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        // Animation
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(span, BLUR_RADIUS_PROPERTY, maxRadius, 0.1f);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //refresh
                textView.setText(spannableString);
            }
        });
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                spannableString.removeSpan(span);
                textView.setText(spannableString);
            }
        });
        objectAnimator.setDuration(1500);
        objectAnimator.start();
    }

    // 设置字体
    public void setTypeFace(TextView textView, Context context, String assetsFilename) {
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), assetsFilename);
        textView.setTypeface(typeFace);
    }

    /************************************************ Inner Set Type ************************************************/
    public enum SpanType {
        BULLET, QUOTE, UNDERLINE, STRIKETHROUGH, BGCOLOR, FGCOLOR, MASKFILTER_EMBOSS, SUBSCRIPT, STYLE, ABSOLUTE_SIZE_SPAN, RELATIVE_SIZE_SPAN, TEXTAPPEARANCE_SPAN, SUPERSCRIPT, LOCALE_SPAN,
        SCALEX_SPAN, TYPEFACE_SPAN, IMAGE_SPAN, MASKFILTER_BLUR, ALIGNMENT_STANDARD
    }

    @SuppressLint("NewApi")
    public CharSequence genSpanString(TextView textView, SpanType spantype, String text, int start, int end, int... spanResult) {
        SpannableString spannableString = new SpannableString(text);
        float density = .5f;
        Object span = null;
        int allTextStart = 0;
        int allTextEnd = spannableString.length() - 1;
        switch (spantype) {
        case BULLET:
            span = new BulletSpan(15, Color.BLACK);
            spannableString.setSpan(span, allTextStart, allTextEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case QUOTE:
            span = new QuoteSpan(Color.RED);
            spannableString.setSpan(span, allTextStart, allTextEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case ALIGNMENT_STANDARD:
            span = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
            spannableString.setSpan(span, allTextStart, allTextEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case UNDERLINE:
            span = new UnderlineSpan();
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case STRIKETHROUGH:
            span = new StrikethroughSpan();
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case BGCOLOR:
            span = new BackgroundColorSpan(Color.GREEN);
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case FGCOLOR:
            span = new ForegroundColorSpan(Color.RED);
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case MASKFILTER_BLUR:
            span = new MaskFilterSpan(new BlurMaskFilter(density * 2, BlurMaskFilter.Blur.NORMAL));
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case MASKFILTER_EMBOSS:
            span = new MaskFilterSpan(new EmbossMaskFilter(new float[]{1, 1, 1}, 0.4f, 6, 3.5f));
            ForegroundColorSpan fg = new ForegroundColorSpan(Color.BLUE);
            StyleSpan style = new StyleSpan(Typeface.BOLD);
            spannableString.setSpan(fg, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(style, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case SUBSCRIPT:
            span = new SubscriptSpan();
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case SUPERSCRIPT:
            span = new SuperscriptSpan();
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case STYLE:
            span = new StyleSpan(Typeface.BOLD | Typeface.ITALIC);
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case ABSOLUTE_SIZE_SPAN:
            span = new AbsoluteSizeSpan(24, true);
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case RELATIVE_SIZE_SPAN:
            span = new RelativeSizeSpan(2.0f);
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case TEXTAPPEARANCE_SPAN:
            int specialTextAppearanceStyle = spanResult[0];
            span = new TextAppearanceSpan(textView.getContext(), specialTextAppearanceStyle);
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case LOCALE_SPAN:
            span = new LocaleSpan(Locale.CHINESE);
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case SCALEX_SPAN:
            span = new ScaleXSpan(3.0f);
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case TYPEFACE_SPAN:
            span = new TypefaceSpan("serif");
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        case IMAGE_SPAN:
            int imageResult = spanResult[0];
            span = new ImageSpan(textView.getContext(), imageResult);
            spannableString.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            break;
        }
        if (span == null) {
            return text;
        }
        return spannableString;
    }

    public class MutableForegroundColorSpan extends ForegroundColorSpan {

        private int mAlpha = 255;
        private int mForegroundColor;

        public MutableForegroundColorSpan(int alpha, int color) {
            super(color);
            mAlpha = alpha;
            mForegroundColor = color;
        }

        public MutableForegroundColorSpan(Parcel src) {
            super(src);
            mForegroundColor = src.readInt();
            mAlpha = src.readInt();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mForegroundColor);
            dest.writeFloat(mAlpha);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getForegroundColor());
        }

        /**
         * @param alpha from 0 to 255
         */
        public void setAlpha(int alpha) {
            mAlpha = alpha;
        }

        public void setForegroundColor(int foregroundColor) {
            mForegroundColor = foregroundColor;
        }

        public float getAlpha() {
            return mAlpha;
        }

        @Override
        public int getForegroundColor() {
            return Color.argb(mAlpha, Color.red(mForegroundColor), Color.green(mForegroundColor), Color.blue(mForegroundColor));
        }
    }

    public class BubbleSpan extends ReplacementSpan {
        private Paint mPaint;
        Random random = new Random();
        private RectF mRectF = new RectF();
        private int mWidth = -1;
        private int[] mColors = new int[20];

        public BubbleSpan() {
            initPaint();
            initColors();
        }

        private void initPaint() {
            mPaint = new Paint();
            mPaint.setColor(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            mPaint.setAntiAlias(true);
        }

        private void initColors() {
            for (int index = 0; index < mColors.length; index++) {
                mColors[index] = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
            }
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            // return text with relative to the Paint
            mWidth = (int) paint.measureText(text, start, end);
            return mWidth;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            float charx = x;
            for (int i = start; i < end; i++) {
                String charAt = extractText(text, i, i + 1);
                float charWidth = paint.measureText(charAt);
                mRectF.set(charx, top, charx += charWidth, bottom);
                mPaint.setColor(mColors[i % mColors.length]);
                canvas.drawOval(mRectF, mPaint);
            }
            canvas.drawText(text, start, end, x, y, paint);
        }

        private String extractText(CharSequence text, int start, int end) {
            return text.subSequence(start, end).toString();
        }
    }

    public class LetterLineBackgroundSpan implements LineBackgroundSpan {
        // 字符高亮
        private final char[] sV = {'a', 'e', 'i', 'o', 'u', 'y'};

        private final Paint mCPaint;
        private final Paint mVPaint;
        private RectF mRectF = new RectF();

        public LetterLineBackgroundSpan() {
            mCPaint = new Paint();
            mCPaint.setColor(Color.MAGENTA);
            mCPaint.setAntiAlias(true);
            mVPaint = new Paint();
            mVPaint.setColor(Color.YELLOW);
            mVPaint.setAntiAlias(true);
        }

        @Override
        public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
            float charx = left;
            for (int i = start; i < end; i++) {
                String charAt = extractText(text, i, i + 1);
                float charWidth = p.measureText(charAt);
                mRectF.set(charx, top, charx += charWidth, bottom);
                if (Arrays.binarySearch(sV, charAt.charAt(0)) >= 0) {
                    c.drawRect(mRectF, mVPaint);
                } else {
                    c.drawRect(mRectF, mCPaint);
                }
            }
        }

        private String extractText(CharSequence text, int start, int end) {
            return text.subSequence(start, end).toString();
        }
    }

    public class FrameSpan extends ReplacementSpan {
        private final Paint mPaint;
        private int mWidth;

        public FrameSpan() {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.BLUE);
            mPaint.setAntiAlias(true);
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            //return text with relative to the Paint
            mWidth = (int) paint.measureText(text, start, end);
            return mWidth;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            //draw the frame with custom Paint
            canvas.drawRect(x, top, x + mWidth, bottom, mPaint);
        }
    }

    private static final class TypeWriterSpanGroup {

        private static final boolean DEBUG = false;
        private static final String TAG = "TypeWriterSpanGroup";

        private final float mAlpha;
        private final ArrayList<MutableForegroundColorSpan> mSpans;

        private TypeWriterSpanGroup(float alpha) {
            mAlpha = alpha;
            mSpans = new ArrayList<MutableForegroundColorSpan>();
        }

        public void addSpan(MutableForegroundColorSpan span) {
            span.setAlpha((int) (mAlpha * 255));
            mSpans.add(span);
        }

        public void setAlpha(float alpha) {
            int size = mSpans.size();
            float total = 1.0f * size * alpha;

            if (DEBUG) {
                Log.d(TAG, "alpha " + alpha + " * 1.0f * size => " + total);
            }

            for (int index = 0; index < size; index++) {
                MutableForegroundColorSpan span = mSpans.get(index);

                if (total >= 1.0f) {
                    span.setAlpha(255);
                    total -= 1.0f;
                } else {
                    span.setAlpha((int) (total * 255));
                    total = 0.0f;
                }

                if (DEBUG) {
                    Log.d(TAG, "alpha span(" + index + ") => " + alpha);
                }
            }
        }

        public float getAlpha() {
            return mAlpha;
        }
    }

    private static final class FireworksSpanGroup {

        private static final boolean DEBUG = false;
        private static final String TAG = "FireworksSpanGroup";

        private final float mProgress;
        private final ArrayList<MutableForegroundColorSpan> mSpans;
        private final ArrayList<Integer> mSpanIndexes;

        private FireworksSpanGroup() {
            mProgress = 0;
            mSpans = new ArrayList<MutableForegroundColorSpan>();
            mSpanIndexes = new ArrayList<Integer>();
        }

        public void addSpan(MutableForegroundColorSpan span) {
            span.setAlpha(0);
            mSpanIndexes.add(mSpans.size());
            mSpans.add(span);
        }

        public void init() {
            Collections.shuffle(mSpans);
        }

        public void setProgress(float progress) {
            int size = mSpans.size();
            float total = 1.0f * size * progress;

            if (DEBUG) {
                Log.d(TAG, "progress " + progress + " * 1.0f * size => " + total);
            }

            for (int index = 0; index < size; index++) {
                MutableForegroundColorSpan span = mSpans.get(index);

                if (total >= 1.0f) {
                    span.setAlpha(255);
                    total -= 1.0f;
                } else {
                    span.setAlpha((int) (total * 255));
                    total = 0.0f;
                }
            }
        }

        public float getProgress() {
            return mProgress;
        }
    }

    public class MutableBlurMaskFilterSpan extends CharacterStyle implements UpdateAppearance {
        private float mRadius;
        private MaskFilter mFilter;

        public MutableBlurMaskFilterSpan(float radius) {
            mRadius = radius;
        }

        public void setRadius(float radius) {
            mRadius = radius;
            mFilter = new BlurMaskFilter(mRadius, BlurMaskFilter.Blur.NORMAL);
        }

        public float getRadius() {
            return mRadius;
        }

        public MaskFilter getFilter() {
            return mFilter;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setMaskFilter(mFilter);
        }
    }

    // XML TAG String
    public Spanned genXMLSpanned(int xmlStringRes) {
        // Read an html string from the resource and set it in a text view
        if (mContext == null) {
            return genXMLSpanned(UtilEnvironment.getCurrentApplicationContext().getString(xmlStringRes));
        } else {
            return genXMLSpanned(mContext.getString(xmlStringRes));
        }
    }

    public Spanned genXMLSpanned(String htmlTaggedString) {
        // Convert it to a text span so that it can be set in a text view
        // android.text.Html class allows painting of "html" strings
        // This is strictly an Android class and does not support all html tags
        return android.text.Html.fromHtml(htmlTaggedString);
    }

    // TextView Method
    public void setText(TextView textView, CharSequence text) {
        if (text == null) return;
        textView.setText(text);
    }

    public void setTextSize(TextView textView, float sizepx) {
        if (sizepx < 0) return;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizepx);
    }

    public void setTextColor(TextView textView, int color) {
        if (color == -1) return;
        textView.setTextColor(color);
    }
    // HTML常用转义字符：
    // 字符   转义字符    描述
    // & -> &amp;  -> 和
    // < -> &lt; -> 小于号
    // > -> &gt; -> 大于号
    // " -> &quot; -> 双引号
    // ' -> &apos; -> 单引号
    //   -> &nbsp; -> 空格
    // © -> &copy; -> 版权符
    // ® -> &reg; -> 注册符
}
