package com.simple2young2.jpx.oodesign.command;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * 
 * @author a1234
 *
 */
public abstract class AbstractCommand implements Command {
	
	private static final Map<Class<?>,Map<String,PropertyDescriptor>> PROPERTY_DESCRIPTOR_CACHE = new ConcurrentHashMap<Class<?>,Map<String,PropertyDescriptor>>();
	
	private static final Map<Class<?>,Mapping> PO_DATABASE_MAPPING = new ConcurrentHashMap<Class<?>,Mapping>();
	
	private static final TemplateParserContext TEMPLATE_PARSER_CONTEXT = new TemplateParserContext();
	
	private static final SpelExpressionParser PARSER = new SpelExpressionParser();
	
	private String table;
	
	private Class<?> poClass;
	
	protected SpelExpressionParser getSpelExpressionParser() {
		return PARSER; 
	}
	
	protected TemplateParserContext getTemplateParserContext() {
		return TEMPLATE_PARSER_CONTEXT; 
	}

	protected String buildCondition(Object bean) {
		if(bean == null) {
			return "";
		}
		
		Mapping mapping = mapping(bean.getClass());
		Map<String,String> propertyColumnMap = mapping.getPropertyColumn();
		Map<String,PropertyDescriptor> pdMap = collectPropertyDescriptor(bean.getClass());
		
		StringBuilder conditionBuilder = new StringBuilder();
		for(Entry<String,String> entry : propertyColumnMap.entrySet()) {
			try {
				Object value = pdMap.get(entry.getKey()).getReadMethod().invoke(bean);
				if(value != null && !value.toString().isBlank()) {
					conditionBuilder.append(" AND ").append(entry.getValue()).append(" = :").append(entry.getKey());
				}
			}catch(Exception e) {
				
			}
		}
		
		return conditionBuilder.toString();
	}
	
	protected Mapping mapping(Class<?> clazz) {
		Mapping mapping = PO_DATABASE_MAPPING.get(clazz);
		if(mapping != null) {
			this.table = mapping.getTable();
			return mapping;
		}
		
		Map<String,PropertyDescriptor> pdMap = collectPropertyDescriptor(clazz);
		
		
		int i = 0;
		String column;
		int size = pdMap.size();
		Map<String,String> propertyColumn = new HashMap<String,String>();
		StringBuilder columnAsAliasBuilder = new StringBuilder();
		
		for(Entry<String,PropertyDescriptor> entry :pdMap.entrySet()){
			column = camel2Snake(entry.getKey(),'_',true);
			propertyColumn.put(entry.getKey(),column);
			columnAsAliasBuilder.append(column).append(" AS ").append(entry.getKey());
			if(i < size - 1) {
				columnAsAliasBuilder.append(",");
			}
			i++;
		}
		
		this.table = camel2Snake(poClass.getSimpleName(),'_',true);
		mapping = new Mapping();
		mapping.setColumnAsAlias(columnAsAliasBuilder.toString());
		mapping.setTable(this.table);
		mapping.setPoClass(poClass);
		mapping.setPropertyColumn(propertyColumn);
		PO_DATABASE_MAPPING.put(clazz, mapping);
		return mapping;
	}
	
	protected Map<String,PropertyDescriptor> collectPropertyDescriptor(Class<?> clazz) {
		Map<String,PropertyDescriptor> pdMap = PROPERTY_DESCRIPTOR_CACHE.get(clazz);
		if(pdMap == null || pdMap.isEmpty()) {
			try {
				BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
				PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
				
				String displayName;
				pdMap = new HashMap<String,PropertyDescriptor>();
				for(PropertyDescriptor pd:pds) {
					displayName = pd.getDisplayName();
		            if("class".equals(displayName)){
		                continue;
		            }
		            pdMap.put(displayName,pd);
				}
				PROPERTY_DESCRIPTOR_CACHE.put(clazz,pdMap);
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return pdMap;
	}
	
	private String camel2Snake(String camel, Character delimiter, boolean uppercase){
        StringBuilder snakeBuilder = new StringBuilder("");
        if(uppercase){
            snakeBuilder.append(Character.toUpperCase(camel.charAt(0)));
        }else{
            snakeBuilder.append(Character.toLowerCase(camel.charAt(0)));
        }
        int len = camel.length();
        for (int i = 1; i < len; i++){
            if(Character.isUpperCase(camel.charAt(i))){
                snakeBuilder.append(delimiter);
            }
            if(uppercase){
                snakeBuilder.append(Character.toUpperCase(camel.charAt(i)));
            }else{
                snakeBuilder.append(Character.toLowerCase(camel.charAt(0)));
            }
        }
        return snakeBuilder.toString();
    }
	
	public String getTable() {
		return table;
	}
	
	public Class<?> getPoClass() {
		return poClass;
	}

	public void setPoClass(Class<?> poClass) {
		this.poClass = poClass;
	}



	public static class Mapping{
		
		private Map<String,String> propertyColumn;
		
		private String columnAsAlias;
		
		private String table;
		
		private Class<?> poClass;

		public String getColumnAsAlias() {
			return columnAsAlias;
		}

		public void setColumnAsAlias(String columnAsAlias) {
			this.columnAsAlias = columnAsAlias;
		}

		public String getTable() {
			return table;
		}

		public void setTable(String table) {
			this.table = table;
		}

		public Class<?> getPoClass() {
			return poClass;
		}

		public void setPoClass(Class<?> poClass) {
			this.poClass = poClass;
		}

		public Map<String, String> getPropertyColumn() {
			return propertyColumn;
		}

		public void setPropertyColumn(Map<String, String> propertyColumn) {
			this.propertyColumn = propertyColumn;
		}
		
	}

}
