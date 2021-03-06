<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.overview.title" /></title>
		
		<r:require module="planning"/>
	</head>
	<body>
		<div class="main">
		
			<g:render template="/planning/planningTabs" model="[planning: planning, location: location, selected: 'undertakings']"/>
			<g:render template="/planning/undertakingsTabs" model="[planning: planning, location: location, selected: 'overview']"/>
	    
			<g:render template="/templates/help" model="[content: i18n(field: planning.overviewHelps)]"/>

			<div>
				<div class="push-20">
					<h4 class="nice-title">
						<span class="nice-title-image">
							<r:img uri="/images/icons/star_small.png"/>
						</span>
						<g:message code="planning.overview.undertakings"/>: <g:i18n field="${location.names}"/>
					</h4>
	        		<ul class="overview-section">
	        			<g:each in="${planningLists}" var="planningList">
	        				<li>
	        					<h5 class="${!planningList.empty?'left':''}">
	        						<a href="${createLink(controller:'editPlanning', action:'planningList', params:[planningType:planningList.planningType.id, location:location.id])}">
	        							<g:i18n field="${planningList.planningType.namesPlural}"/>
	        						</a>
	        					</h5>
	        					
	        					<g:if test="${!planningList.empty}">
		        					<p class="right">
		        						<a class="overview-all" href="${createLink(controller:'editPlanning', action:'planningList', params:[planningType:planningList.planningType.id, location:location.id])}">
		        							<span><g:message code="planning.overview.viewall" args="[planningList.planningEntries.size()+' '+i18n(field: planningList.planningType.namesPlural)]"/></span>
		        						</a>
		        					</p>
		        				
		        					<h6><g:message code="planning.overview.recentlyadded"/></h6>
		        					<ul class="overview-recent">
		        						<g:each in="${planningList.getLatestEntries(5)}" var="entry">
											<li>
												<a href="${createLinkWithTargetURI(controller:'editPlanning', action:'editPlanningEntry', params:[location:location.id, planningType:planningList.planningType.id, lineNumber:entry.lineNumber])}">
													<g:if test="${planningList.planningType.fixedHeader != null && !planningList.planningType.fixedHeader.empty}">
														<g:value value="${entry.fixedHeaderValue}" type="${entry.type.fixedHeaderType}" nullText="${message(code:'planning.none.entered')}"/>
													</g:if>
													<g:else>
														<g:i18n field="${planningList.planningType.names}"/>
														<g:if test="${planningList.planningType.maxNumber != 1}">
															${entry.lineNumber + 1}
														</g:if>
													</g:else>
												</a>
												<span class="overview-manage right">
													<a class="edit-link" href="${createLinkWithTargetURI(controller:'editPlanning', action:'editPlanningEntry', params:[location:location.id, planningType:planningList.planningType.id, lineNumber:entry.lineNumber])}"><g:message code="default.link.edit.label"/></a>
													<a class="delete-link" href="${createLinkWithTargetURI(controller:'editPlanning', action:'deletePlanningEntry', params:[location:location.id, planningType:planningList.planningType.id, lineNumber:entry.lineNumber])}"><g:message code="default.link.delete.label"/></a>
												</span>
											</li>
										</g:each>
									</ul>
	        					</g:if>
	        					<g:else>
	        						<p><g:message code="planning.nothingyet" args="[i18n(field: planningList.planningType.namesPlural)]"/> 
		        						<a href="${createLinkWithTargetURI(controller:'editPlanning', action:'editPlanningEntry', params:[location:location.id, planningType:planningList.planningType.id, lineNumber:planningList.nextLineNumber])}">
		        							<g:message code="planning.addfirst" args="[i18n(field: planningList.planningType.names)]"/>
		        						</a>
	        						</p>
	        					</g:else>
	        					<g:if test="${planningList.planningType.maxNumber == null || planningList.nextLineNumber < planningList.planningType.maxNumber}">
		        					<p class="overview-new">
		        						<a class="next gray medium" href="${createLinkWithTargetURI(controller:'editPlanning', action:'editPlanningEntry', params:[location:location.id, planningType:planningList.planningType.id, lineNumber:planningList.nextLineNumber])}">
		        							<g:message code="planning.createnew" args="[i18n(field: planningList.planningType.names)]"/>
		        						</a>
		        					</p>
	        					</g:if>
	        				</li>
	        			</g:each>
	        		</ul>
				</div>
			</div>
		</div>
	</body>
</html>