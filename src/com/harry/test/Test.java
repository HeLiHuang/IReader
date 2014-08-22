package com.harry.test;

import org.vimgadgets.linebreak.LineBreaker;

import android.test.AndroidTestCase;

public class Test extends AndroidTestCase {
	
	public static void test(){
		String line = "你是我的玫book瑰你是我的花，我是谁哈方。";
		LineBreaker lineBreaker = new LineBreaker("zh");
		int length = line.length();
		byte[] breaks = new byte[length];
		
		lineBreaker.setLineBreaks(line, breaks);
		
		int index = 0;
		for(int n = 0; n < length; ++n){
			if(breaks[n] == LineBreaker.MUSTBREAK){
				String word = line.substring(index, n + 1);
				System.out.println("======================" + word);
				break;
			}
			else if(breaks[n] == LineBreaker.ALLOWBREAK){
				String word = line.substring(index, n + 1);
				System.out.println("======================" + word);
				index = n + 1;
			}
			else if(breaks[n] == LineBreaker.NOBREAK){
				
			}
		}
	}

}
