DHSST
===

The DHSST is an integrated web-based system whose main goal is to help administrations such as
governments collect and analyze information as well as plan. For more information and documentation, visit [dhsst.org][dhsst.org].


Installation
---

To install and run the DHSST, simply clone the git repository and run

	grails run-app

Grails 2.1.0 has to be installed. We recommend using [gvm][gvm] to install grails:

	gvm install grails 2.1.0
	
DHSST will run fine using the default configuration on the development environment. If you want to run DHSST in production with your own configuration, there are a few steps needed.

Production configuration
---

These are the configuration paramters that need to be set when using the site on a production server. When launching the site in production, the application will look for a file named ```<user_home>/.grails/kevin-config.groovy``` and use that configuration to override the default one.

### Data source configuration

The production data source is configured to use a MySQL database and schema by default. If you are fine with that, then here are the settings you should set:

	dataSource.url="jdbc:mysql://<url>:<port>/<db_name>"
	dataSource.username="<username>"
	dataSource.password="<password>"
	
The application has only been tested with a MySQL database. However if you want to use another database at your own risk. Check the [grails documentation][grails-doc] for instructions on how to configure it.

### Application configuration

##### Site URL

	// the URL of the site
	grails.serverURL="http://www.example.org/"

##### Languages

	// languages available in the site
	i18nFields {
        locales = ["en", "fr"]
	}
	
	// default language
	site.fallback.language="en"

##### Email

	// email used when a user clicks on "contact" link
	site.contact.email="contact@example.org"
	
	// email used as the sender email from all mails sent by the site (registration, forgot password, etc...)
	site.from.email="no-reply@example.org"

##### Admin pages

	// default size of lists in admin pages
	site.entity.list.max=40

##### Tagline & images

	// this text appears at the top right of the site
	site.tagline.en="This is the tagline of the site"
	
	// one for each configured language
	site.tagline.<language>="Voici la tagline du site"
	
	// URL of the icon on the top right
	site.icon="http://static.example.org/images/icon.png"
	
	// link for the "report a bug" button
	site.bugtracker.url="http://www.example.org/redmine"

##### Skip levels

	// those location levels will be skipped in the given report (explained below)
	// this parameter is for global skip levels for reports
	report.skip.levels = ["sector"]
	// those 3 override the one above for the specific reports
	dashboard.skip.levels = []
	fct.skip.levels = []
	dsr.skip.levels = []
	
	// those location levels will be skipped in the survey summary page
	survey.skip.levels = ["sector"]
	// thos location levels will be skipped in the survey export
	survey.export.skip.levels = ["country", "sector"]
	
##### CSV File upload

	// available charsets for the file upload forms
	file.upload.available.charset = ["UTF-8", "ISO-8859-1"]
	
	// default delimiters for CSV file upload in the upload forms
	file.upload.delimiter = ","

### Plugins configuration

##### Rabbitmq Task

The full plugin description can be found [here][rabbitmq-tasks].

	// temporary folder for files created by the task plugin
	task.temp.folder='/opt/tmp/'

##### Mail

The application is configured to use Google SMTP server by default. If you want to change that, cf. the [mail plugin][grails-mail] configuration. The configuration below assumes using Google SMTP server.

	// enables the email on the production server
	grails.mail.disabled=false
	
	// google mail username
	grails.mail.username="rwanda@dhsst.org"
	
	// google mail password
	grails.mail.password="FR5qsjvf"

##### Mail on exception

The plugin documentation can be found [here][mail-on-exception].

	// emails sent to in case of exception
	mailOnException.email.to = "admin@dhsst.org"
	
	// sender of exception report emails
	mailOnException.email.from = "no-reply@dhsst.org"
	
##### Resources
	
