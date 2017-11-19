package com.hdyl.pushbox.paraser;

import java.io.InputStream;

import com.hdyl.pushbox.soko.LevelChooseItem;

public interface IParser {
	LevelChooseItem parse2Item(InputStream is) throws Exception;
}
