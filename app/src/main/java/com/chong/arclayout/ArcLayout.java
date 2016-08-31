package com.chong.arclayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;


public class ArcLayout extends ViewGroup implements OnClickListener {
    private static final int POS_LEFT_TOP = 0;
    private static final int POS_LEFT_BOTTOM = 1;
    private static final int POS_RIGHT_TOP = 2;
    private static final int POS_RIGHT_BOTTOM = 3;

    private Position mPosition = Position.RIGHT_BOTTOM;
    private int mRadius;


    /**
     * 菜单的状态
     */
    private Status mCurrentStatus = Status.CLOSE;
    /**
     * 菜单的主按钮
     */
    private View mMainButton;

    private OnMenuItemClickListener mMenuItemClickListener;

    private OnMainMenuItemClickListener mOnMainMenuItemClickListener;

    public enum Status {
        OPEN, CLOSE
    }

    /**
     * 菜单的位置枚举类
     */
    public enum Position {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    /**
     * 点击子菜单项的回调接口
     */
    public interface OnMenuItemClickListener {
        void onItemClick(View view, int pos);
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener mMenuItemClickListener) {
        this.mMenuItemClickListener = mMenuItemClickListener;
    }

    /**
     * 点击主按钮项的回调接口
     */
    public interface OnMainMenuItemClickListener {
        void onMainClick(View view, Status status);
    }

    public void setOnMainMenuItemClickListener(OnMainMenuItemClickListener onMainMenuItemClickListener) {
        mOnMainMenuItemClickListener = onMainMenuItemClickListener;
    }


    public ArcLayout(Context context) {
        this(context, null);
    }

    public ArcLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                100, getResources().getDisplayMetrics());

        // 获取自定义属性的值
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArcLayout);

        int pos = a.getInt(R.styleable.ArcLayout_position, POS_RIGHT_BOTTOM);
        switch (pos) {
            case POS_LEFT_TOP:
                mPosition = Position.LEFT_TOP;
                break;
            case POS_LEFT_BOTTOM:
                mPosition = Position.LEFT_BOTTOM;
                break;
            case POS_RIGHT_TOP:
                mPosition = Position.RIGHT_TOP;
                break;
            case POS_RIGHT_BOTTOM:
                mPosition = Position.RIGHT_BOTTOM;
                break;
        }
        mRadius = (int) a.getDimension(R.styleable.ArcLayout_radius, TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
                        getResources().getDisplayMetrics()));

        Log.e("TAG", "position = " + mPosition + " , radius =  " + mRadius);

        a.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0;
        int height = 0;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            // 测量child
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 当前子view实际占据的宽度
            int childWidth = child.getMeasuredWidth();
            // 当前子控件实际占据的高度
            int childHeight = child.getMeasuredHeight();
            if (i == 0 || i == 1) {
                width += childWidth / 2;
            }
            if (i == 0 || i == count - 1) {
                height += childHeight / 2;
            }
        }

        width += mRadius;
        height += mRadius;

        setMeasuredDimension(
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom()
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            layoutMainButton();

            int count = getChildCount();

            for (int i = 0; i < count - 1; i++) {
                View child = getChildAt(i + 1);

                child.setVisibility(View.GONE);

                int childLeft = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
                int childTop = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));

                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();

                // 左上
                if (mPosition == Position.LEFT_TOP) {
                    childLeft = getPaddingLeft() + childLeft;
                    childTop = getPaddingTop() + childTop;
                }

                // 左下
                if (mPosition == Position.LEFT_BOTTOM) {
                    childLeft = getPaddingLeft() + getPaddingRight() + childLeft;
                    childTop = getMeasuredHeight() - childHeight - childTop - getPaddingTop() - getPaddingBottom();
                }

                // 右上
                if (mPosition == Position.RIGHT_TOP) {
                    childLeft = getMeasuredWidth() - childWidth - childLeft - getPaddingLeft() - getPaddingRight();
                    childTop = getPaddingTop() + getPaddingBottom() + childTop;
                }

                // 右下
                if (mPosition == Position.RIGHT_BOTTOM) {
                    childLeft = getMeasuredWidth() - childWidth - childLeft - getPaddingLeft() - getPaddingRight();
                    childTop = getMeasuredHeight() - childHeight - childTop - getPaddingTop() - getPaddingBottom();
                }

                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            }

        }

    }

    /**
     * 定位主菜单按钮
     */
    private void layoutMainButton() {
        mMainButton = getChildAt(0);
        mMainButton.setOnClickListener(this);

        int l = getPaddingLeft();
        int t = getPaddingTop();

        int width = mMainButton.getMeasuredWidth();
        int height = mMainButton.getMeasuredHeight();

        switch (mPosition) {
            case LEFT_TOP:
                l = getPaddingLeft();
                t = getPaddingTop();
                break;
            case LEFT_BOTTOM:
                l = getPaddingLeft();
                t = getMeasuredHeight() - height - getPaddingBottom() - getPaddingTop();
                break;
            case RIGHT_TOP:
                l = getMeasuredWidth() - width - getPaddingRight() - getPaddingLeft();
                t = getPaddingTop();
                break;
            case RIGHT_BOTTOM:
                l = getMeasuredWidth() - width - getPaddingRight() - getPaddingLeft();
                t = getMeasuredHeight() - height - getPaddingBottom() - getPaddingTop();
                break;
        }
        mMainButton.layout(l, t, l + width, t + height);
    }

    @Override
    public void onClick(View v) {
//        rotateMainButton(v, 0f, 360f, 300);

        toggleMenu(300);

        if (mOnMainMenuItemClickListener != null) {
            mOnMainMenuItemClickListener.onMainClick(v, mCurrentStatus);
        }

    }

    /**
     * 切换菜单
     */
    public void toggleMenu(int duration) {
        // 为menuItem添加平移动画和旋转动画
        int count = getChildCount();

        for (int i = 0; i < count - 1; i++) {
            final View childView = getChildAt(i + 1);
            childView.setVisibility(View.VISIBLE);

            int childLeft = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
            int childTop = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));

            int xflag = 1;
            int yflag = 1;

            if (mPosition == Position.LEFT_TOP || mPosition == Position.LEFT_BOTTOM) {
                xflag = -1;
            }

            if (mPosition == Position.LEFT_TOP || mPosition == Position.RIGHT_TOP) {
                yflag = -1;
            }

            AnimationSet animationSet = new AnimationSet(true);
            // 平移动画
            Animation tranAnim;
            // 透明动画
            Animation alphaAnim;

            if (mCurrentStatus == Status.CLOSE) {
                // to open
                tranAnim = new TranslateAnimation(xflag * childLeft, 0, yflag * childTop, 0);
                alphaAnim = new AlphaAnimation(0f, 1f);
                childView.setClickable(true);
                childView.setFocusable(true);

            } else {
                // to close
                tranAnim = new TranslateAnimation(0, xflag * childLeft, 0, yflag * childTop);
                alphaAnim = new AlphaAnimation(1f, 0f);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            tranAnim.setFillAfter(true);
            tranAnim.setDuration(duration);
            tranAnim.setStartOffset((i * 100) / count);

            alphaAnim.setFillAfter(true);
            alphaAnim.setDuration(duration);
            alphaAnim.setStartOffset((i * 100) / count);

            tranAnim.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE) {
                        childView.setVisibility(View.GONE);
                    }
                }
            });
            // 旋转动画
