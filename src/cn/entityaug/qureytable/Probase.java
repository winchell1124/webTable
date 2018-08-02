package cn.entityaug.qureytable;
/*
 * 定义Probase类，其中存放Probase语义库中的概念、实体以及频率
 */
public class Probase {
	private String concept;
	private String entity;
	private int frequency;
	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
}
