/**
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
package org.chai.kevin.survey
import org.chai.kevin.AbstractEntityController
import org.chai.location.DataLocationType;
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * @author Jean Kahigiso M.
 *
 */
class SectionController extends AbstractEntityController {

	def surveyService
	
	def getEntity(def id) {
		return SurveySection.get(id)
	}
	
	def createEntity() {
		return new SurveySection()
	}

	def getTemplate() {
		return "/survey/admin/createSection"
	}
	
	def getLabel() {
		return 'survey.section.label'
	}

	def getModel(def entity) {
		[
			section: entity,
			programs: entity.program.survey.programs,
			types: DataLocationType.list([cache: true])
		]
	}

	def getEntityClass(){
		return SurveySection.class;
	}
	
	def bindParams(def entity) {
		entity.properties = params
	}
	
	def deleteEntity(def entity) {
		surveyService.deleteSection(entity)
	}
	
	def list = {
		adaptParamsForList()
		
		SurveyProgram program = SurveyProgram.get(params.int('program.id'))
		if (program == null) {
			response.sendError(404)
		}
		else {
			def sections = SurveySection.createCriteria().list(params){eq('program', program)}
	
			render (view: '/entity/list', model:[
				template:"survey/sectionList",
				survey: program.survey,
				program: program,
				entities: sections,
				entityCount: sections.totalCount,
				code: getLabel(),
				entityClass: getEntityClass()
			])
		}
	}

}

