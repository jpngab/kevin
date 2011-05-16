<div id="add-cost-target" class="entity-form-container">
	<g:form url="[controller:'mapsTarget', action:'save']" useToken="true">
		<div class="row ${hasErrors(bean:target,field:'name','errors')}">
			<label for="name">Name</label>		
			<input name="name" value="${fieldValue(bean:target,field:'name')}"></input>
			<div class="error-list"><g:renderErrors bean="${target}" field="name" /></div>
		</div>
		<div class="row ${hasErrors(bean:target,field:'description','errors')}">
			<label for="description">Description</label>
			<textarea name="description" rows="5">${fieldValue(bean:target,field:'description')}</textarea>
			<div class="error-list"><g:renderErrors bean="${target}" field="description" /></div>
		</div>
		<div class="row">
			<h5>Expressions</h5>
			<div class="float-right">
				<a id="add-expression-link" href="${createLink(controller:'expression', action:'create')}">new expression</a>
			</div>
			<div class="clear"></div>
			
			<div id="expressions-block">
				<div class="group-list ${hasErrors(bean:target, field:'expression', 'errors')}">
					<label for="expression.id">Expression:</label>
					<select class="expression-list" name="expression.id">
						<option value="null">-- select an expression --</option>
						<g:each in="${expressions}" var="expression">
							<option value="${expression.id}" ${expression.id+''==fieldValue(bean: target, field: 'expression.id')+''?'selected="selected"':''}>
								${expression.name}
							</option>
						</g:each>
					</select>
					<div class="error-list"><g:renderErrors bean="${target}" field="expression" /></div>
				</div>
			</div>
		</div>

		<div class="row ${hasErrors(bean:target,field:'order','errors')}">
			<label for="order">Order</label>
			<input type="text" name="order" value="${fieldValue(bean:target,field:'order')}"></input>
			<div class="error-list"><g:renderErrors bean="${target}" field="order" /></div>
		</div>
		
		<g:if test="${target?.id != null}">
			<input type="hidden" name="id" value="${target.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit">Save target</button>
			<button id="cancel-button">Cancel</button>
		</div>
    </g:form>
	<div class="clear"></div>
</div>

<div class="hidden flow-container"></div>


<script type="text/javascript">
	$(document).ready(function() {
		$('#add-cost-target').flow({
			addLinks: '#add-ramp-up-link',
			onSuccess: function(data) {
				if (data.result == 'success') {
					var rampUp = data.newEntity;
					$('.ramp-up-list').append('<option value="'+rampUp.id+'">'+rampUp.name+'</option>');
// 					$.sexyCombo.changeOptions('.ramp-up-list');
				}
			}
		});
		
		$('#add-cost-target').flow({
			addLinks: '#add-expression-link',
			onSuccess: function(data) {
				if (data.result == 'success') {
					var expression = data.newEntity
					$('.expression-list').append('<option value="'+expression.id+'">'+expression.name+'</option>');
					$.sexyCombo.changeOptions('.expression-list');
				}
			}
		});
	});
</script>