package com.hdyl.pushbox.paraser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.hdyl.pushbox.soko.LevelChooseItem;
import com.hdyl.pushbox.soko.LevelChooseItem.MyLevel;

public class XsbParser implements IParser {

	@Override
	public LevelChooseItem parse2Item(InputStream is) throws Exception {
		LevelChooseItem item = new LevelChooseItem();
		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line = null;
		boolean isFirstStart = false;
		StringBuilder sbBuilder = new StringBuilder();
		MyLevel myLevel = null;

		while ((line = in.readLine()) != null) {
			if (line.length() > 0) {
				if (isFirstStart == false) {
					if (line.startsWith(";")) {
						isFirstStart = true;
						item.description = sbBuilder.substring(0);
						sbBuilder.setLength(0);
						myLevel = new MyLevel();
						item.listLev.add(myLevel);
						myLevel.ID = line.substring(1);

					} else {
						sbBuilder.append(line);
					}
				} else {
					if (line.startsWith(";")) {
						myLevel = new MyLevel();
						item.listLev.add(myLevel);
						myLevel.ID = line.substring(1);
					} else {
						myLevel.levDatas.add(line);
					}
				}
			}
		}
		return item;
	}
	public final static  String SUPPORT_EXT="xsb";

}
