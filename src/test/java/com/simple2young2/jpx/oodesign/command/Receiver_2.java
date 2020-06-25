package com.simple2young2.jpx.oodesign.command;

/**
 * 
 * @author JianpanxiaLiang
 *
 */
public class Receiver_2 extends AbstractCommandReceiver<Command_2> {
	
	private Object result;

	@Override
	protected void execute() {
		Command_2 command = this.getCommand();
		String sql = command.toString();
		
	}

	@Override
	protected Object collectResult() {
		return result;
	}

}
