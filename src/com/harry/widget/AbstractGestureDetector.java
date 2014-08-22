package com.harry.widget;

import android.content.Context;
import android.view.MotionEvent;

public abstract class AbstractGestureDetector {
	OnGestureListener mListener;

	public static AbstractGestureDetector newInstance(Context context,
			OnGestureListener listener) {
		AbstractGestureDetector detector = null;

		detector = new EclairDetector();
		detector.mListener = listener;

		return detector;
	}

	public abstract boolean onTouchEvent(MotionEvent ev);

	public interface OnGestureListener {
		public void onDrag(int dx); // 拖拽

		public void onAutoMove(); // 自由移动

		public void setDragAndAutoMoveFlag(boolean isDrag, boolean isAutoMove);
	}

	private static class CupcakeDetector extends AbstractGestureDetector {
		int mLastTouchX;

		// 获得当前X坐标
		int getActiveX(MotionEvent ev) {
			return (int) ev.getX();
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN: { // 向下
				mLastTouchX = getActiveX(ev);
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				final int x = getActiveX(ev);
				// 处理拖拽移动
				mListener.onDrag(x - mLastTouchX);
				mLastTouchX = x;
				break;
			}
			}
			return true;
		}
	}

	private static class EclairDetector extends CupcakeDetector {
		private static final int INVALID_POINTER_ID = -1;
		private int mActivePointerId = INVALID_POINTER_ID;
		private int mActivePointerIndex = 0;

		@Override
		int getActiveX(MotionEvent ev) {
			return (int) ev.getX(mActivePointerIndex);
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			final int action = ev.getAction();
			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				mListener.setDragAndAutoMoveFlag(true, false);
				mActivePointerId = ev.getPointerId(0);
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				mActivePointerId = INVALID_POINTER_ID;
				mListener.setDragAndAutoMoveFlag(false, true);
				mListener.onAutoMove();
				break;
			case MotionEvent.ACTION_POINTER_UP:
				// 有个点松开
				final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				// 获取第几个点
				final int pointerId = ev.getPointerId(pointerIndex);
				if (pointerId == mActivePointerId) {
					final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
					mActivePointerId = ev.getPointerId(newPointerIndex);
					// 处理第newPointerIndex个点的x位置
					mLastTouchX = (int) ev.getX(newPointerIndex);

				}
				break;
			}
			mActivePointerIndex = ev.findPointerIndex(mActivePointerId);
			return super.onTouchEvent(ev);
		}
	}
}