package cn.entityaug.table;

import java.util.List;

public class TableBean {
	List<String> schema; //��ͷ��ֻ��ȡ�б�ͷ�ı��json�ļ���"hasHeader"==1��
	List<List<String>> columnContent;  //���д�ű�����
	List<List<String>> rowContent; //���д�ű�����
	List<String> entity;
	List<String> attributes;
	public List<String> getAttrubutes() {
		return attributes;
	}
	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}
	private String pageTitle; //ҳ����⣨������ȡ������
	private String id; //����ļ�����������������ǣ�
	private String url; //��ַ(������ַ���ƶȼ���)
	
	private String contextb; //�����ģ���ҳ�б��ǰ������֣�
	private String contexta; //�����ģ���ҳ�б���������֣�
	
	String abpath; //����ļ�����·��
	int keyColumnIndex; //ָʾ���������ţ���==-1���������У�
	
	//List<Map<String,Double>> listmapsim; //�������
	
	public List<String> getSchema() {
		return schema;
	}
	public void setSchema(List<String> schema) {
		this.schema = schema;
	}
	public List<List<String>> getColumnContent() {
		return columnContent;
	}

	public void setColumnContent(List<List<String>> columnContent) {
		this.columnContent = columnContent;
	}

	public List<List<String>> getRowContent() {
		return rowContent;
	}

	public void setRowContent(List<List<String>> rowContent) {
		this.rowContent = rowContent;
	}
	
	public List<String> getEntity() {
		return entity;
	}
	public void setEntity(List<String> entity) {
		this.entity = entity;
	}
	public String getPageTitle() {
		return pageTitle;
	}
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContextb() {
		return contextb;
	}
	public void setContextb(String contextb) {
		this.contextb = contextb;
	}
	public String getContexta() {
		return contexta;
	}
	public void setContexta(String contexta) {
		this.contexta = contexta;
	}
	public String getAbpath() {
		return abpath;
	}
	public void setAbpath(String abpath) {
		this.abpath = abpath;
	}
	public int getKeyColumnIndex() {
		return keyColumnIndex;
	}
	public void setKeyColumnIndex(int keyColumnIndex) {
		this.keyColumnIndex = keyColumnIndex;
	}

}
