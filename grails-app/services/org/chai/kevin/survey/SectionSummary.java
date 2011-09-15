package org.chai.kevin.survey;

public class SectionSummary implements Comparable<SectionSummary> {

	private SurveySection section;
	
	private int questions;
	private int completedQuestions;
	
	public SectionSummary(SurveySection section, Integer questions, Integer completedQuestions) {
		super();
		this.section = section;
		this.questions = questions;
		this.completedQuestions = completedQuestions;
	}

	public SurveySection getSection() {
		return section;
	}
	
	public Integer getQuestions() {
		return questions;
	}
	
	public Integer getCompletedQuestions() {
		return completedQuestions;
	}
	
	@Override
	public int compareTo(SectionSummary o) {
		if (questions == 0 && o.getQuestions() == 0) return 0;
		if (questions == 0) return -1;
		if (o.getQuestions() == 0) return 1;
		
		double d0 = (double)completedQuestions/(double)questions;
		double d1 = (double)o.getCompletedQuestions()/(double)o.getQuestions();
		
		if (d0 > d1) return 1;
		else if (d1 > d0) return -1;
		return 0;
	}
}
