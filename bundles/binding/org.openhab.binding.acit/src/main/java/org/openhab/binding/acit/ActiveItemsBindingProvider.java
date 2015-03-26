/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.acit;

import java.util.Hashtable;

import org.openhab.core.binding.BindingProvider;



/**
 * This interface is implemented by classes that can provide mapping information
 * between openHAB items and ActiveItems items.
 * 
 * Implementing classes should register themselves as a service in order to be 
 * taken into account.
 * 
 * @author Andrea Giacosi
 * @since 1.7.0
 */
public interface ActiveItemsBindingProvider extends BindingProvider {

	/**
	 * @return the corresponding item behind of the given ActiveItem <code>itemName</code>
	 */
	public String getItemBehindName(String itemName);
	
	/**
	 * 
	 * @param itemName
	 * @return mapping from active item values to item behind values
	 */
	public Hashtable<Double, Double> getCalibration(String itemName);
		
}
