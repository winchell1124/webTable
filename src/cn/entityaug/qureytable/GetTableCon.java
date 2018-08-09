package cn.entityaug.qureytable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.entityaug.data.ReSetSeedTables;
import cn.entityaug.data.StringTransform;
import cn.entityaug.json.ReadJson;
import cn.entityaug.similarity.EditDistance;
import cn.entityaug.similarity.JaccardSim;
import cn.entityaug.sort.HashMapSort;
import cn.entityaug.table.TableBean;

/*
 * 对于实体entity,找到其概念
 */
public class GetTableCon {
	public static double getSim(Object o,TableBean candidate,String topic)throws FileNotFoundException, IOException
	{
		String temp=null;
		List<String> seedEntitys=new ArrayList<String>();
		if(o instanceof QueryTable)
		{
			QueryTable qt=(QueryTable)o;
			seedEntitys=qt.getEntity();
			temp="querytable_con";
		}
		else
		{
			TableBean qt=(TableBean)o;
			seedEntitys=qt.getEntity();
			temp=qt.getId();
			if(temp.contains(".xls"))
			{
				temp=temp.replaceAll(".xls", "");
			}
		}
		File f=new File("DataSet/Experiments/"+topic+"_Concept/"+temp+".txt");
		if(!f.exists())
		{
			//运行很慢
			//获取概念集，存储到文件中
			List<List<String>> seedCons=getTableCon(seedEntitys);
			
			BufferedWriter bw=new BufferedWriter(new FileWriter("DataSet\\Experiments\\"+topic+"_Concept\\"+temp+".txt"));
			for(int i=0;i<seedCons.size();i++)
			{
				for(int j=0;j<seedCons.get(i).size();j++)
				{
					bw.write(seedCons.get(i).get(j)+" ");
				}
				bw.newLine();
				bw.flush();
			}
			bw.close();
		}
		//读文件中语义，存入list
		List<List<String>> seedCons=new ArrayList<List<String>>();
		BufferedReader br=new BufferedReader(new FileReader("DataSet/Experiments/"+topic+"_Concept/"+temp+".txt"));
		String s=null;
		while((s=br.readLine())!=null)
		{
			List<String> l=new ArrayList<String>();
			String[] arrs=s.split(" ");
			for(int i=0;i<arrs.length;i++)
			{
				l.add(arrs[i]);
			}
			seedCons.add(l);
		}
		br.close();
		System.out.println("query table calculate complete");

		double d=0.0;
		String str1=candidate.getId();
		if(str1.contains(".xls"))
		{
			str1=str1.replaceAll(".xls", "");
		}
		File file=new File("DataSet/Experiments/"+topic+"_Concept/"+str1+".txt");

		if(!file.exists())
		{
			List<String> candidateEntitys=candidate.getEntity();
			for(int l=0;l<candidateEntitys.size();l++)
			{
				String te=candidateEntitys.get(l).toLowerCase();
				if(te.indexOf("title")==0)
				{
					candidateEntitys.remove(l);
					candidateEntitys.add(l, te.substring("title".length()+1));
				}
				String te1=candidateEntitys.get(l).toLowerCase();
				if(te1.indexOf("the")==0)
				{
					candidateEntitys.remove(l);
					candidateEntitys.add(l, te1.substring("the".length()+1));
				}
				for(int m=0;m<seedEntitys.size();m++)
				{
					if(EditDistance.similarity(candidateEntitys.get(l), seedEntitys.get(m))>0.8)
					{
						candidateEntitys.remove(l);
						l--;
						break;
					}
				}
			}
            System.out.println("write the file : "+"DataSet/Experiments/"+topic+"_Concept/"+str1+".txt");
			BufferedWriter bw=new BufferedWriter(new FileWriter("DataSet/Experiments/"+topic+"_Concept/"+str1+".txt"));
			List<List<String>> temp_candCons=getTableCon(candidateEntitys);
			for(int i=0;i<temp_candCons.size();i++)
			{
				for(int j=0;j<temp_candCons.get(i).size();j++)
				{
					bw.write(temp_candCons.get(i).get(j)+" ");
				}
				bw.newLine();
				bw.flush();
			}
			bw.close();
		}
		List<List<String>> candCons=new ArrayList<List<String>>();
		BufferedReader br1=new BufferedReader(new FileReader("DataSet/Experiments/"+topic+"_Concept/"+str1+".txt"));
		String s1=null;
		while((s1=br1.readLine())!=null)
		{
			List<String> l=new ArrayList<String>();
			String[] arrs=s1.split(" ");
			for(int i=0;i<arrs.length;i++)
			{
				l.add(arrs[i]);
			}
			candCons.add(l);
		}
		br1.close();
		double count=0.0;
		for(int m=0;m<seedCons.size();m++)
		{
			if(seedCons.get(m).size()==0)
				continue;
			for(int n=0;n<candCons.size();n++)
			{
				if(candCons.get(n).size()==0)
				{
					continue;
				}
				else
				{
					double d1=JaccardSim.getJac(seedCons.get(m), candCons.get(n));
					d=d+d1;
					count++;
				}
			}
		}
		return d/count;
	}
	
