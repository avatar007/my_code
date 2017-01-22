package com.itheima.qqslidemenu;

import com.itheima.qqslidemenu.SlideMenu.DragState;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 使用自定义的linearLayout,状态为打开时,mainView不能响应点击事件
 * 
 * @author Administrator
 * 
 */
public class MyLinearLayout extends LinearLayout {

	public MyLinearLayout(Context context) {
		super(context);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private SlideMenu slideMenu;

	public void setSlideMenu(SlideMenu slideMenu) {
		this.slideMenu = slideMenu;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (slideMenu != null && slideMenu.getCurrentState() == DragState.open) {
			//如果slideMenu打开则应该拦截并消费掉事件
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (slideMenu != null && slideMenu.getCurrentState() == DragState.open) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				//抬起则应该关闭slideMenu
				slideMenu.close();
			}
			//如果slideMenu打开则应该拦截并消费掉事件
			return true;
		}
		return super.onTouchEvent(event);
	}
}
