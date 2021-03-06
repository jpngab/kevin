<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:params.q]" title="${message(code: 'entity.code.label')}" />  		    
  			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q]" title="${message(code: 'entity.name.label')}" />
  			<g:sortableColumn property="${i18nField(field: 'program.names')}" params="[q:params.q]" title="${message(code: 'dsr.target.program.label')}" />
			<g:sortableColumn property="order" params="[q:params.q]" title="${message(code: 'entity.order.label')}" />
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="category">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'dsrTargetCategory', action:'edit', params:[id: category.id])}">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'dsrTargetCategory', action:'delete', params:[id: category.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');"><g:message code="default.link.delete.label" /></a>
						</li>
						
					</ul>
				</td>
				<td>${category.code}</td>
				<td>
					<g:i18n field="${category.names}"/>
				</td>
				<td>
					<g:i18n field="${category.program.names}"/>
				</td>
				<td>${category.order}</td>
			</tr>
		</g:each>
	</tbody>
</table>