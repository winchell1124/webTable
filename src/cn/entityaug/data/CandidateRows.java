package cn.entityaug.data;

import cn.entityaug.table.TableBean;

public class CandidateRows {

	private String entity;
	private TableBean webTable;
	private double similarity;
	public double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public TableBean getWebTable() {
		return webTable;
	}
	public void setWebTable(TableBean webTable) {
		this.webTable = webTable;
	}
	
}
