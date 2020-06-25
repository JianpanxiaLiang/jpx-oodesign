package com.simple2young2.jpx.oodesign.command;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * example 1
 * 
 * @author JianpanxiaLiang
 *
 */
@Receiver(Receiver_1.class)
public class Command_1 extends AbstractCommand {
	
	private static final String SELECT = "SELECT #{columnAsAlias} FROM #{table} WHERE 1 = 1#{condition}";

	private String columnAsAlias;
	
	private String condition;
	
	private ResultReceiver resultReceiver;
	
	private Object conditionBean;
	
	public Command_1() {}
	
	@Override
	public String toString() {
		Mapping mapping = mapping(this.getPoClass());
		this.columnAsAlias = mapping.getColumnAsAlias();
		this.condition = buildCondition(conditionBean);
		Expression expression = this.getSpelExpressionParser().parseExpression(SELECT,this.getTemplateParserContext());
		return (String)expression.getValue(new StandardEvaluationContext(this));
	}
	
	@Override
	public ResultReceiver getResultReceiver() {
		return resultReceiver;
	}

	public String getColumnAsAlias() {
		return columnAsAlias;
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

	
}