Full description of the configuration can be found in the [cdn-resources][cdn-resources] plugin.
	
	// true to enable the cdn plugin, false otherwise
	grails.resources.cdn.enabled=true
	
	// url of the content delivery network where the resources will be served from
	grails.resources.cdn.url="http://static.districthealth.moh.gov.rw/"
	
	// url where the resources will be saved
	grails.resources.work.dir="/var/www/static/"

##### Google analytics

For a full description of the plugin, see the [plugin page][google-analytics].

	// enables google analytics
	google.analytics.enabled = true
	
	// google analytics property ID
	google.analytics.webPropertyID = "<property_id>"
	
	// analytics custom tracking code
	google.analytics.customTrackingCode = [
		[_setDomainName: "www.example.org"], "_trackPageview"
	]
	
##### Location plugin

See [location plugin readme][chai-locations].

Introduction
---

DHSST is an analytical tool. It takes some data about certain data locations (could be a health center, a hospital, a school), and offers tool to display, manipulate and aggregate them along a certain location hierarchy (could be a country-province-district structure for example).
location. 

To understand this manual, it is important to know how the [location plugin][chai-locations] and [data plugin][chai-kevin-data] work. Please go through those readme first.
	
Data entry
---

Data can be entered into the system either by using the integrated survey tool, or by using the import functionality of the site.
	
### Survey

Data can be entered using the survey part of the site. The survey, instance of **Survey** class, is broken down into programs (**SurveyProgram** class), sections (**SurveySection** class) and questions (**SurveyQuestion** cf. below). A program contains several sections and each section can have 1 or more questions. A section is set as complete when all questions in the section are completed and valid. A program is set as complete when all sections in the program are complete.

All questions are linked to one or several **raw data elements** through a **FormElement**. When a user fills in a survey, values entered as saved as **FormEnteredValue**. When a program is submitted, all the values saved in **FormEnteredValue** are saved in the **RawDataElementValue**. It is important to understand the difference between unsubmitted and submitted program. Only when a program is submitted, do the values entered become available as **RawDataElementValue**. Before that, they are stored as **FormEnteredValue** and only available through the survey API and not through the [data plugin API][chai-kevin-data].

**FormElement** are therefore the survey equivalent of a **RawDataElement**, and **FormEnteredValue** the equivalent of **RawDataElementValue**. A **FormElement** holds a relationship to its corresponding **RawDataElement** and adds additional functionality in that it can also store corresponding *validation rules* and *skip rules* (see below for explanation), and a **FormEnteredValue** information on whether the value entered is valid or not, or whether it has been skipped. Validation and skip rules will be explained later.

A survey is linked to a certain **Period** via the *period* property. There can be several surveys for one period. When a value is copied, the *period* property from the survey is used as the raw data element value period. The diagram below explains what happens when values are copied:

	FormElement								RawDataElement
	|	|										|
	|	|										|
	|	FormEnteredValue - > (submit) - >	RawDataElementValue
	|		- value				->				- value
	|		- dataLocation		->				- dataLocation
	|
	Survey
		- period				->				- period

There can be more than one survey associated to one period, but one survey can be associated to only one period. Therefore to enter data for another period, one must use a different survey entirely.

All entities mentioned above can be assigned to one or more data location types, making them available or not to certain types. **It is therefore possible to create a different survey for each location type or group of data location types for the same period.**

#### Survey questions

There are 3 types of questions, **simple** questions, **checkbox** questions and **table** questions. All question types inherit the **SurveyQuestion** abstract class.

##### Simple Questions

Simple questions feed results to one data element, they are instances of the **SurveySimpleQuestion** class, which links to the corresponding **FormElement**.

If simple questions are associated to a data element of type ```list```, it means the user will be able to add or remove elements of the list from the survey itself. 

This allows the user to enumarete a list of item in the survey, for example the list of all staff. Let's say we have a data element of the following type :

	- list (type: map)
		- first_name: string
		- last_name: string
		- age: number

Defining a simple question associated to that data element will create on the survey what is commonly called a *nominative table*, i.e. a list of staff, where items (in this case individual staff members) can be added or removed and whose first name, last name and birthday can be specified using individual fields.

