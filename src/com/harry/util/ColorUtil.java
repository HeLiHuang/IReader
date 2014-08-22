package com.harry.util;

import java.util.ArrayList;

import com.harry.style.ColorInfo;

public class ColorUtil {
	public static ColorInfo colorInfo;
	public static ArrayList<ColorInfo> colorInfos = new ArrayList<ColorInfo>();

	public static void init(int id) {
		colorInfos.clear();

		ColorInfo info0 = new ColorInfo();
		info0.setId(0);
		info0.setBackgroundColor(0xffe7cba5);// rgb 231 203 165
		info0.setTextColor(0xff291c10); // rgb 41 28 16
		info0.setTitleColor(0xff837357); // 131 115 87
		info0.setName("默认");

		ColorInfo info1 = new ColorInfo();
		info1.setId(1);
		info1.setBackgroundColor(0xffefefe7);// rgb 239 239 231
		info1.setTextColor(0xff393c39); // rgb 57 60 57
		info1.setTitleColor(0xff919191); // 145 145 145
		info1.setName("浅灰");

		ColorInfo info2 = new ColorInfo();
		info2.setId(2);
		info2.setBackgroundColor(0xffffd7de);// rgb 255 215 222
		info2.setTextColor(0xff944542); // rgb 148 69 66
		info2.setTitleColor(0xffc58f93); // 197 143 147
		info2.setName("浅粉");

		ColorInfo info3 = new ColorInfo();
		info3.setId(3);
		info3.setBackgroundColor(0xffc6ebc6);// rgb 198 235 198
		info3.setTextColor(0xff293018); // rgb 41 48 24
		info3.setTitleColor(0xff778e72); // 119 142 114
		info3.setName("护眼");

		ColorInfo info4 = new ColorInfo();
		info4.setId(4);
		info4.setBackgroundColor(0xff101010);// rgb 16 16 16
		info4.setTextColor(0xff9c9a9c); // rgb 156 154 156
		info4.setTitleColor(0xff545454); // 84 84 84
		info4.setName("深灰");

		ColorInfo info5 = new ColorInfo();
		info5.setId(5);
		info5.setBackgroundColor(0xff212429);// rgb 33 36 41
		info5.setTextColor(0xff73829c); // rgb 115 130 156
		info5.setTitleColor(0xff4c5361); // 76 83 97
		info5.setName("冷灰");

		ColorInfo info6 = new ColorInfo();
		info6.setId(6);
		info6.setBackgroundColor(0xff292021);// rgb 41 32 33
		info6.setTextColor(0xff847173); // rgb 132 113 115
		info6.setTitleColor(0xff574a49); // 87 74 73
		info6.setName("暖灰");

		colorInfos.add(info0);
		colorInfos.add(info1);
		colorInfos.add(info2);
		colorInfos.add(info3);
		colorInfos.add(info4);
		colorInfos.add(info5);
		colorInfos.add(info6);
		
		set(id);
	}

	public static void set(int id) {
		for (int i = 0; i < colorInfos.size(); ++i) {
			if (id == colorInfos.get(i).getId()) {
				colorInfo = colorInfos.get(i);
				break;
			}
		}
	}
}
