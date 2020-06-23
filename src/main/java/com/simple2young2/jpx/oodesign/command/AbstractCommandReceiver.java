package com.simple2young2.jpx.oodesign.command;

/**
 * 
 * @author JianpanxiaLiang
 *
 * @param <T>
 */
public abstract class AbstractCommandReceiver<T extends Command> {
	
	private T command;
	
	protected abstract Object collectResult();
	
	protected void report() {
		this.command.getResultReceiver().receive(collectResult());
	}


	protected abstract void execute();

	public void receive(T command) {
		this.command = command;
	}

	public T getCommand() {
		return command;
	}

	public void setCommand(T command) {
		this.command = command;
	}

}
