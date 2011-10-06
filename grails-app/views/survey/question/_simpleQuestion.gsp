<g:set var="organisationUnitGroup" value="${surveyPage.organisation.organisationUnitGroup}"/>

<div id="question-${question.id}" class="question question-simple" data-question="${question.id}">
<<<<<<< HEAD
	<h4><span class="question-number">2</span><g:i18n field="${question.names}" /></h4>
	<p class="show_question_help"><a href="#">Show tips</a></p>
	<div class="question-help-container">
		<p class="question-help"><g:i18n field="${question.descriptions}"/><a class='hide_question_help'>Close tips</a></p>
	</div>
	
	<g:set var="surveyElement" value="${question.surveyElement}"/>
	<div class="clear"></div>
=======
	<h4><span class="question-number">${questionNumber}</span><g:i18n field="${question.names}" /></h4>
	<p class='show_question_help'><a href="#">Show tips</a></p>
  <div class="question-help-container">
    
    <p class="question-help"><g:i18n field="${question.descriptions}"/><a class='hide_question_help'>Close tips</a></p>
  </div>
	<div class="clear">
  	<g:set var="surveyElement" value="${question.surveyElement}"/>
  	<g:set var="dataElement" value="${surveyElement.dataElement}"/>
>>>>>>> fixes for siyelo - question numbers, added big table, added text input type

	<g:if test="${print && surveyElement?.dataElement.type.type.name().toLowerCase()=='list' && !appendix}">
		<h4>--- <g:message code="survey.print.see.appendix" default="See Appendix"/> ---</h4>
	</g:if>
	<g:else>
		<div id="element-${surveyElement?.id}" class="survey-element">
			<g:if test="${surveyElement != null}">
				<g:set var="dataElement" value="${surveyElement?.dataElement}"/>
				<g:set var="enteredValue" value="${surveyPage.elements[surveyElement]}" />
			
				<g:render template="/survey/element/${dataElement.type.type.name().toLowerCase()}"  model="[
					value: enteredValue.value,
					lastValue: enteredValue.lastValue,
					type: dataElement.type, 
					suffix:'',
					surveyElement: surveyElement, 
					enteredValue: enteredValue, 
					readonly: readonly,
					print: print,
					appendix: appendix
				]"/>
			</g:if>
			<g:else>
				No survey element for this question.
			</g:else>
		</div>
	</g:else>
</div>
