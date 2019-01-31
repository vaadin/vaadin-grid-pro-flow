package com.vaadin.flow.component.gridpro;

/*
 * #%L
 * Vaadin GridPro
 * %%
 * Copyright (C) 2018 - 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file license.html distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the CVALv3 along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import com.vaadin.flow.function.SerializableBiConsumer;

/**
 * {@link ItemUpdater} is used to modify the instance of the item of {@link GridPro}.
 *
 * @param <T>
 *            the item type
 * @param <V>
 *            the value type
 * @author Vaadin Ltd
 * @since 1.0
 */
public interface ItemUpdater<T, V> extends SerializableBiConsumer<T, V> {

    /**
     * Updates {@link GridPro} item's subproperty with newValue
     *
     * @param item
     *            the instance of the item
     * @param newValue
     *            the new value of the property
     */
    @Override
    void accept(T item, V newValue);
}
