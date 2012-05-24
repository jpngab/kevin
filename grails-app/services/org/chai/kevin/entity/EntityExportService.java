package org.chai.kevin.entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LanguageService;
import org.chai.kevin.entity.export.EntityHeaderSorter;
import org.chai.kevin.entity.export.Exportable;
import org.chai.kevin.util.Utils;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class EntityExportService {
	
	private static final Log log = LogFactory.getLog(EntityExportService.class);
	
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}		
	
	private final static String CSV_FILE_EXTENSION = ".csv";	
	
	public String getExportFilename(Class<?> clazz){
		String exportFilename = clazz.getSimpleName().replaceAll("[^a-zA-Z0-9]", "") + "_";
		return exportFilename;
	}
	
	@Transactional(readOnly=true)
	public File getExportFile(String filename, Class<?> clazz) throws IOException { 				
		
		File csvFile = File.createTempFile(filename, CSV_FILE_EXTENSION);
		
		FileWriter csvFileWriter = new FileWriter(csvFile);
		ICsvListWriter writer = new CsvListWriter(csvFileWriter, CsvPreference.EXCEL_PREFERENCE);
		try {			
			
			// headers
			List<Field> entityFieldHeaders = new ArrayList<Field>();			
			Class<?> headerClass = clazz;
			while(headerClass != null && headerClass != Object.class){				
				Field[] classFields = headerClass.getDeclaredFields();
				for(Field field : classFields){
					entityFieldHeaders.add(field);
				}
				headerClass = headerClass.getSuperclass();
			}			
			
			//TODO custom headers/values
			//ability to add custom headers
			//and a custom "handle" method to add the custom values to each row			
			
			Collections.sort(entityFieldHeaders, EntityHeaderSorter.BY_FIELD());			
			List<String> entityHeaders = new ArrayList<String>();
			for(Field field : entityFieldHeaders){
				entityHeaders.add(field.getName());				
			}
			
			if(entityHeaders.toArray(new String[0]) != null)
				writer.writeHeader(entityHeaders.toArray(new String[0]));									
			
			//entities
			List<Object> entities = getEntities(clazz);
			for(Object entity : entities){
				if (log.isDebugEnabled()) log.debug("getExportFile(entity="+entity+")");				
				List<String> entityData = getEntityData(entity, entityFieldHeaders);
				
				//TODO custom headers/values
				//ability to add custom headers
				//and a custom "handle" method to add the custom values to each row
				
				if(entityData != null && !entityData.isEmpty())
					writer.write(entityData);
			}
			
		} catch (IOException ioe){
			// TODO is this good ?
			throw ioe;
		} finally {
			writer.close();
		}
		
		return csvFile;
	}	
	
	public List<Object> getEntities(Class<?> clazz){
		List<Object> entities = new ArrayList<Object>();
		entities = (List<Object>) sessionFactory.getCurrentSession().createCriteria(clazz).list();
		return entities;
	}
	
	public List<String> getEntityData(Object entity, List<Field> fields){
		List<String> entityData = new ArrayList<String>();
		for(Field field : fields){
			Object value = null;			
			try {
				boolean isNotAccessible = false;
				if(!field.isAccessible()){ 
					field.setAccessible(true);
					isNotAccessible = true;
				}
				
				value = field.get(entity);								
				
				String exportValue = "";	
				
				if(value != null){
					
					Class<?> valueClazz = field.getType();
					Class<?> innerClazz = null;
					
					//value is not a list
					boolean isAssignable = Exportable.class.isAssignableFrom(valueClazz);				
					Class<?>[] clazzInterfaces = valueClazz.getInterfaces();
					Class<?> exportableClazz = valueClazz;					
					
					//value is a list
					List<Object> listValues = null;
					if(valueClazz.equals(List.class)){
						listValues = (List<Object>) value;			
						ParameterizedType type = (ParameterizedType) field.getGenericType();
						innerClazz = (Class<?>) type.getActualTypeArguments()[0];					
						isAssignable = Exportable.class.isAssignableFrom(innerClazz);
						clazzInterfaces = innerClazz.getInterfaces();
						exportableClazz = innerClazz;
					}					
					
					//value is exportable
					if(isAssignable && Arrays.asList(clazzInterfaces).contains(Exportable.class)){
						
						//value is a list
						if(listValues != null){
							exportValue = getExportValues(listValues);
						}
						//value is not a list
						else{
							if(value instanceof Exportable){
								Exportable exportable = (Exportable) value;
								exportValue = getExportValue(exportable);	
							}						
						}		
					}
					//value is a primitive or 'wrapper to primitive' or string type
					else if(exportableClazz.isPrimitive() || ClassUtils.wrapperToPrimitive(exportableClazz) != null || exportableClazz.equals(String.class)){
						exportValue = value.toString();
					}
					//value is not exportable or a primitive type
					else {
						//exportValue = value.toString();
						exportValue = "null";
					}
				}
				
				entityData.add(exportValue);
				
				if(isNotAccessible)
					field.setAccessible(false);	
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return entityData;
	}

	public String getExportValue(Exportable exportable){
		String result = exportable.toExportString();
		return result;
	}
	
	public String getExportValues(List<Object> values){
		List<String> exportValues = new ArrayList<String>();
		for(Object value : values){
			if(value instanceof Exportable){
				Exportable exportableValue = (Exportable) value;
				String exportValue = getExportValue(exportableValue);
				exportValues.add(exportValue);
			}
		}
		String result = "[" + StringUtils.join(exportValues, ", ") + "]";
		return result;
	}
}