package cn.entityaug.txt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReadTxt {

	public static List<String> readTxt(String path)throws IOException{
		List<String> source = new ArrayList<String>();
	
			String encoding = "GBK";
			File file = new File(path);
			InputStreamReader read=null;
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				int count = 0;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					source.add(lineTxt);
					
					}
				}
				read.close();
		
		
		return source;
	}

}
