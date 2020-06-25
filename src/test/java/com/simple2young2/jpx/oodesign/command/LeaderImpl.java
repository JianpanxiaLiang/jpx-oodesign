package com.simple2young2.jpx.oodesign.command;

/**
 * 
 * @author JianpanxiaLiang
 *
 */
public class LeaderImpl extends AbstractLeader {
	
	private Class<?> poClass;
	
	private ResultReceiver resultReceiver;
	
	private Object conditionBean;
	
	private Object valueBean;
	
	public LeaderImpl() {
		this.resultReceiver = this;
	}

	@Override
	public void receive(Object result) {
		
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

	public ResultReceiver getResultReceiver() {
		return resultReceiver;
	}

	public void setResultReceiver(ResultReceiver resultReceiver) {
		this.resultReceiver = resultReceiver;
	}

	public Object getValueBean() {
		return valueBean;
	}

	public void setValueBean(Object valueBean) {
		this.valueBean = valueBean;
	}

}
