package org.chai.kevin.data

import org.chai.kevin.AbstractController;
import org.chai.kevin.Period;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.Value;
import org.codehaus.groovy.grails.commons.ApplicationHolder;

class DataController extends AbstractController {
	
	def dataService
	def valueService
	def refreshValueService
	
	def getDescription = {
		def data = dataService.getData(params.int('id'), Data.class)

		if (data == null) {
			render(contentType:"text/json") { result = 'error' }
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/entity/data/dataDescription', model: [data: data])
			}
		}
	}
	
	def getData = {
		def clazz = Class.forName('org.chai.kevin.data.'+params['class'], true, Thread.currentThread().contextClassLoader)
		def includeTypes = params.list('include')
		def dataList = dataService.searchData(clazz, params['searchText'], includeTypes, [:]);
		
		render(contentType:"text/json") {
			result = 'success'
			html = g.render(template:'/entity/data/dataList', model:[data: dataList])
		}
	}
	
	def getAjaxData = {
		def clazzes = []
		if (params['class'] != null) clazzes.add Class.forName('org.chai.kevin.data.'+params['class'], true, Thread.currentThread().contextClassLoader)
		if (params['classes'] != null) clazzes.addAll params.list('classes').collect {Class.forName('org.chai.kevin.data.'+it, true, Thread.currentThread().contextClassLoader)}
		def includeTypes = params.list('include')
		
		def dataList = []
		clazzes.each {dataList.addAll dataService.searchData(it, params['term'], includeTypes, [:])};
		
		render(contentType:"text/json") {
			elements = array {
				dataList.each { item ->
					elem (
						key: item.id,
						value: i18n(field:item.names)+' ['+item.code+'] ['+item.class.simpleName+']'
					)
				}
			}
		}
	}

	def deleteValues = {
		def data = dataService.getData(params.int('data'), Data.class)
		if (data == null) {
			response.sendError(404)
		}
		else {
			valueService.deleteValues(data, null, null)
			
			data.setLastValueChanged(new Date())
			dataService.save(data)
			
			refreshValueService.flushCaches()
			
			flash.message = message(code: 'data.values.deleted')
			redirect(uri: getTargetURI())
		}
	}
	
	
	// TODO move to DataElementController
	def search = {
		adaptParamsForList()
		
		def data = dataService.getData(params.int('data'), DataElement.class)
		if (data == null) {
			response.sendError(404)
		}
		else {
			List<Period> periods = Period.list([cache: true])
			def period = Period.get(params.int('period'))
			if (period == null) period = Period.list()[Period.count() - 1]
			
			def values = valueService.searchDataElementValues(
				params.q,
				data,
				null,
				period,
				params
			)
			def valueCount = valueService.countDataElementValues(params.q, data, null, period)
			
			render (view: '/entity/list', model:[
				data: data,
				periods: periods,
				selectedPeriod: period,
				entities: values,
				entityCount: valueCount,
				template: "value/data"+data.class.simpleName+"List",
				code: 'dataelementvalue.label',
				search: true
			])
		}
	}
	
	// TODO move to DataElementController
	def dataElementValueList = {
		adaptParamsForList()
		
		def data = dataService.getData(params.int('data'), DataElement.class)
		if (data == null) {
			response.sendError(404)
		}
		else {
			List<Period> periods = Period.list([cache: true])
			def period = Period.get(params.int('period'))
			if (period == null) period = Period.list()[Period.count() - 1]
			
			def values = valueService.listDataElementValues(
				data,
				null,
				period,
				params
			)
			def valueCount = valueService.countDataElementValues(null, data, null, period)
			
			render (view: '/entity/list', model:[
				data: data,
				periods: periods,
				selectedPeriod: period,
				entities: values,
				entityCount: valueCount,
				template: "value/data"+data.class.simpleName+"List",
				code: 'dataelementvalue.label',
				search: true
			])
		}
	}
	
}
