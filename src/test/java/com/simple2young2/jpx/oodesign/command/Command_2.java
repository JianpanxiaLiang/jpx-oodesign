package com.simple2young2.jpx.oodesign.command;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 
 * @author JianpanxiaLiang
 *
 */
@Receiver(Receiver_2.class)
public class Command_2 extends AbstractCommand {

	private static final String SELECT = "UPDATE #{table} SET #{columnValueModification} WHERE 1 = 1#{condition}";

	private String columnValueModification;
	
	private String condition;
	
	private ResultReceiver resultReceiver;
	
	private Object conditionBean;
	
	private Object valueBean;
	
	public Command_2() {}
	
	@Override
	public String toString() {
		this.columnValueModification = buildColumnValueModification();
		this.condition = buildCondition(conditionBean);
		Expression expression = this.getSpelExpressionParser().parseExpression(SELECT,this.getTemplateParserContext());
		return (String)expression.getValue(new StandardEvaluationContext(this));
	}
	
	private String buildColumnValueModification() {
		if(valueBean == null) {
			throw new RuntimeException("update value can not be null");
		}
		Class<?> clazz = this.valueBean.getClass();
		Mapping mapping = mapping(clazz);
		Map<String,String> propertyColumnMap = mapping.getPropertyColumn();
		Map<String,PropertyDescriptor> pdMap = collectPropertyDescriptor(clazz);
		
		int i = 0;
		int size = propertyColumnMap.size();
		int nextToLast = size - 2;
		StringBuilder columnValueModificationBuilder = new StringBuilder();
		for(Entry<String,String> entry : propertyColumnMap.entrySet()) {
			try {
				Object value = pdMap.get(entry.getKey()).getReadMethod().invoke(valueBean);
				if(value != null && !value.toString().isBlank()) {
					columnValueModificationBuilder.append(entry.getValue()).append(" = :").append(entry.getKey());
					if(i < nextToLast) {
						columnValueModificationBuilder.append(", ");
					}
				}
			}catch(Exception e) {
				// TODO 
			}finally {
				i++;
			}
		}
		
		return columnValueModificationBuilder.toString();
	}

	@Override
	public ResultReceiver getResultReceiver() {
		return resultReceiver;
	}
	

	public String getColumnValueModification() {
		return columnValueModification;
	}

	public String getCondition() {
		return condition;
	}


	public void setResultReceiver(ResultReceiver resultReceiver) {
		this.resultReceiver = resultReceiver;
		
	}
	
	public Object getConditionBean() {
		return conditionBean;
	}

	public void setConditionBean(Object conditionBean) {
		this.conditionBean = conditionBean;
	}

	public Object getValueBean() {
		return valueBean;
	}

	public void setValueBean(Object valueBean) {
		this.valueBean = valueBean;
	}

}
