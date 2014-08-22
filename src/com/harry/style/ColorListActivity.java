package com.harry.style;

import com.harry.image.BitmapManager;
import com.harry.ireader.R;
import com.harry.paint.IPaint;
import com.harry.util.ColorUtil;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import yuku.ambilwarna.widget.AmbilWarnaPrefWidgetView;

public class ColorListActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);

		ActionListAdapter adapter = new ActionListAdapter();
		setListAdapter(adapter);
		getListView().setOnItemClickListener(adapter);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	private class ActionListAdapter extends BaseAdapter implements
			AdapterView.OnItemClickListener {

		public final synchronized int getCount() {
			return ColorUtil.colorInfos.size();
		}

		public final synchronized ColorInfo getItem(int position) {
			return ColorUtil.colorInfos.get(position);
		}

		public final long getItemId(int position) {
			return position;
		}

		public final synchronized View getView(int position, View convertView,
				final ViewGroup parent) {
			final View view = convertView != null ? convertView
					: LayoutInflater.from(parent.getContext()).inflate(
							R.layout.color_item, parent, false);
			final ColorInfo style = getItem(position);

			final AmbilWarnaPrefWidgetView colorView = (AmbilWarnaPrefWidgetView) view
					.findViewById(R.id.style_item_color);
			final TextView titleView = (TextView) view
					.findViewById(R.id.style_item_title);

			if (style != null) {
				String name = style.getName();

				int color = style.getBackgroundColor();

				colorView.showCross(false);
				colorView.setBackgroundColor(color);
				// colorView.showCross(true);

				titleView.setText(name);
			}

			return view;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (ColorUtil.colorInfo.getId() != ColorUtil.colorInfos.get(
					position).getId()) {
				ColorUtil.set(ColorUtil.colorInfos.get(position).getId());
				// 重设颜色
				IPaint.resetColor();
				// 清除Bitmap缓存
				BitmapManager.getInstance(ColorListActivity.this).clear();
			}

			finish();

		}
	}
}
