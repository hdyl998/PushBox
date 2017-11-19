package com.hdyl.pushbox.paraser;

import java.io.InputStream;
import java.util.List;

import com.hdyl.pushbox.soko.LevelChooseItem;
import com.hdyl.pushbox.soko.tool.FieldUtls;
import com.hdyl.pushbox.tools.Tools;

public class ParserManager implements IParser {

	IParser paraser;

	public ParserManager(String name) {

		name = name.toLowerCase();

		// List<Class<?>> list = new Scanner().scan();
		// for (Class<?> cls : list) {
		// Log.e("aa", cls.getName());
		// }

		// Log.e("aa", JSON.toJSONString(list));

		List<String> list = Tools.getClassName(ParserManager.class.getPackage().getName());

		String sName = "SUPPORT_EXT";
		for (String ss : list) {
			try {
				Class<?> class1 = Class.forName(ss);

				Object object = FieldUtls.getFieldValue(class1, sName);

				if (object != null) {
					String supportString = object.toString().toLowerCase();
					if (name.endsWith(supportString)) {
						paraser = (IParser) class1.newInstance();
						break;// 支持这种格式
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Log.e("aa", JSON.toJSONString(list));
		//
		// if (name.endsWith(".slc")) {
		// paraser = new SlcParser();
		// } else if (name.endsWith(".xsb")) {
		// paraser = new XsbParser();
		// } else if (name.endsWith(".lp0")) {
		// paraser = new LP0Parser();
		// } else if (name.endsWith(".dat")) {
		// paraser = new DatParser();
		// } else if (name.endsWith(".box")) {
		// paraser = new BoxParser();
		// } else if (name.endsWith(".sok")) {
		// paraser = new SokParser();
		// }
	}

	@Override
	public LevelChooseItem parse2Item(InputStream is) throws Exception {
		return paraser.parse2Item(is);
	}

}
