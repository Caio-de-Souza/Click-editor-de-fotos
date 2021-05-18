package com.souza.caio.click.adaptadores;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

public class AdaptadorViewPager extends ViewPager
{

    public static final String MY_SCROLLER = "myScroller";

    public AdaptadorViewPager(@NonNull Context context)
    {
        super(context);
        setMyScroller();
    }

    public AdaptadorViewPager(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        setMyScroller();
    }

    private void setMyScroller()
    {
        try
        {
            Class<?> viewPager = ViewPager.class;
            Field scroller = viewPager.getDeclaredField(MY_SCROLLER);
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event)
    {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        return false;
    }

    private class MyScroller extends Scroller
    {
        public MyScroller(Context context)
        {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duracao)
        {
            super.startScroll(startX, startY, dx, dy, duracao);
        }
    }
}
