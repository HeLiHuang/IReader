package com.harry.ireader;

import com.harry.db.DbManager;
import com.harry.file.FileUtil;
import com.harry.image.BitmapManager;
import com.harry.paint.IPaint;
import com.harry.service.PagingService;
import com.harry.style.ColorListActivity;
import com.harry.util.ActionUtil;
import com.harry.util.ColorUtil;
import com.harry.util.FontUtil;
import com.harry.util.ScreenUtil;
import com.harry.widget.TextWidget;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	private Context context;
	private TextWidget myWidget;
	private DbManager dbManager;
	private BitmapManager bmpManager;

	private BroadcastReceiver myBatteryInfoReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			final int level = intent.getIntExtra("level", 100);
			myWidget.setBatteryLevel(level);
		}
	};

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ActionUtil.ACTION_NAME_PAGING_PROGRESS)) {
				myWidget.setPageProgress(intent.getIntExtra("data", -1));
			} else if (action.equals(ActionUtil.ACTION_NAME_PAGING_SUCCESS)) {
				myWidget.setPageProgress(100);
				myWidget.setPageTotal(dbManager.getPageTotal());
			} else if (action.equals(ActionUtil.ACTION_NAME_PAGE_INIT)) {
				myWidget.setPageTotal(0);
				myWidget.setPageIndex(1);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);

		myWidget = (TextWidget) findViewById(R.id.main_view);

		context = this;
		dbManager = DbManager.getInstance(context);
		bmpManager = BitmapManager.getInstance(context);

		screenInit();
		paintInit();

		FileUtil.assetsCopyData(this);

		// 外部应用启动
		Intent intent = getIntent();
		final Uri data = intent.getData();
		// 外部应用调用时
		// 栈中不存在此Activity的实例时调用
		// 如果栈中存在此Activity的实例则调用OnNewIntent()
		if (Intent.ACTION_VIEW.equals(intent.getAction()) && data != null
				&& "file".equals(data.getScheme())) {
			fileFromIntent(intent);
		} else {
			fileFromIntent(intent);
		}

		bmpManager.start();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_bg_color: {
			final Intent intent = new Intent(this, ColorListActivity.class);
			startActivity(intent);
			break;
		}

		case R.id.action_open_file: {
			final Intent intent = new Intent(this, FileBrowerActivity.class);
			startActivity(intent);
			break;
		}
		}
		return true;
	}

	public void paintInit() {
		ScreenUtil.init(context);

		SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
		int colorId = preferences.getInt("colorInfo", 0);
		int fontId = preferences.getInt("fontInfo", 0);
		FileUtil.fileName = preferences.getString("fileName", null);
		ColorUtil.init(colorId);
		FontUtil.init(context, fontId);
		IPaint.init();
	}

	private void screenInit() {
		DisplayMetrics myMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(myMetrics);

		ScreenUtil.screenWidth = myMetrics.widthPixels;
		ScreenUtil.screenHeight = myMetrics.heightPixels;
	}

	private void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ActionUtil.ACTION_NAME_PAGING_PROGRESS);
		myIntentFilter.addAction(ActionUtil.ACTION_NAME_PAGING_SUCCESS);
		myIntentFilter.addAction(ActionUtil.ACTION_NAME_PAGE_INIT);
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	@Override
	protected void onStop() {
		unregisterReceiver(mBroadcastReceiver);
		try {
			unregisterReceiver(myBatteryInfoReceiver);
		} catch (IllegalArgumentException e) {
			// do nothing, this exception means myBatteryInfoReceiver was not
			// registered
		}
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerBoradcastReceiver();
		registerReceiver(myBatteryInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));

		final SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
		if (preferences.getBoolean("isFirst", true)) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					openOptionsMenu();
					preferences.edit().putBoolean("isFirst", false).commit();
				}
			}, 100);
		}
	}

	@Override
	protected void onDestroy() {
		bmpManager.destroy();
		SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
		preferences.edit().putInt("colorInfo", ColorUtil.colorInfo.getId())
				.commit();
		preferences.edit().putInt("fontInfo", FontUtil.fontInfo.getId())
				.commit();
		preferences.edit().putString("fileName", FileUtil.fileName).commit();

		myWidget.destroy();

		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {

		final Uri data = intent.getData();
		// 由截获外界发送的android.intent.action.VIEW 启动
		// 在AndroidManifest.xml定义android:launchMode="singleTask"
		// android:taskAffinity="com.harry.ireader.task"
		if (Intent.ACTION_VIEW.equals(intent.getAction()) && data != null
				&& "file".equals(data.getScheme())) {
			// Toast.makeText(this, "外部启动", Toast.LENGTH_SHORT).show();
			fileFromIntent(intent);
		}
		// 由本应用文件浏览打开文件启动
		else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// Toast.makeText(this, intent.getAction(),
			// Toast.LENGTH_SHORT).show();
			fileFromIntent(intent);
		}
		// 点应用启动，按下Home键应用在后台挂起，然后再点应用
		else {
			// Toast.makeText(this, intent.getAction(),
			// Toast.LENGTH_SHORT).show();
		}
		super.onNewIntent(intent);
	}

	protected void fileFromIntent(Intent intent) {
		String filePath = intent.getStringExtra(FileUtil.BOOK_PATH_KEY);
		if (filePath == null) {
			final Uri data = intent.getData();
			if (data != null) {
				filePath = data.getPath();
				FileUtil.fileName = filePath;
			}
		} else {
			FileUtil.fileName = filePath;
		}

		if (FileUtil.fileName != null) {
			startPaging();
		} else {
			FileUtil.fileName = FileUtil.helpFilePath;
			startPaging();
		}

	}

	/**
	 * 开始分页
	 */
	private void startPaging() {
		Intent intent = new Intent(this, PagingService.class);
		startService(intent);
	}
}