package org.chai.kevin.survey

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.value.ExpressionValue;
import org.hibernate.FlushMode;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.chai.kevin.util.Utils;
import org.chai.kevin.data.DataElement
import org.chai.kevin.survey.validation.SurveyEnteredObjective
import org.chai.kevin.survey.validation.SurveyEnteredSection
import org.chai.kevin.survey.validation.SurveyEnteredValue
import org.hibernate.criterion.Restrictions
import org.hisp.dhis.organisationunit.OrganisationUnit

class SurveyElementService {
	
	OrganisationService organisationService;
	def sessionFactory;
	
	SurveyElement getSurveyElement(Long id) {
		return SurveyElement.get(id)
	}

	SurveyQuestion getSurveyQuestion(Long id) {
		return sessionFactory.currentSession.get(SurveyQuestion.class, id)
	}
	
	void save(SurveyEnteredObjective surveyEnteredObjective) {
		if (log.isDebugEnabled()) log.debug("save(surveyEnteredObjective=${surveyEnteredObjective}})")
		surveyEnteredObjective.save();
	}

	void save(SurveyEnteredValue surveyEnteredValue) {
		if (log.isDebugEnabled()) log.debug("save(surveyEnteredValue=${surveyEnteredValue}})")
		surveyEnteredValue.save();
	}
	
	void save(SurveyEnteredSection enteredSection) {
		enteredSection.save();
	}
	
	void delete(SurveyEnteredValue surveyEnteredValue) {
		surveyEnteredValue.delete();
	}
	
	SurveyEnteredSection getSurveyEnteredSection(SurveySection surveySection, OrganisationUnit organisationUnit) {
		def c = SurveyEnteredSection.createCriteria()
		c.add(Restrictions.naturalId()
			.set("organisationUnit", organisationUnit)
			.set("section", surveySection)
		)
		.setCacheable(true)
		.setFlushMode(FlushMode.MANUAL)
		.setCacheRegion("org.hibernate.cache.SurveyEnteredSectioneQueryCache")
		.uniqueResult();
	}
	
	SurveyEnteredObjective getSurveyEnteredObjective(SurveyObjective surveyObjective, OrganisationUnit organisationUnit) {
		def c = SurveyEnteredObjective.createCriteria()
		c.add(Restrictions.naturalId()
				.set("organisationUnit", organisationUnit)
				.set("objective", surveyObjective)
				)
				.setCacheable(true)
				.setFlushMode(FlushMode.MANUAL)
				.setCacheRegion("org.hibernate.cache.SurveyEnteredObjectiveQueryCache")
				.uniqueResult();
	}

	SurveyEnteredValue getSurveyEnteredValue(SurveyElement surveyElement, OrganisationUnit organisationUnit) {
		def c = SurveyEnteredValue.createCriteria()
		c.add(Restrictions.naturalId()
				.set("organisationUnit", organisationUnit)
				.set("surveyElement", surveyElement)
				)
				.setCacheable(true)
				.setFlushMode(FlushMode.MANUAL)
				.setCacheRegion("org.hibernate.cache.SurveyEnteredValueQueryCache")
				.uniqueResult();
	}

	Set<SurveySkipRule> getSkipRules(SurveyElement surveyElement) {
		Survey survey = surveyElement.getSurvey();
		Set<SurveySkipRule> result = new HashSet<SurveySkipRule>();
		survey.skipRules.each { rule ->
			if (rule.skippedSurveyElements.contains(surveyElement)) result.add(rule)	
		}
		return result;
//		def c = SurveySkipRule.createCriteria()
//		.createAlias("skippedSurveyElements", "se")
//		.add(Restrictions.eq("se.id", surveyElement.id))
//		.list();
	}

	Set<SurveySkipRule> getSkipRules(SurveyQuestion surveyQuestion) {
		Survey survey = surveyQuestion.getSurvey();
		Set<SurveySkipRule> result = new HashSet<SurveySkipRule>();
		survey.skipRules.each { rule ->
			if (rule.skippedSurveyQuestions.contains(surveyQuestion)) result.add(rule)
		}
		return result;
//		def c = SurveySkipRule.createCriteria()
//		.createAlias("skippedSurveyQuestions", "sq")
//		.add(Restrictions.eq("sq.id", surveyQuestion.id))
//		.list();
	}

	Set<SurveyElement> getSurveyElements(DataElement dataElement) {
		SurveyElement.createCriteria()
				.add(Restrictions.eq("dataElement", dataElement))
				.list();
	}

	Integer getTotalOrgUnitApplicable(SurveyElement surveyElement){
		Set<String> uuIds = surveyElement.getOrganisationUnitGroupApplicable();
		Integer number = 0;
		for(String uuId: uuIds)
			number = number+organisationService.getNumberOfOrganisationForGroup(OrganisationUnitGroup.findByUuid(uuId));
		return number;
	}
	
}
