/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.acit.internal;

import java.util.Dictionary;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.openhab.binding.acit.ActiveItemsBindingProvider;
import org.openhab.core.binding.AbstractBinding;
import org.openhab.core.binding.BindingProvider;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.*;

import org.openhab.binding.acit.internal.ShutterManager;
/**
 * The RefreshService polls all configured hostnames with a configurable 
 * interval and post all values to the internal event bus. The interval is 1 
 * minute by default and can be changed via openhab.cfg. 
 * 
 * @author Andrea Giacosi
 * @since 1.7.0
 */
public class ActiveItemsBinding extends AbstractBinding<ActiveItemsBindingProvider> implements ManagedService {

	private static final Logger logger = LoggerFactory.getLogger(ActiveItemsBinding.class);

	/**
	 * actors are kept in a Map whose key is itemBehind
	 */
	private Map<String,ActorRef> actors = new ConcurrentHashMap<String, ActorRef>(new WeakHashMap<String, ActorRef>());
	
	//private boolean runTest = true;
	
	@Override
	public void receiveCommand(String itemName, Command command) {
		// does any provider contain a binding config?
		if (providesBindingFor(itemName)) {
			//if(runTest) {
			//	runTest = false;
			//	if(command instanceof org.openhab.core.library.types.UpDownType) {
			//		command = new org.openhab.core.library.types.PercentType(96);
			//	}
			//}
			logger.debug("ANDREA: active Item cmd="+command+" for "+itemName);
			internalReceiveCommand(itemName, command);
		}
		else {
			String itemBehind = itemName;
			ActorRef actor = getActorFromItemBehind(itemBehind);
			if(actor != null) {
				// notify the proper actor that something is happening on his item behind
				logger.debug("ANDREA: actor found forward active Item Behind cmd="+command+" for "+itemBehind);
				ShutterManager.forwardItemBehindCmd(actor, command);
				//warning: verificare che non riceva il messaggio che ha inviato
			}
		}
		
	}

	@Override
	public void receiveUpdate(String itemName, State newState) {
		// does any provider contain a binding config?
		if (providesBindingFor(itemName)) {
			logger.debug("ANDREA: active Item state="+newState+" for "+itemName);
			internalReceiveUpdate(itemName, newState);
		}
		else {
			String itemBehind = itemName;
			ActorRef actor = getActorFromItemBehind(itemBehind);
			if(actor != null) {
				// notify the proper actor that something is happening on his item behind
				logger.debug("ANDREA: actor found forward active Item Behind state="+newState+" for "+itemBehind);
				ShutterManager.forwardItemBehindStatus(actor, newState);
				//warning: verificare che non riceva il messaggio che ha inviato
			}
		}
		
	}
	//private ShutterManager shuttersManager = new ShutterManager();
	/**
	 * @{inheritDoc}
	 */
	@Override
	public void internalReceiveCommand(String itemName, Command command) {
		// forward the request to the actor (create if does not exist)
		for(ActiveItemsBindingProvider provider:providers) {
			ActorRef actor = getOrCreateActorFromActiveItem(itemName, provider);
			ShutterManager.forwardItemCmd(actor, command);
		}		
	}

	private ActorRef getActorFromItemBehind(String itemBehind) {
		ActorRef actor = null;
		for(ActiveItemsBindingProvider provider:providers) {
			for(String activeItemName:provider.getItemNames()) {
				if(provider.getItemBehindName(activeItemName).equals(itemBehind)) {
					// we found one of our active items that is using the itemBehind as 
					// its itemBehind
					actor = getOrCreateActorFromActiveItem(activeItemName, provider);
					break;
				}
			}
		}		
		return actor;
	}
	
	private ActorRef getOrCreateActorFromActiveItem(String itemName, ActiveItemsBindingProvider provider) {
		String itemBehind = provider.getItemBehindName(itemName);
		ActorRef actor = actors.get(itemBehind);
		if(actor == null) { // not created yet, let's create it
			actor = ShutterManager.getActorForItem(
					itemName, 
					provider,
					eventPublisher);
			actors.put(itemBehind, actor);
		}
		logger.debug("ANDREA: actor found for item "+itemName+" forward active Item "+itemBehind);
		return actor;
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveUpdate(String itemName, State newState) {
		for(ActiveItemsBindingProvider provider:providers) {
			ActorRef actor = getOrCreateActorFromActiveItem(itemName, provider);
			ShutterManager.forwardItemStatus(actor, newState);
		}
	}
	
//	@Override
//	public boolean providesBindingFor(String itemName) {
//		return true;
//	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void allBindingsChanged(BindingProvider provider) {
		super.allBindingsChanged(provider);
		// shutdown all the actors and wait for
		for(String itemName:provider.getItemNames()) {
			resetActor(itemName);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bindingChanged(BindingProvider provider, String itemName) {
		super.bindingChanged(provider, itemName);
		// restart/reset the actor associated to this item
		resetActor(itemName);
	}

	private void resetActor(String itemName) {
		ActorRef actor = actors.remove(itemName);
		if(actor != null) {
			actor.tell(PoisonPill.getInstance(), null);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	public void updated(Dictionary config) throws ConfigurationException {
		if (config != null) {
			String timeoutString = (String) config.get("timeout");
			if (timeoutString != null && !timeoutString.isEmpty()) {
			}
			
			String refreshIntervalString = (String) config.get("refresh");
			if (refreshIntervalString != null && !refreshIntervalString.isEmpty()) {
			}		
		}
	}
	
}
