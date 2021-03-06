package org.chai.kevin.dsr

import org.chai.kevin.data.Summ
import org.chai.kevin.data.Type
import org.chai.location.DataLocationType
import org.chai.location.Location
import org.chai.location.LocationLevel
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.util.Utils
import org.chai.kevin.dashboard.DashboardIntegrationTests
import org.chai.kevin.dashboard.DashboardTarget
import org.chai.kevin.reports.ReportService.ReportType

class DsrControllerSpec extends DsrIntegrationTests {

	def dsrController
	
	def "view dashboard with nothing"() {
		setup:
		dsrController = new DsrController()
		
		when:
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl == null
		model.dsrTable == null
	}

	
	def "get dsr for table"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, program, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, category)
		def reportType = ReportType.TABLE
		
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]		
		dsrController.params.dsrCategory = category.id
		dsrController.params.reportType = reportType.toString().toLowerCase()
		def model = dsrController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentProgram.equals(program)
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.locationSkipLevels.equals(s([LocationLevel.findByCode(SECTOR)]))
		model.currentCategory.equals(category)
		model.currentIndicators.equals(null)
		model.currentView.equals(reportType)
		model.dsrTable != null
	}
	
	def "get dsr for map"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, program, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, category)
		def reportType = ReportType.MAP
		
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = category.id
		dsrController.params.indicators = s([target.id])
		dsrController.params.reportType = reportType.toString().toLowerCase()
		def model = dsrController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentProgram.equals(program)
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.locationSkipLevels.equals(s([LocationLevel.findByCode(SECTOR)]))
		model.currentCategory.equals(category)
		model.currentIndicators.equals(s([target]))
		model.currentView.equals(reportType)
		model.dsrTable != null
	}
	
	def "get dsr with no category, redirect"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CATEGORY1, program, 0)
		def target = newDsrTarget(CODE(4), 1, dataElement, category)
		def reportType = ReportType.TABLE
		
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.reportType = reportType.toString().toLowerCase()
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(reportType.toString().toLowerCase())
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get dsr with no report type, redirect"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CATEGORY1, program, 0)
		def target = newDsrTarget(CODE(4), 1, dataElement, category)
		
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = category.id
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(ReportType.MAP.toString().toLowerCase())
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}

	def "get dsr with no targets"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, program, 0)
		def reportType = ReportType.TABLE
		
		when:
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(BURERA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = category.id
		dsrController.params.reportType = reportType.toString().toLowerCase()
		def model = dsrController.view()
		
		then:
		model.currentPeriod.id == period.id
		model.currentProgram.id == program.id
		model.currentLocation.id == Location.findByCode(BURERA).id
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.locationSkipLevels.equals(s([LocationLevel.findByCode(SECTOR)]))
		model.currentCategory.id == category.id
		model.currentIndicators.equals(null)
		model.currentView.equals(reportType)
		model.dsrTable != null
		model.dsrTable.hasData() == false
	}
			
	def "get dsr with no parameters, redirect to period, program, location, category, and report type"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, program, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, category)
		
		when: "no parameters"
		dsrController = new DsrController()
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(ReportType.MAP.toString().toLowerCase())
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}	
	
	def "get dsr with invalid parameters, redirect to default period, root program, root location, location types, category, and report type"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, program, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, category)
		
		when: "invalid parameters"
		dsrController = new DsrController()
		dsrController.params.period = -1
		dsrController.params.program = -1
		dsrController.params.location = -1
		dsrController.params.dsrCategory = -1
		dsrController.params.dataLocationTypes = [-1, -2]
		dsrController.params.reportType = "yourmom"
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(ReportType.MAP.toString().toLowerCase())
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}		
	
	def "get dsr with invalid parameters, redirect with correct parameter"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, program, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, category)
		
		when: "valid location parameter"
		dsrController = new DsrController()
		dsrController.params.period = -1
		dsrController.params.program = -1
		dsrController.params.location = Location.findByCode(BURERA).id
		dsrController.params.dsrCategory = -1
		dsrController.params.dataLocationTypes = [-1, -2]
		dsrController.params.reportType = "yourmom"
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(ReportType.MAP.toString().toLowerCase())
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(BURERA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get dsr with invalid parameters corresponding to dashboard, redirect with correct parameter"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		DashboardIntegrationTests.setupDashboardTree()
		def category = newDsrTargetCategory(CATEGORY1, ReportProgram.findByCode(ROOT), 0)
		def sum = Summ.findByCode('CODE2')
		def target = newDsrTarget(CODE(4), 1, sum, category)
		def reportType = ReportType.TABLE
		
		when:
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = ReportProgram.findByCode(ROOT).id
		dsrController.params.location = Location.findByCode(BURERA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = DashboardTarget.findByCode(TARGET1)
		dsrController.params.reportType = reportType.toString().toLowerCase()
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(reportType.toString().toLowerCase())
		dsrController.response.redirectedUrl.contains(period.id+"/"+ReportProgram.findByCode(ROOT).id+"/"+Location.findByCode(BURERA).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get dsr with invalid parameters, redirect with correct parameter, and default period, root program, root location, location types, category and indicators"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, program, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, category)
		def reportType = ReportType.MAP
		
		when: "invalid parameters"
		dsrController = new DsrController()
		dsrController.params.period = -1
		dsrController.params.program = -1
		dsrController.params.location = -1
		dsrController.params.dsrCategory = -1
		dsrController.params.dataLocationTypes = [-1, -2]
		dsrController.params.reportType = reportType.toString().toLowerCase()
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(reportType.toString().toLowerCase()+"?")
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
		dsrController.response.redirectedUrl.contains("indicators="+target.id)
	}
	
	def "get dsr for only district hospitals"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, program, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, category)
		def reportType = ReportType.TABLE
		
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = category.id
		dsrController.params.dsrTarget = target.id
		dsrController.params.reportType = reportType.toString().toLowerCase()
		def model = dsrController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentProgram.equals(program)
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.locationSkipLevels.equals(s([LocationLevel.findByCode(SECTOR)]))
		model.currentCategory.equals(category)
		model.currentView.equals(reportType)
		model.dsrTable != null
	}

}