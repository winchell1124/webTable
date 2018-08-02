package cn.entityaug.qureytable;

import java.io.FileNotFoundException;
import java.io.IOException;

import cn.entityaug.excel.ExcelManage;
import cn.entityaug.table.TableBean;

public class ReadQueryTable {
	
	public static QueryTable readQT(String path,String sheetName)throws FileNotFoundException, IOException
	{
		QueryTable qt=new QueryTable();
		ExcelManage em=new ExcelManage();
		TableBean table=em.readFromExcel(path, sheetName);
		qt.setAttributes(table.getAttrubutes());
		qt.setEntity(table.getEntity());
		qt.setRowContent(table.getRowContent());
		qt.setSchema(table.getSchema());
		return qt;
	}
}