<%@ page import="org.chai.kevin.survey.SurveyPage.SectionStatus" %>
<%@ page import="org.chai.kevin.survey.validation.SurveyEnteredObjective.ObjectiveStatus" %>

<div id="survey-left-objective-container" class="grey-rounded-box-bottom">
	<ul id="survey-objective-list">
		<g:each in="${surveyPage.survey.getObjectives(surveyPage.organisation.organisationUnitGroup)}" var="objective">
			<g:set var="objectiveStatus" value="${surveyPage.getStatus(objective)}"/>
			
			<li id="objective-${objective.id}" class="${surveyPage.section?.objective?.id == objective.id?'current':''}">
				<a class="item" href="${createLink(controller:'survey', action:'objectivePage', params:[organisation: surveyPage.organisation.id, objective:objective.id])}">
					<span><g:i18n field="${objective.names}" /></span>
					<span class="item-status">
						<span class="objective-status-complete objective-status ${objectiveStatus != ObjectiveStatus.COMPLETE?'hidden':''}"></span>
						<span class="objective-status-invalid  objective-status ${objectiveStatus != ObjectiveStatus.INVALID?'hidden':''}"></span>
						<span class="objective-status-incomplete objective-status ${objectiveStatus != ObjectiveStatus.INCOMPLETE?'hidden':''}"></span>
						<span class="objective-status-closed objective-status ${objectiveStatus != ObjectiveStatus.CLOSED?'hidden':''}"></span>
						<span class="objective-status-unavailable objective-status ${objectiveStatus != ObjectiveStatus.UNAVAILABLE?'hidden':''}"></span>
					</span>
				</a>
				<g:if test="${surveyPage.objective.equals(objective)}">
					<ul class="survey-section">
						<g:each in="${objective.getSections(surveyPage.organisation.organisationUnitGroup)}" var="section">
							<g:set var="sectionStatus" value="${surveyPage.getStatus(section)}"/>

							<li id="section-${section.id}">
								<a class="item ${surveyPage.section?.id == section.id?'opened':''}" href="${createLink(controller:'survey', action:'sectionPage', params:[organisation: surveyPage.organisation.id, section:section.id])}">
									<span><g:i18n field="${section.names}" /></span>
									<span class="item-status">
										<span class="section-status-complete section-status ${sectionStatus != SectionStatus.COMPLETE?'hidden':''}"></span>
										<span class="section-status-invalid section-status ${sectionStatus != SectionStatus.INVALID?'hidden':''}"></span>
										<span class="section-status-incomplete section-status ${sectionStatus != SectionStatus.INCOMPLETE?'hidden':''}"></span>
										<span class="section-status-closed section-status ${sectionStatus != SectionStatus.CLOSED?'hidden':''}"></span>
										<span class="section-status-unavailable section-status ${sectionStatus != SectionStatus.UNAVAILABLE?'hidden':''}"></span>
									</span>
								</a>
							</li>
						</g:each>
					</ul>
				</g:if>
			</li>
		</g:each>
	</ul>
	
	<script>
		function surveyValueChanged(element, callback) {
			var elementId = $(element).parents('.element').data('element');
			var questionId = $(element).parents('.question').data('question'); 
			
			var data = $('#survey-form').serialize();
			data += '&element='+elementId+'&question='+questionId;
			
			$.ajax({
				type : 'POST',
				dataType: 'json',
				data : data,
				url : "${createLink(controller:'survey', action:'saveValue', params: [organisation: surveyPage.organisation.id, section: surveyPage.section?.id, objective: surveyPage.objective?.id])}",
				success : function(data, textStatus) {
					if (data.result == "success") {
						$('#objective-'+data.objective.id+' .objective-status').addClass('hidden');
						$('#objective-'+data.objective.id+' .objective-status-'+data.objective.status.toLowerCase()).removeClass('hidden');
						$(data.sections).each(function(key, section) {
							$('#section-'+section.id+' .section-status').addClass('hidden');
							$('#section-'+section.id+' .section-status-'+section.status.toLowerCase()).removeClass('hidden');
						});
						
						callback(data, element);
					}
				}
			});
		}
	</script>
</div>