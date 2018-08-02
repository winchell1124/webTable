package cn.entityaug.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cn.entityaug.json.ReadJson;
import cn.entityaug.qureytable.GetTableCon;
import cn.entityaug.similarity.NumricSim;
import cn.entityaug.sort.HashMapSort;
import cn.entityaug.table.TableBean;

public class ReSetSeedTables {
	public static void getCandidateSort(String txt)throws IOException
	{
		Map<String,Double> unSortedCand=new HashMap<String,Double>();
		List<String> nanTables=new ArrayList<String>();
		Map<String,Double> map=new HashMap<String,Double>();
		File file=new File(txt);
		BufferedReader br=new BufferedReader(new FileReader(txt));
		String s=null;
		while((s=br.readLine())!=null)
		{
			String[] arrs=s.split(" ");
			
			if(!arrs[1].equals("NaN"))
			{
				double d=Double.parseDouble(arrs[1]);
				unSortedCand.put(arrs[0], d);
				
			}
			if(arrs[1].equals("NaN"))
			{
				nanTables.add(arrs[0]);
			}
			
		}
		br.close();
		file.delete();
		double sum=0;
		Set<String> unKeySet=unSortedCand.keySet();
		Iterator<String> unit=unKeySet.iterator();
		while(unit.hasNext())
		{
			String stri=(String)unit.next();
			sum=sum+unSortedCand.get(stri);
		}
		int count=unSortedCand.size();
		double l=sum/count;
		for(int m=0;m<nanTables.size();m++)
		{
			unSortedCand.put(nanTables.get(m), l);
		}
		unSortedCand=HashMapSort.sortMapByDouValue(unSortedCand);
		BufferedWriter bw=new BufferedWriter(new FileWriter(txt));
		Set<String> keyset1=unSortedCand.keySet();
		Iterator<String> it_1=keyset1.iterator();
		while(it_1.hasNext())
		{
			String str=(String)it_1.next();
			double dou=unSortedCand.get(str);
			bw.write(str+" "+dou);
			bw.newLine();
			bw.flush();
		}
		bw.close();
	}
	public static void main(String[] args)throws IOException
	{
//		long startTime=System.currentTimeMillis();
		getCandidateSort("DataSet\\Experiments\\query-table_Con.txt");
//		long endTime=System.currentTimeMillis();
//		System.out.println("time"+(endTime-startTime));
	}
}
