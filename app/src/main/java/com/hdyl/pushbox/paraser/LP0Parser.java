package com.hdyl.pushbox.paraser;

import java.io.InputStream;

import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hdyl.pushbox.base.Consts;
import com.hdyl.pushbox.base.MyApplication;
import com.hdyl.pushbox.soko.LevelChooseItem;
import com.hdyl.pushbox.soko.LevelChooseItem.MyLevel;
import com.hdyl.pushbox.tools.ToastUtils;

public class LP0Parser implements IParser, Consts {
	public class MyData {
		int endIndex = 0;
		String string;

		public MyData(String sss) {
			string = sss;
		}

		public String getString() {
			return string;
		}

		public String getNextString(String endString) {
			int i = string.indexOf(endString);
			if (i != -1) {
				endIndex = i;
				String currentString = string.substring(0, endIndex);// 前面这段
				string = string.substring(endIndex + endString.length());
				System.out.println(currentString);
				// printBytes(currentString);
				return currentString;
			}
			return null;
		}
	}

	@Override
	public LevelChooseItem parse2Item(InputStream input) throws Exception {

		int count = input.available();
		byte[] b = new byte[count];
		input.read(b);
		input.close();
		String sss1 = new String(b);
		MyData myData = new MyData(sss1);

		String _3 = new String(new byte[3]);
		String _7 = new String(new byte[7]);

		LevelChooseItem item = new LevelChooseItem();

		myData.getNextString(_7);// 0 Soko level pack
		myData.getNextString(_7);// 1 
		item.description = myData.getNextString(_3);// 2 MicGAMES 4 BRAINS
													// Kalandsweg 4
		String info = myData.getNextString(_3);// 3关卡信息，人的位置
		item.title = myData.getNextString(_3);// 4 title
		item.description += "\n" + myData.getNextString(_3);// 5 Comment: LOM
															// 12/10 8.5
		item.auther = myData.getNextString(_3);// 6作者

		item.description = clearByteIfSmill(item.description);
		item.auther = clearByteIfSmill(item.auther);
		item.title = clearByteIfSmill(item.title);

		byte byts[] = info.getBytes();

		int width = byts[0];// width (col)
		int height = byts[1];// height (row)
		int manCol = byts[2];// col
		int manRow = byts[3];// row

//		Log.e("aa", width + " " + height + " " + manCol + " " + manRow);

		byts = myData.getString().getBytes();

		MyLevel level = new MyLevel();

		level.ID = item.title;
		item.listLev.add(level);
		count = 0;
		byte byarr[] = new byte[width];
		for (byte by : byts) {
			byarr[count++] = by;
			if (count == width) {
				count = 0;
				level.levDatas.add(bytes2String(byarr));
			}
		}

		String temp = level.levDatas.get(manRow);

		char ch[] = temp.toCharArray();

		ch[manCol] = S_MAN ;

		String sssString = new String(ch);
		level.levDatas.set(manRow, sssString);
		Log.e("aa", JSON.toJSONString(item));
		return item;
	}

	//清掉里面比空格小的特殊字符，因为它们会给解析造成异常
	private String clearByteIfSmill(String sss) {
		byte by = 0;
		for (int i = 0; i < ' '; i++) {
			sss = sss.replace(new String(new byte[] { by }), " ");
			by++;
		}
		return sss;
	}

	private String bytes2String(byte[] byts) {
		StringBuilder buffer = new StringBuilder();
		for (byte b : byts) {
			switch (b) {
			case 0x0:
				buffer.append(S_EMPTY);
				break;
			case 0x1:
			case 0x2:
				buffer.append(S_WALL);
				break;
			case 0x11:
				buffer.append(S_POINT);
				break;
			case 0x12:
				buffer.append(S_BOX_IN);
				break;
			case 0x0c:
				buffer.append(S_BOX);
				break;
			}
		}
		// 00 -- floor ( )
		// 01 -- wall (#)
		// 02 -- wall (#) -- this is a different wall than 01, but it's still a
		// wall.
		// 11 -- goal (.)
		// 12 -- box on goal (*)
		// 0C -- box ($)

		return buffer.substring(0);
	}

	public final static  String SUPPORT_EXT="lp0";

	// private int getNextNumIndex(byte[] bytes, int start, int lianNum, int
	// varlue) {
	// for (int i = start; i < bytes.length && i + lianNum < bytes.length; i++)
	// {
	// boolean isLian = true;
	// for (int j = i; j < i + lianNum; j++) {
	// if (bytes[j] != varlue) {
	// isLian = false;
	// break;
	// }
	// }
	// if (isLian) {
	// return i+lianNum;
	// }
	// }
	// return -1;
	//
	// }

}
