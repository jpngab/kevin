package org.chai.kevin.fct;
/**
 * @author Jean Kahigiso M.
 *
 */

import grails.plugin.springcache.annotations.CacheFlush;
import org.chai.kevin.AbstractEntityController
import org.chai.kevin.data.Data;

class FctTargetOptionController extends AbstractEntityController {

	def dataService;
	
	def getEntity(def id) {
		return FctTargetOption.get(id)
	}

	def createEntity() {
		return new FctTargetOption()
	}

	def getLabel() {
		return "fct.targetoption.label"
	}
	
	def getTemplate() {
		return "/entity/fct/createTargetOption"
	}

	def getModel(def entity) {
		[
			targetOption: entity,
			targets: FctTarget.list(),
			sums: entity.sum!=null?[entity.sum]:[]
		]
	}

	def getEntityClass(){
		return FctTargetOption.class;
	}
	
//	def validateEntity(def entity) {
//		return entity.validate()
//	}

	def saveEntity(def entity) {
		entity.save();
	}

	def deleteEntity(def entity) {	
		entity.target.targetOptions.remove(entity)
		entity.target.save()
		entity.delete()
	}

	def bindParams(def entity) {
		entity.properties = params
		if (params.int('data.id')) entity.data = dataService.getData(params.int('data.id'), Data.class)
	}
	
	@CacheFlush("fctCache")
	def save = {
		super.save()
	}
	
	@CacheFlush("fctCache")
	def delete = {
		super.delete()
	}
	
	@CacheFlush("fctCache")
	def edit = {
		super.edit()
	}
	
	def search = {
		adaptParamsForList()
		
		def targetOptions = dataService.searchData(FctTargetOption.class, params['q'], [], params);
		
		render (view: '/entity/list', model:[
			entities: targetOptions,
			entityCount: targetOptions.totalCount,
			entityClass: getEntityClass(),
			template: "fct/targetOptionList",
			code: getLabel(),
			search: true
		])
	}
	
	def list = {
		adaptParamsForList()				
		List<FctTargetOption> targetOptions = FctTargetOption.list(params);
		
		render (view: '/entity/list', model:[
			entities: targetOptions,
			template: "fct/targetOptionList",
			code: getLabel(),
			entityCount: FctTargetOption.count(),
			entityClass: getEntityClass()
		])
	}
	
}
