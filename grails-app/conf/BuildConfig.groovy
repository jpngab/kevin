/*
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

import org.codehaus.groovy.grails.resolve.GrailsRepoResolver;

grails.servlet.version = "2.5"
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.source.level = 1.6

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
         excludes 'ehcache'
		 
		 buildSettings.dependenciesExternallyConfigured = true
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
//	checksums true
	
    repositories {
		inherits false 
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
		mavenLocal()
		mavenCentral()	
		mavenRepo "http://m2repo.spockframework.org/snapshots"
//		mavenRepo "http://snapshots.repository.codehaus.org"
//		mavenRepo "http://repository.codehaus.org"
//		mavenRepo "http://www.intalio.org/public/maven2/"
		mavenRepo "http://repo.opennms.org/maven2/"
//		mavenRepo "https://repository.jboss.org/nexus/content/groups/public-jboss/"
				
		/**
		 * Configure our resolver.
		 */
		def libResolver = new GrailsRepoResolver(null, null);
		libResolver.addArtifactPattern("https://github.com/fterrier/repository/raw/master/[location]/[module]/[type]s/[artifact]-[revision].[ext]")
		libResolver.addIvyPattern("https://github.com/fterrier/repository/raw/master/[location]/[module]/ivys/ivy-[revision].xml")
		libResolver.name = "github"
//		libResolver.settings = ivySettings
		resolver libResolver
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

		// JAQL dependencies - jaql itself is in the lib/ folder
		compile ("org.apache.hadoop:hadoop-core:0.20.203.0") {
			transitive = false
		}
		
		// because of GRAILS-6147, this dependency is in lib instead of here
//		compile group: "net.sf.json-lib", name: "json-lib", version: "2.4", classifier: "jdk15"
		compile 'net.sf.ezmorph:ezmorph:1.0.6'
		
        runtime 'mysql:mysql-connector-java:5.1.13'
		
//		compile ("net.bull.javamelody:javamelody-core:1.31.0")

//		test"org.codehaus.geb:geb-spock:0.6.0"
//		test("org.seleniumhq.selenium:selenium-firefox-driver:latest.release")
//		test("org.seleniumhq.selenium:selenium-chrome-driver:2.0rc2")
//		test("org.seleniumhq.selenium:selenium-htmlunit-driver:2.0rc2") {
//			excludes "xml-apis", "xmlParserAPIs"
//		}
//		test 'org.gmock:gmock:0.8.1'
		
		// those are for the migration script to work
//		compile 'org.hisp.dhis:dhis-service-importexport:2.2-SNAPSHOT'
		compile 'org.supercsv:SuperCSV:1.52'
    }

	plugins {
		build ":tomcat:$grailsVersion"
		
		compile ":hibernate:$grailsVersion"
		compile ":jquery:1.7.1"
		compile ":resources:1.1.6"
		compile ":shiro:1.1.5"
		compile ":springcache:1.3.1"
		compile ":compass-sass:0.6"
		compile ":google-analytics:1.0"
		compile ":grails-melody:1.10"
		compile ":quartz:0.4.2"
		compile ":constraints:0.7.0"
		compile ":cached-resources:1.0"
		compile ":cache-headers:1.1.5"
		compile ":svn:1.0.1"
		compile ":cdn-resources:0.2"
		compile ":mail:1.0"
		
		test (":spock:0.6-SNAPSHOT") {changing = false}
	}

	
}
