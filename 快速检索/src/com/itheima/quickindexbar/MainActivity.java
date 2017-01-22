package com.itheima.quickindexbar;

import java.util.ArrayList;
import java.util.Collections;

import com.itheima.quickindexbar.MyAdapter.ViewHolder;
import com.itheima.quickindexbar.QuickIndexBar.onTouchLetterListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private QuickIndexBar quickIndexBar;
	private ListView listView;
	private ArrayList<Friend> friends = new ArrayList<Friend>();
	private TextView centerWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		quickIndexBar = (QuickIndexBar) findViewById(R.id.quickIndexBar);
		listView = (ListView) findViewById(R.id.listView);
		centerWord = (TextView) findViewById(R.id.centerWord);

		// 1.初始化数据
		fillList();
		// 2.给数据排序
		Collections.sort(friends);
		// 3.设置数据适配器
		listView.setAdapter(new MyAdapter(this, friends));

		quickIndexBar.setOnTouchLetterListener(new onTouchLetterListener() {

			@Override
			public void onTouchLetter(String letter) {
				// 当点击到特定的字母,自动将此item跳转到顶部
				for (int i = 0; i < friends.size(); i++) {
					// 获取当前首字母
					String firstWord = friends.get(i).pinyin.charAt(0) + "";
					if (firstWord.equals(letter)) {
						listView.setSelection(i);
						break; // 只跳转到第一个就跳出循环,因为可能后面item的首字母的也相同,会一直跳转到最后一个
					}
				}

				// 显示点击的字母,再定义个textView居中展示
				showLetter(letter);

			}
		});

		// 设置动画隐藏控件
		ViewHelper.setScaleX(centerWord, 0);
		ViewHelper.setScaleY(centerWord, 0);

	}

	private Handler handler = new Handler();
	private boolean isDisplay = false;
	protected void showLetter(String letter) {
		if (!isDisplay) {
			isDisplay = true;
			// centerWord.setVisibility(View.VISIBLE);
			// 使用帧动画让控件展示出来	new OvershootInterpolator():弹性插补器
			ViewPropertyAnimator.animate(centerWord).scaleX(1f).scaleY(1f).rotation(720)
			.setInterpolator(new OvershootInterpolator()).setDuration(450).start();
			centerWord.setText(letter);
		}

		// 1.5秒后自动隐藏,当点击到下一个字母时又会走此方法,但以前的消息还生效,控件还在会自动隐藏,所以发消息前先移除以前的消息
		handler.removeCallbacksAndMessages(null);

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				//centerWord.setVisibility(View.INVISIBLE);
				// 设置动画隐藏控件
				ViewPropertyAnimator.animate(centerWord).scaleX(0).scaleY(0).setDuration(450).start();
			}
		}, 1500);
		isDisplay = false;
	}

	private void fillList() {
		// 虚拟数据
		friends.add(new Friend("李伟"));
		friends.add(new Friend("大张伟"));
		friends.add(new Friend("张三"));
		friends.add(new Friend("阿三"));
		friends.add(new Friend("阿四"));
		friends.add(new Friend("段誉"));
		friends.add(new Friend("段正淳"));
		friends.add(new Friend("张三丰"));
		friends.add(new Friend("陈坤"));
		friends.add(new Friend("林俊杰1"));
		friends.add(new Friend("陈坤2"));
		friends.add(new Friend("王二a"));
		friends.add(new Friend("林俊杰a"));
		friends.add(new Friend("张四"));
		friends.add(new Friend("林俊杰"));
		friends.add(new Friend("王二"));
		friends.add(new Friend("王二b"));
		friends.add(new Friend("赵四"));
		friends.add(new Friend("杨坤"));
		friends.add(new Friend("赵子龙"));
		friends.add(new Friend("杨坤1"));
		friends.add(new Friend("李伟1"));
		friends.add(new Friend("宋江"));
		friends.add(new Friend("宋江1"));
		friends.add(new Friend("李伟3"));
	}

}
