package cn.entityaug.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.entityaug.data.CandidateTable;
import cn.entityaug.data.Cell;
import cn.entityaug.data.FillCell;
import cn.entityaug.data.GetFirstCandidateTable;
import cn.entityaug.data.TableToTableSim;
import cn.entityaug.excel.CreateAugTable;
import cn.entityaug.excel.ExcelManage;
import cn.entityaug.qureytable.QueryTable;
import cn.entityaug.qureytable.ReadQueryTable;
import cn.entityaug.similarity.EditDistance;
import cn.entityaug.sort.HashMapSort;
import cn.entityaug.sort.ListSort;
import cn.entityaug.table.TableBean;
import cn.entityaug.txt.WriteTxt;

public class GetSeedTableSets {
	
	public static List<SeedTableSet> getCandidateTable(QueryTable qt,SeedTableSet seedSet,int count,String topic)throws IOException
	{
		List<SeedTableSet> sts=new ArrayList<SeedTableSet>();
		List<CandidateTable> seedTables=seedSet.getSeedTables();
		ExcelManage em1=new ExcelManage();
		TableBean seedTable=em1.readFromExcel(seedTables.get(seedTables.size()-1).getId(), "sheet1");
		String abpath=seedTable.getAbpath();
		String[] name=abpath.split("/");
		String path="";
		for(int i=0;i<name.length-1;i++)
		{
			path=path+name[i]+"/";
		}
//		System.out.println(path);
		String path1="DataSet/Experiments/"+topic+count;
		File file1=new File(path1);
		if(!file1.exists())
		{
			file1.mkdirs();
		}
		//候选表和种子集中表的边潜能
		String fileName=path1+"/TableToTableSim.txt";
		BufferedWriter bw=new BufferedWriter(new FileWriter(fileName,true));
		//候选表和种子集中表的团潜能
		String potential=path1+"/potential.txt";
		File file=new File(path.substring(0,path.length()-1));
		String[] arrs=file.list();
//		CandidateTable cand=GetFirstCandidateTable.getTableScore(qt, seedTable);
		Map<CandidateTable,Double> map=new HashMap<CandidateTable,Double>();
        LinkedList<String> l1=new LinkedList<String>();
        List<Cell> setFillCells = seedSet.getSetFillCells();
        FillCell seedFillCells=seedTables.get(seedTables.size()-1).getFillcell();
		for(int i=0;i<arrs.length;i++)
		{
			boolean flag=true;
			for(int j=0;j<seedTables.size();j++)
			{
				if(arrs[i].equals(seedTable.getId())||arrs[i].contains(".txt"))
				{
					flag=false;
					break;
				}
			}
			if(flag==false)
			{
				continue;
			}
			ExcelManage em=new ExcelManage();
			TableBean table=em.readFromExcel(path+arrs[i],"sheet1");
			File file2=new File(path+"TableToTableSim.txt");
			double temp=0.0;
			if(file2.exists())
			{
				BufferedReader br=new BufferedReader(new FileReader(path+"TableToTableSim.txt"));
				String str=null;
				while((str=br.readLine())!=null)
				{
					String[] array=str.split("  ");
					if(array[0].equals(seedTable.getId()))
					{
						temp=Double.parseDouble(array[1]);
						break;
					}
				}
				br.close();
			}
			CandidateTable cand=TableToTableSim.getPotential(bw,temp,qt, setFillCells, table, path1, seedTable, seedFillCells);
			l1.add(cand.getId()+"\r\n"+cand.getScore());
			map.put(cand, cand.getScore());
		 }
		bw.close();
//		l1=ListSort.sortBySim(l1);
		map=HashMapSort.sortCandidateTableByDouValue(map);
		Set<CandidateTable> set=map.keySet();
		Iterator<CandidateTable> it=set.iterator();
		int e=0;
		while(e<1&&it.hasNext())
		{
			SeedTableSet st=new SeedTableSet();
			CandidateTable candi=(CandidateTable)it.next();
			e++;
			double score=map.get(candi);
			st.setScore(seedSet.getScore()+score);
			List<CandidateTable> list=new ArrayList<CandidateTable>();
			for(CandidateTable ct:seedSet.getSeedTables())
			{
				list.add(ct);
			}
			list.add(candi);
			st.setSeedTables(list);
			List<Cell> cells=seedSet.getSetFillCells();
			List<Cell> candcells=new ArrayList<Cell>();
			List<String> candentity=candi.getFillcell().getEntitys();
			List<String> candattri=candi.getFillcell().getAttributes();
			for(int i=0;i<candentity.size();i++)
			{
				for(int j=0;j<candattri.size();j++)
				{
					Cell cell=new Cell();
					cell.setEntity(candentity.get(i));
					cell.setAttribute(candattri.get(j));
					candcells.add(cell);
				}
			}
			for(int i=0;i<candcells.size();i++)
			{
				for(int j=0;j<cells.size();j++)
				{
					if(candcells.get(i).equals(cells.get(j)))
					{
						candcells.remove(i);
						i--;
						break;
					}
				}
			}
			List<Cell> newFillCell=new ArrayList<Cell>();
			for(Cell cell:cells)
			{
				newFillCell.add(cell);
			}
			for(int i=0;i<candcells.size();i++)
			{
				Cell cell=new Cell();
				cell.setEntity(candcells.get(i).getEntity());
				cell.setAttribute(candcells.get(i).getAttribute());
				boolean flag=false;
				for(Cell cell1:cells)
				{
					if(cell1.equals(cell))
					{
						flag=true;
						break;
					}
				}
				if(flag==false)
				{
					newFillCell.add(cell);
				}
			}
			st.setSetFillCells(newFillCell);
			double up=newFillCell.size();
			double down=qt.getEntity().size()*qt.getAttributes().size();
			st.setCoverage(up/down);
			sts.add(st);
		}
		
			WriteTxt.writeAllcontent(potential, l1);
			return sts;
	}
}
