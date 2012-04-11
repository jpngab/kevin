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

class UrlMappings {

	static mappings = {
		// TODO serve those statically, they shouldn't be here
		// since they are implementation-specific
		"/about"(controller:'home', action:"about")
		"/helpdesk"(controller:'home', action:"helpdesk")
		"/contact"(controller:'home', action:"contact")
		"/upgrade"(controller:'home', action:"upgrade")
		
		"/dashboard/$action/$period?/$program?/$location?"(controller:"dashboard")
		
		"/cost/$action/$period?/$program?/$location?"(controller:"cost")

		"/dsr/$action/$period?/$program?/$location?"(controller:"dsr")
		
		"/fct/$action/$period?/$program?/$location?"(controller:"fct")
		
		"/maps/view"(controller:"maps", action:"view")
		
		"/maps/map/$period?/$location?/$level?/$target?"(controller:"maps", action: "map")
		
		"/chart/chart/$data/$location"(controller:"chart", action: "chart")
		
		"/auth/$action"(controller:"auth")
		
		"/editSurvey/$action/$location?"(controller:"editSurvey")
		
		"/summary/$action/$location?"(controller:"summary")
		
		"/editPlanning/$action/$location?"(controller:"editPlanning")
		
		"/exporter/$action"(controller:"exporter")
		
		"/$controller/$action?"{
			constraints {
				// apply constraints here
			}
		}

		// homepage in home controller
		"/"(controller:"home", action:"index")
		"404"(view:'/404')
		"500"(view:'/error')
	}
}
