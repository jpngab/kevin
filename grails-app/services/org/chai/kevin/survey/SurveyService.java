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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.DataElement;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.value.DataValue;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class SurveyService {
	private Log log = LogFactory.getLog(SurveyService.class);
	private OrganisationService organisationService;
	private ExpressionService expressionService;
	private Integer organisationLevel;

	@Transactional(readOnly = true)
	public SurveyPage getSurvey(Period currentPeriod,
			Organisation currentOrganisation, SurveySubSection currentSection) {
		Map<SurveyQuestion, Map<DataElement, DataValue>> values = null;
		log.info("====>currentPeriod" + currentPeriod
				+ "====>currentOrganisation" + currentOrganisation
				+ "====>All Sections:" + currentSection);

//		if (currentSection != null) {
//			for (SurveySubSection subSection : currentSection.getSubSections()) {
//				Map<DataElement, DataValue> dataElementValue = null;
//				for (SurveyQuestion question : subSection.getQuestions()) {
//					if (question.getTemplate().equals("singleQuestion")) {
//						// dataElementValue.put((SurveySingleQuestion)question.,
//						// arg1);
//					}
//					if (question.getTemplate().equals("checkboxQuestion")) {
//
//					}
//					if (question.getTemplate().equals("tableQuestion")) {
//
//					}
//				}
//
//			}
//		}

		log.info("====>currentPeriod" + currentPeriod
				+ "====>currentOrganisation" + currentOrganisation
				+ "====>All Sections:" + currentSection);
		return new SurveyPage(currentPeriod, currentOrganisation,
				currentSection, values);
	}

	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}

	public OrganisationService getOrganisationService() {
		return organisationService;
	}

	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}

	public ExpressionService getExpressionService() {
		return expressionService;
	}

	public void setOrganisationLevel(Integer organisationLevel) {
		this.organisationLevel = organisationLevel;
	}

	public Integer getOrganisationLevel() {
		return organisationLevel;
	}

}
