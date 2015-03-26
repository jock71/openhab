/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.acit.internal;

import java.util.Hashtable;

import org.openhab.binding.acit.ActiveItemsBindingProvider;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;


/**
 * <p>This class can parse information from the generic binding format and 
 * provides ActiveItem binding information from it. It registers as a 
 * {@link ActiveItemBindingProvider} service as well.</p>
 * 
 * <p>Here are some examples for valid binding configuration strings:
 * <ul>
 * 	<li><code>{ acit="Lights_kitchen" }</code> - which binds the active item to Lights_kitchen item</li>
 * </ul>
 * 
 * @author Andrea Giacosi
 * 
 * @since 1.7.0
 */
public class ActiveItemsGenericBindingProvider extends AbstractGenericBindingProvider implements ActiveItemsBindingProvider {

	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "acit";
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
	//	if (!(item instanceof SwitchItem || item instanceof StringItem)) {
	//		throw new BindingConfigParseException("item '" + item.getName()
	//				+ "' is of type '" + item.getClass().getSimpleName()
	//				+ "', only Switch- and StringItems are allowed - please check your *.items configuration");
	//	}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item, String bindingConfig) throws BindingConfigParseException {
		
		super.processBindingConfiguration(context, item, bindingConfig);
		
		String[] configParts = bindingConfig.trim().split(";");
		if (configParts.length < 1) {
			throw new BindingConfigParseException("ActiveItem configuration shall contain at least a parts: itemBehindName");
		}
		
		AiBindingConfig config = new AiBindingConfig();
		
		//item.getName();
		config.itemBehindName = configParts[0];
		config.calibrations = new Hashtable<Double, Double>();
		for(int i=1; i<configParts.length; i++) {
			String calPoint = configParts[i];
			String[] calParts = calPoint.trim().split(":");
			Double calVal = Double.valueOf(calParts[0]);
			Double rawVal = Double.valueOf(calParts[1]);
			config.calibrations.put(calVal, rawVal);
		}
		addBindingConfig(item, config);
	}
	
	//@Override
	//public AiBindingConfig getBindingConfig(String itemName) {
	//	return (AiBindingConfig) bindingConfigs.get(itemName);
	//}	
	
	/**
	 * {@inheritDoc}
	 */
	public String getItemBehindName(String itemName) {
		AiBindingConfig config = (AiBindingConfig) bindingConfigs.get(itemName);
		return config != null ? config.itemBehindName : null;
	}
	
	public Hashtable<Double, Double> getCalibration(String itemName) {
		AiBindingConfig config = (AiBindingConfig) bindingConfigs.get(itemName);
		return config != null ? config.calibrations : null;
	}
	
	/**
	 * This is an internal data structure to store information from the binding
	 * config strings and use it to answer the requests to the ActiveItem
	 * binding provider.
	 */
	static private class AiBindingConfig implements BindingConfig {
		public String itemBehindName;
		public Hashtable<Double, Double> calibrations;
	}


}
