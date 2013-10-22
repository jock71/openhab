/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010-2013, openHAB.org <admin@openhab.org>
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */
package org.openhab.binding.xbmc.internal;


import org.openhab.binding.xbmc.XbmcBindingProvider;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.types.Command;
import org.openhab.core.types.TypeParser;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;


/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author Andrea Giacosi
 * @since 1.3.0
 */
public class XbmcGenericBindingProvider extends AbstractGenericBindingProvider implements XbmcBindingProvider {

	/** the binding type to register for as a binding config reader */
	public static final String XBMC_BINDING_TYPE = "xbmc";
	
	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return XBMC_BINDING_TYPE;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
		if(item instanceof SwitchItem) {
			SwitchItem switchItem = (SwitchItem)item;
		}
		else if(item instanceof StringItem) {
			StringItem stringItem = (StringItem)item;
		}
		else {
			throw new BindingConfigParseException("item '" + item.getName()
				+ "' is of type '" + item.getClass().getSimpleName()
				+ "', and it is not supported - please check your *.items configuration");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item, String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);
		// parse bindingconfig here ...
		// examples GUI.ShowNotification:title:5    show for 5 seconds item value with title "title"
		//          Application.SetVolume:50        set volume to 50%
		try {
			String[] tokens = bindingConfig.split(":");
			XbmcBindingConfigElement el = new XbmcBindingConfigElement();
			String[] method = tokens[0].split("\\.");
			el.sectionName = method[0];
			el.methodName = method[1];
			el.parameters = new Object[tokens.length-1];
			for(int i=0; i<el.parameters.length; i++) {
				el.parameters[i] = tokens[i+1];
			}
			addBindingConfig(item, el);
		}
		catch(Throwable t) {
			throw new BindingConfigParseException("wrong binding config for xbmc binding:" + bindingConfig);
		}

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSectionName(String itemName) {
		XbmcBindingConfigElement config = (XbmcBindingConfigElement) bindingConfigs.get(itemName);
		return config != null ? config.sectionName : null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMethodName(String itemName) {
		XbmcBindingConfigElement config = (XbmcBindingConfigElement) bindingConfigs.get(itemName);
		return config != null ? config.methodName : null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getParameters(String itemName) {
		XbmcBindingConfigElement config = (XbmcBindingConfigElement) bindingConfigs.get(itemName);
		return config != null ? config.parameters : null;
	}


	/**
	 * Creates a {@link Command} out of the given <code>commandAsString</code>
	 * incorporating the {@link TypeParser}.
	 *  
	 * @param item
	 * @param commandAsString
	 * 
	 * @return an appropriate Command (see {@link TypeParser} for more 
	 * information
	 * 
	 * @throws BindingConfigParseException if the {@link TypeParser} couldn't
	 * create a command appropriately
	 * 
	 * @see {@link TypeParser}
	 */
	private Command createCommandFromString(Item item, String commandAsString) throws BindingConfigParseException {
		
		Command command = TypeParser.parseCommand(
			item.getAcceptedCommandTypes(), commandAsString);
		
		if (command == null) {
			throw new BindingConfigParseException("couldn't create Command from '" + commandAsString + "' ");
		}
		
		return command;
	}
	
	static class XbmcBindingConfigElement implements BindingConfig {
		
		public String sectionName = null;
		public String methodName = null;
		public Object[] parameters = null;
		
		@Override
		public String toString() {
			return "XbmcBindingConfigElement [method=" + methodName + "]";
		}
		
	}
	
	
}
