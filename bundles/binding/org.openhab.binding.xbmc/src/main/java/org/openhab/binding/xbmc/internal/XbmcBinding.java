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

import java.util.Dictionary;

import org.openhab.binding.xbmc.XbmcBindingProvider;
import org.openhab.binding.xbmc.internal.Xbmc.GUI;

import org.apache.commons.lang.StringUtils;
import org.openhab.core.binding.AbstractBinding;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

	

/**
 * Implement this class if you are going create an actively polling service
 * like querying a Website/Device.
 * 
 * @author Andrea Giacosi
 * @since 1.3.0
 */
public class XbmcBinding extends AbstractBinding<XbmcBindingProvider> implements ManagedService {

	private static final Logger logger = 
		LoggerFactory.getLogger(XbmcBinding.class);


	/** 
	 * the ip address of the xbmc machine
	 */
	private String xbmcIp = "192.168.10.17";
	
	
	public XbmcBinding() {
	}
		
	
	public void activate() {
		super.activate();
	}
	
	public void deactivate() {
		// deallocate Resources here that are no longer needed and 
		// should be reset when activating this binding again
	}

	

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		// the code being executed when a command was sent on the openHAB
		// event bus goes here. This method is only called if one of the 
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveCommand() is called!");
		
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveUpdate(String itemName, State newState) {
		// the code being executed when a state was sent on the openHAB
		// event bus goes here. This method is only called if one of the 
		// BindingProviders provide a binding for the given 'itemName'.
		logger.debug("internalReceiveUpdate() is called!");
		for(XbmcBindingProvider provider : providers) {
			String method = provider.getMethodName(itemName);
			Object[] params = provider.getParameters(itemName);
			switch(provider.getSectionName(itemName)) {
			case "GUI":
				guiUpdate(method, params);
				break;
			case "system":
				systemUpdate(method, params);
				break;
			default:
				break;
			}
		}
	}
	



	private void guiUpdate(String method, Object[] params) {
		GUI gui = xbmc.gui();
		switch(method) {
		case "ShowNotification":
			gui.showNotification("Ciao", "Ciao", 5000);
			break;
		default:
		}
		
	}

	private void systemUpdate(String method, Object[] params) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	public void updated(Dictionary<String, ?> config) throws ConfigurationException {
		if (config != null) {
			
			// to override the default refresh interval one has to add a 
			// parameter to openhab.cfg like <bindingName>:refresh=<intervalInMs>
			String ip = (String) config.get("ip");
			if (StringUtils.isNotBlank(xbmcIp)) {
				xbmcIp = ip;
			}
			else {
				// throw new ConfigurationException(property, reason);
			}
			
			xbmc = new Xbmc(xbmcIp, 80);
			
		}
	}
	
	private Xbmc xbmc;
	

}
