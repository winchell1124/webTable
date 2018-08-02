package cn.entityaug.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class test {

	public static void main(String[] args)throws IOException
	{
		String path1="DataSet\\Experiments\\Books_excel1";
		File file1=new File(path1);
		if(!file1.exists())
		{
			
				file1.mkdirs();
				System.out.println(1);
		
		}
//		File file=new File("DataSet\\Experiments\\Books_Excel");
//		BufferedReader br=new BufferedReader(new FileReader("DataSet\\Experiments\\Books_Score.txt"));
//		String s=null;
//		Map<String,Double> map=new HashMap<String,Double>();
//		while((s=br.readLine())!=null)
//		{
//			String[] arrs=s.split(" ");
//			map.put(arrs[0], Double.parseDouble(arrs[1]));
//		}
//		br.close();
//		String[] str=file.list();
//		for(int i=0;i<str.length;i++)
//		{
//			String s1="DataSet\\Experiments\\Books_Excel\\"+str[i];
//			
//			double d=map.get(s1);
//			if(d<0.9)
//			{
//				File file1=new File("DataSet\\Experiments\\Books_Excel\\"+str[i]);
//				file1.delete();
//			}
//		}
	}
}
