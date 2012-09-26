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
package org.chai.kevin.dsr;
/**
 * @author Jean Kahigiso M.
 *
 */
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Exportable;
import org.chai.kevin.Importable;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.AbstractReportTarget;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.reports.ReportTarget;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.proxy.HibernateProxy;

@Entity(name = "DsrTarget")
@Table(name = "dhsst_dsr_target")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class DsrTarget extends AbstractReportTarget implements ReportTarget, Exportable, Importable {
	
	private DsrTargetCategory category;
	private String format;
	private Boolean average; //this can either be an average (true) or sum (null or false)
	private ReportProgram program;

	
	@Override
	@Transient
	public Data<?> getData() {
		if (super.getData() instanceof HibernateProxy) {
			return Data.class.cast(((HibernateProxy) super.getData()).getHibernateLazyInitializer().getImplementation());  
		}
		else {
			return Data.class.cast(super.getData());
		}
	}
	
	@Transient
	public Type getType() {
		return getData().getType();
	}
	
	@ManyToOne(targetEntity=DsrTargetCategory.class)
	public DsrTargetCategory getCategory() {
		return category;
	}

	public void setCategory(DsrTargetCategory category) {
		this.category = category;
	}

	@Basic
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode().toString()) + "]";
	}
	
	@Override
	public DsrTarget fromExportString(Object value) {
		return (DsrTarget) value;
	}
	
	public Boolean getAverage() {
		return average;
	}
	
	public void setAverage(Boolean average) {
		this.average = average;
	}
	
	@ManyToOne(targetEntity=ReportProgram.class)
	public ReportProgram getProgram() {
		return program;
	}

	public void setProgram(ReportProgram program) {
		this.program = program;
	}
}