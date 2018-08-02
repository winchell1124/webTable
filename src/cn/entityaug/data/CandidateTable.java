package cn.entityaug.data;

import java.util.List;

public class CandidateTable {
	private String id;
	private double score;
	private FillCell fillcell;
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public FillCell getFillcell() {
		return fillcell;
	}
	public void setFillcell(FillCell fillcell) {
		this.fillcell = fillcell;
	}
	
}
