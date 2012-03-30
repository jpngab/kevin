/** 
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
package org.chai.kevin.fct;
/**
 * @author Jean Kahigiso M.
 *
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.reports.AbstractReportTarget;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "FctTarget")
@Table(name = "dhsst_fct_target")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class FctTarget extends AbstractReportTarget {
	
	private Long id;
	private List<FctTargetOption> targetOptions = new ArrayList<FctTargetOption>();
	private String typeCodeString; //comma-separated list of location type ids
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}	
	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToMany(targetEntity=FctTargetOption.class, mappedBy="target")
	public List<FctTargetOption> getTargetOptions() {
		return targetOptions;
	}
	
	public void setTargetOptions(List<FctTargetOption> targetOptions) {
		this.targetOptions = targetOptions;
	}

    @Lob
	public String getTypeCodeString() {
		return typeCodeString;
    }

	public void setTypeCodeString(String typeCodeString) {
		this.typeCodeString = typeCodeString;
	}
	
    @Transient
	public Set<String> getTypeCodes() {
		return Utils.split(typeCodeString);
	}
	
	public void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = Utils.unsplit(typeCodes);
	}

	@Override
	public String toString() {
		return "FctTarget [code=" + code + "]";
	}

}
