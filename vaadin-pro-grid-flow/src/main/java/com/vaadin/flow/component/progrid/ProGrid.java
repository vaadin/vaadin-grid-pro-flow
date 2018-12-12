package com.vaadin.flow.component.progrid;

/*
 * #%L
 * Vaadin ProGrid for Vaadin 10
 * %%
 * Copyright (C) 2017 - 2018 Vaadin Ltd
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

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;

/**
 *
 * @param <E> the bean type
 */
@Tag("vaadin-pro-grid")
@HtmlImport("frontend://bower_components/vaadin-grid/src/vaadin-grid.html")
public class ProGrid<E> extends Grid<E> {

    /**
     * Instantiates a new CrudGrid for the supplied bean type.
     */
    public ProGrid() {
        super();
    }

}