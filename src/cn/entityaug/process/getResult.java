package cn.entityaug.process;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.entityaug.txt.ReadTxt;

public class getResult {
	public static void main(String[] args)throws IOException
	{
		File file=new File("DataSet\\Experiments\\0.7\\results");
		String[] list=file.list();
		for(String path:list)
		{
			String s="DataSet\\Experiments\\0.7\\results\\"+path;
			List<String> content=ReadTxt.readTxt(s);
			for(int i=0;i<content.size();)
			{}
		}
	}

}
