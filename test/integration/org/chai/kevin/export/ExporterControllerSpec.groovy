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
package org.chai.kevin.export

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Period;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;

/**
 * @author Jean Kahigiso M.
 *
 */
class ExporterControllerSpec extends IntegrationTests {
	
	def exporterController;
	
	def "get exporter"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		def typeOne = Type.TYPE_NUMBER();
		def typeTwo = Type.TYPE_BOOL();
		def dataElementOne = newRawDataElement(CODE(1), typeOne);
		def dataElementTwo = newRawDataElement(CODE(2), typeTwo);
		def locations=new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		def data=new HashSet([dataElementOne,dataElementTwo]);
		def exporter = newExporter(j("en":"Testing Seach One"),periods, locationType, locations, data);
		exporterController = new  ExporterController();
		
		when:
		exporterController.params.('export.id')=exporter.id
		exporterController.export()
		then:
		exporterController.response.getContentType() == "application/zip"
		
	}
	
	def "bind params with duplicate location"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		def typeOne = Type.TYPE_NUMBER();
		def typeTwo = Type.TYPE_BOOL();
		def dataElementOne = newRawDataElement(CODE(1), typeOne);
		def dataElementTwo = newRawDataElement(CODE(2), typeTwo);
		def locations=new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		def data=new HashSet([dataElementOne,dataElementTwo]);
		exporterController = new  ExporterController();
		
		when:
		exporterController.params.('locationIds')=[Location.findByCode(BURERA).id+"",DataLocation.findByCode(KIVUYE).id+"",Location.findByCode(BURERA).id+""]
		exporterController.params.('periodIds')=[Period.list()[0].id+""]
		exporterController.params.('dataIds')=[dataElementOne.id+"",dataElementTwo.id+""]
		exporterController.params.('typeCodes')="Health Center,District Hospital";
		exporterController.save()
		def exporters = Exporter.list();
		then:
		exporters.size()==1;
		exporters[0].periods==periods;
		exporters[0].typeCodeString.equals('Health Center,District Hospital');
		exporters[0].locations==locations;
		exporters[0].data==data;
		
	}
	
	
	def "bind params with duplicate data"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		def typeOne = Type.TYPE_NUMBER();
		def typeTwo = Type.TYPE_BOOL();
		def dataElementOne = newRawDataElement(CODE(1), typeOne);
		def dataElementTwo = newRawDataElement(CODE(2), typeTwo);
		def locations=new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		def data=new HashSet([dataElementOne,dataElementTwo]);
		exporterController = new  ExporterController();
		
		when:
		exporterController.params.('locationIds')=[Location.findByCode(BURERA).id+"",DataLocation.findByCode(KIVUYE).id+""]
		exporterController.params.('periodIds')=[Period.list()[0].id+""]
		exporterController.params.('dataIds')=[dataElementOne.id+"",dataElementTwo.id+"",dataElementTwo.id+""]
		exporterController.params.('typeCodes')="Health Center,District Hospital";
		exporterController.save()
		def exporters = Exporter.list();
		then:
		exporters.size()==1;
		exporters[0].periods==periods;
		exporters[0].typeCodeString.equals('Health Center,District Hospital');
		exporters[0].locations==locations;
		exporters[0].data==data;
		
	}
	
	def "bind params with duplicate period"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		def typeOne = Type.TYPE_NUMBER();
		def typeTwo = Type.TYPE_BOOL();
		def dataElementOne = newRawDataElement(CODE(1), typeOne);
		def dataElementTwo = newRawDataElement(CODE(2), typeTwo);
		def locations=new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		def data=new HashSet([dataElementOne,dataElementTwo]);
		exporterController = new  ExporterController();
		
		when:
		exporterController.params.('locationIds')=[Location.findByCode(BURERA).id+"",DataLocation.findByCode(KIVUYE).id+""]
		exporterController.params.('periodIds')=[Period.list()[0].id+"",Period.list()[0].id+""]
		exporterController.params.('dataIds')=[dataElementOne.id+"",dataElementTwo.id+""]
		exporterController.params.('typeCodes')="Health Center,District Hospital";
		exporterController.save()
		def exporters = Exporter.list();
		then:
		exporters.size()==1;
		exporters[0].periods==periods;
		exporters[0].typeCodeString.equals('Health Center,District Hospital');
		exporters[0].locations==locations;
		exporters[0].data==data;
	}
	def "exporter clone successfull"(){
		setup:
		setupLocationTree();
		def periods=new HashSet([newPeriod()]);
		def locationType="Health Center,District Hospital";
		def typeOne = Type.TYPE_NUMBER();
		def typeTwo = Type.TYPE_BOOL();
		def dataElementOne = newRawDataElement(CODE(1), typeOne);
		def dataElementTwo = newRawDataElement(CODE(2), typeTwo);
		def locations=new HashSet();
		locations.addAll(getLocations([BURERA]));
		locations.addAll(getDataLocations([KIVUYE]));
		def data=new HashSet([dataElementOne,dataElementTwo]);
		def exporter = newExporter(j("en":"Testing Seach One"),periods, locationType, locations, data);
		exporterController = new  ExporterController();
		
		when:
		exporterController.params.('export.id')=exporter.id
		exporterController.clone()
		then:
		Exporter.list().size()==2;
		Exporter.list()[0].periods.equals(Exporter.list()[1].periods)
		Exporter.list()[0].typeCodeString.equals(Exporter.list()[1].typeCodeString)
		Exporter.list()[0].locations.equals(Exporter.list()[1].locations)
		Exporter.list()[0].data.equals(Exporter.list()[1].data)
		
	}

}
