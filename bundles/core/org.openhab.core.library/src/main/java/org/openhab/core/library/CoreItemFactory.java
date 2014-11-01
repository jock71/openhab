/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.core.library;

import org.openhab.core.items.GenericItem;
import org.openhab.core.items.ItemFactory;
import org.openhab.core.library.items.ColorItem;
import org.openhab.core.library.items.ContactItem;
import org.openhab.core.library.items.DateTimeItem;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.RollershutterItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;


/**
 * {@link CoreItemFactory}-Implementation for the core ItemTypes 
 * 
 * @author Thomas.Eichstaedt-Engelen
 * @since 0.9.0
 */
public class CoreItemFactory implements ItemFactory {
	
	private static String[] ITEM_TYPES = new String[] { "Switch", "Rollershutter", "Contact", "String", "Number", "Dimmer", "DateTime", "Color" };

	/**
	 * @{inheritDoc}
	 */
	public GenericItem createItem(String itemTypeName, String itemName) {
		GenericItem item;
		if (itemTypeName.equals(ITEM_TYPES[0])) item = new SwitchItem(itemName);
		else if (itemTypeName.equals(ITEM_TYPES[1])) item = new RollershutterItem(itemName);
		else if (itemTypeName.equals(ITEM_TYPES[2])) item = new ContactItem(itemName);
		else if (itemTypeName.equals(ITEM_TYPES[3])) item = new StringItem(itemName);
		else if (itemTypeName.equals(ITEM_TYPES[4])) item = new NumberItem(itemName);
		else if (itemTypeName.equals(ITEM_TYPES[5])) item = new DimmerItem(itemName);
		else if (itemTypeName.equals(ITEM_TYPES[6])) item = new DateTimeItem(itemName);
		else if (itemTypeName.equals(ITEM_TYPES[7])) item = new ColorItem(itemName);
		else item = null;
		return item;
	}
	
	/**
	 * @{inheritDoc}
	 */
	public String[] getSupportedItemTypes() {
		return ITEM_TYPES;
	}

}
