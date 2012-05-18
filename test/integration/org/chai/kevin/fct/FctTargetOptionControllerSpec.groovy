package org.chai.kevin.fct

public class FctTargetOptionControllerSpec extends FctIntegrationTests {

	def fctTargetOptionController
	def locationService
	
	def "save targetOption option"() {
		setup:
		def program = newReportProgram(CODE(1))
		def sum = newSum("1", CODE(3))		
		def target = newFctTarget(CODE(4), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		
		fctTargetOptionController = new FctTargetOptionController()
		
		when:
		fctTargetOptionController.params.code = CODE(2)
		fctTargetOptionController.params['target.id'] = target.id+""
		fctTargetOptionController.params['sum.id'] = sum.id+""
		fctTargetOptionController.saveWithoutTokenCheck()
		
		then:
		FctTargetOption.count() == 1
		FctTargetOption.list()[0].sum.equals(sum)
	}
	
	def "fct targetOption list"() {
		setup:
		def program = newReportProgram(CODE(1))
		def sum = newSum("1", CODE(3))
		def target = newFctTarget(CODE(4), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def targetOption = newFctTargetOption(CODE(5), 1, target, sum)
		fctTargetOptionController = new FctTargetOptionController()
		
		when:
		fctTargetOptionController.list()
		
		then:
		fctTargetOptionController.modelAndView.model.entities.equals([targetOption])
		fctTargetOptionController.modelAndView.model.entityCount == 1
		
	}
	
}
