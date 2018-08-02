package cn.entityaug.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class smallerJson {
	public static void main(String[] args)throws IOException
	{
		File file=new File("E:\\学习\\Web Tables相关文献\\01\\01\\1");
		String[] jsonPath=file.list();
		int count=0;
		for(int i=0;i<jsonPath.length;i++)
		{
			if(i%10000==0)
			{
				new File("json"+i).mkdir();
			}
			if(count<=10000)
			{
				FileReader fr=new FileReader("E:\\学习\\Web Tables相关文献\\01\\01\\1\\"+jsonPath[i]);
				FileWriter fw=new FileWriter("E:\\学习\\Entity Augmentation\\"+"json"+i+"\\"+jsonPath[i]);
				BufferedReader br=new BufferedReader(fr);
				BufferedWriter bw=new BufferedWriter(fw);
				char[] buff=new char[1024];
				int num=0;
				while((num=br.read(buff))!=-1)
				{
					bw.write(buff, 0, num);
				}
				br.close();
				bw.close();
				count++;
			}
			if(count>10000)
				count=0;
		}
	}

}
