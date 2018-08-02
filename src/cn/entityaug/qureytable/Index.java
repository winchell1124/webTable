package cn.entityaug.qureytable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import cn.entityaug.data.StringTransform;

public class Index {
	public static void main(String[] args)throws IOException
	{
		secondLevel_index();
		
	}
	public static void secondLevel_index()
	{
		for(char ch='c';ch<='z';ch++)
		{
			
			String path="E:\\workspace\\fileread\\Probase_index\\"+ch+".txt";
			FileReader fr=null;
			FileWriter fw=null;
			int count=0;
			int count1=0;
			try
			{
				fr=new FileReader(path);
				BufferedReader br=new BufferedReader(fr);
				String str=null;
				String[] arrs=null; 
				while((str=br.readLine())!=null)
				{
					count++;
					Probase pro=new Probase();
					str=str.trim();
					arrs=str.split("\t");
					pro.setConcept(arrs[0]);
					pro.setEntity(arrs[1]);
					pro.setFrequency(Integer.parseInt(arrs[2]));
						if(pro.getEntity().length()==1)
						{
							count1++;
							fw=new FileWriter("E:\\workspace\\fileread\\Probase_index2\\"+pro.getEntity()+".txt",true);
							BufferedWriter bw=new BufferedWriter(fw);
							bw.write(pro.getConcept()+"\t"+pro.getEntity()+"\t"+pro.getFrequency());
							bw.newLine();
							bw.close();
						}
						else if(pro.getEntity().length()==2)
						{
							count1++;
							fw=new FileWriter("E:\\workspace\\fileread\\Probase_index2\\"+pro.getEntity()+".txt",true);
							BufferedWriter bw=new BufferedWriter(fw);
							bw.write(pro.getConcept()+"\t"+pro.getEntity()+"\t"+pro.getFrequency());
							bw.newLine();
							bw.close();
						}
						else
						{
							count1++;
							String c=pro.getEntity().substring(0, 3);
							c=c.replaceAll("[\\\\/:*?<>¡ú\"]", " ");
							fw=new FileWriter("E:\\workspace\\fileread\\Probase_index2\\"+c+".txt",true);
							BufferedWriter bw=new BufferedWriter(fw);
							bw.write(pro.getConcept()+"\t"+pro.getEntity()+"\t"+pro.getFrequency());
							bw.newLine();
							bw.close();
						}
						
					
				}
				br.close();	
			}
			catch(IOException ie)
			{}
			System.out.println(count+"   "+count1);
		}
		
	}
	public static void firstLevel_index()
	{
		String path="E:\\workspace\\fileread\\probase1";
		File file=new File(path);
		String[] source=file.list();
		for(int i=0;i<source.length;i++)
		{
			FileReader fr=null;
			FileWriter fw=null;
			try
			{
				fr=new FileReader(path+"\\"+source[i]);
				BufferedReader br=new BufferedReader(fr);
				String str=null;
				while((str=br.readLine())!=null)
				{
					Probase pro=new Probase();
					int j=0,len=0;
					str=str.trim();
					for(int start=0;start<=str.length()-1;start++)
					{
						 
						 if(str.charAt(start)=='\t')
						 {
							 switch(j)
							 {
							 	case 0:
								 {pro.setConcept(str.substring(0, start));
								 j++;
								 if(start==str.length())
									 break;
								 else
									 len=start+1;
								 break;
								 }
							 	case 1:
							 	{
							 		String s=StringTransform.stringTransform(str.substring(len,start).toLowerCase());
							 		pro.setEntity(s);
							 		j++;
							 		if(start==str.length())
										 break;
							 		else
							 			len=start+1;
							 		break;
							 	}
							 	case 2:
							 	{
							 		pro.setFrequency(Integer.parseInt(str.substring(len, start)));
							 		j++;
							 		break;
							 	}
							 	default:
						               break;
							 
							 }
						 }
					}
					try
					{
						String c=pro.getEntity().substring(0, 1);
						fw=new FileWriter("E:\\workspace\\fileread\\Probase_index\\"+c+".txt",true);
						BufferedWriter bw=new BufferedWriter(fw);
						bw.write(pro.getConcept()+"\t"+pro.getEntity()+"\t"+pro.getFrequency());
						bw.newLine();
						bw.close();
					}
					catch(NullPointerException npe)
					{}
				}
				br.close();	
			}
			catch(IOException ie)
			{}
		}
		
	}

}
