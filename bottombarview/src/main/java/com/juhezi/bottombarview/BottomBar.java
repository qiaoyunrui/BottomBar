package com.juhezi.bottombarview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.view.menu.MenuBuilder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Juhezi[juhezix@163.com] on 2017/5/11.
 */

public class BottomBar extends LinearLayout {

    private final static String TAG = BottomBar.class.getSimpleName();

    private Menu mMenu;

    private MenuInflater mMenuInflater;

    private float[] childXs;

    public BottomBar(Context context) {
        this(context, null);
    }

    public BottomBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mMenuInflater = new MenuInflater(context);
        mMenu = new MenuBuilder(context);
        mMenuInflater.inflate(R.menu.default_menu, mMenu);
        initView(context);
    }

    /**
     * 根据 menu 添加子 View
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initView(Context context) {
        setOrientation(HORIZONTAL); //设置为水平方向
        View temp;
        TextView tempTitle;
        ImageView tempIcon;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        for (int i = 0; i < mMenu.size(); i++) {
            temp = layoutInflater.inflate(R.layout.item_content_layout, this, false);
            tempTitle = (TextView) temp.findViewById(R.id.tv_title);
            tempTitle.setText(mMenu.getItem(i).getTitle());
            tempIcon = (ImageView) temp.findViewById(R.id.img_icon);
            tempIcon.setBackground(mMenu.getItem(i).getIcon());
            addView(temp);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        childXs = new float[getChildCount()];
        for (int i = 0; i < getChildCount(); i++) {
            childXs[i] = getChildAt(i).getX();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) { //滑动
            return true;    //进行拦截
        }
        return false;
    }

    private float startX;   //滑动起点X
    private float startY;   //Y
    private int selectPosition;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                startY = ev.getY();
                selectPosition = getTouchChild(startX);
                if (selectPosition != -1) {
                    moveIconUp(selectPosition);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - startX;
                float dy = ev.getY() - startY;
                if (selectPosition != -1 && dx != 0f) {
                    float t = dx / dy;
                    double radian = Math.atan(t);
                    float degree = (float) Math.toDegrees(radian);
                    //对角度进行改变
                    if (dy > 0f) {
                        degree += 180f;
                    }
                    if (dx > 0 && dy < 0) {
                        degree += 360f;
                    }
                    moveIcon(selectPosition, degree);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (selectPosition != -1) {
                    resetIcon(selectPosition);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    private int getTouchChild(float x) {
        for (int i = 0; i < childXs.length - 1; i++) {
            if (x > childXs[i] && x <= childXs[i + 1])
                return i;
        }
        if (x > childXs[childXs.length - 1]) {
            return childXs.length - 1;
        } else {
            return -1;
        }
    }

    /**
     * 将图标向上或者向下移动
     *
     * @param position
     * @return
     */
    private void moveIconUp(int position) {
        View v = getChildAt(position);
        ImageView imgIcon = (ImageView) v.findViewById(R.id.img_icon);
        if (imgIcon != null) {
            imgIcon.setTranslationY(-10f);
        }
    }

    private void moveIcon(int position, float degree) {
        View v = getChildAt(position);
        ImageView imgIcon = (ImageView) v.findViewById(R.id.img_icon);
        if (imgIcon != null) {
            float dx = (float) (10 * Math.sin(Math.toRadians(degree)));
            float dy = (float) (10 * Math.cos(Math.toRadians(degree)));
            imgIcon.setTranslationX(-dx);
            imgIcon.setTranslationY(-dy);
        }
    }

    private void resetIcon(int position) {
        View v = getChildAt(position);
        ImageView imgIcon = (ImageView) v.findViewById(R.id.img_icon);
        if (imgIcon != null) {
            imgIcon.setTranslationX(0);
            imgIcon.setTranslationY(0);
        }
    }

}