package com.simple2young2.jpx.oodesign.command;

/**
 * 
 * @author JianpanxiaLiang
 *
 */
public class Receiver_1 extends AbstractCommandReceiver<Command_1> {
	

	@Override
	protected void execute() {
		Command_1 command = this.getCommand();
		System.out.print(command.toString());
	}

	@Override
	protected Object collectResult() {
		return null;
	}
	
	

}
