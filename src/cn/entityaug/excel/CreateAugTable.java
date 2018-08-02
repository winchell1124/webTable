package cn.entityaug.excel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.entityaug.data.Cell;
import cn.entityaug.data.TableToTableSim;
import cn.entityaug.process.SeedTableSet;
import cn.entityaug.qureytable.QueryTable;
import cn.entityaug.qureytable.ReadQueryTable;
import cn.entityaug.similarity.EditDistance;
import cn.entityaug.table.TableBean;
import cn.entityaug.txt.ReadTxt;
import cn.entityaug.txt.WriteTxt;

public class CreateAugTable {
	public static void main(String[] args)throws IOException{
		long start1=System.currentTimeMillis();
		//通过分数获取
		double thcov=0.1;
		QueryTable qt=ReadQueryTable.readQT("query-country.xls", "sheet1");
		List<String> l=ReadTxt.readTxt("DataSet\\Experiments\\dataset-excel\\country_Score.txt");
		List<String> tables=new ArrayList<String>();
		for(int i=0;i<l.size();i++){
			String s=l.get(i);
			String[] ss=s.split(" ");
			tables.add(ss[0]);
		}
		ExcelManage em=new ExcelManage();
		TableBean t=em.readFromExcel(tables.get(0), "sheet1");
		int[][] start=getCov(qt,t);
		double count=0;
		for(int i=0;i<start.length;i++){
			for(int j=0;j<start[0].length;j++){
				if(start[i][j]==1)
					count++;
			}
		}
		List<TableBean> store=new ArrayList<TableBean>();
		store.add(t);
		if((count/(start.length*start[0].length))>=thcov)
			createAugTabletopk("DataSet\\Experiments\\dataset-excel\\book0.1",qt,store);
		else{
			for(int i=1;i<tables.size();i++){
				TableBean t1=em.readFromExcel(tables.get(i), "sheet1");
				int[][] cov=getCov(qt,t1);
				for(int m=0;m<start.length;m++){
					for(int n=0;n<start[0].length;n++){
						if(cov[m][n]==1&&start[m][n]==0)
							start[m][n]=1;
					}
				}
				double count1=0.0;
				store.add(t1);
				for(int x=0;x<start.length;x++){
					for(int y=0;y<start[0].length;y++){
						if(start[x][y]==1)
							count1++;
					}
				}
				if((count1/(start.length*start[0].length))>=thcov)
					break;
			}
			createAugTabletopk("DataSet\\Experiments\\dataset-excel\\book0.1",qt,store);
		}
		List<String> ll=new ArrayList<String>();
		for(TableBean tab:store)
			ll.add(tab.getAbpath());
		WriteTxt.writeAllcontent("DataSet\\Experiments\\dataset-excel\\book0.1\\record.txt", ll);
	   long end=System.currentTimeMillis();
	   System.out.println(end-start1);
	}
//	public static String getValue(QueryTable qt,TableBean t){
//		int row=qt.getEntity().size();
//		int column=qt.getAttributes().size();
//		int[][] cov=new int[row][column];
//		List<String> qtEntity=qt.getEntity();
//		List<String> qtAttri=qt.getAttributes();
//		List<String> tEntity=t.getEntity();
//		List<String> tAttri=t.getAttrubutes();
//		for(int i=0;i<qtEntity.size();i++){
//			String entity=qtEntity.get(i);
//			for(int j=0;j<qtAttri.size();j++){
//				String attri=qtAttri.get(j);
//				for(int m=0;m<tEntity.size();m++){
//					boolean flag=false;
//					String entity1=tEntity.get(m);
//					for(int n=0;n<tAttri.size();n++){
//						String attri1=tAttri.get(n);
//						if(EditDistance.similarity(entity, entity1)>0.8&&EditDistance.similarity(attri, attri1)>0.8){
//							cov[i][j]=1;
//							flag=true;
//							break;
//						}
//					}
//					if(flag)
//						break;
//				}
//			}
//		}
// 		return cov;
//	}
	public static int[][] getCov(QueryTable qt,TableBean t){
		int row=qt.getEntity().size();
		int column=qt.getAttributes().size();
		int[][] cov=new int[row][column];
		List<String> qtEntity=qt.getEntity();
		List<String> qtAttri=qt.getAttributes();
		List<String> tEntity=t.getEntity();
		List<String> tAttri=t.getAttrubutes();
		for(int i=0;i<qtEntity.size();i++){
			String entity=qtEntity.get(i);
			for(int j=0;j<qtAttri.size();j++){
				String attri=qtAttri.get(j);
				for(int m=0;m<tEntity.size();m++){
					boolean flag=false;
					String entity1=tEntity.get(m);
					for(int n=0;n<tAttri.size();n++){
						String attri1=tAttri.get(n);
						if(EditDistance.similarity(entity, entity1)>0.8&&EditDistance.similarity(attri, attri1)>0.8){
							cov[i][j]=1;
							flag=true;
							break;
						}
					}
					if(flag)
						break;
				}
			}
		}
 		return cov;
	}
	public static void createAugTabletopk(String path,QueryTable qt,List<TableBean> tabs)throws IOException
	{
		List<List<String>> rowContent=new ArrayList<List<String>>();
		int rowNum=qt.getEntity().size();
		List<String> entity=qt.getEntity();
		int columnNum=qt.getAttributes().size()+1;
		List<String> schema=qt.getSchema();
		for(int i=0;i<rowNum;i++){
			List<String> row=new ArrayList<String>();
			row.add(entity.get(i));
			for(int j=0;j<columnNum-1;j++){
				row.add(null);
			}
			rowContent.add(row);
		}
		for(int i=0;i<tabs.size();i++){
			TableBean t=tabs.get(i);
			String[][] data=getData(qt,t);
			for(int m=0;m<data.length;m++){
				for(int n=0;n<data[0].length;n++){
					if(data[m][n]!=null)
						rowContent.get(m).set(n+1, data[m][n]);
				}
			}
		}
		ExcelManage em = new ExcelManage();
		String title[] = schema.toArray(new String[schema.size()]);
		em.createExcel(path+"\\querytable-book.xls", "sheet1", title, 0);
		for(List<String> rows:rowContent)
		{
			em.writeToExcel(path+"\\querytable-book.xls", "sheet1", rows, 0);
		}
	}
	public static String[][] getData(QueryTable qt,TableBean t){
		int row=qt.getEntity().size();
		int column=qt.getAttributes().size();
		String[][] data=new String[row][column];
		List<String> qtEntity=qt.getEntity();
		List<String> qtAttri=qt.getAttributes();
		List<String> tEntity=t.getEntity();
		List<String> tAttri=t.getAttrubutes();
		List<List<String>> rowContent=t.getRowContent();
		for(int i=0;i<qtEntity.size();i++){
			String entity=qtEntity.get(i);
			for(int j=0;j<qtAttri.size();j++){
				String attri=qtAttri.get(j);
				for(int m=0;m<tEntity.size();m++){
					boolean flag=false;
					String entity1=tEntity.get(m);
					for(int n=0;n<tAttri.size();n++){
						String attri1=tAttri.get(n);
						if(EditDistance.similarity(entity, entity1)>0.8&&EditDistance.similarity(attri, attri1)>0.8){
							data[i][j]=rowContent.get(m).get(n+1);
							flag=true;
							break;
						}
					}
					if(flag)
						break;
				}
			}
		}
		return data;
	}
	public static void createAugTable(String path,QueryTable qt,SeedTableSet seedTables)throws IOException
	{
		
		List<String> querySchema=qt.getSchema();
		List<String> queryColumn=qt.getAttributes();
		List<List<String>> queryRow=qt.getRowContent();
		List<Cell> fillcells=seedTables.getSetFillCells();
		
		for(int i=0;i<seedTables.getSeedTables().size();i++)
		{
			ExcelManage em = new ExcelManage();
			String s=seedTables.getSeedTables().get(i).getId();
			TableBean t=em.readFromExcel(s, "sheet1");
			List<String> seedEntity=t.getEntity();
			List<String> seedAttri=t.getAttrubutes();
			List<List<String>> rowContent=t.getRowContent();
		//	List<Cell> seedTableCells=new ArrayList<Cell>();
			for(int l=0;l<seedEntity.size();l++)
			{
				List<String> row=rowContent.get(l);
				for(int j=0;j<seedAttri.size();j++)
				{
					String s1=seedAttri.get(j);
					int column=TableToTableSim.getAttriData(t,s1);
					Cell cell=new Cell();
					cell.setEntity(seedEntity.get(l));
					cell.setAttribute(seedAttri.get(j));
					cell.setData(row.get(column));
					for(Cell fillcell:fillcells)
					{
						if(cell.equals1(fillcell)&&!fillcell.isFalg())
						{
							fillcell.setData(cell.getData());
							fillcell.setFalg(true);
						}
					}
				}
			}
			
		}
		for(int i=0;i<queryRow.size();i++)
		{
			String temp=queryRow.get(i).get(0);
			for(int j=0;j<queryColumn.size();j++)
			{
				Cell cell=new Cell();
				cell.setEntity(temp);
				cell.setAttribute(queryColumn.get(j));
				String data=null;
				int t=querySchema.indexOf(queryColumn.get(j));
				for(Cell c:fillcells)
				{
					if(c.equals(cell))
					{
						data=c.getData();
						break;
					}
				}
				//System.out.println(t);
				queryRow.get(i).set(t,data);
			}
		}
		ExcelManage em = new ExcelManage();
		String title[] = querySchema.toArray(new String[querySchema.size()]);
		em.createExcel(path+"/querytable-book.xls", "sheet1", title, 0);
		for(List<String> rows:queryRow)
		{
			em.writeToExcel(path+"/querytable-book.xls", "sheet1", rows, 0);
		}
	}

}
