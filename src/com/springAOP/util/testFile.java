/*package com.springAOP.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class testFile {
	public static void main(String[] args) {
		List<String> txtLine = null;
		try {
			// 利用BufferedReader套在FileReader，处理流套在节点流上。
			
			BufferedReader 		br = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(fileAddress)), "UTF-8"));
		
			if (charset == "UTF-8") {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(fileAddress), "UTF-8"));
			}
			txtLine = new ArrayList<String>();
			// 判断读写的内容是否为空。
			String line = br.readLine();
			txtLine.add(line);
			System.out.println(line);
			while (line != null) {
				line = br.readLine();
				txtLine.add(line);
			}
			// 关闭这个“管道”。
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
*/