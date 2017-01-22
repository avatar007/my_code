package com.itheima.quickindexbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.provider.ContactsContract.DataUsageFeedback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class QuickIndexBar extends View {
	private String[] indexArr = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };
	private Paint paint;
	private int width;	//控件的宽
	private float cellHeight;
	public QuickIndexBar(Context context) {
		super(context);
		init();
	}

	public QuickIndexBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public QuickIndexBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = getMeasuredWidth();
		//这里要变成float类型,不然每个格子都会忽略掉一点距离,最后Z字母下方会空出一点距离
		cellHeight = getMeasuredHeight()*1f/indexArr.length;
	}

	private void init() {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);	//设置模式为抗锯齿,不会有锯齿状
		paint.setTextSize(DataTools.dip2px(getContext(), 14));
		paint.setColor(Color.WHITE);
		//设置文字的起始计算位置,center是底部中间的位置
		paint.setTextAlign(Align.CENTER);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (int i = 0; i < indexArr.length; i++) {
			float x = width/2;
			float y = cellHeight/2 + textHeight(indexArr[i])/2 + cellHeight*i;
			
			//点击字母变色(根据索引来判断,最后点击的索引和i一直时候,设置文字颜色为黑色),触摸事件后要刷新界面
			paint.setColor(lastIndex == i ? Color.BLACK : Color.WHITE);
			
			//绘制文字
			canvas.drawText(indexArr[i], x, y, paint);
		}
	}
	
	private int lastIndex = -1;	//上次显示的字母的索引,默认值不能为0,0是第一个字母
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE://滑动时候也要执行此逻辑
			//获取按下时的高度除以格子的高度就可以获取当前按下字母的索引
			float y = event.getY();
			int index = (int) (y / cellHeight);
			if (lastIndex != index) {
//				System.out.println("按下字母为:" + indexArr[index]);
				if (listener != null) {
					listener.onTouchLetter(indexArr[index]);
				}
			}
			lastIndex = index;
			break;
		case MotionEvent.ACTION_UP:
			//抬起时重置lastIndex的值
			lastIndex = -1;
			break;
		}
		
		//刷新界面,重新绘制文字颜色
		invalidate();
		
		return true;
	}
	
	/**
	 * 获取文本高度
	 */
	private float textHeight(String str) {
		//获取文本的高度,要传递一个矩形对象,设置后矩形对象就又对应的宽高了
		 Rect bounds = new Rect();
		 //参2:文本开始位置索引(从第一个文字开始) 参3:文本结束位置索引(到第几个文字结束,这里就一个也可用填1)
		paint.getTextBounds(str, 0, str.length(), bounds);
		return bounds.height();
	}
	
	private onTouchLetterListener listener;
	public void setOnTouchLetterListener(onTouchLetterListener listener) {
		this.listener = listener;
	}
	
	//设置接口回调通知外面具体点击的是哪个字母
	public interface onTouchLetterListener {
		void onTouchLetter(String letter);
	}

}