If the simple question is linked to a data element of type ```list``` or ```map```, then the labels of the individual fields are found in **FormElement** *formElementHeadersMap* one-to-many relationship using **FormElementHeaderMap** object whose *header* field gives the path to the individual field in the type (cf. [data plugin README][chai-kevin-data] *Type* section), and *names* is the label. In this example, we could define the following header maps :

	header				names[en]		names[fr]
	------				---------		---------
	[_].first_name		First name		Prénom	
	[_].last_name		Last name		Nom
	[_].age				Age				Age

##### Checkbox Questions

Checkbox questions feed results to one or more raw data elements of type bool and displays them as a list of checkbox options. The question itself is an instance of a **SurveyCheckboxQuestion** class and has a one-to-many relationship with **SurveyCheckboxOption**, which link to the corresponding **FormElement**. A survey checkbox option can only take form elements whose corresponding raw data element is of type ```bool```.

##### Table Questions

Table questions feed results to several data elements arranged in rows and columns. The question is an instance of the **SurveyTableQuestion** class and it has several **SurveyTableColumn** and **SurveyTableRow**. The **SurveyTableRow** object contains a map ```<SurveyTableColumn, FormElement>``` that links a certain column for a row to a certain form element. Each cell of the table therefore links to a different **FormElement** and therefore to a different **RawDataElement**.

#### Validation

On the survey, validation rules can be defined for individual fields. Those are instances of **FormValidationRule** and are attached to a **FormElement** and a certain *prefix*, indicating which field the rule applies on. A validation rule contains an expression. If the expression evaluates to ```false```, the field will be invalid and the *error message* defined in the rule will be displayed underneath the field.

The *expression* in the validation rule can refer to any **FormElement** using the syntax ```$<form_element_id>```. Let's use the example of staff above, and we'll put a validation rule on the ```age``` field that it should be bigger than 0 but smaller than 100. Let's say the FormElement has id ```13```:

	FormElement		expression				message[en]
	-----------		----------				-----------
	13				$13[_].age > 0 			The age must be between 0 and 100.
					and $13[_].age < 100

An expression doesn't necessarily have to refer to the form element to which the validation rule is attached. It can refer to any form element, also to a form element belonging to another survey. This way, it is possible to create a validation rule that refers to another year's survey. Using the same example, let's say we create a validation rule for the survey of the year after, saying that the age should have increased by 1. Let's say the form element for the new survey has id ```26```:

	FormElement		expression				message[en]
	-----------		----------				-----------
	26				$26[_].age 				The age must have increased by 1.
					== $13[_].age + 1

If the *allowOutlier* flag is set on a validation rule, then the user will have to possibility to click on a link to ignore the validation rule. The text "If you are sure this is correct, click here." will appear after the message.

#### Skip rules

The logic behind skip rules is almost the same as for validation rules, except skip rules are attached directly to a survey and can apply to more than one form element or question at the same time. A skip rule contains an *expression*. When that expression evaluates to true, the *formSkipRuleElementMaps* and *skippedQuestions* properties define what form element and what questions are skipped.

If a form element is skipped, a comma-delimited list of path can be specified. This allows one to target a specific field inside a complex type. Let's take the example above with form element ```13``` and define a skip rule that skips the age question if the first name is ```Susan```.

	expression			formElement		skippedFormElements
	----------			-----------		-------------------
	$13[_].first_name	13				[_].age
	== "Susan"

The skipped question property works similarly.

#### Progress calculation

