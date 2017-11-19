package com.hdyl.pushbox;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class PullBookParser {

	public List<Book> parse(InputStream is) throws Exception {
		List<Book> books = null;
		Book book = null;

		// XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		// XmlPullParser parser = factory.newPullParser();

		XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
		parser.setInput(is, "UTF-8"); // 设置输入流 并指明编码方式

		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				books = new ArrayList<Book>();
				break;
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("level")) {
					book = new Book();
				} else if (parser.getName().equals("id")) {
					eventType = parser.next();
					book.id = (Integer.parseInt(parser.getText()));
				} else if (parser.getName().equals("status")) {
					eventType = parser.next();
					// book.setName(parser.getText());
				} else if (parser.getName().equals("actor")) {
					eventType = parser.next();
					book.actor = parser.getText();
				} else if (parser.getName().equals("wall")) {
					eventType = parser.next();
					book.wall = parser.getText();
				} else if (parser.getName().equals("box")) {
					eventType = parser.next();
					book.box = parser.getText();
				} else if (parser.getName().equals("target")) {
					eventType = parser.next();
					book.target = parser.getText();
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals("level")) {
					books.add(book);
					book = null;
				}
				break;
			}
			eventType = parser.next();
		}
		return books;
	}
	//
	// public String serialize(List<Book> books) throws Exception {
	// // XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	// // XmlSerializer serializer = factory.newSerializer();
	//
	// XmlSerializer serializer = Xml.newSerializer(); //
	// 由android.util.Xml创建一个XmlSerializer实例
	// StringWriter writer = new StringWriter();
	// serializer.setOutput(writer); // 设置输出方向为writer
	// serializer.startDocument("UTF-8", true);
	// serializer.startTag("", "books");
	// for (Book book : books) {
	// serializer.startTag("", "book");
	// serializer.attribute("", "id", book.getId() + "");
	//
	// serializer.startTag("", "name");
	// serializer.text(book.getName());
	// serializer.endTag("", "name");
	//
	// serializer.startTag("", "price");
	// serializer.text(book.getPrice() + "");
	// serializer.endTag("", "price");
	//
	// serializer.endTag("", "book");
	// }
	// serializer.endTag("", "books");
	// serializer.endDocument();
	//
	// return writer.toString();
	// }
}
