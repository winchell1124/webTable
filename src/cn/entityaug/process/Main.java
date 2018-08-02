package cn.entityaug.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.entityaug.data.CandidateTable;
import cn.entityaug.data.Cell;
import cn.entityaug.data.GetFirstCandidateTable;
import cn.entityaug.excel.CreateAugTable;
import cn.entityaug.excel.ExcelManage;
import cn.entityaug.json.CreateDataSource;
import cn.entityaug.qureytable.GetTableCon;
import cn.entityaug.qureytable.QueryTable;
import cn.entityaug.qureytable.ReadQueryTable;
import cn.entityaug.table.TableBean;

public class Main {
	private static QueryTable queryTable;
	private static String datasource;
	static int count;
	public static void main(String[] args)throws IOException
	{
		queryTable=ReadQueryTable.readQT("QueryData/query-book.xls", "sheet1");
		datasource="WebTables_books";//�������ŵ�ַ
		double thCov=0.7;
//		String targetFolder="DataSet/Experiments/WebTables_books";
        String targetFolder="DataSet/Experiments/test";
		//��������Դ���洢��targetFolder��

//        long startTime=System.nanoTime();   //��ȡ��ʼʱ��
//        //���ѯ��Աȣ�ѡȡ����ͬʵ��������д��targetFolder·����,���ݵ�Ԥ����,�ܺ�ʱ
//		CreateDataSource.getWebTables(queryTable, datasource, targetFolder);
//        long endTime=System.nanoTime(); //��ȡ����ʱ��
//        System.out.println("��������ʱ�䣺 "+(endTime-startTime)+"ns");

		//�õ���������ѯ��ĸ����ʵ��ƥ��ȣ�������ĸ������Books_Concept��ƥ��ȴ���query-table_Con.txt��
		String topic="book";//��ѯ�����⣬���ڲ鿴�����õ�������
        //�ҳ�ÿ�������ʵ��ĸ��
		GetTableCon.getTablesConcept(queryTable,targetFolder,topic);
		//Ԥ����
		String excel="DataSet/Experiments/excel";//Excel����д��ĵ�ַ
		PreProcess.preProcess(targetFolder,excel);//��json����ת��Ϊexcel����
		File f=new File(excel);
		File[] files = f.listFiles();
		int number = files.length;
		//�������ӱ�
		CandidateTable seedTable=GetFirstCandidateTable.getSeedTables(queryTable, excel,topic);
		//����ͼģ�ͣ�ͬʱ�õ���
		//���ɳ�ʼ�ڵ�seedSet
		ExcelManage em = new ExcelManage();
		//���Ӽ�
		SeedTableSet seedSet=new SeedTableSet();
		List<CandidateTable> table=new ArrayList<CandidateTable>();
		table.add(seedTable);
		seedSet.setSeedTables(table);
		seedSet.setScore(seedTable.getScore());
		List<Cell> cell=new ArrayList<Cell>();
		for(String i:seedTable.getFillcell().getEntitys())
		{
			for(String j:seedTable.getFillcell().getAttributes())
			{
				Cell cell1=new Cell();
				cell1.setEntity(i);
				cell1.setAttribute(j);
				cell.add(cell1);
			}
		}
		seedSet.setSetFillCells(cell);
		double up=seedTable.getFillcell().getEntitys().size()*seedTable.getFillcell().getAttributes().size();
		double down=queryTable.getEntity().size()*queryTable.getAttributes().size();
		//���㵱ǰ������
		seedSet.setCoverage(up/down);
		//�ݹ����
		count=1;
		List<SeedTableSet> result=getResultTables(topic,seedSet,thCov,number);
		for(int i=0;i<result.size();i++)
		{
			
			File file=new File("DataSet/Experiments/"+topic+"/results/"+i);
			if(!file.exists())
			{
                file.mkdirs();
			}
			String path="DataSet/Experiments/"+topic+"/results/"+i;
			CreateAugTable.createAugTable(path, queryTable, result.get(i));
			BufferedWriter bw=new BufferedWriter(new FileWriter(path+"/record.txt",true));
			for(CandidateTable candTable:result.get(i).getSeedTables())
			{
				bw.write(candTable.getId());
				bw.newLine();
			}
			bw.write("coverage: "+result.get(i).getCoverage());
			bw.newLine();
			bw.write("score: "+result.get(i).getScore());
			bw.close();
		}
	}
    public static List<SeedTableSet> getResultTables(String topic,SeedTableSet sts,double thCov,int number)throws IOException 
	{
    	System.out.println(sts.getCoverage());
    	if(sts.getCoverage()>=thCov)
    	{
    		List<SeedTableSet> result=new ArrayList<SeedTableSet>();
    		result.add(sts);
    		return result;
    	}
    	if(sts.getSeedTables().size()>number)
    		return null;
    	else
    	{
    		List<SeedTableSet> list=GetSeedTableSets.getCandidateTable(queryTable, sts, count,topic);
    		if(list.size()==0)
    			return null;
    		SeedTableSet s1=list.get(0);
    		if(s1.getCoverage()-sts.getCoverage()<0.001){
    			List<SeedTableSet> result=new ArrayList<SeedTableSet>();
        		result.add(sts);
        		return result;
    		}
    		count++;
    		List<SeedTableSet> result=getResultTables(topic,s1,thCov,number);
    		if(list.size()>1)
    		{
    			SeedTableSet s2=list.get(1);
        		count++;
        		result.addAll(getResultTables(topic,s2,thCov,number));
    		}
    		return result;
    	}
	}
}
