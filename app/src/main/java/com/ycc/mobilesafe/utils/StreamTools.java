package com.ycc.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamTools {
	/**
	 * @param is 输入流
	 * @return String 返回的字符串
	 * @throws IOException 
	 */
	public static String readFromStream(InputStream is) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while((len = is.read(buffer))!=-1){
			baos.write(buffer, 0, len);
		}
		is.close();
		String result = baos.toString();
		baos.close();
		return result;
	}

	public static String readFromStreamReader(InputStream is) {
		InputStreamReader isr = null;
		StringBuffer sb = new StringBuffer();
		try {
			isr = new InputStreamReader(is,"GBK");
			int len = 0;
			char[] buf = new char[1024];

			while ((len = isr.read(buf)) != -1)
			{
				sb.append(new String(buf, 0, len));
			}
			is.close();
			isr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return String.valueOf(sb);
	}
}
