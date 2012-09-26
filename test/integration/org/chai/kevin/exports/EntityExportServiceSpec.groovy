package org.chai.kevin.exports

import java.lang.reflect.Field
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.chai.kevin.Exportable;
import org.chai.kevin.IntegrationTests
import org.chai.kevin.Translation
import org.chai.kevin.dashboard.DashboardTarget
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.survey.SurveyQuestion
import org.chai.kevin.survey.SurveyCheckboxQuestion
import org.chai.kevin.survey.SurveySimpleQuestion
import org.chai.kevin.survey.SurveyTableQuestion
import org.chai.kevin.survey.SurveyElement
import org.chai.kevin.survey.SurveyIntegrationTests
import org.chai.kevin.util.Utils;

class EntityExportServiceSpec extends IntegrationTests {

	def entityExportService
	
	def "test for export entity"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = SurveyIntegrationTests.newSurvey(CODE(10), j(["en":"survey"]), period)
		def program = SurveyIntegrationTests.newSurveyProgram(CODE(11), j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(CODE(12), j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(CODE(13), j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = SurveyIntegrationTests.newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))		
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		def file = entityExportService.getExportFile("file", SurveySimpleQuestion.class)
		def zipFile = Utils.getZipFile(file, "file")
		
		then:
		zipFile.exists() == true
		zipFile.length() > 0
	}
	
	def "test for valid export filename"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = SurveyIntegrationTests.newSurvey(CODE(10), j(["en":"survey"]), period)
		def program = SurveyIntegrationTests.newSurveyProgram(CODE(11), j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(CODE(12), j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(CODE(13), j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = SurveyIntegrationTests.newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))		
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		def file = entityExportService.getExportFilename(SurveySimpleQuestion.class)
		
		then:
		file.startsWith("SurveySimpleQuestion_")
	}	
	
