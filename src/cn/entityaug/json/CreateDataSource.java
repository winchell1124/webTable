package cn.entityaug.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import cn.entityaug.qureytable.QueryTable;
import cn.entityaug.qureytable.ReadQueryTable;
import cn.entityaug.similarity.EditDistance;
import cn.entityaug.table.TableBean;

public class CreateDataSource {
	private static TableBean webTables;
	private static QueryTable qt;
	public static void getWebTables(QueryTable queryTable, String datasource, String target)throws IOException
	{
		qt=queryTable;
		List<String> queryentity=qt.getEntity();
		File file=new File(datasource);
		String[] jsonPath = file.list();
		//       System.out.println(jsonPath.length);
//		for(int i=0;i<jsonPath.length;i++)
		int i=0;
		{
			try
			{
				    webTables=ReadJson.ReadJsonFile(datasource+"/"+jsonPath[i]);
					List<String> entity1=webTables.getEntity();
					int count=0;
					if(entity1!=null&&queryentity!=null)
					{
						int m = entity1.size();
						int n = queryentity.size();
						for(int k=0;k<m;k++){
							boolean flag=false;
							for(int l=0;l<n;l++){
								//System.out.println("column1:"+column1.get(i)+" column2:"+column2.get(j));
								//若两个属性列属性名为空，则视为相同
								if((entity1.get(k)!=" ")&&(queryentity.get(l)!=" ")&&EditDistance.similarity(entity1.get(k).toLowerCase(), queryentity.get(l).toLowerCase()) >=0.7){
									flag=true;
								}
							}
							if(flag==true){
								count++;
							}

						}
						//将原文件路径下符合要求的文件写入到新路径下
						if(count>=1)
						{
							BufferedWriter bw=null;
							BufferedReader br=null;
							try
							{
								FileWriter fw=new FileWriter(target+"/"+jsonPath[i]);
								FileReader fr=new FileReader(datasource+"/"+jsonPath[i]);
								bw=new BufferedWriter(fw);
								br=new BufferedReader(fr);
							}
							catch(IOException e)
							{} 
							char[] buffer=new char[1024];
							int len=0;
							while((len=br.read(buffer))!=-1)
							{
								bw.write(buffer, 0, len);
							}

							bw.close();
							br.close();
						}
					}

				}
			
			catch(IndexOutOfBoundsException ie)
			{

			}

			catch(NumberFormatException nfe)
			{

			}
		}
		
	}
}