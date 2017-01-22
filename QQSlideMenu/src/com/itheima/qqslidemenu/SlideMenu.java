package com.itheima.qqslidemenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.itheima.qqslidemenu.text.ColorUtil;
import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.view.ViewHelper;

public class SlideMenu extends FrameLayout {
	private ViewDragHelper viewDragHelper;
	private View menuView; // 菜单view
	private View mainView; // 主view
	private int width; // slideMenu的宽度
	private float dragRange; // mainView水平移动的最大范围
	private FloatEvaluator floatEvaluator; // 浮点计算器(根据百分比计算)
	private DragState currentState = DragState.close; // 记录当前的状态

	public SlideMenu(Context context) {
		super(context);
		init();
	}

	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		viewDragHelper = ViewDragHelper.create(this, callBack);
		floatEvaluator = new FloatEvaluator();
	}

	public DragState getCurrentState() {
		return currentState;
	}

	@Override
	// 此方法是在onMeasure方法执行后执行的,可用在这个方法中获取宽高
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = getMeasuredWidth();
		dragRange = width * 0.6f;

	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (getChildCount() != 2) {
			throw new IllegalArgumentException("SlideMenu only have 2 children");
		}
		menuView = getChildAt(0);
		mainView = getChildAt(1);

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return viewDragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		viewDragHelper.processTouchEvent(event);
		return true;
	}

	private ViewDragHelper.Callback callBack = new ViewDragHelper.Callback() {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child == menuView || child == mainView;
		}

		@Override
		public int getViewHorizontalDragRange(View child) {
			return (int) dragRange;
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (child == mainView) {
				if (left < 0)
					left = 0; // 限制左边界
				if (left > dragRange)
					left = (int) dragRange;// 限制右边界
			}
			// 注意:menuView不让其移动,这里不能返回menuView的left-dx,这样写虽然不移动,
			// 但是就不让让mainView做跟随移动了,因为下面方法的left值一直为0
			return left;
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if (changedView == menuView) {
				// 手动固定住menuView,不让其移动
				menuView.layout(0, 0, menuView.getMeasuredWidth(),
						menuView.getMeasuredHeight());
				int newLeft = mainView.getLeft() + dx;
				if (newLeft < 0)
					newLeft = 0;
				if (newLeft > dragRange)
					newLeft = (int) dragRange;

				mainView.layout(newLeft, mainView.getTop(),
						newLeft + mainView.getMeasuredWidth(),
						mainView.getBottom());
			}

			// 1.获取百分比,执行动画
			float fraction = mainView.getLeft() / dragRange;
			// 2.根据百分比执行动画
			executeAnim(fraction);
			// 3.把当前状态传递给外面
			if (fraction == 0 && currentState != DragState.close) {
				// 关闭的状态
				currentState = DragState.close;
				if (listener != null) {
					listener.onClose();
				}
			} else if (fraction > 0.999f && currentState != DragState.open) {
				// 打开的状态
				currentState = DragState.open;
				if (listener != null) {
					listener.onOpen();
				}
			}
			// 4.将百分比传递给外面
			if (listener != null) {
				listener.onDraging(fraction);
			}

		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			int centerPos = (int) (dragRange / 2);
			if (releasedChild.getLeft() <= centerPos) {
				// 自动向左移动,执行平滑动画
				close();
			} else if (releasedChild.getLeft() > centerPos) {
				// 自动向右移动,执行平滑动画
				open();
			}
			
			//根据滑动的速度来处理快速滑动时候打开或关闭menuView,xvel绝对值越大滑动的越快,向左滑动值为负
			if (xvel > 200 && currentState != DragState.open) {
				System.out.println("向右滑动了");
				//向右,打开
				open();
			}else if(xvel < -200 && currentState != DragState.close) {
				//向左,关闭
				System.out.println("向左滑动了");
				close();
			}
		}

	};

	/**
	 * 定义枚举来判断当前状态(打开关闭)
	 */
	public enum DragState {
		open, close;
	}

	public void computeScroll() {
		if (viewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
		}
	}

	protected void executeAnim(float fraction) {
		// 执行mainView的缩放动画
		ViewHelper.setScaleX(mainView,
				floatEvaluator.evaluate(fraction, 1f, 0.8f));
		ViewHelper.setScaleY(mainView,
				floatEvaluator.evaluate(fraction, 1f, 0.8f));
		// 执行menuView的平移动画
		ViewHelper.setTranslationX(menuView, floatEvaluator.evaluate(fraction,
				-menuView.getMeasuredWidth() / 2, 0));
		// 执行menuView缩放动画
		ViewHelper.setScaleX(menuView,
				floatEvaluator.evaluate(fraction, 0.6f, 1f));
		ViewHelper.setScaleY(menuView,
				floatEvaluator.evaluate(fraction, 0.6f, 1f));
		// 执行menuView透明动画
		ViewHelper.setAlpha(menuView,
				floatEvaluator.evaluate(fraction, 0.1f, 1f));
		// 执行slidengMenu的背景渐变(Mode:导这个包下android.graphics.PorterDuff.Mode
		// ,Mode.SRC_OVER:覆盖模式)
		getBackground().setColorFilter(
				(Integer) ColorUtil.evaluateColor(fraction, Color.BLACK,
						Color.TRANSPARENT), Mode.SRC_OVER);
	};

	private onDragStateChangedListener listener;

	public void setOnDargStateChangedListener(
			onDragStateChangedListener listener) {
		this.listener = listener;
	}

	/**
	 * 关闭menuView的方法
	 */
	public void close() {
		// 自动向左移动,执行平滑动画
		viewDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
		// 刷新整个界面
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
	}
	
	/**
	 * 打开menuView的方法
	 */
	public void open() {
		// 自动向右移动,执行平滑动画
		viewDragHelper.smoothSlideViewTo(mainView, (int) dragRange,mainView.getTop());
		// 刷新整个界面
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
	}

	// 设置接口回调,让外面状态的变化,来做相应的逻辑处理
	public interface onDragStateChangedListener {
		// 打开的回调
		void onOpen();

		// 关闭的回调
		void onClose();

		// 滑动百分比通知给外面
		void onDraging(float fraction);
	}
}
