package com.simple2young2.jpx.oodesign.command;

import org.junit.Test;

/**
 * 
 * @author JianpanxiaLiang
 *
 */
public class TestCommandEndpoint {
	
	@Test
	public void testExecuteCommand() {
		
		Backlog po = new Backlog();
		po.setId(1);
		
		LeaderImpl leader = new LeaderImpl();
		leader.setConditionBean(po);
		leader.setPoClass(po.getClass());
		CommandEndpoint endpoint = new CommandEndpoint(leader);
		endpoint.executeCommand(Command_1.class);
	}

}
