package com.hdyl.pushbox.paraser;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.hdyl.pushbox.soko.LevelChooseItem;
import com.hdyl.pushbox.soko.LevelChooseItem.MyLevel;

public class SlcParser implements IParser {

	@Override
	public LevelChooseItem parse2Item(InputStream is) throws Exception {
		LevelChooseItem books = null;
		XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
		parser.setInput(is, "UTF-8"); // 设置输入流 并指明编码方式
		LevelChooseItem.MyLevel myLevel = null;
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				books = new LevelChooseItem();
				break;
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("Title")) {
					eventType = parser.next();
					books.title = parser.getText();
				} else if (parser.getName().equals("Description")) {
					eventType = parser.next();
					books.description = parser.getText();
				} else if (parser.getName().equals("Email")) {
					eventType = parser.next();
					books.email = parser.getText();
				} else if (parser.getName().equals("Url")) {
					eventType = parser.next();
					books.url = parser.getText();
				} else if (parser.getName().equals("LevelCollection")) {
					books.auther = parser.getAttributeValue(null, "Copyright");
				} else if (parser.getName().equals("Level")) {
					myLevel = new MyLevel();
					myLevel.ID = parser.getAttributeValue(null, "Id");
				} else if (parser.getName().equals("L")) {
					eventType = parser.next();
					myLevel.levDatas.add(parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals("Level")) {
					books.listLev.add(myLevel);
					myLevel = null;
				}
				break;
			}
			eventType = parser.next();
		}
		return books;
	}

	public final static  String SUPPORT_EXT="slc";

}
