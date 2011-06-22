package org.chai.kevin.survey;

/* 
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


@SuppressWarnings("serial")
@Entity(name = "SurveySection")
@Table(name = "dhsst_survey_section")
public class SurveySection extends SurveyTranslatable  {
	
	public static enum Completed {
		COMPLETED, INPROGRESS, NOTSTARTED
	};
	
	private Integer id;
	private Integer order;
	private Completed completed = Completed.NOTSTARTED;
	private List<SurveySubSection> subSections = new ArrayList<SurveySubSection>();

	public void setId(Integer id) {
		this.id = id;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Basic
	@Column(name = "ordering")
	public Integer getOrder() {
		return order;
	}

	public void setCompleted(Completed completed) {
		this.completed = completed;
	}

	public Completed getCompleted() {
		return completed;
	}

	public void setSubSections(List<SurveySubSection> subSections) {
		this.subSections = subSections;
	}
    @OneToMany(cascade=CascadeType.ALL, targetEntity=SurveySubSection.class, mappedBy="section")
	public List<SurveySubSection> getSubSections() {
		return subSections;
	}

	@Transient
	public void setSectionCompleted() {
		int i = 0;
		for (SurveySubSection subsection : subSections)
			if (subsection.isStatus() && subsection.toString()=="completed")
				i++;
		if (subSections.size() == i)
			this.setCompleted(Completed.COMPLETED);
		if (subSections.size() > 0 && subSections.size() < i)
			this.setCompleted(Completed.INPROGRESS);
		else
			this.setCompleted(Completed.NOTSTARTED);
	}
	
	public void addSubSection(SurveySubSection subSection) {
		subSection.setSection(this);
		subSections.add(subSection);
	}
	
	@Transient
	public String toString() {
		if (this.completed == Completed.COMPLETED)
			return "completed";
		if (this.completed == Completed.INPROGRESS)
			return "inprogress";
		else
			return "notstarted";

	}

	@Transient
	public Map<SurveySubSection, List<SurveyQuestion>> getAllQuestionsOfSurveySection() {
		Map<SurveySubSection, List<SurveyQuestion>> qSubSection = null;
		if (!subSections.isEmpty()) {
			qSubSection = new HashMap<SurveySubSection, List<SurveyQuestion>>();
			for (SurveySubSection subSection : subSections)
				qSubSection.put(subSection, subSection.getQuestions());
		}

		return qSubSection;

	}
}
