package com.hdyl.pushbox.paraser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.hdyl.pushbox.soko.LevelChooseItem;
import com.hdyl.pushbox.soko.LevelChooseItem.LevelInfo;
import com.hdyl.pushbox.soko.LevelChooseItem.MyLevel;
import com.hdyl.pushbox.soko.tool.SaveDataUtls;
import com.hdyl.pushbox.tools.LogUtils;

public class SokParser implements IParser {

	@Override
	public LevelChooseItem parse2Item(InputStream is) throws Exception {
		LevelChooseItem item = new LevelChooseItem();
		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line = null;
		StringBuilder sbBuilder = new StringBuilder();
		MyLevel myLevel = null;

		boolean isReadDescription = true;

		int countNewLine = 0;
		int status = 0;

		while ((line = in.readLine()) != null) {
			if (line.length() > 0) {
				if (!line.startsWith("::")) {
					System.out.println(line);
					// 正在读描述
					if (isReadDescription) {
						sbBuilder.append(line);
						if (countNewLine == 2) {// 两行空格
							if (line.startsWith("::")) {
								continue;
							} else {// 结束了
								isReadDescription = false;// 也就是描述读完了
								item.description = sbBuilder.substring(0);

								item.description = clearByteIfSmill(item.description);
								sbBuilder.setLength(0);

								// 开始读关卡
								myLevel = new MyLevel();
								item.listLev.add(myLevel);
								myLevel.ID = line;// 标题
								status = READ_LEVEL_FIRST_LINE;
							}
						}

					} else {// 描述读完了
						// 有两行
						if (countNewLine == 2) {// 表示新关开始
							// 开始读关卡
							myLevel = new MyLevel();
							item.listLev.add(myLevel);
							myLevel.ID = line;// 标题
							status = READ_LEVEL_FIRST_LINE;

							// if (maxCont == 2)
							// break;
							// maxCont++;

						} else {
							boolean haveSpace = countNewLine == 1;
							if (status == READ_LEVEL_FIRST_LINE) {
								status = READ_LEVEL;
								myLevel.levDatas.add(line);
							} else if (status == READ_LEVEL) {// 正在读取关卡数据
								if (haveSpace) {
									status = READ_LEVEL_COMMENT;// 正在读注解
									myLevel.author = line;// 读作者
									myLevel.author = clearByteIfSmill(myLevel.author);

								} else {
									myLevel.levDatas.add(line);
								}
							} else if (status == READ_LEVEL_COMMENT) {
								if (line.startsWith("Sol")) {// 注解读完了
									myLevel.comment = sbBuilder.substring(0);

									myLevel.comment = clearByteIfSmill(myLevel.comment);

									sbBuilder.setLength(0);
									status = READ_LEVEL_SOLUTION;// 读下一个解决方案
								} else {
									sbBuilder.append(line);
								}
							} else if (status == READ_LEVEL_SOLUTION) {
								sbBuilder.append(line);

							}
						}
					}
				}
				countNewLine = 0;
			} else {
				countNewLine++;
				if (countNewLine == 1) {
					if (status == READ_LEVEL_SOLUTION) {

						saveSolution(myLevel, sbBuilder.substring(0));
						sbBuilder.setLength(0);

					}
				}

			}
		}
		if (status == READ_LEVEL_SOLUTION) {

			saveSolution(myLevel, sbBuilder.substring(0));
			sbBuilder.setLength(0);

		}

		// Log.e("aa", JSON.toJSONString(item));

		return item;
	}

	// 存储MD5
	private void saveSolution(MyLevel level, String solution) {
		String idMD5 = LevelChooseItem.getLevelMD5(level);
		// 从原库查看有没有
		LevelInfo levelInfo = SaveDataUtls.getLevelInfoData(idMD5);
		if (levelInfo == null) {
			levelInfo = new LevelInfo();
			levelInfo.solution = solution;
			levelInfo.idMD5 = idMD5;
			SaveDataUtls.saveLevelInfoData(levelInfo);
		}
		level.isSolution = true;
		LogUtils.Print(levelInfo);
	}

	private String clearByteIfSmill(String sss) {
		byte by = 0;
		for (int i = 0; i < ' '; i++) {
			sss = sss.replace(new String(new byte[] { by }), " ");
			by++;
		}
		return sss;
	}

	public static int READ_NOTE = 1;
	public static int READ_LEVEL_TITLE = 2;
	public static int READ_LEVEL_FIRST_LINE = 3;
	public static int READ_LEVEL = 4;
	public static int READ_LEVEL_END = 5;
	public static int READ_LEVEL_AUTHOR = 6;
	public static int READ_LEVEL_COMMENT = 7;
	public static int READ_LEVEL_SOLUTION = 8;

	public final static String SUPPORT_EXT = "sok";

}
