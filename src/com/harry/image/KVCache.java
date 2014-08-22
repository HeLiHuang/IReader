package com.harry.image;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

// Cache保存一个强引用来限制内容数量，每当Item被访问的时候，此Item就会移动到队列的头部。
// 当cache已满的时候加入新的item时，在队列尾部的item会被回收。

abstract class KVCache<K, V> {
	/**
	 * 最大缓存数据 10MB
	 */
	private static final int CACHE_MAX_SIZE = 10 * 1024 * 1024;

	/**
	 * 内存缓存变量
	 */
	final LruCache<String, V> mMemCache;

	protected final Context mContext;

	public KVCache(Context context) {
		mMemCache = new LruCache<String, V>(CACHE_MAX_SIZE) {

			@Override
			protected int sizeOf(String key, V value) {
				if (value instanceof Bitmap) {
					/**
					 * 返回位图所占用的内存字节数
					 */
					return ((Bitmap) value).getRowBytes()
							* ((Bitmap) value).getHeight();
				}
				return 1;
			}

		};

		mContext = context.getApplicationContext();
	}

	/**
	 * 获取Key的哈希代码
	 */
	protected String getHash(K key) {
		return String.valueOf(key.toString().hashCode());
	}

	/**
	 * 从缓存系统获取一个对象
	 * 
	 * 首先我们在内存中查找缓存对象.如果找到就返回它.如果没有找到返回Null
	 */
	public V get(K key) throws IOException {
		if (key == null) {
			return null;
		}
		final String hashKey = getHash(key);
		return mMemCache.get(hashKey);
	}

	/**
	 * 设置一个对象到缓存中
	 * 
	 * 首先我们设置对象到内存缓存
	 */
	public void put(K key, V value) throws IOException {
		if (key == null || value == null) {
			return;
		}

		final String hashKey = getHash(key);
		mMemCache.put(hashKey, value);
	}
	

	/**
	 * 判断内存缓存中是否包含此对象
	 */
	public boolean containsInMem(K key) {
		return !(mMemCache.get(getHash(key)) == null);
	}

	/**
	 * 清除指定的内存缓存
	 */
	public boolean clearMemCache(K key) {
		return mMemCache.remove(getHash(key)) != null;
	}

	/**
	 * 清除所有的内存缓存
	 */
	public boolean clearAll() {
		mMemCache.evictAll();
		return mMemCache.size() == 0;
	}
}