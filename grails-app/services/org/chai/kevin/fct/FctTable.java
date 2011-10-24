package org.chai.kevin.fct;

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

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.Organisation;
import org.chai.kevin.dsr.DsrObjective;
import org.chai.kevin.dsr.DsrTarget;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;

@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class FctTable implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Period period;
	private Organisation organisation;
	private FctObjective objective;
	private List<Organisation> organisations;
	private List<FctTarget> targets;
	private Set<OrganisationUnitGroup> facilityTypes;
	private Map<Organisation, Map<FctTarget, Fct>> values;
	private Map<Organisation,Organisation> organisationMap;


	public FctTable(Organisation organisation,List<Organisation> organisations, Period period, FctObjective objective,
			List<FctTarget> targets, Set<OrganisationUnitGroup> facilityTypes,
			Map<Organisation, Map<FctTarget, Fct>> values, Map<Organisation,Organisation> organisationMap) {
		super();
		this.organisation = organisation;
		this.organisations = organisations;
		this.facilityTypes = facilityTypes;
		this.period = period;
		this.objective = objective;
		this.targets = targets;
		this.values = values;
		this.organisationMap=organisationMap;
	}

	public Period getPeriod() {
		return period;
	}

	public List<Organisation> getOrganisations() {
		return organisations;
	}

	public List<FctTarget> getTargets() {
		return targets;
	}

	public FctObjective getObjective() {
		return objective;
	}
	
	public Long getObjectiveId(){
		return objective!=null?objective.getId():null;
	}
	
	public Organisation getOrganisation() {
		return organisation;
	}
	
	public Integer getOrganisationId(){
		return organisation!=null?organisation.getId():null;
	}
	
	public Fct getFct(Organisation organisation, FctTarget target) {
		return values.get(organisation).get(target);
	}

	public Set<OrganisationUnitGroup> getFacilityTypes() {
		return facilityTypes;
	}

	public void setOrganisationMap(Map<Organisation,Organisation> organisationMap) {
		this.organisationMap = organisationMap;
	}

	public Map<Organisation,Organisation> getOrganisationMap() {
		return organisationMap;
	}

}