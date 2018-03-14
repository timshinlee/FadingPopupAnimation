package com.timshinlee.fadingpopupanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FadingPopupAnimationHelper {
    private static final String TAG = "FadingPopupAnimationHel";
    private long mPopInTimeInMillis;
    private long mFadingPopupTimeInMillis;
    private static int mTextContentSizeInSp = 16;
    private static int mTextContentColor = Color.BLACK;
    private static String mTextContent = "棒棒哒";
    private PopupContentProvider mContentProvider;

    private FadingPopupAnimationHelper() {
        int SHORT_ANIM_TIME = Resources.getSystem().getInteger(android.R.integer.config_shortAnimTime);
        int LONG_ANIM_TIME = Resources.getSystem().getInteger(android.R.integer.config_longAnimTime);
        mPopInTimeInMillis = SHORT_ANIM_TIME;
        mFadingPopupTimeInMillis = LONG_ANIM_TIME * 2;
    }

    private Activity mActivity;

    public static class Builder {
        private FadingPopupAnimationHelper instance;

        public Builder(final Activity activity) {
            instance = new FadingPopupAnimationHelper();
            instance.mActivity = activity;
        }

        public FadingPopupAnimationHelper build() {
            if (instance.mContentProvider == null) {
                instance.mContentProvider = new TextContentProvider();
            }
            return instance;
        }

        public Builder setPopupContent(String content) {
            FadingPopupAnimationHelper.mTextContent = content;
            return this;
        }

        public Builder setPopInTimeInMillis(long millis) {
            instance.mPopInTimeInMillis = millis;
            return this;
        }

        public Builder setFadingPopUpTimeInMillis(long millis) {
            instance.mFadingPopupTimeInMillis = millis;
            return this;
        }

        public Builder setTextContentSizeInSp(int contentSizeInSp) {
            FadingPopupAnimationHelper.mTextContentSizeInSp = contentSizeInSp;
            return this;
        }

        public Builder setTextContentColor(int color) {
            FadingPopupAnimationHelper.mTextContentColor = color;
            return this;
        }

        public Builder setContentProvider(PopupContentProvider provider) {
            instance.mContentProvider = provider;
            return this;
        }

    }

    public void addFadingPopupAnimation(final CheckableWrapper target) {
        if (target.isCheck()) {
            return;
        }
        target.getView().setClickable(true);
        target.getView().setFocusable(true);

        final ViewGroup decorView = (ViewGroup) mActivity.getWindow().getDecorView();
        GestureDetector.OnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent event) {

                if (target.isCheck()) {
                    return false;
                }

                final float rawX = event.getRawX();
                final float rawY = event.getRawY();
                final View contentView = mContentProvider.providePopupContent(mActivity);
                final int[] contentSize = mContentProvider.size;
                final float destX = rawX;
                // - contentView.getWidth() / 2;
                final float destY = rawY;
                // - contentView.getHeight() / 2;

//                int[] location = new int[2];
//                target.getView().getLocationInWindow(location);
//                Log.e(TAG, "onSingleTapUp: " + (contentSize));
//                Log.e(TAG, "onSingleTapUp: " + (contentSize));
//                final float destX = location[0] + target.getView().getWidth() / 2
//                        - contentSize[0] / 2;
//                final float destY = location[1] + target.getView().getHeight() / 2
//                        - contentSize[1] / 2;


                final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                final ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(layoutParams);
                marginLayoutParams.leftMargin = (int) destX;
                marginLayoutParams.topMargin = (int) destY;
                contentView.setLayoutParams(marginLayoutParams);
                decorView.addView(contentView);

                final ObjectAnimator scaleX = ObjectAnimator.ofFloat(contentView, "scaleX", 0.5f, 1);
                final ObjectAnimator scaleY = ObjectAnimator.ofFloat(contentView, "scaleY", 0.5f, 1);
                final AnimatorSet scaleAnim = new AnimatorSet();
                scaleAnim.setDuration(mPopInTimeInMillis).playTogether(scaleX, scaleY);

                final ObjectAnimator translationY = ObjectAnimator.ofFloat(contentView, "translationY", 0, -100);
                final ObjectAnimator alpha = ObjectAnimator.ofFloat(contentView, "alpha", 1, 0);
                final AnimatorSet translationAlphaAnim = new AnimatorSet();
                translationAlphaAnim.setDuration(mFadingPopupTimeInMillis);
                translationAlphaAnim.playTogether(translationY, alpha);

                final AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(scaleAnim, translationAlphaAnim);
                animatorSet.start();


                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                                && mActivity.isDestroyed()) {
                            return;
                        }
                        if (mActivity.isFinishing()) {
                            return;
                        }
                        decorView.removeView(contentView);
                    }
                });
                return false;
            }
        };
        final GestureDetector gestureDetector = new GestureDetector(mActivity, listener);
        target.getView().setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    public static abstract class PopupContentProvider {
        int[] size;

        PopupContentProvider() {
            size = setContentSize();
        }

        abstract View providePopupContent(Context context);

        abstract int[] setContentSize();
    }

    public static class TextContentProvider extends PopupContentProvider {
        int[] size;

        @Override
        public View providePopupContent(Context context) {
            final TextView textView = new TextView(context);
            textView.setText(FadingPopupAnimationHelper.mTextContent);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextContentSizeInSp);
            textView.setTextColor(mTextContentColor);

            final int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            textView.measure(measureSpec, measureSpec);
            size = new int[]{textView.getMeasuredWidth(), textView.getMeasuredHeight()};
            return textView;
        }

        @Override
        int[] setContentSize() {
            return size;
        }
    }
}
