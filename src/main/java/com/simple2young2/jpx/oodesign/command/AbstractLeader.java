package com.simple2young2.jpx.oodesign.command;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author JianpanxiaLiang
 *
 */
public abstract class AbstractLeader implements Leader {
	
	private static final Map<Class<?>,Map<String,PropertyDescriptor>> PROPERTY_DESCRIPTOR_CACHE = new ConcurrentHashMap<Class<?>,Map<String,PropertyDescriptor>>();
	
	public AbstractLeader() {
		collectPropertyDescriptor(this.getClass());
	}

	@Override
	public <T extends Command> AbstractCommandReceiver<T> publishCommand(Class<T> command){
		T instance = this.buildCommand(command);
		AbstractCommandReceiver<T> receiver = this.choseReceiver(command);
        receiver.receive(instance);
        return receiver;
	}

	private <T extends Command> AbstractCommandReceiver<T> choseReceiver(Class<T> command){
		Receiver receiver = command.getAnnotation(Receiver.class);
		Class<? extends AbstractCommandReceiver> receiverClass = receiver.value();
		try {
			Constructor<? extends AbstractCommandReceiver> constructor = receiverClass.getConstructor();
			return constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private <T extends Command> T buildCommand(Class<T> command) {
		try {
			
			Constructor<T> constructor = command.getConstructor();
			T instance = constructor.newInstance();
			
			Map<String,PropertyDescriptor> commandPdMap = collectPropertyDescriptor(command);
			
			Map<String,PropertyDescriptor> leaderPdMap = collectPropertyDescriptor(this.getClass());
			
			String leaderProperty;
			CommandValue commandValue;
			PropertyDescriptor leaderPd;
			for(Entry<String,PropertyDescriptor> entry :commandPdMap.entrySet()){
				leaderPd = leaderPdMap.get(entry.getKey());
				if(leaderPd == null) {
					commandValue = entry.getValue().getReadMethod().getAnnotation(CommandValue.class);
					if(commandValue == null) {
						continue;
					}
					leaderProperty = commandValue.leaderProperty();
					leaderPd = leaderPdMap.get(leaderProperty);
					if(leaderPd == null) {
						continue;
					}
				}
				Method leaderMethod = leaderPd.getReadMethod();
				Object value = leaderMethod.invoke(this);
				entry.getValue().getWriteMethod().invoke(instance, value);
			}
			return instance;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private Map<String,PropertyDescriptor> collectPropertyDescriptor(Class<?> clazz) {
		Map<String,PropertyDescriptor> pdMap = PROPERTY_DESCRIPTOR_CACHE.get(clazz);
		if(pdMap == null || pdMap.isEmpty()) {
			try {
				BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
				PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
				
				String displayName;
				pdMap = new HashMap<String,PropertyDescriptor>();
				for(PropertyDescriptor pd:pds) {
					displayName = pd.getDisplayName();
		            if("class".equals(displayName)){
		                continue;
		            }
		            pdMap.put(displayName,pd);
				}
				PROPERTY_DESCRIPTOR_CACHE.put(clazz,pdMap);
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return pdMap;
	}

}
