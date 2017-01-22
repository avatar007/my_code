package com.itheima.qqslidemenu;

import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.qqslidemenu.SlideMenu.onDragStateChangedListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class MainActivity extends Activity {
	private ListView main_listview;
	private ListView menu_listview;
	private SlideMenu slideMenu;
	private ImageView iv_head;
	private MyLinearLayout my_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		initView();
		initData();
	}

	private void initData() {
		// 使用系统自带的简单的listVeiw的item布局,并重写getView方法,获取到item布局中的textView并修改字体颜色
		// 系统自带的item布局不能修改
		menu_listview.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// 父类返回的就是一个textView
				TextView tv = (TextView) (convertView == null ? super.getView(
						position, convertView, parent) : convertView);
				;
				tv.setTextColor(Color.WHITE);
				// 先执行tv的缩小
				ViewHelper.setScaleX(tv, 0.5f);
				ViewHelper.setScaleY(tv, 0.5f);
				// 在使用属性动画放大
				ViewPropertyAnimator.animate(tv).scaleX(1f).scaleY(1f)
						.setDuration(350).start();
				return tv;
			}
		});

		main_listview.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Constant.NAMES) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// 复用convertView
				TextView tv = (TextView) (convertView == null ? super.getView(
						position, convertView, parent) : convertView);
				// 先执行tv的缩小
				ViewHelper.setScaleX(tv, 0.5f);
				ViewHelper.setScaleY(tv, 0.5f);
				// 在使用属性动画放大
				ViewPropertyAnimator.animate(tv).scaleX(1f).scaleY(1f)
						.setDuration(350).start();

				return tv;
			}
		});

		// 调用slideMenu的接口获取状态处理相应的逻辑
		slideMenu
				.setOnDargStateChangedListener(new onDragStateChangedListener() {
					@Override
					public void onOpen() {
						// 打开,menu_listview执行平滑动画,滑动到随机的条目
						menu_listview.smoothScrollToPosition(new Random()
								.nextInt(menu_listview.getCount()));
					}

					@Override
					public void onClose() {
						// 关闭, 小人执行平滑动画
						ViewPropertyAnimator.animate(iv_head).translationX(20)
								.setInterpolator(new CycleInterpolator(8))
								.setDuration(500).start();
					}

					@Override
					public void onDraging(float fraction) {
						// 获取百分比,来执行渐变的透明动画
						ViewHelper.setAlpha(iv_head, 1 - fraction);
					}
				});
	}

	private void initView() {
		main_listview = (ListView) findViewById(R.id.main_listview);
		menu_listview = (ListView) findViewById(R.id.menu_listview);
		slideMenu = (SlideMenu) findViewById(R.id.slideMenu);
		iv_head = (ImageView) findViewById(R.id.iv_head);
		my_layout = (MyLinearLayout) findViewById(R.id.my_layout);
		my_layout.setSlideMenu(slideMenu);
	}
}
