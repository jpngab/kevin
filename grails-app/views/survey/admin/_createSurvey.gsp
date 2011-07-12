<div id="add-survey" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Survey</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'createSurvey', action:'save']" useToken="true">
		<g:i18nInput name="names" bean="${survey}" value="${survey?.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${survey}" value="${survey?.descriptions}" label="Description" field="descriptions"/>
		<g:input name="order" label="Order" bean="${survey}" field="order"/>
	   <div class="row">
			<div>
				<a id="add-iteration-link" class="float-right"  href="${createLink(controller:'iteration', action:'create')}">New Iteration</a>
			</div>
			<div class="clear"></div>
			<div id="iteration-block">
					<div class="group-list ${hasErrors(bean:period, field:'period', 'errors')}">
						<label for="period.id">Period:</label>
						<select class="iteration-list" name="period.id">
							<option value="null">-- Select an Iteration --</option>
							<g:each in="${periods}" var="period">
								<option value="${period.id}" ${period.id+''==fieldValue(bean: survey, field: 'period.id')+''?'selected="selected"':''}>
									${period.startDate} <--> ${period.endDate}
								</option>
							</g:each>
						</select>
						<div class="error-list"><g:renderErrors bean="${period}" field="period" /></div>
					</div>
			</div>
		</div>
		<div class="clear"></div>
		<g:if test="${survey?.id != null}">
			<input type="hidden" name="id" value="${survey?.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit">Save Survey</button>&nbsp;&nbsp;
			<button id="cancel-button">Cancel</button>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
<script type="text/javascript">
	$(document).ready(function() {	
		$('#add-survey').flow({
			addLinks: '#add-iteration-link',
			onSuccess: function(data) {
				if (data.result == 'success') {
					var period = data.newEntity;
					$('.iteration-list').append('<option value="'+period.id+'">'+period.startDate+'<->'+periodendDate+'</option>');
					$.sexyCombo.changeOptions('.iteration-list');
				}
			}
		});
	})
		</script>