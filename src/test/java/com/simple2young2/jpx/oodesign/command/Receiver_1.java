package com.simple2young2.jpx.oodesign.command;

/**
 * 
 * @author JianpanxiaLiang
 *
 */
public class Receiver_1 extends AbstractCommandReceiver<Command_1> {
	
	private Object result;

	@Override
	protected void execute() {
		Command_1 command = this.getCommand();
		String sql = command.toString();
		
		// Here is a problem.
		// I need a data source here.
		// I have no idea to put database properties into this instance.
		// Cause no one hold receiver reference out side command end point.
		// Should i remove the command end point ?
		// Even though the command end point was removed, it still has a problem.
		// That is, there no way to put database properties into this instance until convert from abstract receiver to concrete receiver.
		// So, is this a bad design?
	}

	@Override
	protected Object collectResult() {
		return result;
	}
	
	

}
