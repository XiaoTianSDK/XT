package com.xiaotian.framework.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

import com.xiaotian.framework.common.Mylog;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description
 * @date 2015/11/4
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 深圳名仕之约信息科技有限公司.LTD, All Rights Reserved.
 */
public class UtilAnimation implements Animation.AnimationListener {
    public static final int HANDLER_TYPE_START = 0x001;
    public static final int HANDLER_TYPE_REPEAT = 0x002;
    public static final int HANDLER_TYPE_RESTART = 0x003;
    public static final int STATUS_REPEAT = 0X001;
    public static final int STATUS_STOP = 0X002;
    public static final int STATUS_STAR = 0X003;
    public static final int STATUS_CLEAN = 0X004;
    //
    private Context mContext;
    private int animationCode;
    private View animationView;
    private boolean isRepeatAnimation;
    private int animationRes1, animationRes2;
    private Animation animation1, animation2;
    Handler mHandler = new Handler();

    public UtilAnimation() {}

    public UtilAnimation(Context context) {
        this.mContext = context;
    }

    public static UtilAnimation getInstanceRepeat(View repeatView) {
        UtilAnimation animation = new UtilAnimation(repeatView.getContext());
        animation.animationView = repeatView;
        animation.initRepeatAnimatioin();
        return animation;
    }

    private void initRepeatAnimatioin() {
        mHandler = new Handler(new Handler.Callback() {
            int animationCode;

            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                case HANDLER_TYPE_START:
                    animationCode = msg.arg1;
                    animationView.clearAnimation();
                    animationView.startAnimation(animation1);
                    break;
                case HANDLER_TYPE_REPEAT:
                    if (animationCode == msg.arg1) {
                        animationView.clearAnimation();
                        animationView.startAnimation(animation2);
                    }
                    break;
                case HANDLER_TYPE_RESTART:
                    if (animationCode == msg.arg1) {
                        animationView.clearAnimation();
                        animationView.startAnimation(animation1);
                    }
                }
                return true;
            }
        });
    }

    /********************************************** Animation Method **********************************************/
    public void startAnimation(View view, int animation, Animation.AnimationListener listener) {
        Animation anima;
        try {
            anima = AnimationUtils.loadAnimation(mContext, animation);
        } catch (Resources.NotFoundException e) {
            Mylog.printStackTrace(e);
            return;
        }
        if (listener != null) anima.setAnimationListener(listener);
        if (view.getAnimation() != null) {
            view.getAnimation().setAnimationListener(null);
            view.clearAnimation();
        }
        view.startAnimation(anima);
    }

    // 循环交替执行Animation
    public void startAnimaRepeat(int animation1, int animation2) {
        if (animationView == null) return;
        clearAnimation();
        try {
            this.animation1 = AnimationUtils.loadAnimation(mContext, animation1);
            this.animation2 = AnimationUtils.loadAnimation(mContext, animation2);
            this.animation1.setAnimationListener(this);
            this.animation2.setAnimationListener(this);
        } catch (Resources.NotFoundException e) {
            Mylog.printStackTrace(e);
            return;
        }
        animationCode = String.valueOf(System.currentTimeMillis()).hashCode();
        mHandler.sendMessageDelayed(mHandler.obtainMessage(HANDLER_TYPE_START, animationCode, 0), 50);
    }

    // Push Down Show View
    public void startPushDownHide(View view, Animation.AnimationListener listener) {
        startPushDownHide(view, 300, listener);
    }

    public void startPushDownHide(View view, long duration, Animation.AnimationListener listener) {
        final Interpolator interpolator = new LinearInterpolator(); // Animation Interpolator
        TranslateAnimation pushDown = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
        pushDown.setDuration(duration);
        pushDown.setFillAfter(true);
        pushDown.setInterpolator(interpolator);
        if (listener != null) pushDown.setAnimationListener(listener);
        if (view.getAnimation() != null) {
            view.getAnimation().setAnimationListener(null);
            view.clearAnimation();
        }
        view.startAnimation(pushDown);
    }

    public void startPushDownShow(View view, Animation.AnimationListener listener) {
        startPushDownShow(view, 500, listener);
    }

    public void startPushDownShow(View view, long duration, Animation.AnimationListener listener) {
        final Interpolator interpolator = new LinearInterpolator(); // Animation Interpolator
        TranslateAnimation pushDown = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0);
        pushDown.setDuration(duration);
        pushDown.setFillAfter(true);
        pushDown.setInterpolator(interpolator);
        if (listener != null) pushDown.setAnimationListener(listener);
        if (view.getAnimation() != null) {
            view.getAnimation().setAnimationListener(null);
            view.clearAnimation();
        }
        view.startAnimation(pushDown);
    }

    // Push Up Hide View
    public void startPushUpHide(View view, Animation.AnimationListener listener) {
        startPushUpHide(view, 300, listener);
    }

    public void startPushUpHide(View view, long duratioin, Animation.AnimationListener listener) {
        final Interpolator interpolator = new LinearInterpolator();
        TranslateAnimation pushUp = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1);
        pushUp.setDuration(duratioin);
        pushUp.setFillAfter(true);
        pushUp.setInterpolator(interpolator);
        if (listener != null) pushUp.setAnimationListener(listener);
        if (view.getAnimation() != null) {
            view.getAnimation().setAnimationListener(null);
            view.clearAnimation();
        }
        view.startAnimation(pushUp);
    }

    public void startPushUpShow(View view, Animation.AnimationListener listener) {
        startPushUpShow(view, 500, listener);
    }

    public void startPushUpShow(View view, long duration, Animation.AnimationListener listener) {
        final Interpolator interpolator = new LinearInterpolator();
        TranslateAnimation pushUp = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
        pushUp.setDuration(duration);
        pushUp.setFillAfter(true);
        pushUp.setInterpolator(interpolator);
        if (listener != null) pushUp.setAnimationListener(listener);
        if (view.getAnimation() != null) {
            view.getAnimation().setAnimationListener(null);
            view.clearAnimation();
        }
        view.startAnimation(pushUp);
    }

    /********************************************** Other Method **********************************************/
    // Re Start Animation
    public void reStartAnimaRepeat() {
        if (animationView == null || animation1 == null || animation2 == null) return;
        clearAnimation();
        animationCode = String.valueOf(System.currentTimeMillis()).hashCode();
        mHandler.sendMessage(mHandler.obtainMessage(HANDLER_TYPE_START, animationCode, 0));
    }

    // Clear Animation
    public void clearAnimation() {
        if (animationView == null) return;
        animationCode = 0;
        mHandler.removeMessages(HANDLER_TYPE_START);
        mHandler.removeMessages(HANDLER_TYPE_REPEAT);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (isRepeatAnimation) {
            mHandler.sendMessage(mHandler.obtainMessage(HANDLER_TYPE_RESTART, animationCode, 0));
            isRepeatAnimation = false;
            return;
        }
        mHandler.sendMessage(mHandler.obtainMessage(HANDLER_TYPE_REPEAT, animationCode, 0));
        isRepeatAnimation = true;
    }

    @Override
    public void onAnimationStart(Animation animation) {}

    @Override
    public void onAnimationRepeat(Animation animation) {}

    public static class SimpleAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {}

        @Override
        public void onAnimationRepeat(Animation animation) {}
    }
}