	def "test for export multiple files in zip file"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = SurveyIntegrationTests.newSurvey(CODE(10), j(["en":"survey"]), period)
		def program = SurveyIntegrationTests.newSurveyProgram(CODE(11), j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(CODE(12), j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(CODE(13), j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = SurveyIntegrationTests.newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		def entityClazz = [SurveyCheckboxQuestion.class, SurveySimpleQuestion.class, SurveyTableQuestion.class] 
		
		when:
		List<String> filenames = new ArrayList<String>();
		List<File> csvFiles = new ArrayList<File>();
		for(Class clazz : entityClazz){
			String filename = entityExportService.getExportFilename(clazz);
			filenames.add(filename);
			csvFiles.add(entityExportService.getExportFile(filename, clazz));
		}
		String zipFilename = StringUtils.join(filenames, "_")
		def zipFile = Utils.getZipFile(csvFiles, zipFilename)
		
		then:
		zipFilename.startsWith("SurveyCheckboxQuestion__SurveySimpleQuestion__SurveyTableQuestion_")
		zipFile.exists() == true
		zipFile.length() > 0
	}
	
	def "test for entity header sort"(){
		setup:		
		def entitySurveyQuestionFieldHeaders = []		
		Class<?> headerClass = SurveyQuestion.class;
		while(headerClass != null && headerClass != Object.class){				
			Field[] classFields = headerClass.getDeclaredFields();
			for(Field field : classFields){
				if(field.getName().equalsIgnoreCase("id")) continue;
				entitySurveyQuestionFieldHeaders.add(field);
			}
			headerClass = headerClass.getSuperclass();
		}
		
		def entityDashboardTargetFieldHeaders = []
		headerClass = DashboardTarget.class;
		while(headerClass != null && headerClass != Object.class){				
			Field[] classFields = headerClass.getDeclaredFields();
			for(Field field : classFields){
				if(field.getName().equalsIgnoreCase("id")) continue;
				entityDashboardTargetFieldHeaders.add(field);
			}
			headerClass = headerClass.getSuperclass();
		}
		
		when:
		Collections.sort(entitySurveyQuestionFieldHeaders, EntityHeaderSorter.BY_FIELD())
		def surveyQuestionHeaders = entitySurveyQuestionFieldHeaders.collect { it.getName() }
		Collections.sort(entityDashboardTargetFieldHeaders, EntityHeaderSorter.BY_FIELD())
		def dashboardTargetHeaders = entityDashboardTargetFieldHeaders.collect { it.getName() }
		
		then:
		surveyQuestionHeaders.equals(["code", "names", "order", "section", "typeCodeString", "descriptions"])
		dashboardTargetHeaders.equals(["code", "names", "order", "program", "weight", "data", "descriptions"])
	}	
	
	
	def "test entity is exportable"(){
		when:
		def ie = new IsExportableEntity()
		def clazz = Utils.isExportable(ie.class)
		
		then:
		clazz != null
	}
	
	def "test entity is exportable primitive"(){
		when:
		def clazz = Utils.isExportablePrimitive(Integer.class)
		
		then:
		clazz != null
	}
	
	def "test entity is not exportable"(){
		when:
		def ne = new IsNotExportableEntity()
		def clazz = Utils.isExportable(ne.class)
		
		then:
		clazz == null		
	}
	
	def "test entity fields are exportable"(){
		when:
		def today =new Date()
		def ie = new IsExportableEntity("ieCode1", 1, today)		 			
		List<Field> fields = new ArrayList<Field>();		
		Class<?> headerClass = ie.class;
		while(headerClass != null && headerClass != Object.class){
			Field[]classFields = headerClass.getDeclaredFields();
			for(Field field : classFields){
				fields.add(field);
			}
			headerClass = headerClass.getSuperclass();
		}
		Collections.sort(fields, EntityHeaderSorter.BY_FIELD());				
		def entityData = entityExportService.getEntityData(ie, fields)
		
		then:
		entityData[0].equals("ieCode1")
		entityData[1].equals("1")
		entityData[2].equals(Utils.formatDate(today))
		entityData[3].equals("")
		
		when:
		ie.trans = new Translation(j(["en":"English", "fr":"French"]))
		entityData = entityExportService.getEntityData(ie, fields)
		
		then:
		entityData[0].equals("ieCode1")
		entityData[1].equals("1")
		entityData[2].equals(Utils.formatDate(today))
		entityData[3].equals(ie.trans.toExportString())
	}	
	
	def "test entity fields that are exportable and not exportable"(){
		when:
		def te = new TestExportableEntity("testCode")					 
		List<Field> fields = new ArrayList<Field>();
		Class<?> headerClass = te.class;
		while(headerClass != null && headerClass != Object.class){
			Field[] classFields = headerClass.getFields();
			classFields = headerClass.getDeclaredFields();
			for(Field field : classFields){
				fields.add(field);
			}
			headerClass = headerClass.getSuperclass();
		}
		Collections.sort(fields, EntityHeaderSorter.BY_FIELD());
		def entityData = entityExportService.getEntityData(te, fields)
		
		then:
		entityData[0].equals("testCode")
		entityData[1].equals("")
		entityData[2].equals("")
		
		when:
		te.iee = new IsExportableEntity("ieCode", 1, new Date())
		te.inee = new IsNotExportableEntity()
		entityData = entityExportService.getEntityData(te, fields)
		
		then:
		entityData[0].equals("testCode")
		entityData[1].equals("[~ieCode~]")
		entityData[2].equals(Utils.VALUE_NOT_EXPORTABLE)
	}
	
	def "test entity fields that are exportable lists and not exportable lists"(){
		when:
		def te = new TestExportableEntities("testCode")					 
		List<Field> fields = new ArrayList<Field>();
		Class<?> headerClass = te.class;
		while(headerClass != null && headerClass != Object.class){
			Field[] classFields = headerClass.getFields();
			classFields = headerClass.getDeclaredFields();
			for(Field field : classFields){
				fields.add(field);
			}
			headerClass = headerClass.getSuperclass();
		}
		Collections.sort(fields, EntityHeaderSorter.BY_FIELD());
		def entityData = entityExportService.getEntityData(te, fields)
		
		then:
		entityData[0].equals("testCode")
		entityData[1].equals("")
		entityData[2].equals("")
		
		when:
		te.listIee = [new IsExportableEntity("ieCode1", 1, new Date())] 
		te.listInee = [new IsNotExportableEntity(), new IsNotExportableEntity()]		
		entityData = entityExportService.getEntityData(te, fields)
		
		then:
		entityData[0].equals("testCode")
		entityData[1].equals("[[~ieCode1~]]")
		entityData[2].equals(Utils.VALUE_NOT_EXPORTABLE)
		
		when:
		te.listIee = [new IsExportableEntity("ieCode1", 1, new Date()), new IsExportableEntity("ieCode2", 2, new Date())]
		te.listInee = [new IsNotExportableEntity(), new IsNotExportableEntity()]
		entityData = entityExportService.getEntityData(te, fields)
		
		then:
		entityData[0].equals("testCode")
		entityData[1].equals("[[~ieCode1~], [~ieCode2~]]")
		entityData[2].equals(Utils.VALUE_NOT_EXPORTABLE)		
	}	
	
	
	public class IsExportableEntity extends Object implements Exportable {
		
		public Integer num;
		public String code;
		public Date dat;
		public Translation trans;
		
		public IsExportableEntity() {
			this.num = 0;
			this.code = "";
			this.dat = new Date();
			this.trans = new Translation();
		}
		
		public IsExportableEntity(String code, Integer num, Date dat) {
			this.num = num;
			this.code = code;
			this.dat = dat;
		}
		
		public String toExportString() {
			return "[" + Utils.formatExportCode(code) + "]";
		}
	}
	
	public class IsNotExportableEntity extends Object {
		IsNotExportableEntity() { }
	}	
	
	public class TestExportableEntity extends Object implements Exportable {
		
		public String code;
		public IsExportableEntity iee;
		public IsNotExportableEntity inee;
		
		public TestExportableEntity(String code) {
			this.code = code;
		}
				
		public String toExportString() {
			return "[" + Utils.formatExportCode(code) + "]";
		}
	}
	
	public class TestExportableEntities extends Object implements Exportable {
		
		public String code;
		public List<IsExportableEntity> listIee;
		public List<IsNotExportableEntity> listInee;
		
		public TestExportableEntities(String code) {
			this.code = code;
		}
			
		public String toExportString() {
			return "[" + Utils.formatExportCode(code) + "]";
		}
	}
}
