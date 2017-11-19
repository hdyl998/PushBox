package com.hdyl.pushbox.paraser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.hdyl.pushbox.base.Consts;
import com.hdyl.pushbox.soko.LevelChooseItem;
import com.hdyl.pushbox.soko.LevelChooseItem.MyLevel;
import com.hdyl.pushbox.tools.LogUtils;

public class BoxParser implements IParser, Consts {

	@Override
	public LevelChooseItem parse2Item(InputStream is) throws Exception {
		LevelChooseItem item = new LevelChooseItem();
		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line = null;

		String flagName = "Name";
		String flagLevel = "Level";
		int width = 0;
		int height = 0;
		while ((line = in.readLine()) != null) {
			if (line.length() > 0) {
				if (line.startsWith("[" + flagLevel)) {
					MyLevel myLevel = new MyLevel();
					width = Integer.parseInt(getKeyValue(MapWidth, line = in.readLine()));
					height = Integer.parseInt(getKeyValue(MapHeight, line = in.readLine()));
					String mapDataString = getKeyValue(MapData, line = in.readLine());
					myLevel.ID = getKeyValue(Title, line = in.readLine());
					myLevel.author = getKeyValue(Author, line = in.readLine());
					string2LevelDatas(myLevel.levDatas, mapDataString, width, height);
					item.listLev.add(myLevel);
				} else if (line.startsWith("[" + flagName)) {
					line = in.readLine();
					if (line != null) {
						item.title = getKeyValue(GameName, line);
					}
				}
			}
		}
		return item;
	}

	final String GameName = "GameName";
	final String MapWidth = "MapWidth";
	final String MapHeight = "MapHeight";
	final String MapData = "MapData";
	final String Title = "Title";
	final String Author = "Author";

	public String getKeyValue(String key, String str) {
		if (str.startsWith(key + "=")) {
			return str.substring(key.length() + 1);
		}
		return null;
	}

	public void string2LevelDatas(List<String> lists, String str, int width, int height) {
		StringBuilder sbBuilder = new StringBuilder();
		for (int i = 0; i < height; i++) {
			sbBuilder.setLength(0);
			for (int j = 0; j < width; j++) {
				char ch = str.charAt(i * width + j);

				switch (ch) {
				case '0':
					ch = S_EMPTY;
					break;
				case '1':
					ch = S_WALL;
					break;
				case '2':
					ch = S_EMPTY;// empty
					break;
				case '3':
					ch = S_POINT;
					break;
				case '4':
					ch = S_BOX;
					break;
				case '5':
					ch = S_BOX_IN;
					break;
				case '6':
					ch = S_MAN;
					break;
				case '7':
					ch = S_MAN_IN;
					break;
				}

				sbBuilder.append(ch);
			}
			lists.add(sbBuilder.substring(0));
		}
	}

	// BOX meaning (XSB)
	// === =============
	// 0 = space
	// 1 = wall (#)
	// 2 = floor (space)
	// 3 = goal (.)
	// 4 = box ($)
	// 5 = box on goal (*)
	// 6 = man (@)
	// 7 = man on goal (+)

	public final static String SUPPORT_EXT = "box";

}