When a survey is being filled out, progress is tracked using instances of the SurveyEnteredQuestion, SurveyEnteredSection and SurveyEnteredProgram classes. They are linked to their corresponding question, section and program and hold certain properties :

	SurveyEnteredQuestion
		- skipped (is the question skipped or not)
		- complete (have all the fields inside this question been filled)
		- invalid (true if one or more fields are invalid)
		
	SurveyEnteredSection
		- complete (is the section entirely completed, are all the fields complete)
		- invalid (true if one or more fields are invalid)
		- totalQuestions (number of questions asked)
		- completedQuestions (number of questions complete)
	
	SurveyEnteredProgram
		- complete (is the section entirely completed, are all the fields complete)
		- invalid (true if one or more fields are invalid)
		- closed (true if the program has been submitted)
		- totalQuestions (number of questions asked)
		- completedQuestions (number of questions complete)
		
Those fields are set whenever a value is saved using the SurveyPageService, or when the survey or section or program is refreshed. **It is not set when the survey is change. Therefore, changing a survey while data is being filled in will lead to inconsistencies.**

#### Operations on the survey

Below are the various operations that can be done on a survey, using either the **SurveySummaryController** or the **EditSurveyController**.

##### Refreshing the survey

Refreshing a survey is the opposite of submitting a program. It takes the values that are already saved as RawDataElementValue and puts them into FormEnteredValue, creating it if it does not exist, and overwriting the value if it exists and the *reset* flag is set. It also creates the SurveyEnteredQuestion, SurveyEnteredSection and SurveyEnteredProgram. 

Before rolling out a survey, it is advised to refresh the entire survey for all the data locations in the system as to create all the values in advance using **SurveySummaryController**.

	def refresh = {
		// refreshes the survey, copying the values from RawDataElementValue 
		// to FormEnteredValue, and takes the following flags:
		// reset - if set to true, resets the values in FormEnteredValue if they already exist.
		// closeIfComplete - if set to true, closes the programs that are complete and valid
	}

##### Saving a value

The **EditSurveyController** has an ajax action that can be used to save a value when it's been entered by the user:

	def saveValue = {
		// takes as input :
		//
		// location - the data location for which one or more values have changed
		// section - the section for which the value has changed - can be null
		// program - the program for which the value has changed
		// element - the form element for which the value has changed
		// suffix - the suffix to the field in the value (cf. data plugin doc)
		//
		// and one or more of those, depending on what operations is done, cf. below for more explanations
		// elements[<element_id>].value<suffix>
		
		// gives as output - in JSON :
		// programs: a list of SurveyEnteredProgram that changed, with the following info
		//	 id: the id of the program
		//   status: the status of the program
		//   totalQuestions: the number of questions
		//   completedQuestions: the number of questions complete and valid
		// sections: a list of SurveyEnteredSection that changed, with the following info
		//	 id: the id of the section
		//	 programId: the id of the program this section belongs to
		//	 invalid: true if the section is invalid
		//	 complete: true if the section is complete
		//   status: the status of the program
		//   totalQuestions: the number of questions
		//   completedQuestions: the number of questions complete and valid
		// questions: a list of SurveyEnteredQuestion that changed, with the following info
		//	 id: the id of the question
		//	 sectionId: the id of the section this question belongs to
		//	 complete: true if the question is complete
		//	 invalid: true if the question is invalid
		//	 skipped: true if the question is skipped		// elements: a list of FormEnteredElement that changed, with the following info
		//   id: the id of the element
		//   questionId: the id of the question this element belongs to
		//   skipped: a list of all skipped fields inside that element
		//   invalid: a list of all invalid fields inside that element
		//   nullPrefixes: a list of all null fields (no value entered)
	}
	
The controller calls the **SurveyPageService** modify method, whose role is to save the value, evaluate all validation and skip rules that are affected by the change and return a list of all FormEnteredValue, SurveyEnteredQuestion, SurveyEnteredSection and SurveyEnteredProgram whose properties changed because of the  value change. The action then returns a JSON version of that, which can be used by the survey Javascript.

There are a few tricky things to know when saving values, particularly when it comes to handling values of complex types ```list``` and ```map```. Let's first see how to change a value inside a list, then we'll see how to add and remove rows from a list. Let's take the example we used above which was using the following type:

	- list (type: map)
		- first_name: string
		- last_name: string
		- age: number

