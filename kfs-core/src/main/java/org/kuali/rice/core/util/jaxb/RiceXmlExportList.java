/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.rice.core.util.jaxb;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;

/**
 * Custom subclass of AbstractList that, whenever the "get" method is called, will pass an
 * internally-stored list's object to the given listener for conversion into another object matching
 * the list's type. This allows for the marshalling process to discard generated items after they
 * have been marshalled.
 *
 * <p>These lists are constructed by passing in another list containing the unconverted items,
 * as well as a listener that will create items of this list's type upon each invocation of
 * the "get" method.
 *
 * <p>This is similar to the "streaming" unmarshalling strategy used in the RiceXmlImportList
 * class, except that this list has been adapted for marshalling instead.
 *
 * @param E The type that the list is expected to return.
 * @param T The type that the list stores internally and passes to the listener for conversion as needed.
 */
public final class RiceXmlExportList<E,T> extends AbstractList<E> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<? extends T> sourceList;
    private final RiceXmlListGetterListener<E,T> listGetterListener;

    /**
     * Constructs a new export list that will rely on the given listener for converting the provided
     * list's items into the appropriate type.
     *
     * @param sourceList The list of objects to convert.
     * @param listGetterListener The listener to use.
     * @throws IllegalArgumentException if sourceList or listGetterListener are null.
     */
    public RiceXmlExportList(List<? extends T> sourceList, RiceXmlListGetterListener<E,T> listGetterListener) {
        super();
        if (sourceList == null) {
            throw new IllegalArgumentException("sourceList cannot be null");
        } else if (listGetterListener == null) {
            throw new IllegalArgumentException("listGetterListener cannot be null");
        }
        this.sourceList = sourceList;
        this.listGetterListener = listGetterListener;
    }

    /**
     * Passes the item at the given index of the internal list to the listener, and then returns
     * the listener's result.
     *
     * @param index The unconverted item's index in the internal list.
     * @return The item converted by the listener at the given list index.
     */
    @Override
    public E get(int index) {
        return listGetterListener.gettingNextItem(sourceList.get(index), index);
    }

    /**
     * Returns the size of the internal list.
     *
     * @return The size of the internal list.
     */
    @Override
    public int size() {
        return sourceList.size();
    }

}
