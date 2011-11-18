package org.chai.kevin

import grails.validation.ValidationException;

import org.chai.kevin.data.Average;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class NormalizedDataElementSpec extends IntegrationTests {

	def "expression type is valid"() {
		when:
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap:e([:])).save(failOnError: true)
		
		then:
		NormalizedDataElement.count() == 1
		NormalizedDataElement.list()[0].type.jsonValue == "{\"type\":\"number\"}";
	}

	
	def "expression type cannot be invalid"() {
		when:
		new NormalizedDataElement(code: CODE(1), type: INVALID_TYPE, expressionMap:e([:])).save(failOnError: true)
		
		then:
		thrown ValidationException
	}

	def "expression type cannot be null"() {
		when:
		def normalizedDataElement = new NormalizedDataElement(code: CODE(1), expressionMap:e([:])).save(failOnError: true)

		then:
		thrown ValidationException

	}

	def "expression code is unique"() {
		when:
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap:e([:])).save(failOnError: true)

		then:
		NormalizedDataElement.count();

		when:
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap:e([:])).save(failOnError: true)

		then:
		thrown ValidationException

	}
	
	def "expression value hashcode and equals"() {
		setup:
		def organisationUnit = newOrganisationUnit(name: BUTARO)
		def period = newPeriod()
		def expression = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))

		when:
		def expr1 = new NormalizedDataElementValue(normalizedDataElement: normalizedDataElement, period: period, organisationUnit: organisationUnit);
		def expr2 = new NormalizedDataElementValue(normalizedDataElement: normalizedDataElement, period: period, organisationUnit: organisationUnit);

		then:
		expr1.hashCode() == expr2.hashCode();
		expr1.equals(expr2)
		expr2.equals(expr1)

		when:
		def set = new HashSet()
		set.add(expr1)

		then:
		set.contains(expr2)
	}

	def "invalid expression"() {
		when:
		new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: e(["1":["DH":formula]])).save(failOnError: true)

		then:
		thrown ValidationException

		where:
		formula << [
			"if((123) 1 else 0",
			"if(3) 3",
			"if(\$328==1 || \$286==1 || \$277==1 || \$215==1) \"&#10003;\" else \"NEGS\""
		]
	}

	
	//	def "expression can be a constant"() {
	//		setup:
	//		IntegrationTestInitializer.createConstants()
	//
	//		when:
	//		new newExpression(names:j(["en":"Expression"]), code:"EXPR", type:Type.TYPE_NUMBER(), expression:"["+Constant.findByCode("CONST1").id+"]").save(failOnError:true)
	//
	//		then:
	//		Expression.count() == 1;
	//
	//	}
	
	
	//	def "expression date is updated on save"() {
	//		setup:
	//		new newExpression(code:"CODE", expression: "1", type: Type.TYPE_NUMBER(), timestamp: new Date()).save(failOnError: true)
	//
	//		when:
	//		def expression = Expression.findByCode("CODE");
	//		def oldDate = expression.timestamp
	//
	//		then:
	//		oldDate != null
	//
	//		when:
	//		expression.save(failOnError: true)
	//		expression = Expression.findByCode("CODE");
	//		def newDate = expression.timestamp
	//
	//		then:
	//		!oldDate.equals(newDate)
	//
	//	}
}