	public static void getTablesConcept(QueryTable qt,String source,String topic)throws FileNotFoundException, IOException
	{
		BufferedWriter bw=new BufferedWriter(new FileWriter("DataSet/Experiments/query-table_Con.txt"));
		Map<String,Double> map=new HashMap<String,Double>();
		File file1=new File(source);
		String[] path=file1.list();
		for(int i=0;i<path.length;i++)
		{
			try
			{
				TableBean candidate=ReadJson.ReadJsonFile(source+"/"+path[i]);
			    //获得查询表和网络表实体语义，计算相似度
				double d=getSim(qt,candidate,topic);
			    map.put(path[i], d);
			}
			catch(StringIndexOutOfBoundsException sbe)
			{}
			
		}
		//将webTable与查询表相似度写在文件中
		Set<String> set=map.keySet();
		Iterator<String> it=set.iterator();
		while(it.hasNext())
		{
			String s1=(String)it.next();
			double d1=map.get(s1);
			bw.write(s1+" "+d1);
			bw.newLine();
			bw.flush();
		}
		bw.close();
		ReSetSeedTables.getCandidateSort("DataSet/Experiments/query-table_Con.txt");
	}

	public static List<List<String>> getTableCon(List<String> entitys)throws IOException
	{
		
		List<List<String>> rowscon=getEntityCon(entitys);
		for(int l=0;l<rowscon.size();l++)
		{
			try{
				for (int i = 0; i < rowscon.get(l).size(); i++) {  
					
					for (int j = i + 1; j < rowscon.get(l).size(); j++) {  
						if (rowscon.get(l).get(i).equals(rowscon.get(l).get(j))) {  
						 
							rowscon.get(l).remove(j);  
							j--;// 这里是重点，因为集合remove（）后，长度改变了，对应的下标也不再是原来的下标，//仔细体会  
						}  
					}  
					
				}
			}
			catch(NullPointerException npe)
			{}
			  
		}
		
		return rowscon;
	}

	public static  List<List<String>> getEntityCon(List<String> entitys)throws IOException
	{
		
		 List<List<String>> seedCons=new ArrayList<List<String>>();
		 FileReader fr=null;
		 for(int i=0;i<entitys.size();i++)
		 {
			 
			 String str=entitys.get(i);
			 str=StringTransform.stringTransform(str).toLowerCase().trim();
			 
			 List<String> list=new ArrayList<String>();
			 if(str.length()==0)
			 {
				 System.out.println(1);
				 seedCons.add(i, list);
			 }
			 else
			 {
				 List<String> list1=new ArrayList<String>();
				 String c=(str.length()==1)?str:str.substring(0, 2);
				 c=c.replaceAll("[\\\\/:*?<>→\"]", " ");
				 try
				 {
					 fr=new FileReader("DataSet/Experiments/Probase_index/"+c+".txt");
					 BufferedReader br=new BufferedReader(fr);
					 String s=null;
					 while((s=br.readLine())!=null)
					 {
						 Probase p=new Probase();
						 String[] arrs=null;
						 s=s.trim();
						 arrs=s.split("\t");
						 p.setConcept(arrs[0]);
						 p.setEntity(arrs[1]);
						 p.setFrequency(Integer.parseInt(arrs[2]));
						 if(EditDistance.similarity(str, p.getEntity())>0.8)
						 {
							 list1.add(p.getConcept());
						 }
					 }
					 br.close();
				 }
				 catch(FileNotFoundException fnf)
				 {}
				  
				 list=list1;
				 seedCons.add(i, list);
				 
		    }
		 }
		
		 return seedCons;
	}
}
