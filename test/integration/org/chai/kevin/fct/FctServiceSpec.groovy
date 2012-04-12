package org.chai.kevin.fct

import org.chai.kevin.Period;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;

class FctServiceSpec extends FctIntegrationTests { 

	def fctService
	def locationService
	
	def "normal fct service"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
		def program = newReportProgram(CODE(2))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def target = newFctTarget(CODE(3), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def targetOption = newFctTargetOption(CODE(4), 1, target, sum)
		def location = Location.findByCode(RWANDA)
		def level = locationService.getLevelAfter(location.getLevel(), new HashSet([LocationLevel.findByCode(SECTOR)]))
		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def fctTable = null
		refresh()
		
		when:
		fctTable = fctService.getFctTable(location, program, target, period, level, dataLocationTypes)
		
		then:
		fctTable.getReportValue(Location.findByCode(NORTH), targetOption).numberValue == 2d
		
		when:
		dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)])
		fctTable = fctService.getFctTable(location, program, target, period, level, dataLocationTypes)
		
		then:
		fctTable.getReportValue(Location.findByCode(NORTH), targetOption).numberValue == 1d
	}
		
	def "normal fct service with dummy location"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
		def program = newReportProgram(CODE(2))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def target = newFctTarget(CODE(3), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def targetOption = newFctTargetOption(CODE(4), 1, target, sum)
		def location = Location.findByCode(RWANDA)
		def level = locationService.getLevelAfter(location.getLevel(), new HashSet([LocationLevel.findByCode(SECTOR)]))
		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def fctTable = null
		
		when:
		def dummy = newLocation("dummy", location, level)
		refresh()
		fctTable = fctService.getFctTable(location, program, target, period, level, dataLocationTypes)
		
		then:
		fctTable.getReportValue(Location.findByCode("dummy"), targetOption) == null
		fctTable.getReportValue(Location.findByCode(NORTH), targetOption).numberValue == 2d
				
	}
	
	//TODO fix tests
//	def "get fct with sorted target options"(){
//		setup:
//		setupLocationTree()
//		def period = newPeriod()
//		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
//		def program = newReportProgram(CODE(2))
//		def sum = newSum("\$"+normalizedDataElement.id, CODE(3))
//		def target = newFctTarget(CODE(4), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
//		def targetOption1 = newFctTargetOption(CODE(5), 1, target, sum)
//		def targetOption2 = newFctTargetOption(CODE(6), 2, target, sum)
//		refresh()
//		def location = Location.findByCode(RWANDA)
//		def level = locationService.getLevelAfter(location.getLevel(), new HashSet([LocationLevel.findByCode(SECTOR)]))
//		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
//		
//		when:
//		def fctTable = fctService.getFctTable(location, program, target, period, level, dataLocationTypes)
//		
//		then:
//		fctTable.targetOptions[0].equals(FctTargetOption.findByCode(CODE(5)))
//		fctTable.targetOptions[1].equals(FctTargetOption.findByCode(CODE(6)))
//		
//		when:
//		FctTargetOption.findByCode(CODE(5)).order = 2
//		FctTargetOption.findByCode(CODE(6)).order = 1
//		refresh()
//		fctTable = fctService.getFctTable(location, program, target, period, level, dataLocationTypes)
//		
//		then:
//		fctTable.targetOptions[0].equals(FctTargetOption.findByCode(CODE(6)))
//		fctTable.targetOptions[1].equals(FctTargetOption.findByCode(CODE(5)))
//	}

//	def "get fct with sorted targets"(){
//		setup:
//		setupLocationTree()
//		def period = newPeriod()
//		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
//		def program = newReportProgram(CODE(2))
//		def sum = newSum("\$"+normalizedDataElement.id, CODE(3))
//		def target1 = newFctTarget(CODE(4), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
//		def targetOption1 = newFctTargetOption(CODE(5), 1, target1, sum)
//		def target2 = newFctTarget(CODE(6), 2, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
//		def targetOption2 = newFctTargetOption(CODE(7), 1, target2, sum)
//		refresh()
//		def location = Location.findByCode(RWANDA)
//		def level = locationService.getLevelAfter(location.getLevel(), new HashSet([LocationLevel.findByCode(SECTOR)]))
//		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
//		
//		when:
//		def fctTable = fctService.getFctTable(location, program, target1, period, level, dataLocationTypes)
//		
//		then:
//		fctTable.targets[0].equals(FctTarget.findByCode(CODE(4)))
//		fctTable.targets[1].equals(FctTarget.findByCode(CODE(6)))
//		
//		when:
//		FctTarget.findByCode(CODE(4)).order = 2
//		FctTarget.findByCode(CODE(6)).order = 1
//		fctTable = fctService.getFctTable(location, program, target1, period, level, dataLocationTypes)
//		
//		then:
//		fctTable.targets[0].equals(FctTarget.findByCode(CODE(6)))
//		fctTable.targets[1].equals(FctTarget.findByCode(CODE(4)))
//	}
		
	def "fct get max report value"() {
		setup:
		setupLocationTree()
		def period = newPeriod()		
		def program = newReportProgram(CODE(1))		
		def location = Location.findByCode(RWANDA)
		def level = locationService.getLevelAfter(location.getLevel(), new HashSet([LocationLevel.findByCode(SECTOR)]))
		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def fctTable = null
		refresh()
		
		when:
		def lessThan100 = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
		def sum = newSum("\$"+lessThan100.id, CODE(3))
		def target = newFctTarget(CODE(4), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def targetOption = newFctTargetOption(CODE(5), 1, target, sum)
		refresh()
		fctTable = fctService.getFctTable(location, program, target, period, level, dataLocationTypes)
		
		then:
		fctTable.getMaxReportValue() == 2d
		
		when:
		def moreThan100 = newNormalizedDataElement(CODE(6), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"50", (HEALTH_CENTER_GROUP):"51"]]))
		sum = newSum("\$"+moreThan100.id, CODE(7))
		target = newFctTarget(CODE(8), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		targetOption = newFctTargetOption(CODE(9), 1, target, sum)
		refresh()
		fctTable = fctService.getFctTable(location, program, target, period, level, dataLocationTypes)
		
		then:
		fctTable.getMaxReportValue() == 101d
	}

	def "get fct skip levels"(){
		setup:
		setupLocationTree()
		
		when:
		def fctSkipLevels = fctService.getSkipLocationLevels()
		
		then:
		fctSkipLevels.equals(s([LocationLevel.findByCode(SECTOR)]))
	}
	
}
