package com.itheima.parallax;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ParallaxListView listView;
	private String[] indexArr = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };
	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listView = (ParallaxListView) findViewById(R.id.listView);
		//设置超过滚动的模式,默认有蓝色阴影,设置没有效果就不显示蓝色阴影
		listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		
		View header = View.inflate(this, R.layout.layout_header, null);
		imageView = (ImageView) header.findViewById(R.id.imageView);
		listView.addHeaderView(header);
		listView.setImageView(imageView);
		
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, indexArr));
	}
}
