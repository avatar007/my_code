package com.itheima.parallax;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewPropertyAnimator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

public class ParallaxListView extends ListView {
	private ImageView imageView;
	private int maxHeight; // 最大高度
	private int imageViewHeight;	//imageView控件本身高度
	
	public ParallaxListView(Context context) {
		super(context);
	}

	public ParallaxListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ParallaxListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setImageView(final ImageView imageView) {
		this.imageView = imageView;
		if (imageView != null) {
			// 获取视图树添加全局布局的监听(监听一次就删除监听,否则会执行多次),此方法是在onlayout方法执行后执行的,能获取到控件高度
			imageView.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							imageView.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
							imageViewHeight = imageView.getHeight();
							// 获取imageView控件中图片的实质高度
							int imageHeight = imageView.getDrawable()
									.getIntrinsicHeight();
							maxHeight = imageViewHeight > imageHeight ? imageViewHeight * 2
									: imageHeight;
						}
					});
		}
	}

	// 此方法在listView滑动到头的时候执行,顶部或底部,可用获取到继续滑动的距离,所有可以滚动的view都有此方法
	// 可用在listView,gridView,scrollView,horienzontalScrollView中使用
	// deltaX:水平方向继续滑动的距离 值为正向上滑动(底部到头),值为负向下滑动(顶部到头)
	// scrollX: 水平方向滑动到的位置
	// scrollRangeX : 水平方向可滚动到的位置
	// maxOverScrollX:水平方向最大滑动到的最大的距离
	// isTouchEvent : true:表示是手指滑动, false:表示惯性滑动 fling(快速滑动后的惯性滑动)
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		if (deltaY < 0 && isTouchEvent) { // 顶部到头,并且是手动滑动
			if (imageView != null) {
				int newHeight = imageView.getHeight() - deltaY/3;
				if (newHeight < maxHeight) {
					imageView.getLayoutParams().height = newHeight;
					imageView.requestLayout(); //使ImageView的布局参数生效
				}
			}
		}

		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
				scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
				isTouchEvent);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			//此方法只是传递一个值,具体的做法还是在动画中执行的
			ValueAnimator animator = ValueAnimator.ofInt(imageView.getHeight(), imageViewHeight);
			//添加动画更新的监听,时时获取高度改变的值,并设置给imageView(执行动画)
			animator.addUpdateListener(new AnimatorUpdateListener() {
				
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					int newHeight = (Integer) animation.getAnimatedValue();
					imageView.getLayoutParams().height = newHeight;
					imageView.requestLayout(); // 此方法是设置布局参数,完成布局的设定
				}
			});
			
			animator.setInterpolator(new OvershootInterpolator(3));//增加弹性插补器,参数是弹起的幅度
			animator.setDuration(500);
			animator.start();
		}
		return super.onTouchEvent(ev);
	}

}