To change the first_name property of the first element of the list for data location of id ```10``` and in section ```2```, here is what would need to be sent to the *saveValue* action :

	Request parameter					Value
	-----------------					-----
	location							10
	section								2
	element								13
	suffix								[0].first_name
	elements[13].value[0].first_name	"Giselle"
	
Similarly, to add a row at the end of the list (say the list currently has 1 row) :

	Request parameter		Value		Remark
	-----------------		-----		------
	location				10
	section					2
	element					13
	suffix								the list we want to modify is at suffix "<empty>".
	elements[13].value		[0]			we need to repeat that one
	elements[13].value		[1]			this is the new row
	
To remove the first row (say the list currently has 2 rows) :


	Request parameter		Value		Remark
	-----------------		-----		------
	location				10
	section					2
	element					13
	suffix								the list we want to modify is at suffix "<empty>".
	elements[13].value		[1]			this is the row that already exist
										we just omit the row that we want to remove

##### Submitting a survey program

When a program is submitted, all values are copied from the **FormEnteredValue** to the corresponding **RawDataElementValue**. All values that are valid are copied as is, values that are invalid or skipped are set as a ```null``` value. The table below summarizes the different cases.

	FormEnteredValue			-			RawDataElementValue
	----------------						-------------------
	- value entered and valid				- value is copied
	- value entered and invalid				- value is set to null 
	- value not entered						- value is set to null
	- value skipped							- value is set to null

The *period* property of the **RawDataElementValue** is set to the period referred to by the survey, as explined above, and the *dataLocation* to whichever data location is filling out the survey.

After a program has been submitted, the values are saved as **RawDataElementValue** and therefore available to the reports (cf. below) component of the site.

To submit a program, the **EditSurveyController** provides a submit action:

	def submit = {
		// submits a program, given the following parameters
		// location: the data location for which to submit a program
		// program: the program to submit
	}	

Submitting a program closes the program, preventing all subsequent edits. If the administrator wants to reallow data entry on a submitted program, it can be reopen using the reopen action:

	def reopen = {
		// reopens a closed program, given the following parameters
		// location: the data location for which to reopen a program
		// program: the program to reopen
	}

##### Cloning a survey

As it can be a hassle to recreate a whole survey from scratch for a new period, existing surveys can be cloned using the **SurveyController** copy action :

	def copy = {
		// clones a survey, takes the following parameter
		// survey: the survey to copy
	}
	
Copying the survey will create a duplicate of all programs, sections, questions, form elements, validation and skip rules. As all those elements will have new IDs, cloning will also update all expressions to refere to the new IDs instead of the old ones. In cases where the IDs used do not belong to the survey being cloned (as could be the case for example if a validation rule is referring to another survey's form element), then the ID is left as is.

#### Survey javascript

TODO

### Import


#### Nominative data import

#### General data import


Reports
---

Planning
---

Users
---

Export
---

License
---

The DHSST is licensed under the terms of the [BSD 3-clause License][BSD 3-clause License].


[BSD 3-clause License]: http://www.w3.org/Consortium/Legal/2008/03-bsd-license.html
[dhsst.org]: http://www.dhsst.org/  
[gvm]: http://gvmtool.net/
[chai-locations]: http://github.com/fterrier/grails-chai-locations
[chai-kevin-data]: http://github.com/fterrier/grails-chai-kevin-data
[grails-mail]: http://grails.org/plugins/mail
[mail-on-exception]: http://github.com/fterrier/mail-on-exception
[rabbitmq-tasks]: http://github.com/fterrier/rabbitmq-tasks
[cdn-resources]: http://grails.org/plugins/cdn-resources
[google-analytics]: http://grails.org/plugins/analytics
[grails-doc]: http://grails.org/doc/2.1.x/guide/
