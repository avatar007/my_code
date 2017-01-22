package com.itheima.quickindexbar;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	private ArrayList<Friend> friends;
	private Context context;

	public MyAdapter(Context context, ArrayList<Friend> friends) {
		this.friends = friends;
		this.context = context;
	}

	@Override
	public int getCount() {
		return friends.size();
	}

	@Override
	public Friend getItem(int position) {
		return friends.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.adapter_item, null);
		}

		ViewHolder holder = ViewHolder.getHolder(convertView);
		Friend friend = friends.get(position);
		holder.tv_name.setText(friend.name);
		// 设置拼音的首字母显示控件上,注意:这里要加空串,否则会报错,charAt方法返回的是int类型的,系统会当成一个int资源,提示找不到
		String currentFirstWord = friend.pinyin.charAt(0) + "";
		// 根据情况隐藏首字母相同的条目的首字母控件
		if (position > 0) { // 不为第一个条目时才隐藏
			String lastFirstWord = friends.get(position - 1).pinyin.charAt(0)
					+ ""; // 前一个条目的首字母
			if (lastFirstWord.equals(currentFirstWord)) { // 当前item首字母和前一个首字母一致,影藏控件
				holder.tv_first_word.setVisibility(View.GONE);
			} else {
				//由于listView的复用机制,不一致的时候要显示控件
				holder.tv_first_word.setVisibility(View.VISIBLE); // 不一致,显示控件
				holder.tv_first_word.setText(currentFirstWord);
			}

		} else {	//为第一个条目
			//第一个条目也要设置显示,因为向上滑动时第一个条目也可能复用下面的条目
			holder.tv_first_word.setVisibility(View.VISIBLE); // 不一致,显示控件
			holder.tv_first_word.setText(currentFirstWord);
		}
		return convertView;
	}

	static class ViewHolder {
		TextView tv_first_word, tv_name;

		public ViewHolder(View convertView) {
			tv_first_word = (TextView) convertView
					.findViewById(R.id.tv_first_word);
			tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		}

		public static ViewHolder getHolder(View convertView) {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			if (viewHolder == null) {
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			}
			return viewHolder;
		}
	}
}
