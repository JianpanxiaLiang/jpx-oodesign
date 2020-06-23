package com.simple2young2.jpx.oodesign.command;

/**
 * 
 * @author JianpanxiaLiang
 *
 */
public interface Leader extends ResultReceiver {

	<T extends Command> AbstractCommandReceiver<T> publishCommand(Class<T> command);
	
}
