package com.harry.ireader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

public class TextFilenameFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String filename) {
		if (filename.toLowerCase(Locale.getDefault()).endsWith(".txt")) {
			return true;
		}
		return false;
	}

}
