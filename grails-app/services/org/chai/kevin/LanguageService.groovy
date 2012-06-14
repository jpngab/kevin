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

import org.apache.commons.lang.LocaleUtils;
import org.chai.kevin.Translation;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

class LanguageService {
	
	def dataService
	
	static transactional = false
	
	List<String> getAvailableLanguages() {
		List<String> languages = ConfigurationHolder.config.site.languages;
		return languages;
	}
	
	String getCurrentLanguage() {
		Locale locale = RequestContextUtils.getLocale(RequestContextHolder.currentRequestAttributes().getRequest());
		return locale.getLanguage();
	}
	
	String getFallbackLanguage() {
		return ConfigurationHolder.config.site.fallback.language;
	}
	
	String getText(def translation) {
		def text = translation?.get(getCurrentLanguage())
		if (text != null) text = text.toString()
		if (text == null || text.trim().equals("") || Utils.stripHtml(text).trim().equals("")) text = translation?.get(getFallbackLanguage())
		if (text != null) text = text.toString()
		if (text == null) return "";
		return text.toString();
	}

	String getStringValue(Value value, Type type, def enums = null, def format = null, def zero = null) {
		def result;
		switch (type.type) {
			case (ValueType.ENUM):
				def enume = null
				 
				if (enums == null) enume = dataService.findEnumByCode(type.enumCode);
				else enume = enums?.get(type.enumCode)
				
				if (enume == null) result = value.enumValue
				else {
					def option = enume?.getOptionForValue(value.enumValue)
					if (option == null) result = value.enumValue
					else result = getText(option.names)
				}
				break;
			case (ValueType.NUMBER):
				if (zero != null && value.numberValue == 0) result = zero
				else result = Utils.formatNumber(format, value.numberValue)
				break;
			case (ValueType.MAP):
				// TODO
			case (ValueType.LIST):
				// TODO
			default:
				result = value.stringValue
		}
		return result;
	}
	
}
