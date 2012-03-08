<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.new.label" default="District Health System Portal" /></title>
		
		<r:require module="planning"/>
	</head>
	<body>
		<div id="content" class="push"/>
			<div id="planning">
				<div class="main">  
	
				<ul class="horizontal" id="tab-nav">
					<li><a class="${selected=='undertakings'?'selected':''}" href="${createLink(controller:'editPlanning', action:'overview', params:[planning: planningType.planning.id, location: location.id])}">Undertakings</a></li>
					<li><a class="selected" href="#">New <g:i18n field="${planningType.names}"/></a></li>
					<li><a class="${selected=='budget'?'selected':''}" href="${createLink(controller:'editPlanning', action:'budget', params:[planning: planningType.planning.id, location: location.id])}">Projected Budget</a></li>
				</ul>
				    
		    	<!-- TODO tips could go into a template -->
				<p class="show-question-help moved"><a href="#">Show Tips</a></p>
				<div class="question-help-container">
					<div class="question-help push-20">
						<a class="hide-question-help" href="#">Close tips</a>Some help information for the Performance tab
					</div>
				</div>
					
				<div id="questions">
					<g:form url="[controller:'editPlanning', action:'submit', params: [location: location.id, planningType: planningType.id, targetURI: targetURI]]">
		  				<input class="js_always-send" type="hidden" name="lineNumber" value="${planningEntry.lineNumber}"/>
		
		  				<g:each in="${planningType.sections}" var="section" status="i">
		  					<div id="section-${section}" class="${planningEntry.invalidSections.contains(section)?'invalid':''} ${planningEntry.incompleteSections.contains(section)?'incomplete':''}">
			  					<div class="section-title-wrap">
			  						<h4 class="section-title"> 
			  							<span class="question-default">${i+1}</span>
			  							<g:i18n field="${planningType.headers[section]}"/>
			  						</h4>
			  					</div>
			
			  					<g:render template="/survey/element/${planningType.getType(section).type.name().toLowerCase()}"  model="[
									value: planningEntry.getValue(section),
									lastValue: null,
									type: planningType.getType(section), 
									suffix: planningEntry.getPrefix(section),
									headerSuffix: section,
									
									// get rid of those in the templates??
									element: planningType,
									validatable: planningEntry.validatable,
									
									readonly: readonly,
									enums: planningEntry.enums
								]"/>
								
								<div class="adv-aside question-help-container">
									<div class="question-help"><g:i18n field="${planningType.sectionDescriptions[section]}"/></div>
								</div>
							</div>
		  				</g:each>
						<ul class=" form-actions clearfix">
							<li>
    		  					<button type="submit" class="loading-disabled">
    		  						<g:if test="${!planningEntry.submitted}">
    		  							Accept in budget
    		  						</g:if>
    		  						<g:else>
    		  							Update budget
    		  						</g:else>
    		  					</button>
  		  					</li>
  		  					<li>
    		  					<button type="cancel" class="hidden">
  								    <g:message code="survey.section.cancel.label" default="Cancel"/>
  							    </button>
  							  </li>
  							  <li>
    		  					<a class="go-back" href="${createLink(uri: targetURI)}">
    		  						Return to listing
    		  					</a>
    		  				</li>
		  				</div>
		  				<br />
		  				<g:if test="${planningEntry.submitted}">
							<p class="context-message warning">
								Message that inform the client about removing activities from the budget.
								<a class="next gray medium right pull-7" href="${createLink(controller:'editPlanning', action:'unsubmit', params: [location: location.id, planningType: planningType.id, lineNumber: planningEntry.lineNumber, targetURI: targetURI])}">
									Remove from budget
								</a>
							</p>
						</g:if>
	  				</g:form>
				</div>
			</div>
		</div>
		
		<r:script>
			$(document).ready(function() {
				${render(template:'/templates/messages')}
			
				new DataEntry({
					element: $('#planning'),
					callback: function(dataEntry, data, element) {
						$.each(data.sections, function(index, value) {
							if (value.complete == true) $(escape('#section-'+value.section)).removeClass('incomplete')
							else $(escape('#section-'+value.section)).addClass('incomplete')
							
							if (value.invalid == false) $(escape('#section-'+value.section)).removeClass('invalid')
							else $(escape('#section-'+value.section)).addClass('invalid')
						});
					},
					url: "${createLink(controller:'editPlanning', action:'saveValue', params: [location: location.id, planningType: planningType.id])}", 
					messages: messages,
					trackEvent: ${grails.util.Environment.current==grails.util.Environment.PRODUCTION}
				});
			});
		</r:script>
	</body>
</html>