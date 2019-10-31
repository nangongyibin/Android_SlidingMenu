package com.ngyb.slidingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * 作者：南宫燚滨
 * 描述：
 * 邮箱：nangongyibin@gmail.com
 * 日期：2019/10/30 12:42
 */
public class SlidingMenu extends RelativeLayout {
    private static final String TAG = "SlidingMenu";
    private float downX = 0;//按下的X坐标
    private float downY = 0;//按下的Y坐标
    private int width;//侧滑菜单的宽度
    private float distanceX = 0;//当前的侧滑菜单宽度
    private float currentLeft = 0;//侧滑菜单距离左侧的坐标
    private float currentX = 0;//当前X的坐标
    private final Scroller scroller;
    private float startX;//移动开始的位置
    private float endX;//移动结束的位置
    private int sx;
    private int dx;//移动的距离
    private int time;//侧滑所用时间
    private int currX;
    private float moveX;//移动的X
    private float moveY;//移动的Y

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(getContext());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        View menu = getChildAt(0);
        View main = getChildAt(1);
        width = menu.getMeasuredWidth();
        main.layout(l, t, r, b);
        menu.layout(-width, t, l, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                distanceX = currentX - downX + currentLeft;
                if (distanceX < 0) {
                    distanceX = 0;
                } else if (distanceX > width) {
                    distanceX = width;
                }
                scrollToX((int) distanceX);
                break;
            case MotionEvent.ACTION_UP:
                if (distanceX >= width / 2) {
                    currentLeft = width;
                } else {
                    currentLeft = 0;
                }
                startX = distanceX;
                endX = currentLeft;
                scrollToEnd(startX, endX);
                break;
        }
        return true;
    }

    private void scrollToEnd(float startX, float endX) {
        sx = (int) startX;
        dx = (int) (endX - startX);
        time = dx * 15;
        scroller.startScroll(sx, 0, dx, 0, time);
        invalidate();
    }

    private void scrollToX(int x) {
        super.scrollTo(-x, 0);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            currX = scroller.getCurrX();
            scrollToX(currX);
            invalidate();
        }
    }

    /**
     * 事件是否拦截的处理
     * 处理逻辑是：x坐标的移动量大于y坐标的移动量，就把触摸事件拦截掉。
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getMetaState()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = ev.getX() - downX;
                moveY = ev.getY() - downY;
                downX = ev.getX();
                downY = ev.getY();
                if (Math.abs(moveX) > Math.abs(moveY)) {
                    Log.e(TAG, "onInterceptTouchEvent: ");
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

    public void switchMenu() {
        if (currentLeft == 0) {
            currentLeft = width;
            startX = 0;
        } else {
            currentLeft = 0;
            startX = width;
        }
        scrollToEnd(startX, currentLeft);
    }
}
