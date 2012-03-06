package org.chai.kevin.survey.validation;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveyProgram;

@Entity(name="SurveyLog")
@Table(name="dhsst_survey_log")
public class SurveyLog {

	private Long id;
	private String event;
	private Date timestamp;
	private DataLocationEntity entity;
	private Survey survey;
	private SurveyProgram program;
	
	public SurveyLog() {}
	
	public SurveyLog(Survey survey, SurveyProgram program, DataLocationEntity entity) {
		this.survey = survey;
		this.program = program;
		this.entity = entity;
	}
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Basic
	public String getEvent() {
		return event;
	}
	
	public void setEvent(String event) {
		this.event = event;
	}
	
	@Basic
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	@ManyToOne(targetEntity=DataLocationEntity.class)
	public DataLocationEntity getEntity() {
		return entity;
	}
	
	public void setEntity(DataLocationEntity entity) {
		this.entity = entity;
	}
	
	@ManyToOne(targetEntity=Survey.class)
	@JoinColumn(nullable=false)
	public Survey getSurvey() {
		return survey;
	}
	
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}
	
	@ManyToOne(targetEntity=SurveyProgram.class)
	public SurveyProgram getProgram() {
		return program;
	}
	
	public void setProgram(SurveyProgram program) {
		this.program = program;
	}
	
}
