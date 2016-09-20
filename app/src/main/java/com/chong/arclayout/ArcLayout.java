package com.chong.arclayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
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
    private static final int LOCATION_LEFT_TOP = 0;
    private static final int LOCATION_LEFT_BOTTOM = 1;
    private static final int LOCATION_RIGHT_TOP = 2;
    private static final int LOCATION_RIGHT_BOTTOM = 3;

    /**
     * 主按钮位置，默认右下
     */
    private Location mLocation = Location.RIGHT_BOTTOM;
    /**
     * 展开弧形半径
     */
    private int mRadius;

    /**
     * 展开关闭动画持续时间，默认300ms
     */
    private int mAnimDuration = 300;
    /**
     * 子菜单点击后缩放动画的持续时间，默认300ms
     */
    private int mScaleAnimDuration = 300;

    /**
     * 菜单的状态，默认关闭
     */
    private Status mCurrentStatus = Status.CLOSE;
    /**
     * 菜单的主按钮
     */
    private View mMainView;

    private OnMenuItemClickListener mMenuItemClickListener;

    private OnMainMenuItemClickListener mOnMainMenuItemClickListener;

    public enum Status {
        OPEN, CLOSE
    }

    /**
     * 菜单的位置枚举类
     */
    public enum Location {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    /**
     * 点击子菜单项的回调接口
     */
    public interface OnMenuItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener mMenuItemClickListener) {
        this.mMenuItemClickListener = mMenuItemClickListener;
    }

    /**
     * 点击主按钮项的回调接口
     */
    public interface OnMainMenuItemClickListener {
        /**
         * 主按钮项点击事件
         *
         * @param view   主按钮
         * @param status 当前展开关闭状态
         * @return true 需要展开;false 不需要展开
         */
        boolean onMainClick(View view, Status status);
    }

    public void setOnMainMenuItemClickListener(OnMainMenuItemClickListener onMainMenuItemClickListener) {
        mOnMainMenuItemClickListener = onMainMenuItemClickListener;
    }

    /**
     * 主按钮点击后是否需要展开
     */
    private boolean isNeedOpen = true;


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

        int location = a.getInt(R.styleable.ArcLayout_location, LOCATION_RIGHT_BOTTOM);
        switch (location) {
            case LOCATION_LEFT_TOP:
                mLocation = Location.LEFT_TOP;
                break;
            case LOCATION_LEFT_BOTTOM:
                mLocation = Location.LEFT_BOTTOM;
                break;
            case LOCATION_RIGHT_TOP:
                mLocation = Location.RIGHT_TOP;
                break;
            case LOCATION_RIGHT_BOTTOM:
                mLocation = Location.RIGHT_BOTTOM;
                break;
        }
        mRadius = (int) a.getDimension(R.styleable.ArcLayout_arc_radius, TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
                        getResources().getDisplayMetrics()));
        mAnimDuration = a.getInt(R.styleable.ArcLayout_anim_duration, mAnimDuration);
        mScaleAnimDuration = a.getInt(R.styleable.ArcLayout_anim_duration, mScaleAnimDuration);

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
            layoutMainView();

            int count = getChildCount();

            for (int i = 0; i < count - 1; i++) {
                View child = getChildAt(i + 1);

                child.setVisibility(View.GONE);

                int childLeft = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
                int childTop = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));

                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();

                // 左上
                if (mLocation == Location.LEFT_TOP) {
                    childLeft = getPaddingLeft() + childLeft;
                    childTop = getPaddingTop() + childTop;
                }

                // 左下
                if (mLocation == Location.LEFT_BOTTOM) {
                    childLeft = getPaddingLeft() + getPaddingRight() + childLeft;
                    childTop = getMeasuredHeight() - childHeight - childTop - getPaddingTop() - getPaddingBottom();
                }

                // 右上
                if (mLocation == Location.RIGHT_TOP) {
                    childLeft = getMeasuredWidth() - childWidth - childLeft - getPaddingLeft() - getPaddingRight();
                    childTop = getPaddingTop() + getPaddingBottom() + childTop;
                }

                // 右下
                if (mLocation == Location.RIGHT_BOTTOM) {
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
    private void layoutMainView() {
        mMainView = getChildAt(0);
        mMainView.setOnClickListener(this);

        int l = getPaddingLeft();
        int t = getPaddingTop();

        int width = mMainView.getMeasuredWidth();
        int height = mMainView.getMeasuredHeight();

        switch (mLocation) {
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
        mMainView.layout(l, t, l + width, t + height);
    }

    @Override
    public void onClick(View v) {

        if (mOnMainMenuItemClickListener != null) {
            isNeedOpen = mOnMainMenuItemClickListener.onMainClick(v, mCurrentStatus);
        }

        if (isNeedOpen) {
            toggleMenu(mAnimDuration);
//            rotateMainView(v, 0f, 360f, mAnimDuration);
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

            int xFlag = 1;
            int yFlag = 1;

            if (mLocation == Location.LEFT_TOP || mLocation == Location.LEFT_BOTTOM) {
                xFlag = -1;
            }

            if (mLocation == Location.LEFT_TOP || mLocation == Location.RIGHT_TOP) {
                yFlag = -1;
            }

            AnimationSet animationSet = new AnimationSet(true);
            // 平移动画
            Animation tranAnim;
            // 透明动画
            Animation alphaAnim;

            if (mCurrentStatus == Status.CLOSE) {
                // to open
                tranAnim = new TranslateAnimation(xFlag * childLeft, 0, yFlag * childTop, 0);
                alphaAnim = new AlphaAnimation(0f, 1f);
                childView.setClickable(true);
                childView.setFocusable(true);

            } else {
                // to close
                tranAnim = new TranslateAnimation(0, xFlag * childLeft, 0, yFlag * childTop);
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
            final int position = i + 1;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMenuItemClickListener != null) {
                        mMenuItemClickListener.onItemClick(childView, position);
                    }
                    changeStatus();
                    menuItemAnim(position - 1);
                }
            });
        }
        // 切换菜单状态
        changeStatus();
    }

    /**
     * 添加menuItem的点击动画
     *
     * @param position menu position
     */
    private void menuItemAnim(int position) {
        for (int i = 0; i < getChildCount() - 1; i++) {

            final View childView = getChildAt(i + 1);
            if (i == position) {
                childView.startAnimation(scaleBigAnim(mScaleAnimDuration, childView));
            } else {
                childView.startAnimation(scaleSmallAnim(mScaleAnimDuration, childView));
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
     * @param duration 动画持续时间
     * @return 动画
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


    private void rotateMainView(View v, float start, float end, int duration) {

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

    public void setAnimDuration(int animDuration) {
        mAnimDuration = animDuration;
    }

    public void setScaleAnimDuration(int scaleAnimDuration) {
        mScaleAnimDuration = scaleAnimDuration;
    }

    public View getMainView() {
        return mMainView;
    }
}
