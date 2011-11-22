package org.chai.kevin;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.chai.kevin.data.Calculation;
import org.chai.kevin.value.CalculationPartialValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.Value;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public abstract class CalculationValue<T extends CalculationPartialValue> implements DataValue {

	private OrganisationUnit organisationUnit;
	private Period period;
	private List<T> calculationPartialValues;
	private Calculation<T> calculation;
	
	public CalculationValue(List<T> calculationPartialValues, Calculation<T> calculation, Period period, OrganisationUnit organisationUnit) {
		this.calculationPartialValues = calculationPartialValues;
		this.calculation = calculation;
		this.period = period;
		this.organisationUnit = organisationUnit;
	}
	
	@Transient
	public abstract Value getValue();
	
	@Override
	public Period getPeriod() {
		return period;
	}
	
	@Override
	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
	}
	
	public boolean isComplete() {
		return getGroupUuids().size() == calculationPartialValues.size();
	}
	
	public Calculation<T> getData() {
		return calculation;
	}
	
	public List<String> getGroupUuids() {
		List<String> groupUuids = new ArrayList<String>();
		for (T calculationPartialValue : calculationPartialValues) {
			groupUuids.add(calculationPartialValue.getGroupUuid());
		}
		return groupUuids;
	}
	
	public List<T> getCalculationPartialValues() {
		return calculationPartialValues;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((calculationPartialValues == null) ? 0
						: calculationPartialValues.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CalculationValue))
			return false;
		CalculationValue other = (CalculationValue) obj;
		if (calculationPartialValues == null) {
			if (other.calculationPartialValues != null)
				return false;
		} else if (!calculationPartialValues
				.equals(other.calculationPartialValues))
			return false;
		return true;
	}

}
