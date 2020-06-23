package com.simple2young2.jpx.oodesign.command;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * example 1
 * 
 * @author JianpanxiaLiang
 *
 */
@Receiver(Receiver_1.class)
public class Command_1 implements Command {
	
	private static final Map<Class<?>,Map<String,PropertyDescriptor>> PROPERTY_DESCRIPTOR_CACHE = new ConcurrentHashMap<Class<?>,Map<String,PropertyDescriptor>>();
	
	private static final Map<Class<?>,Mapping> PO_DATABASE_MAPPING = new ConcurrentHashMap<Class<?>,Mapping>();
	
	private static final TemplateParserContext TEMPLATE_PARSER_CONTEXT = new TemplateParserContext();
	
	private static final SpelExpressionParser PARSER = new SpelExpressionParser();
	
	private static final String SELECT = "SELECT #{columnAsAlias} FROM #{table} WHERE 1 = 1#{condition}";

	private String columnAsAlias;
	
	private String table;
	
	private String condition;
	
	private ResultReceiver resultReceiver;
	
	private Class<?> poClass;
	
	private Object conditionBean;
	
	public Command_1() {}
	
	@Override
	public String toString() {
		Mapping mapping = mapping(poClass);
		this.columnAsAlias = mapping.getColumnAsAlias();
		this.table = mapping.getTable();
		this.condition = buildCondition(conditionBean);
		Expression expression = PARSER.parseExpression(SELECT,TEMPLATE_PARSER_CONTEXT);
		return (String)expression.getValue(new StandardEvaluationContext(this));
	}
	
	private String buildCondition(Object bean) {
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
	
	private Mapping mapping(Class<?> clazz) {
		Mapping mapping = PO_DATABASE_MAPPING.get(clazz);
		if(mapping != null) {
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
		
		mapping = new Mapping();
		mapping.setColumnAsAlias(columnAsAliasBuilder.toString());
		mapping.setTable(camel2Snake(clazz.getSimpleName(),'_',true));
		mapping.setPoClass(clazz);
		mapping.setPropertyColumn(propertyColumn);
		PO_DATABASE_MAPPING.put(clazz, mapping);
		return mapping;
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
	
	@Override
	public ResultReceiver getResultReceiver() {
		return resultReceiver;
	}

	public String getColumnAsAlias() {
		return columnAsAlias;
	}

	public String getTable() {
		return table;
	}

	public String getCondition() {
		return condition;
	}


	public void setResultReceiver(ResultReceiver resultReceiver) {
		this.resultReceiver = resultReceiver;
		
	}

	public Class<?> getPoClass() {
		return poClass;
	}

	public void setPoClass(Class<?> poClass) {
		this.poClass = poClass;
	}
	
	
	
	public Object getConditionBean() {
		return conditionBean;
	}

	public void setConditionBean(Object conditionBean) {
		this.conditionBean = conditionBean;
	}

	private Map<String,PropertyDescriptor> collectPropertyDescriptor(Class<?> clazz) {
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
