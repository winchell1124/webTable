package cn.entityaug.txt;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WriteTxt {
	public static void main(String[] args)
	{
		List<String> l=new ArrayList<String>();
		l.add("luffy"+" "+0.2);
		l.add("leon"+" "+0.5);
		writeAllcontent("14.txt",l);
	}
	public static void writeOneContent(String fileName,String content){
		FileWriter writer = null;  
        try {     
            // ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�     
            writer = new FileWriter(fileName, true);     
            writer.write(content+"\r\n");       
        } catch (IOException e) {     
            e.printStackTrace();     
        } finally {     
            try {     
                if(writer != null){  
                    writer.close();     
                }  
            } catch (IOException e) {     
                e.printStackTrace();     
            }     
        }  
	}
	
	public static void writeAllcontent(String fileName,List<String> allcontent){
		for(String content:allcontent){
			writeOneContent(fileName,content);
		}
		writeOneContent(fileName,"\r\n");
	}

}
