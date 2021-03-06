package org.chai.kevin.dsr

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

import grails.plugin.springcache.annotations.CacheFlush;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.AbstractEntityController;

class DsrTargetCategoryController extends AbstractEntityController {
	
	def dataService
	def locationService
	
	def getEntity(def id) {
		return DsrTargetCategory.get(id);
	}
	
	def createEntity() {
		return new DsrTargetCategory();
	}
	
	def getLabel() {
		return "dsr.category.label"
	}
	
	def getTemplate() {
		return "/entity/dsr/createTargetCategory"
	}
	
	def getModel(def entity) {
		[ 
			category: entity,
			programs: ReportProgram.list()
		]
	}
	
	def getEntityClass(){
		return DsrTargetCategory.class;
	}
	
	def deleteEntity(def entity) {
		entity.delete();
	}
	
	@CacheFlush("dsrCache")
	def edit = {
		super.edit()	
	}
	
	@CacheFlush("dsrCache")
	def save = {
		super.save()
	}
	
	@CacheFlush("dsrCache")
	def delete = {
		super.delete()
	}
	
	def bindParams(def entity) {
		entity.properties = params
	}
	
	def search = {
		adaptParamsForList()
		
		def categories = dataService.searchData(DsrTargetCategory.class, params['q'], [], params);
		
		render (view: '/entity/list', model:[
			entities: categories,
			entityCount: categories.totalCount,
			entityClass: getEntityClass(),
			template: "dsr/targetCategoryList",
			code: getLabel(),
			search: true
		])
	}
	
	def list = {
		adaptParamsForList()
		List<DsrTargetCategory> categories = DsrTargetCategory.list(params);
		
		render (view: '/entity/list', model:[
			entities: categories,
			template: "dsr/targetCategoryList",
			code: getLabel(),
			entityCount: DsrTargetCategory.count(),
			entityClass: getEntityClass()
		])
	}

}
