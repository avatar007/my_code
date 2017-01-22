package com.itheima.slidedelete;

public class SwipeViewManager {
	private SwipeView currentSwipeView; // 记录当前打开的swipeView对象

	private SwipeViewManager() {
	}

	private static SwipeViewManager swipeViewManager = new SwipeViewManager();

	public static SwipeViewManager getInstance() {
		return swipeViewManager;
	}

	/**
	 * 获取当前条目对象
	 * 
	 * @param swipeView
	 */
	public void setSwipeView(SwipeView swipeView) {
		this.currentSwipeView = swipeView;
	}

	/**
	 * 关闭当前打开的状态
	 */
	public void closeCurrentSwipeView() {
		if (currentSwipeView != null) {
			currentSwipeView.close();
		}
	}

	// 清空已打开的记录
	public void clearCurrentSwipeView() {
		currentSwipeView = null;
	}

	/**
	 * 判断当前页面能否滑动
	 * 
	 * @param currentSwipeView
	 * @return
	 */
	public boolean isShouldSwipe(SwipeView swipeView) {
		if (currentSwipeView == null) { // 表示没有打开的
			return true;
		} else { // 表示有打开的,若打开的对象和按的对象是一个对象,就可以滑动,若不是一个对象就不能滑动
			return currentSwipeView == swipeView;
		}
	}

}
