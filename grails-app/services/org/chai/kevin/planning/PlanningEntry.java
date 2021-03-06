package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.data.Enum;
import org.chai.kevin.form.FormElement.ElementCalculator;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.location.DataLocation;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;

public class PlanningEntry {

	protected PlanningType type;
	private DataLocation dataLocation;
	private Integer lineNumber;
	private Map<String, Enum>  enums;
	private FormEnteredValue enteredValue;
	
	public PlanningEntry() {
		// allows mocking
	}
	
	public PlanningEntry(FormEnteredValue enteredValue, Integer lineNumber) {
		this.enteredValue = enteredValue;
		this.lineNumber = lineNumber;
		this.type = null;
		this.enums = null;
		this.dataLocation = null;
	}
	
	public PlanningEntry(DataLocation dataLocation, PlanningType type, FormEnteredValue enteredValue, Integer lineNumber, Map<String, Enum> enums) {
		this.dataLocation = dataLocation;
		this.enteredValue = enteredValue;
		this.type = type;
		this.lineNumber = lineNumber;
		this.enums = enums;
	}

	public Set<String> getInvalidSections() {
		Set<String> result = new HashSet<String>();
		for (String section : type.getSections()) {
			if (!getValidatable().isTreeValid(PlanningUtils.getPrefix(section, lineNumber))) result.add(section);
		}
		return result;
	}
	
	public Set<String> getIncompleteSections() {
		Set<String> result = new HashSet<String>();
		for (String section : type.getSections()) {
			if (!getValidatable().isTreeComplete(PlanningUtils.getPrefix(section, lineNumber))) result.add(section);
		}
		return result;
	}
	
	public String getLineSuffix(String section) {
		return "";
	}
	
	public Integer getLineNumber() {
		return lineNumber;
	}
	
	public ValidatableValue getValidatable() {
		return enteredValue.getValidatable();
	}
	
	public String getPrefix(String prefix) {
		return PlanningUtils.getPrefix(prefix, lineNumber);
	}
	
	public Value getValue(String prefix) {
		return enteredValue.getValidatable().getType().getValue(enteredValue.getValidatable().getValue(), PlanningUtils.getPrefix(prefix, lineNumber));
	}
	
	public Value getFixedHeaderValue() {
		return getValue(type.getFixedHeader());
	}
	
	public void mergeValues(Map<String, Object> params) {
		params.put("elements["+type.getFormElement().getId()+"].value", getLineNumbers(null));
		getValidatable().mergeValue(params, "elements["+type.getFormElement().getId()+"].value", new HashSet<String>());
	}

	public void delete() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("elements["+type.getFormElement().getId()+"].value", getLineNumbers(lineNumber));
		getValidatable().mergeValue(params, "elements["+type.getFormElement().getId()+"].value", new HashSet<String>());
	}
	
	private List<String> getLineNumbers(Integer skip) {
		List<String> result = new ArrayList<String>();
		Integer linesInValue = enteredValue.getValidatable().getValue().isNull()?0:enteredValue.getValidatable().getValue().getListValue().size();
		for (int i = 0; i <= Math.max(linesInValue-1, lineNumber); i++) {
			if (skip == null || i != skip) result.add("["+i+"]");
		}
		return result;
	}
	
	public Map<String, Enum> getEnums() {
		return enums;
	}

	public void evaluateRules(ElementCalculator elementCalculator) {
		type.getFormElement().validate(dataLocation, elementCalculator);
		type.getFormElement().executeSkip(dataLocation, elementCalculator);
	}

}
