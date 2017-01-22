package com.itheima.qqslidemenu.text;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class DargLayout extends ViewGroup {

	private View redView;
	private View blueView;
	private ViewDragHelper viewDragHelper;

	public DargLayout(Context context) {
		super(context);
		init();
	}

	public DargLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DargLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		// 参1:父view 参2:灵敏度(不写是正常值1.0) 参3:回调事件(可回传移动距离等)
		viewDragHelper = ViewDragHelper.create(this, 1.0f, callBack);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		redView = getChildAt(0);
		blueView = getChildAt(1);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// int size = (int) getResources().getDimension(R.dimen.width);
		int measureSpec = MeasureSpec.makeMeasureSpec(
				redView.getLayoutParams().width, MeasureSpec.EXACTLY);
		redView.measure(measureSpec, measureSpec);
		blueView.measure(measureSpec, measureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		l = getPaddingLeft();
		t = getPaddingTop();
		redView.layout(l, t, l + redView.getMeasuredWidth(),
				t + redView.getMeasuredHeight());
		blueView.layout(l, t + redView.getBottom(),
				l + blueView.getMeasuredWidth(), t + redView.getBottom()
						+ blueView.getMeasuredHeight());
	}

	@Override
	// 让让viewDragHelper的返回值去决定是否拦截点击事件
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
		return result;
	}

	@Override
	// 让viewDragHelper去处理触摸事件,返回true表示消费了事件
	public boolean onTouchEvent(MotionEvent event) {
		viewDragHelper.processTouchEvent(event);
		return true;
	}

	// viewDragHelper的回调匿名内部类
	private ViewDragHelper.Callback callBack = new Callback() {

		@Override
		// 尝试捕获触摸事件的子view,让哪一个子view事件被捕获
		public boolean tryCaptureView(View child, int pointerId) {
			return child == blueView || child == redView;
		}

		@Override
		// 被捕获的View的点击后处理的逻辑
		public void onViewCaptured(View capturedChild, int activePointerId) {
			if (capturedChild == blueView) {
				Toast.makeText(getContext(), "蓝色的被点击了", 0).show();
			} else if (capturedChild == redView) {
				Toast.makeText(getContext(), "红色的被点击了", 0).show();
			}
			super.onViewCaptured(capturedChild, activePointerId);
		}

		@Override
		// 子view水平方向拖拽的范围.注意:此返回不代表子view在父控件的宽高范围类拖拽
		// 代表的是子view拖拽放手后自动执行缓慢移动动画的范围,自动移动不会超出边界
		public int getViewHorizontalDragRange(View child) {
			return getMeasuredWidth() - redView.getMeasuredWidth();
		}

		/**
		 * left:为子view移动到的水平位置 left = view.getleft() + dx dx:水平方向移动了多少的距离
		 * 返回值:子view真实向要移动到的水平位置
		 */
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// System.out.println("移动到: " + left + "    移动了:" + dx);
			if (left < 0) {
				// 左边不超出边界
				left = 0;
			} else if (left > getMeasuredWidth() - redView.getMeasuredWidth()) {
				// 右边不超出边界
				left = getMeasuredWidth() - redView.getMeasuredWidth();
			}
			return left;
		}

		@Override
		// 子view垂直方向移动范围(执行缓慢动画的范围)
		public int getViewVerticalDragRange(View child) {
			return getMeasuredHeight() - redView.getMeasuredHeight();
		}

		/**
		 * top:为子view移动到的垂直位置 left = view.getTop() + dy dy:竖直方向移动了多少的距离
		 * 返回值:子view真实向要移动到的垂直位置
		 */
		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			if (top < 0) {
				top = 0;
			} else if (top > getMeasuredHeight() - redView.getMeasuredHeight()) {
				top = getMeasuredHeight() - redView.getMeasuredHeight();
			}
			return top;
		}

		@Override
		// 当子view的位置改变的时候执行,一般用来做其他子View的伴随移动,changedView:位置改变的子view
		// left:子view最新left边距位置,top:最新top边距位置,dx:本次x水平移动距离,dy:本次垂直移动距离
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if (changedView == blueView) {
				// 让红色view跟随移动
				redView.layout(redView.getLeft() + dx, redView.getTop() + dy,
						redView.getRight() + dx, redView.getBottom() + dy);
			} else if (changedView == redView) {
				// 让蓝色view跟随移动
				blueView.layout(blueView.getLeft() + dx,
						blueView.getTop() + dy, blueView.getRight() + dx,
						blueView.getBottom() + dy);
			}
			// 获取移动百分比,来控制移动过程中执行的动画
			float fraction = changedView.getLeft() * 1f
					/ (getMeasuredWidth() - changedView.getMeasuredWidth());
			executeAnim(fraction);
		}

		@Override
		// 当子view抬起手时执行此方法 releasedChild:当前抬起的子view, xvel:x轴的移动速度 ,yvel:y轴的移动速度
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// 执行缓慢动画使用scrollor,但viewDragHelper中封装了scroller,所以直接调用viewDragHelper中方法
			int centerLeft = getMeasuredWidth() / 2
					- redView.getMeasuredWidth() / 2;
			if (releasedChild.getLeft() < centerLeft) {
				// 自动滑动到左边
				viewDragHelper.smoothSlideViewTo(redView, 0, redView.getTop());
			} else {
				// 自动滑动到右边
				viewDragHelper.smoothSlideViewTo(redView, getMeasuredWidth()
						- redView.getMeasuredWidth(), redView.getTop());
			}

			// 刷新整个界面,所以参数要当前自定义控件,和invalidate()方法差不多
			ViewCompat.postInvalidateOnAnimation(DargLayout.this);

			super.onViewReleased(releasedChild, xvel, yvel);
		}

	};

	protected void executeAnim(float fraction) {
		// 执行缩放动画
		// ViewHelper.setScaleX(redView, 1+0.5f*fraction);
		// ViewHelper.setScaleY(redView, 1+0.5f*fraction);
		// 执行旋转动画
		// ViewHelper.setRotation(redView, 720*fraction);
		ViewHelper.setRotationX(redView, 720*fraction);
		//ViewHelper.setRotationY(redView, 720*fraction);
		ViewHelper.setRotationX(blueView, 720*fraction);
		//ViewHelper.setRotationY(blueView, 720*fraction);
		// 执行平移动画
		// ViewHelper.setTranslationX(redView, 100*fraction);
		//ViewHelper.setTranslationY(redView, 100 * fraction);
		// 执行透明动画
		//ViewHelper.setAlpha(redView, (float) (0.1 + 1 * fraction));

		// 执行颜色渐变动画
		//redView.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction,
				//Color.RED, Color.GREEN));
		setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction,
				Color.DKGRAY, Color.GREEN));
	};

	public void computeScroll() {
		if (viewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(DargLayout.this);
		}
	}

}
