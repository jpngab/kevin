package org.chai.kevin.dashboard;

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

import grails.plugin.springcache.annotations.Cacheable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.CalculationInfo;
import org.chai.kevin.CalculationValue;
import org.chai.kevin.Info;
import org.chai.kevin.InfoService;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.ValueService;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class DashboardService {

//	private Log log = LogFactory.getLog(DashboardService.class);
	
	private OrganisationService organisationService;
	private InfoService infoService;
	private ValueService valueService;
	
	private Set<Integer> skipLevels;
	
	@Transactional(readOnly = true)
	@Cacheable("dashboardCache")
	public Dashboard getDashboard(Organisation organisation, DashboardObjective objective, Period period) {
		organisationService.loadChildren(organisation, getSkipLevelArray());
		for (Organisation child : organisation.getChildren()) {
			organisationService.loadChildren(child, getSkipLevelArray());
			organisationService.loadGroup(child);
		}
		Organisation parent = organisation;
		while (organisationService.loadParent(parent, getSkipLevelArray())) {
			parent = parent.getParent();
		}
		
		Set<OrganisationUnitGroup> facilityTypeSet = new LinkedHashSet<OrganisationUnitGroup>();
		for (Organisation child : organisation.getChildren()) {
			if (child.getOrganisationUnitGroup() != null) facilityTypeSet.add(child.getOrganisationUnitGroup());
		}
		List<OrganisationUnitGroup> facilityTypes = new ArrayList<OrganisationUnitGroup>(facilityTypeSet);

		List<Organisation> organisations = organisation.getChildren();
		List<DashboardObjectiveEntry> weightedObjectives = objective.getObjectiveEntries();
		List<Organisation> organisationPath = calculateOrganisationPath(organisation);
		List<DashboardObjective> objectivePath = calculateObjectivePath(objective);
		return new Dashboard(organisations, weightedObjectives, 
				organisationPath, objectivePath, facilityTypes,
				getValues(organisations, weightedObjectives, period));
	}

	@Transactional(readOnly = true)
	public DashboardExplanation getExplanation(Organisation organisation, DashboardEntry entry, Period period) {
		organisationService.loadChildren(organisation, getSkipLevelArray());
		organisationService.loadParent(organisation, getSkipLevelArray());
		organisationService.loadGroup(organisation);
		organisationService.loadLevel(organisation);
		
		return entry.visit(new ExplanationVisitor(), organisation, period);
	}

	private class ExplanationVisitor implements DashboardVisitor<DashboardExplanation> {

		@Override
		public DashboardExplanation visitObjective(DashboardObjective objective, Organisation organisation, Period period) {
			DashboardPercentage percentage = objective.visit(new PercentageVisitor(), organisation, period);
			if (percentage == null) return null;
			Map<DashboardObjectiveEntry, DashboardPercentage> values = getValues(objective.getObjectiveEntries(), period, organisation);
			Info<DashboardPercentage> info = new DashboardObjectiveInfo(percentage, values);
			
			return new DashboardExplanation(info, objective);
		}

		@Override
		public DashboardExplanation visitTarget(DashboardTarget target, Organisation organisation, Period period) {
			// TODO groups
			CalculationInfo calculationInfo = infoService.getCalculationInfo(target.getCalculation(), organisation, period, Utils.getUuids(organisationService.getGroupsForExpression()));
			if (calculationInfo == null) return null;
			return new DashboardExplanation(calculationInfo, target);
		}
		
	}
	
	private static Type type = Type.TYPE_NUMBER();
	
	private class PercentageVisitor implements DashboardVisitor<DashboardPercentage> {
		
		private final Log log = LogFactory.getLog(PercentageVisitor.class);
		
		@Override
		public DashboardPercentage visitObjective(DashboardObjective objective, Organisation organisation, Period period) {
			if (log.isDebugEnabled()) log.debug("visitObjective(objective="+objective+",organisation="+organisation+",period="+period+")");
			
			Integer totalWeight = 0;
			Double sum = 0.0d;

			for (DashboardObjectiveEntry child : objective.getObjectiveEntries()) {
				DashboardPercentage childPercentage = child.getEntry().visit(this, organisation, period);
				if (childPercentage == null) {
					if (log.isErrorEnabled()) log.error("found null percentage, objective: "+child.getEntry()+", organisation: "+organisation.getOrganisationUnit()+", period: "+period);
					return null;
				}
				Integer weight = child.getWeight();
				if (childPercentage.isValid()) {
					sum += childPercentage.getGradientValue() * weight;
					totalWeight += weight;
				}
				else {
					// MISSING_EXPRESSION - we skip it
					// MISSING_NUMBER - should we count it in as zero ?
				}

			}
			// TODO what if sum = 0 and totalWeight = 0 ?
			Double average = sum/totalWeight;
			Value value = null;
			if (average.isNaN() || average.isInfinite()) value = Value.NULL;
			else value = type.getValue(average);
			DashboardPercentage percentage = new DashboardPercentage(value, organisation.getOrganisationUnit(), period);
			
			if (log.isDebugEnabled()) log.debug("visitObjective()="+percentage);
			return percentage;
		}

		@Override
		public DashboardPercentage visitTarget(DashboardTarget target, Organisation organisation, Period period) {
			if (log.isDebugEnabled()) log.debug("visitTarget(target="+target+",organisation="+organisation+",period="+period+")");
			
			CalculationValue<?> calculationValue = valueService.getCalculationValue(target.getCalculation(), organisation.getOrganisationUnit(), period, Utils.getUuids(organisationService.getGroupsForExpression()));
			if (calculationValue == null) return null;
			DashboardPercentage percentage = new DashboardPercentage(calculationValue.getValue(), organisation.getOrganisationUnit(), period);

			if (log.isDebugEnabled()) log.debug("visitTarget(...)="+percentage);
			return percentage;
		}
	}
	
	private Map<Organisation, Map<DashboardObjectiveEntry, DashboardPercentage>> getValues(List<Organisation> organisations, List<DashboardObjectiveEntry> objectiveEntries, Period period) {
		Map<Organisation, Map<DashboardObjectiveEntry, DashboardPercentage>> values = new HashMap<Organisation, Map<DashboardObjectiveEntry, DashboardPercentage>>();

		for (Organisation organisation : organisations) {
			Map<DashboardObjectiveEntry, DashboardPercentage> organisationMap = getValues(objectiveEntries, period, organisation);
			values.put(organisation, organisationMap);
		}
		return values;
	}

	private Map<DashboardObjectiveEntry, DashboardPercentage> getValues(List<DashboardObjectiveEntry> objectiveEntries, Period period, Organisation organisation) {
		Map<DashboardObjectiveEntry, DashboardPercentage> organisationMap = new HashMap<DashboardObjectiveEntry, DashboardPercentage>();
		for (DashboardObjectiveEntry objectiveEntry : objectiveEntries) {
			DashboardPercentage percentage = objectiveEntry.getEntry().visit(new PercentageVisitor(), organisation, period);
			organisationMap.put(objectiveEntry, percentage);
		}
		return organisationMap;
	}
	
	private List<Organisation> calculateOrganisationPath(Organisation organisation) {
		List<Organisation> organisationPath = new ArrayList<Organisation>();
		Organisation parent = organisation;
		while ((parent = parent.getParent()) != null) {
			organisationPath.add(parent);
		}
		Collections.reverse(organisationPath);
		return organisationPath;
	}
	
	private List<DashboardObjective> calculateObjectivePath(DashboardObjective objective) {
		List<DashboardObjective> objectivePath = new ArrayList<DashboardObjective>();
		DashboardObjectiveEntry parent = objective.getParent();
		while (parent != null) {
			objective = parent.getParent();
			objectivePath.add(objective);
			parent = objective.getParent();
		}
		Collections.reverse(objectivePath);
		return objectivePath;
	}
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setSkipLevels(Set<Integer> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public Integer[] getSkipLevelArray() {
		return skipLevels.toArray(new Integer[skipLevels.size()]);
	}
}
