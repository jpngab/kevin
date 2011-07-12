import grails.util.GrailsUtil;

import org.chai.kevin.TimestampListener;
import org.chai.kevin.ValueService;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.InfoService;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.chart.ChartService;
import org.chai.kevin.cost.CostTableService;
import org.chai.kevin.dashboard.DashboardController;
import org.chai.kevin.dashboard.DashboardService;
import org.chai.kevin.dashboard.ExplanationCalculator;
import org.chai.kevin.dashboard.PercentageCalculator;
import org.chai.kevin.dsr.DsrService;
import org.chai.kevin.maps.MapsService;
import org.chai.kevin.survey.SurveySectionService;
import org.chai.kevin.survey.SurveyService;
import org.chai.kevin.survey.SurveyAdminService;
import org.springframework.format.number.PercentFormatter;

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.codehaus.groovy.grails.orm.hibernate.HibernateEventListeners;
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

def config = CH.config

String facilityTypeGroup = config.facility.type.group
Set<Integer> dashboardSkipLevels = config.dashboard.skip.levels
Set<Integer> costSkipLevels = config.dashboard.skip.levels
int organisationLevel = config.facility.level
int infoGroupLevel = config.info.group.level

beans = {
	
	surveyAdminService(SurveyAdminService){
		
	}

	surveyService(SurveyService){
		valueService = ref("valueService")
	}
	
	chartService(ChartService){
		valueService = ref("valueService")
		periodService = ref("periodService")
	}

	dsrService(DsrService){
		organisationService = ref("organisationService")
		valueService = ref("valueService")
	}

	mapsService(MapsService) {
		organisationService = ref("organisationService")
		valueService = ref("valueService")
		infoService = ref("infoService")
	}

	costTableService(CostTableService) {
		costService = ref("costService")
		organisationService = ref("organisationService")
		valueService = ref("valueService")
		skipLevels = costSkipLevels
	}
	
	valueService(ValueService) {
		sessionFactory = ref("sessionFactory")
	}
	
	expressionService(ExpressionService) {
		dataService = ref("dataService")
		organisationService = ref("organisationService")
		valueService = ref("valueService")
	}
	
	infoService(InfoService) {
		expressionService = ref("expressionService")
		valueService = ref("valueService")
		organisationService = ref("organisationService")
		groupLevel = infoGroupLevel
	}

	dashboardService(DashboardService) {
		organisationService = ref("organisationService")
		infoService = ref("infoService")
		expressionService = ref("expressionService")
		valueService = ref("valueService")
		skipLevels = dashboardSkipLevels
	}

	organisationService(OrganisationService) {
		group = facilityTypeGroup
		organisationUnitService = ref("organisationUnitService")
		organisationUnitGroupService = ref("organisationUnitGroupService")
		facilityLevel = organisationLevel
	}

//	timestampListener(TimestampListener)
//	
//	hibernateEventListeners(HibernateEventListeners) {
//		listenerMap = [	'pre-insert': timestampListener,
//						'pre-update': timestampListener]
//	}
	
//	beans = {
//		customPropertyEditorRegistrar(TranslationPropertyEditorRegistrar)
//	}
}