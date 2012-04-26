<div class="entity-list">
	<div id="entities">
		<div class="heading">
	     	<g:message code="default.list.label" args="[entityName]" />
	     	<g:if test="${!search}">
		     	<span class="right">
					<g:if test="${!addTemplate}">
		  				<a href="${createLinkWithTargetURI(controller: controllerName, action:'create')+(request.queryString==null?'':'&'+request.queryString)}">
		  					<g:message code="default.new.label" args="[entityName]"/>
		  				</a>
		  				&nbsp;
		  				<a href="${createLinkWithTargetURI(controller: controllerName, action:'export')+(request.queryString==null?'':'&'+request.queryString)}">
		  					<g:message code="default.export.label" />
		  				</a>/
		  				<a href="${createLinkWithTargetURI(controller: entityImporter, action:'importer', params:[entityClass: exportEntity()])+(request.queryString==null?'':'&'+request.queryString)}">
		  					<g:message code="default.import.label" />
		  				</a>
		  			</g:if>
		  			<g:else>
		  				<g:render template="${addTemplate}"/>
		  				<a href="${createLinkWithTargetURI(controller: controllerName, action:'export')+(request.queryString==null?'':'&'+request.queryString)}">
		  					<g:message code="default.export.label" />
		  				</a>/
		  				<a href="${createLinkWithTargetURI(controller: entityImporter, action:'importer', params:[entityClass: exportEntity()])+(request.queryString==null?'':'&'+request.queryString)}">
		  					<g:message code="default.import.label" />
		  				</a>
		  			</g:else>
		     	</span>
	     	</g:if>
		</div>
		
		<!-- Template goes here -->
		<g:if test="${grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName).hasProperty('search')}">
			<g:searchBox action="search"/>
		</g:if>
		<g:if test="${!entities.empty}">
			<div class="main">
				<g:render template="${template}"/>
			</div>
			<!-- End of template -->
			<div class="paginateButtons main">
				<g:if test="${entityCount != null}">
					<g:paginate total="${entityCount}" params="${params}" action="${actionName}"/>
				</g:if>
			</div>
		</g:if>
		<g:else>
			<div class="main"><g:message code="entity.list.empty.label" args="[entityName]"/></div>
		</g:else>				
	</div>
</div>
