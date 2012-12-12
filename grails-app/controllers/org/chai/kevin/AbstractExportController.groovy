package org.chai.kevin

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

import org.apache.commons.lang.StringUtils
import org.chai.kevin.util.Utils

abstract class AbstractExportController extends AbstractController {		
	
	def entityExportService
	
	def exporter = {
		def entityClazz = getEntityClass();
		if (entityClazz instanceof Class) entityClazz = [entityClazz]
		
		List<String> filenames = new ArrayList<String>();
		List<File> csvFiles = new ArrayList<File>();
		
		for (Class clazz : entityClazz){
			String filename = entityExportService.getExportFilename(clazz);
			filenames.add(filename);
			csvFiles.add(entityExportService.getExportFile(filename, clazz));
		}
		
		String zipFilename = StringUtils.join(filenames, "_")
		def zipFile = Utils.getZipFile(csvFiles, zipFilename)
		
		if(zipFile.exists()){
			response.setHeader("Content-disposition", "attachment; filename=" + zipFile.getName());
			response.setContentType("application/zip");
			response.setHeader("Content-length", zipFile.length().toString());
			response.outputStream << zipFile.newInputStream()
		}
	}
	
	protected abstract def getEntityClass();
	
}