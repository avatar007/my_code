package com.itheima.slidedelete;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SwipeView extends FrameLayout {

	private View contentView; // 内容的view
	private View deleteView; // 删除的view
	private int contentHeight;
	private int contentWidth;
	private int deleteHeight;
	private int deleteWidth;
	private ViewDragHelper viewDragHelper;

	public SwipeView(Context context) {
		super(context);
		init();
	}

	public SwipeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SwipeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		viewDragHelper = ViewDragHelper.create(this, callback);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
		if (!SwipeViewManager.getInstance().isShouldSwipe(this)) {
			// 当前状态为打开时候,不能触摸,只有关闭后才能触摸
			SwipeViewManager.getInstance().closeCurrentSwipeView();
			SwipeViewManager.getInstance().clearCurrentSwipeView();
			result = true;
		}
		return result;
	}

	private float downX;
	private float downY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!SwipeViewManager.getInstance().isShouldSwipe(this)) {
			requestDisallowInterceptTouchEvent(true);
			return true;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float moveX = event.getX();
			float moveY = event.getY();

			float offsetX = moveX - downX;
			float offsetY = moveY - downY;

			if (Math.abs(offsetX) > Math.abs(offsetY)) {
				// 偏向于水平滑动
				requestDisallowInterceptTouchEvent(true);
			}
			downX = moveX;
			downY = moveY;

			break;
		case MotionEvent.ACTION_UP:

			break;
		}
		viewDragHelper.processTouchEvent(event);
		return true;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		contentHeight = contentView.getMeasuredHeight();
		contentWidth = contentView.getMeasuredWidth();
		deleteHeight = deleteView.getMeasuredHeight();
		deleteWidth = deleteView.getMeasuredWidth();
	}

	@Override
	protected void onFinishInflate() {
		contentView = getChildAt(0);
		deleteView = getChildAt(1);
		super.onFinishInflate();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		contentView.layout(0, 0, contentWidth, contentHeight);
		deleteView.layout(contentView.getRight(), 0, contentView.getRight()
				+ deleteWidth, deleteHeight);

	}

	private SwipeState currentState = SwipeState.close;

	enum SwipeState {
		open, close;
	}

	private ViewDragHelper.Callback callback = new Callback() {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child == contentView || child == deleteView;
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (child == contentView) {
				if (left > 0)
					left = 0;
				if (left < -deleteWidth)
					left = -deleteWidth;
			} else if (child == deleteView) {
				if (left < contentWidth - deleteWidth)
					left = contentWidth - deleteWidth;
				if (left > contentWidth)
					left = contentWidth;
			}
			return left;
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if (changedView == contentView) {
				deleteView.layout(deleteView.getLeft() + dx,
						deleteView.getTop() + dy, deleteView.getRight() + dx,
						deleteView.getBottom() + dy);
			} else if (changedView == deleteView) {
				contentView.layout(contentView.getLeft() + dx,
						contentView.getTop() + dy, contentView.getRight() + dx,
						contentView.getBottom() + dy);
			}

			// 获取移动的百分比
			float fraction = (contentWidth - deleteView.getLeft()) * 1f
					/ deleteWidth;
			// System.out.println("fraction = " + fraction);

			// 根据contentView的left值判断当前状态是打开还是关闭
			if (fraction == 0 && currentState != SwipeState.close) {
				// 当前状态为关闭状态
				currentState = SwipeState.close;
				if (listener != null) {
					listener.onClose();
				}
				// 关闭掉一个状态就清空记录的内容
				SwipeViewManager.getInstance().clearCurrentSwipeView();
			} else if (fraction == 1 && currentState != SwipeState.open) {
				// 当前状态为打开状态
				currentState = SwipeState.open;
				//状态传递给外界
				if (listener != null) {
					listener.onOpen();
				}
				// 打开一个对象就记录下
				SwipeViewManager.getInstance().setSwipeView(SwipeView.this);
			}
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			// 缓慢滑动
			float center = deleteWidth * 1f / 4;
			if (contentView.getLeft() <= -center) {
				// 打开
				open();
			} else if (contentView.getLeft() > -center) {
				// 关闭
				close();
			}

		}

	};

	public void computeScroll() {
		if (viewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(SwipeView.this);
		}
	}

	public void open() {
		// 打开
		viewDragHelper.smoothSlideViewTo(contentView, -deleteWidth,
				contentView.getTop());
		ViewCompat.postInvalidateOnAnimation(SwipeView.this);
	}

	public void close() {
		// 关闭
		viewDragHelper.smoothSlideViewTo(contentView, 0, contentView.getTop());
		ViewCompat.postInvalidateOnAnimation(SwipeView.this);
	};

	private onSwipeStateChangedListener listener;

	public void setOnSwipeStateChangedListener(
			onSwipeStateChangedListener listener) {
		this.listener = listener;
	}

	public interface onSwipeStateChangedListener {
		void onOpen();

		void onClose();
	}

}
