package com.itheima.slidedelete;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ListView listView;
	private ArrayList<String> list = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		listView = (ListView) findViewById(R.id.listView);

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					SwipeViewManager.getInstance().closeCurrentSwipeView();
					SwipeViewManager.getInstance().clearCurrentSwipeView();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		for (int i = 0; i < 30; i++) {
			list.add("内容-" + i);
		}

		listView.setAdapter(new MyAdapter());
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(MainActivity.this,
						R.layout.layout_swipeview, null);
			}

			ViewHolder holder = ViewHolder.getHolder(convertView);
			holder.tv_name.setText(list.get(position));

			holder.tv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					list.remove(position);
					SwipeViewManager.getInstance().closeCurrentSwipeView();
					notifyDataSetChanged();
					Toast.makeText(getApplicationContext(), "删除了第" + position + "行信息", 0).show();
				}
			});
			return convertView;
		}

	}

	static class ViewHolder {
		public TextView tv_name, tv_delete;
		public SwipeView swipeView;

		public ViewHolder(View convertView) {
			tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
			swipeView = (SwipeView) convertView.findViewById(R.id.swipeView);
		}

		public static ViewHolder getHolder(View convertView) {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			if (holder == null) {
				holder = new ViewHolder(convertView);
			}
			return holder;
		}
	}
}
