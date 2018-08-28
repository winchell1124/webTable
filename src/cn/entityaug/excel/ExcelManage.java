package cn.entityaug.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import cn.entityaug.json.ReadJson;
import cn.entityaug.table.TableBean;

/**
 * ��excel��ȡ����/��excel��д�� excel�б�ͷ����ͷÿ�е����ݶ�Ӧʵ���������
 * 
 * @author nagsh
 * 
 */
public class ExcelManage {
	private HSSFWorkbook workbook = null;

	/**
	 * �ж��ļ��Ƿ����.
	 * 
	 * @param fileDir
	 *            �ļ�·��
	 * @return
	 */
	public boolean fileExist(String fileDir) {
		boolean flag = false;
		File file = new File(fileDir);
		flag = file.exists();
		return flag;
	}

	/**
	 * �ж��ļ���sheet�Ƿ����.
	 * 
	 * @param fileDir
	 *            �ļ�·��
	 * @param sheetName
	 *            ���������
	 * @return
	 */
	public boolean sheetExist(String fileDir, String sheetName) {
		boolean flag = false;
		File file = new File(fileDir);
		if (file.exists()) { // �ļ�����
			// ����workbook
			try {
				workbook = new HSSFWorkbook(new FileInputStream(file));
				// ���Worksheet�������sheetʱ���ɵ�xls�ļ���ʱ�ᱨ��)
				HSSFSheet sheet = workbook.getSheet(sheetName);
				if (sheet != null)
					flag = true;
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else { // �ļ�������
			flag = false;
		}

		return flag;
	}

	/**
	 * ������excel.
	 * 
	 * @param fileDir
	 *            excel��·��
	 * @param sheetName
	 *            Ҫ�����ı������
	 * @param titleRow
	 *            excel�ĵ�һ�м����ͷ
	 */
	public void createExcel(String fileDir, String sheetName, String titleRow[],int keyColumn) {
		// ����workbook
		workbook = new HSSFWorkbook();
		String temp=null;
		temp=titleRow[0];
		titleRow[0]=titleRow[keyColumn];
		titleRow[keyColumn]=temp;
		// ���Worksheet�������sheetʱ���ɵ�xls�ļ���ʱ�ᱨ��)
		Sheet sheet1 = workbook.createSheet(sheetName);
		// �½��ļ�
		FileOutputStream out = null;
		try {
			// ��ӱ�ͷ
			Row row = workbook.getSheet(sheetName).createRow(0); // ������һ��
			for (int i = 0; i < titleRow.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(titleRow[i]);
			}

			out = new FileOutputStream(fileDir);
			workbook.write(out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * ɾ���ļ�.
	 * 
	 * @param fileDir
	 *            �ļ�·��
	 */
	public boolean deleteExcel(String fileDir) {
		boolean flag = false;
		File file = new File(fileDir);
		// �ж�Ŀ¼���ļ��Ƿ����
		if (!file.exists()) { // �����ڷ��� false
			return flag;
		} else {
			// �ж��Ƿ�Ϊ�ļ�
			if (file.isFile()) { // Ϊ�ļ�ʱ����ɾ���ļ�����
				file.delete();
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * ��excel��д��(�Ѵ��ڵ������޷�д��).
	 * 
	 * @param fileDir
	 *            �ļ�·��
	 * @param sheetName
	 *            �������
	 * @param
	 */
	public void writeToExcel(String fileDir, String sheetName, List<String> rowContent,int keyColumn) {
		// ����workbook
		
		String temp=rowContent.get(0);
		rowContent.set(0, rowContent.get(keyColumn));
		rowContent.set(keyColumn, temp);
		File file = new File(fileDir);
		try {
			workbook = new HSSFWorkbook(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// ��
		FileOutputStream out = null;
		HSSFSheet sheet = workbook.getSheet(sheetName);
		// ��ȡ����������
		int rowCount = sheet.getLastRowNum() + 1; // ��Ҫ��һ
		// ��ȡ��ͷ������
		int columnCount = sheet.getRow(0).getLastCellNum();
		try {
			Row row = sheet.createRow(rowCount); // ����Ҫ��ӵ�һ��
			// ͨ��������object���ֶ�,��Ӧ��ͷ����
			// ��ȡ�ö����class����
			// ��ñ�ͷ�ж���
			HSSFRow titleRow = sheet.getRow(0);
			if (titleRow != null) {
				for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) { // ������ͷ
					// ִ�и�get����,��Ҫ���������
					Cell cell = row.createCell(columnIndex);
					cell.setCellValue(rowContent.get(columnIndex));
				}
			}

			out = new FileOutputStream(fileDir);
			workbook.write(out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ȡexcel���е�����.
	 * 
	 * @param fileDir
	 *            �ļ�·��
	 * @param sheetName
	 *            �������(EXCEL �Ƕ���ĵ�,������Ҫ����������ţ���sheet1)
	 * @param object
	 *            object
	 */
	public TableBean readFromExcel(String fileDir, String sheetName) {
		// ����workbook
		File file = new File(fileDir);
		List<String> schema=new ArrayList<String>();
		List<List<String>> columnContent=new ArrayList<List<String>>();  //���д�ű�����
		List<List<String>> rowContent=new ArrayList<List<String>>(); //���д�ű�����
		List<String> entity=new ArrayList<String>();
		List<String> attributes=new ArrayList<String>();
		String id=null; //����ļ�����������������ǣ�
		String abpath=null; //����ļ�����·��
		int keyColumnIndex=-1; //ָʾ���������ţ���==-1���������У�
		try {
			workbook = new HSSFWorkbook(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		TableBean result = new TableBean();
		// ��ȡ�ö����class����
		
	

		// ��ȡexcel����
		// ���ָ����excel��
		HSSFSheet sheet = workbook.getSheet(sheetName);
		// ��ȡ����������
		int rowCount = sheet.getLastRowNum() + 1; // ��Ҫ��һ
//		System.out.println("rowCount:" + rowCount);
		if (rowCount < 1) {
			return result;
		}
		keyColumnIndex=0;
		abpath=fileDir;
		String[] arrs=fileDir.split("/");
		id=arrs[arrs.length-1];
		// ��ȡ��ͷ������
		int columnCount = sheet.getRow(0).getLastCellNum();
		// ��ȡ��ͷ��Ϣ,ȷ����Ҫ�õķ�����---set����
		// ���ڴ洢������
		String[] methodNames = new String[columnCount]; // ��ͷ������Ϊ��Ҫ��set��������
		// ���ڴ洢��������
		String[] fieldTypes = new String[columnCount];
		// ��ñ�ͷ�ж���
		HSSFRow titleRow = sheet.getRow(0);
		// ����
		for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) { // ������ͷ��
			String data = titleRow.getCell(columnIndex).toString(); // ĳһ�е�����
			schema.add(data);
		}
		// ���ж�ȡ���� ��1��ʼ ���Ա�ͷ
		for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
			// ����ж���
			HSSFRow row = sheet.getRow(rowIndex);
			if (row != null) {
                 List<String> row1=new ArrayList<String>();
				// ��ñ����и���Ԫ���е�����
				for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
					if(row.getCell(columnIndex)!=null)
					{
						String data = row.getCell(columnIndex).toString();
						row1.add(data);
					}
					else
						row1.add(" ");
					// ��ȡҪ���÷����ķ�����
					
				}
				rowContent.add(row1);
			}
		}
		for(int columnIndex=0;columnIndex<columnCount;columnIndex++)
		{
			List<String> column=new ArrayList<String>();
			for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
				// ����ж���
				HSSFRow row = sheet.getRow(rowIndex);
				if(row.getCell(columnIndex)!=null)
				{
					String data = row.getCell(columnIndex).toString();
					column.add(data);
				}
				else
					column.add(" ");
				
			}
			columnContent.add(column);
		}
		entity=columnContent.get(keyColumnIndex);
		for(int i=0;i<schema.size();i++)
    	{
    		if(i!=keyColumnIndex)
    		{
    			attributes.add(schema.get(i));
    		}
    	}
//		System.out.println(attributes);
		result.setAbpath(abpath);
		result.setAttributes(attributes);
		result.setColumnContent(columnContent);
		result.setEntity(entity);
		result.setId(id);
		result.setKeyColumnIndex(keyColumnIndex);
		result.setRowContent(rowContent);
		result.setSchema(schema);
		return result;
	}

	public static void main(String[] args) {
		
		// �ж��ļ��Ƿ����
//		System.out.println(em.fileExist("DataSet\\Experiments\\test2.xls"));
		// �����ļ�
//		TableBean t=ReadJson.ReadJsonFile("DataSet\\Experiments\\1438042988061.16_20150728002308-00051-ip-10-236-191-2_475459181_1.json");
//		List<String> l=t.getSchema();
//		int keyColumn=t.getKeyColumnIndex();
//		String title[] = l.toArray(new String[l.size()]);
//		em.createExcel("DataSet\\Experiments\\test2.xls", "sheet1", title,keyColumn);
//		// �ж�sheet�Ƿ����
//		System.out.println(em.sheetExist("DataSet\\Experiments\\test2.xls", "sheet1"));
		// д�뵽excel
//		List<List<String>> RowContent=t.getRowContent();
//		for(int i=0;i<RowContent.size();i++)
//		{
//			em.writeToExcel("DataSet\\Experiments\\test2.xls", "sheet1", RowContent.get(i),keyColumn);
//		}
		// ��ȡexcel
//
		ExcelManage em = new ExcelManage();
		TableBean table=em.readFromExcel("DataSet\\Experiments\\Books_Excel\\1438042988061.16_20150728002308-00296-ip-10-236-191-2_435282719_1.json.xls", "sheet1");
//		List<List<String>> rowContent=table.getRowContent();
//		for(int i=0;i<rowContent.size();i++)
//		{
//			System.out.println(rowContent.get(i));
//		}

		System.out.println(table.getEntity());
		System.out.println(table.getSchema());
//		for (int i = 0; i < list.size(); i++) {
//			User newUser = (User) list.get(i);
//			System.out.println(newUser.getId() + " " + newUser.getName() + " " + newUser.getPassword());
//		}
		// ɾ���ļ�
		// System.out.println(em.deleteExcel("E:/test2.xls"));
	}

}
