package com.simple2young2.jpx.oodesign.command;

/**
 * 
 * end point
 * 1. leader publish a command, and assigned to receiver
 * 2. receiver execute command
 * 3. receiver report
 * 
 * @author JianpanxiaLiang
 *
 */
public class CommandEndpoint {
	
	private Leader leader;
	
	public CommandEndpoint(Leader leader) {
		this.leader = leader;
	}
	
	public <T extends Command> void executeCommand(Class<T> command) {
		AbstractCommandReceiver<T> receiver = this.leader.<T>publishCommand(command);
		receiver.execute();
		receiver.report();
	}

}
