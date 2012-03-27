package org.chai.kevin.form

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.data.Type
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormValidationService.ValidatableLocator
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.value.Value

class FormValidationServiceSpec extends IntegrationTests {

	def formValidationService;
	
	def getLocator() {
		return new ValidatableLocator() {
			public ValidatableValue getValidatable(Long id, DataLocation location) {
				FormElement element = FormElement.get(id)
				FormEnteredValue enteredValue = FormEnteredValue.findByFormElementAndDataLocation(element, location);
				return enteredValue.getValidatable();
			}
		};
	}
	
	def "skip elemnts"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement1 = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(['key1':Type.TYPE_NUMBER(),'key2':Type.TYPE_NUMBER()])))
		def element1 = newFormElement(dataElement1)
		
		def rule = newFormSkipRule("\$"+element1.id+"[_].key1 == 1", [(element1): "[_].key1,[_].key2"])
		
		when:
		newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), new Value("{\"value\": [{\"value\":[{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]},{\"value\":[{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}]}"))
		def skipped = formValidationService.getSkippedPrefix(element1, rule, DataLocation.findByCode(KIVUYE), getLocator())
		
		then:
		skipped.equals(s(["[0].key1","[0].key2","[1].key1","[1].key2"]))
	}
	
	def "false validation based on other elements"() {
		setup:
		setupLocationTree()
		def period = newPeriod()

		def dataElement1 = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def dataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def element1 = newFormElement(dataElement1)
		def element2 = newFormElement(dataElement2)
		
		def validationRule = null
		
		when:
		validationRule = newFormValidationRule(element1, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "\$"+element1.id+" > \$"+element2.id)
		
		newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), v("1"))
		newFormEnteredValue(element2, period, DataLocation.findByCode(KIVUYE), v("1"))
		def prefixes = formValidationService.getInvalidPrefix(validationRule, DataLocation.findByCode(KIVUYE), getLocator())
		
		then:
		prefixes.equals(new HashSet([""]))
	}	
	
	def "no validation errors"() {
		setup:
		setupLocationTree()
		def period = newPeriod()

		def dataElement1 = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def dataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def element1 = newFormElement(dataElement1)
		def element2 = newFormElement(dataElement2)
		
		def validationRule = null
		
		when:
		validationRule = newFormValidationRule(element1, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "\$"+element1.id+" > \$"+element2.id)
		
		newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), v("2"))
		newFormEnteredValue(element2, period, DataLocation.findByCode(KIVUYE), v("1"))
		def prefixes = formValidationService.getInvalidPrefix(validationRule, DataLocation.findByCode(KIVUYE), getLocator())
		
		then:
		prefixes.isEmpty()
	}
	
	def "validation with null elements"() {
		
		setup:
		setupLocationTree()
		def period = newPeriod()

		def dataElement1 = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def dataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def element1 = newFormElement(dataElement1)
		def element2 = newFormElement(dataElement2)
		
		def validationRule = null
		
		when:
		validationRule = newFormValidationRule(element1, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "if (\$"+element1.id+" == 0) \$"+element2.id+" == null else false")
		
		newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), v("0"))
		newFormEnteredValue(element2, period, DataLocation.findByCode(KIVUYE), Value.NULL_INSTANCE())
		def prefixes = formValidationService.getInvalidPrefix(validationRule, DataLocation.findByCode(KIVUYE), getLocator())
		
		then:
		prefixes.isEmpty()
	}

	def "validation on certain unit types only"() {
		setup:
		setupLocationTree()
		def period = newPeriod()

		def dataElement1 = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element1 = newFormElement(dataElement1)
		
		def validationRule = newFormValidationRule(element1, "", [(DISTRICT_HOSPITAL_GROUP)], "\$"+element1.id+" > 0")
		def prefixes = null
		
		when:
		newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), v("0"))
		prefixes = formValidationService.getInvalidPrefix(validationRule, DataLocation.findByCode(KIVUYE), getLocator())
		
		then:
		prefixes.isEmpty()
		
		when:
		newFormEnteredValue(element1, period, DataLocation.findByCode(BUTARO), v("0"))
		prefixes = formValidationService.getInvalidPrefix(validationRule, DataLocation.findByCode(BUTARO), getLocator())
		
		then:
		prefixes.equals(new HashSet([""]))
	}
		
}