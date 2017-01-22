package com.itheima.gooView;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

public class GooView extends View {

	private Paint paint;
	private PointF dragCenter, stickyCenter;
	private float dragRadius;
	private float stickyRadius;
	private PointF stickyPoints[];
	private PointF dargPoints[];
	private PointF controlPoint;
	private double lineK; // 斜率
	private float maxDistance = 300; // 设置两圆圆心距离的最大值
	private boolean isOutOfRange = false;

	public GooView(Context context) {
		super(context);
		init();
	}

	public GooView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GooView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.RED);
		dragCenter = new PointF(350f, 650f); // 拖拽圆的圆心
		stickyCenter = new PointF(400f, 650f); // 固定圆的圆心
		dragRadius = 30f; // 拖拽圆半径
		stickyRadius = 30f; // 固定圆半径

		// 固定圆贝塞尔曲线的两个起点
		stickyPoints = new PointF[] {
				new PointF(stickyCenter.x, stickyCenter.y - stickyRadius),
				new PointF(stickyCenter.x, stickyCenter.y + stickyRadius) };
		// 拖拽圆贝塞尔曲线的两个终点
		dargPoints = new PointF[] {
				new PointF(dragCenter.x, dragCenter.y - dragRadius),
				new PointF(dragCenter.x, dragCenter.y + dragRadius) };
		// 控制点的坐标(起始设定为两圆心Y的一半,Y为圆心高度)
		controlPoint = new PointF((dragCenter.x + stickyCenter.x) / 2,
				dragCenter.y);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 我们使用的是getRawX(),getRawY(),所以这里要去除掉状态栏的高度(画布上一个状态栏高度)
		canvas.translate(0, -Utils.getStatusBarHeight(getResources()));

		// 根据两个圆的圆心距离要动态的改变固定圆的半径
		stickyRadius = getStickRadius();

		// 根据dragCenter来求出动态的dargPoints和stickyPoints的值
		// dargPoints的值:两圆圆心连线的垂线与darg圆的两个交点
		// stickyPoints的值:两圆圆心连线的垂线与sticky圆的两个交点
		// 1.获取x轴的偏差(斜角的对边)
		float xOffset = stickyCenter.x - dragCenter.x;
		// 2.获取y轴的偏差(斜角的邻边)
		float yOffset = stickyCenter.y - dragCenter.y;
		// 3.获取斜率(对边除以邻边)
		if (xOffset != 0) { // 考虑两圆重合时候xOffset的值为0
			lineK = yOffset / xOffset;
		}
		// 4.根据斜率计算出拖拽圆的dargPoints和固定圆的stickyPoints(使用工具类)
		dargPoints = GeometryUtil.getIntersectionPoints(dragCenter, dragRadius,
				lineK);
		stickyPoints = GeometryUtil.getIntersectionPoints(stickyCenter,
				stickyRadius, lineK);
		// 5.动态修改控制点(根据0.618的比例去更改控制点的,黄金分割线)
		controlPoint = GeometryUtil.getPointByPercent(dragCenter, stickyCenter,
				0.618f);

		canvas.drawCircle(dragCenter.x, dragCenter.y, dragRadius, paint); // 绘制拖拽圆
		
		//超出范围后就不绘制贝塞尔曲线
		if (!isOutOfRange) {	//超出固定圆也不绘制
			canvas.drawCircle(stickyCenter.x, stickyCenter.y, stickyRadius, paint); // 绘制固定圆
			// 绘制贝塞尔曲线(起点,控制点,终点 三个点控制)
			Path path = new Path();
			path.moveTo(stickyPoints[0].x, stickyPoints[0].y); // 设置贝塞尔一曲线的起点
			path.quadTo(controlPoint.x, controlPoint.y, dargPoints[0].x,
					dargPoints[0].y); // 使用贝塞尔曲线链接(x1,x2:控制点,x2,y2:曲线终点)
			path.lineTo(dargPoints[1].x, dargPoints[1].y); // 连接线:曲线一的终点连接到曲线二的终点
			path.quadTo(controlPoint.x, controlPoint.y, stickyPoints[1].x,
					stickyPoints[1].y); // 绘制贝塞尔曲线二
			// path.close(); //闭合曲线,自动闭合的,所以不用调
			canvas.drawPath(path, paint); // 绘制贝塞尔曲线
		}

		// 绘制一个拖拽圆的范围的大的空心圆(半径为最大值,圆心为固定圆圆心)
		paint.setStyle(Style.STROKE); // 只设置圆的边线
		canvas.drawCircle(stickyCenter.x, stickyCenter.y, maxDistance, paint);
		paint.setStyle(Style.FILL); // 在设置回来(实心圆)
	}

	/**
	 * 动态获取固定圆的半径
	 * 
	 * @return
	 */
	private float getStickRadius() {
		// 1.获取两个圆的圆心距离
		float distance = GeometryUtil.getDistanceBetween2Points(dragCenter,
				stickyCenter);
		// 2.根据最大值获取圆心距离的移动的百分比
		float fraction = distance / maxDistance;
		// 3.根据百分比动态获取固定圆的半径(半径从最大的12,变化到4,不能让其消失)
		float radius = GeometryUtil.evaluateValue(fraction, dragRadius, 4);
		return radius;
	}

	private float x;
	private float y;

	@SuppressLint("NewApi")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 根据手指一动拖拽圆改变贝塞尔曲线
		x = event.getRawX(); // 根据屏幕的左上角为圆点(标题栏的左上角)获取的X值
		y = event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			dragCenter.set(x, y);
			// 拖拽时超出最大距离
			if (GeometryUtil.getDistanceBetween2Points(dragCenter, stickyCenter) > maxDistance) {
				// 超出边界,记录当前状态(具体逻辑在抬起时处理)
				isOutOfRange = true;
			}

			break;
		case MotionEvent.ACTION_UP:
			// 抬手时候判断拖拽圆的位置来做对应的逻辑处理
			if (GeometryUtil.getDistanceBetween2Points(dragCenter, stickyCenter) > maxDistance) {
				// 超出边界,应该断开(不在绘制贝塞尔曲线)
				dragCenter.set(stickyCenter.x, stickyCenter.y);
			} else {
				// 未超出边界:2种情况
				if (isOutOfRange) { // 1:超出范围又拖拽回来
					// 直接跳转回去
					dragCenter.set(stickyCenter.x, stickyCenter.y);
				} else { // 2:没拖拽出范围
					// 执行回去的动画(此时的动画是传递一个点的值不能写具体值,随便填写一个数字)
					ValueAnimator animator = ValueAnimator.ofFloat(1);
					animator.addUpdateListener(new AnimatorUpdateListener() {
						
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							//设置动画的开始点坐标
							PointF startPoint = new PointF(dragCenter.x, dragCenter.y);
							//1.获取动画移动移动的百分比
							float animatedFraction = animation.getAnimatedFraction();
							//2.根据2个点获取百分比移动中的点(开始点(拖拽圆移动中的圆心),结束点(固定圆心),百分比)
							PointF movePoint = GeometryUtil.getPointByPercent(startPoint, stickyCenter, animatedFraction);
							//3.设置拖拽圆的位置点
							dragCenter.set(movePoint);
							invalidate();
						}
					});
					
					animator.setDuration(200);
					animator.setInterpolator(new OvershootInterpolator(8));
					animator.start();
				}
			}
			isOutOfRange = false;
			break;

		}
		invalidate();
		return true;
	}

}
