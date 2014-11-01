/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.core.library.items;

import java.util.ArrayList;
import java.util.List;

import org.openhab.core.items.GenericItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.RefreshCommand;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;

/**
 * A NumberItem has a decimal value and is usually used for all kinds
 * of sensors, like temperature, brightness, wind, etc.
 * It can also be used as a counter or as any other thing that can be expressed
 * as a number.
 * 
 * @author Kai Kreuzer
 * @since 0.1.0
 *
 */
public class NumberItem extends GenericItem {
	
	private static List<Class<? extends State>> acceptedDataTypes = new ArrayList<Class<? extends State>>();
	private static List<Class<? extends Command>> acceptedCommandTypes = new ArrayList<Class<? extends Command>>();
	private static List<Class<? extends Command>> acceptedCommandTypesRel = new ArrayList<Class<? extends Command>>();

	static {
		acceptedDataTypes.add(DecimalType.class);
		acceptedDataTypes.add(UnDefType.class);

		acceptedCommandTypes.add(DecimalType.class);

		acceptedCommandTypesRel.add(RefreshCommand.class);
		acceptedCommandTypesRel.add(DecimalType.class);
	}
	
	public NumberItem(String name) {
		this(name, true);
	}

	public NumberItem(String name, boolean reloadable) {
		super(name, reloadable);
	}

	public List<Class<? extends State>> getAcceptedDataTypes() {
		return acceptedDataTypes;
	}

	public List<Class<? extends Command>> getAcceptedCommandTypes() {
		if(reloadable) {
			return acceptedCommandTypesRel;
		}
		else {
			return acceptedCommandTypes;			
		}
	}
}
