package cn.entityaug.process;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import cn.entityaug.excel.ExcelManage;
import cn.entityaug.json.ReadJson;
import cn.entityaug.table.TableBean;

public class PreProcess {
	public static void reSet(TableBean table,String excel)
	{
		
		List<String> schema=table.getSchema();
		List<List<String>> columnContent=table.getColumnContent();
		//统一日期格式
		int t=-1;
		for(int j=0;j<schema.size();j++)
		{
			String temp=schema.get(j).toLowerCase();
			if(temp.contains("year")||temp.contains("date"))
			{
				t=j;
				break;
			}
		}
		if(t!=-1)
		{
			List<String> attri=columnContent.get(t);
			for(int l=0;l<attri.size();l++)
			{
				String string1=schema.get(t);
				if(attri.get(l).contains(string1))
				{
					attri.set(l, attri.get(l).replace(string1, "").replaceAll("\\(.*?\\)", ""));
				}
				String s=attri.get(l).trim();
				int t1=isDate1(s);
				int t2=isDate2(s);
				int t3=isDate3(s);
				int t4=isDate4(s);
				int date=t1;
				if(date==-1)
				{
					date=t2;
					if(date==-1)
					{
						date=t3;
						if(date==-1)
							date=t4;
					}	
					
				}
				//TODO String转为int后又转为String
				attri.set(l,""+date);
				
			}
			
			columnContent.set(t, attri);
		}
		int keyColumn=table.getKeyColumnIndex();
		ExcelManage em=new ExcelManage();
		String title[] = schema.toArray(new String[schema.size()]);
		em.createExcel(excel+"/"+table.getId()+".xls", "sheet1", title,keyColumn);
		List<List<String>> RowContent=new ArrayList<List<String>>();
		for(int i=0;i<columnContent.get(0).size();i++)
		{
			List<String> l=new ArrayList<String>();
			for(int j=0;j<columnContent.size();j++)
			{
				String str=schema.get(j);
				String str1=columnContent.get(j).get(i).replaceAll("\\(.*?\\)", "");
				if(columnContent.get(j).get(i).contains(str))
				{
					str1=str1.replace(str, "");
				}
				l.add(str1);
				
			}
			RowContent.add(l);
		}
		for(int i=0;i<RowContent.size();i++)
		{
			em.writeToExcel(excel+table.getId()+".xls", "sheet1", RowContent.get(i),keyColumn);
		}
	}
	
	public static void preProcess(String path,String excel)throws IOException
	{
		File file=new File(path);
		String[] s=file.list();
		for(int i=0;i<s.length;i++)
		{
			TableBean table=ReadJson.ReadJsonFile(path+"/"+s[i]);
			//TODO json数据写入excel 运行非常慢
			reSet(table,excel);
		}
		
	}
	public static int isDate1(String s)
	{
		SimpleDateFormat format=new SimpleDateFormat("MMM dd, yyyy",Locale.ENGLISH);
		boolean dateflag=true;
		Date date=null;
		try
		{
			format.setLenient(false);
			date=format.parse(s);
		}
		catch(ParseException e)
		{
			dateflag=false;
			return -1;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	public static int isDate4(String s)
	{
		SimpleDateFormat format=new SimpleDateFormat("MMM yyyy",Locale.ENGLISH);
		boolean dateflag=true;
		Date date=null;
		try
		{
			format.setLenient(false);
			date=format.parse(s);
		}
		catch(ParseException e)
		{
			dateflag=false;
			return -1;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	public static int isDate2(String s)
	{
		boolean isInt = Pattern.matches("\\d+", s);
		 if (isInt) {
			 int t=Integer.parseInt(s);
			 if((s.length() == 4)&&1400<=t&&t<=2050)
				 return Integer.parseInt(s);
		  }
		  
		  return -1;
	}
	public static int isDate3(String s)
	{
		SimpleDateFormat format=new SimpleDateFormat("MM/dd/yyyy");
		boolean dateflag=true;
		Date date=null;
		try
		{
			format.setLenient(false);
			date=format.parse(s);
		}
		catch(ParseException e)
		{
			dateflag=false;
			return -1;
		}
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	
}
