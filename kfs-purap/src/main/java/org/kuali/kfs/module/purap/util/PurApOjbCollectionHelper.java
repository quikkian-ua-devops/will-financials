/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.module.purap.util;

import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.util.OjbCollectionAware;
import org.springframework.orm.ObjectRetrievalFailureException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Helper object to deal with persisting collections.
 */
public class PurApOjbCollectionHelper {
    public final static int MAX_DEPTH = 2;

    /**
     * OJB RemovalAwareLists do not survive through the response/request lifecycle. This method is a work-around to forcibly remove
     * business objects that are found in Collections stored in the database but not in memory.
     *
     * @param orig
     * @param id
     * @param template
     */
    public void processCollections(OjbCollectionAware template, PersistableBusinessObject orig, PersistableBusinessObject copy) {
        processCollectionsRecurse(template, orig, copy, MAX_DEPTH);
    }

    /**
     * This method processes collections recursively up to the depth level specified
     *
     * @param template
     * @param orig
     * @param copy
     */
    private void processCollectionsRecurse(OjbCollectionAware template, PersistableBusinessObject orig, PersistableBusinessObject copy, int depth) {
        if (copy == null || depth < 1) {
            return;
        }

        List originalCollections = orig.buildListOfDeletionAwareLists();

        if (originalCollections != null && !originalCollections.isEmpty()) {
            /*
             * Prior to being saved, the version in the database will not yet reflect any deleted collections. So, a freshly
             * retrieved version will contain objects that need to be removed:
             */
            try {
                List copyCollections = copy.buildListOfDeletionAwareLists();
                int size = originalCollections.size();

                if (copyCollections.size() != size) {
                    throw new RuntimeException("size mismatch while attempting to process list of Collections to manage");
                }

                for (int i = 0; i < size; i++) {
                    Collection<PersistableBusinessObject> origSource = (Collection<PersistableBusinessObject>) originalCollections.get(i);
                    Collection<PersistableBusinessObject> copySource = (Collection<PersistableBusinessObject>) copyCollections.get(i);
                    List list = findUnwantedElements(copySource, origSource, template, depth - 1);
                    cleanse(template, origSource, list);

                }
            } catch (ObjectRetrievalFailureException orfe) {
                // object wasn't found, must be pre-save
            }
        }
    }

    /**
     * OJB RemovalAwareLists do not survive through the response/request lifecycle. This method is a work-around to forcibly remove
     * business objects that are found in Collections stored in the database but not in memory.
     *
     * @param orig
     * @param id
     * @param template
     */
    public void processCollections2(OjbCollectionAware template, PersistableBusinessObject orig, PersistableBusinessObject copy) {
        // if copy is null this is the first time we are saving the object, don't have to worry about updating collections
        if (copy == null) {
            return;
        }

        List originalCollections = orig.buildListOfDeletionAwareLists();

        if (originalCollections != null && !originalCollections.isEmpty()) {
            /*
             * Prior to being saved, the version in the database will not yet reflect any deleted collections. So, a freshly
             * retrieved version will contain objects that need to be removed:
             */
            try {
                List copyCollections = copy.buildListOfDeletionAwareLists();
                int size = originalCollections.size();

                if (copyCollections.size() != size) {
                    throw new RuntimeException("size mismatch while attempting to process list of Collections to manage");
                }

                for (int i = 0; i < size; i++) {
                    Collection origSource = (Collection) originalCollections.get(i);
                    Collection copySource = (Collection) copyCollections.get(i);
                    List list = findUnwantedElements(copySource, origSource, null, 0);
                    cleanse(template, origSource, list);

                }
            } catch (ObjectRetrievalFailureException orfe) {
                // object wasn't found, must be pre-save
            }
        }
    }

    /**
     * This method deletes unwanted objects from the database as well as from the given input List
     *
     * @param origSource    - list containing unwanted business objects
     * @param unwantedItems - business objects to be permanently removed
     * @param template
     */
    private void cleanse(OjbCollectionAware template, Collection origSource, List unwantedItems) {
        if (unwantedItems.size() > 0) {
            Iterator iter = unwantedItems.iterator();
            while (iter.hasNext()) {
                template.getPersistenceBrokerTemplate().delete(iter.next());
            }
        }

    }

    /**
     * This method identifies items in the first List that are not contained in the second List. It is similar to the (optional)
     * java.util.List retainAll method.
     *
     * @param fromList    list from the database
     * @param controlList list from the object
     * @return true iff one or more items were removed
     */
    private List findUnwantedElements(Collection fromList, Collection controlList, OjbCollectionAware template, int depth) {
        List toRemove = new ArrayList();

        Iterator iter = fromList.iterator();
        while (iter.hasNext()) {
            PersistableBusinessObject copyLine = (PersistableBusinessObject) iter.next();

            PersistableBusinessObject line = (PersistableBusinessObject) PurApObjectUtils.retrieveObjectWithIdentitcalKey(controlList, copyLine);
            if (ObjectUtils.isNull(line)) {
                toRemove.add(copyLine);
            } else { // since we're not deleting try to recurse on this element
                processCollectionsRecurse(template, line, copyLine, depth);
            }
        }
        return toRemove;
    }
}