//            RotateAnimation rotateAnim = new RotateAnimation(0, 720,
//                    Animation.RELATIVE_TO_SELF, 0.5f,
//                    Animation.RELATIVE_TO_SELF, 0.5f);
//            rotateAnim.setDuration(duration);
//            rotateAnim.setFillAfter(true);

//            animationSet.addAnimation(rotateAnim);
            animationSet.addAnimation(alphaAnim);
            animationSet.addAnimation(tranAnim);
            childView.startAnimation(animationSet);

            // 子菜单点击
            final int pos = i + 1;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMenuItemClickListener != null) {
                        mMenuItemClickListener.onItemClick(childView, pos);
                    }
                    changeStatus();
                    menuItemAnim(pos - 1);
                }
            });
        }
        // 切换菜单状态
        changeStatus();
    }

    /**
     * 添加menuItem的点击动画
     *
     * @param pos
     */
    private void menuItemAnim(int pos) {
        for (int i = 0; i < getChildCount() - 1; i++) {

            final View childView = getChildAt(i + 1);
            if (i == pos) {
                childView.startAnimation(scaleBigAnim(300, childView));
            } else {
                childView.startAnimation(scaleSmallAnim(300, childView));
            }

            childView.setClickable(false);
            childView.setFocusable(false);

        }

    }

    private Animation scaleSmallAnim(int duration, final View view) {

        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0.0f);
        scaleAnim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mCurrentStatus == Status.CLOSE) {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationSet.addAnimation(scaleAnim);
        animationSet.addAnimation(alphaAnim);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;

    }

    /**
     * 为当前点击的Item设置变大和透明度降低的动画
     *
     * @param duration
     * @return
     */
    private Animation scaleBigAnim(int duration, final View view) {
        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0.0f);
        scaleAnim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mCurrentStatus == Status.CLOSE) {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animationSet.addAnimation(scaleAnim);
        animationSet.addAnimation(alphaAnim);

        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;

    }

    /**
     * 切换菜单状态
     */
    private void changeStatus() {
        mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
    }

    public boolean isOpen() {
        return mCurrentStatus == Status.OPEN;
    }


    private void rotateMainButton(View v, float start, float end, int duration) {

        RotateAnimation anim = new RotateAnimation(start, end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setDuration(duration);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

    public Status getCurrentStatus() {
        return mCurrentStatus;
    }
}
