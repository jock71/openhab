/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.cv.internal.resources.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a java bean that is used with JAXB to serialize item lists.
 *  
 * @author Tobias Bräutigam
 * @since 1.4.0
 *
 */
@XmlRootElement(name="items")
public class ItemStateListBean {
	@XmlElement(name="i")
	public long index;
	@XmlElement(name="d")
	public ItemListBean stateList;
	public ItemStateListBean() {}
	public ItemStateListBean(ItemListBean stateList) {
		this.stateList = stateList;
	}
}
