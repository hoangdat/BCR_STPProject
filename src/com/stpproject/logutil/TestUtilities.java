package com.stpproject.logutil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class TestUtilities {
	public static void writeLog(String fileName, String msg) {
		File fLog = new File(fileName);
		PrintWriter prtWriter = null;
		try {
			prtWriter = new PrintWriter(new FileOutputStream(fLog, true));
			prtWriter.append("\n" + msg);
			prtWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
