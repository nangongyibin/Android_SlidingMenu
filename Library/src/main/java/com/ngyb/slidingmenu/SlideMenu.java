package com.ngyb.slidingmenu;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 作者：南宫燚滨
 * 描述：如果我们需要继承ViewGroup并且自己不想实现onMeasure,OnLayout，那么可以选择继承已有的一个布局，比如FrameLayout
 * 邮箱：nangongyibin@gmail.com
 * 日期：2020/6/26 11:19
 */
public class SlideMenu extends FrameLayout {
    private static final String TAG = "SlideMenu";
    ViewDragHelper viewDragHelper;
    int maxLeft;
    View main, menu;
    FloatEvaluator floatEvaluator = new FloatEvaluator();

    public SlideMenu(@NonNull Context context) {
        this(context, null);
    }

    public SlideMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        viewDragHelper = ViewDragHelper.create(this, cb);
    }

    /**
     * 当填充完子View的时候执行，所以可以获取到自己所有的子View
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        main = getChildAt(1);
        menu = getChildAt(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //让viewDragHelper帮我们处理触摸事件
        viewDragHelper.processTouchEvent(ev);
        return true;
//        return super.onInterceptTouchEvent(ev);
    }

    ViewDragHelper.Callback cb = new ViewDragHelper.Callback() {
        /**
         * 是否捕获当前view的触摸事件
         * @param view 当前触摸到的子view
         * @param i 触摸点的索引 就是多点触摸的时候，每个触摸点的ID
         * @return true表示捕获，false不捕获
         */
        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            return view == main || view == menu;
        }

        /**
         *当一个view的触摸事件被捕获的时候执行
         * @param capturedChild
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            maxLeft = (int) (getMeasuredWidth() / 0.6f);
        }

        /**
         * 获取capturedChild水平拖拽范围的，这个方法的作用其实是用来控制是否想强制水平滑动的，如果返回任意大于0的数，就可以强制水平滑动，反之则不能任意滑动
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return 1;
        }

        /**
         * 控制，修改修正view水平方向的位置
         * @param child 当前触摸的子view
         * @param left 表示viewDragHelper计算好的left值，left= child.getLeft()+dx
         * @param dx 表每次移动的水平距离，向右移动就是正值，向左移动就是负值
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            //只限制main
            if (child == main) {
                left = fixLeft(left);
            }
            return left;
        }

        /**
         * 控制view垂直移动的滑动
         * @param child
         * @param top
         * @param dy
         * @return
         */
        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return 0;
        }

        /**
         * 当一个view的位置改变后执行，可以获取到移动的距离，从而实现一些伴随移动的效果
         * @param changedView  位置改变的view
         * @param left 改变后的最新的left
         * @param top 改变后最新的top
         * @param dx 每次手指移动的距离
         * @param dy
         */
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //如果当前位置改变的menu，那么我们手动让main也移动
            if (changedView == menu) {
                //通过layout方法来固定menu，那么我们让main也移动
                menu.layout(0, 0, menu.getMeasuredWidth(), menu.getMeasuredHeight());
                int newLeft = main.getLeft() + dx;
                newLeft = fixLeft(newLeft);
                main.layout(newLeft, 0, newLeft + main.getMeasuredWidth(), main.getBottom());
            }
            //执行一系列的动画
            //计算main滑动的百分比进度
            float fraction = main.getLeft() * 1.0f / maxLeft;
            //根据百分比执行动画
            execAnim(fraction);
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (main.getLeft() > maxLeft / 2) {
                //open
                viewDragHelper.smoothSlideViewTo(main, maxLeft, 0);
                ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
            } else {
                //close
                viewDragHelper.smoothSlideViewTo(main, 0, 0);
                ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
            }
        }
    };

    /**
     * @param fraction 执行动画
     */
    private void execAnim(float fraction) {
        //main执行缩放
        main.setScaleX(floatEvaluator.evaluate(fraction, 1.0f, 0.2f));
        main.setScaleY(floatEvaluator.evaluate(fraction, 1.0f, 0.2f));
        //menu执行缩放
        menu.setScaleX(floatEvaluator.evaluate(fraction, 0.2f, 1.0f));
        menu.setScaleY(floatEvaluator.evaluate(fraction, 0.2f, 1.0f));
        //平移
        menu.setTranslationX(floatEvaluator.evaluate(fraction, -menu.getMeasuredWidth() / 2, 0));
        //3D动画
        main.setRotationY(floatEvaluator.evaluate(fraction, 0, 90));
        main.setTranslationY(floatEvaluator.evaluate(fraction, 0, main.getMeasuredWidth() / 2));
        menu.setRotationY(floatEvaluator.evaluate(fraction, -90, 0));
    }

    /**
     *
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    /**
     * 限制left变量的取值范围
     *
     * @param left
     * @return
     */
    private int fixLeft(int left) {
        if (left > maxLeft) {
            left = maxLeft;
        } else if (left < 0) {
            left = 0;
        }
        return left;
    }
}
