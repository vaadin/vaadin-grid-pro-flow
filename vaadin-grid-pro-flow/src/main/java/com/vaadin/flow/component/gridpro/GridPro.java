package com.vaadin.flow.component.gridpro;

/*
 * #%L
 * Vaadin GridPro for Vaadin 10
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

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.grid.*;
import com.vaadin.flow.component.Synchronize;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.internal.JsonSerializer;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.data.provider.Query;

import elemental.json.JsonArray;
import elemental.json.JsonObject;

import java.util.*;


@Tag("vaadin-grid-pro")
@HtmlImport("frontend://bower_components/vaadin-grid-pro/src/vaadin-grid-pro.html")
@HtmlImport("frontend://bower_components/vaadin-grid-pro/src/vaadin-grid-pro-edit-column.html")
/**
 * Server-side component for the {@code <vaadin-grid-pro>} element.
 *
 * @author Vaadin Ltd
 *
 * @param <T>
 *            the grid bean type
 *
 */
public class GridPro<E> extends Grid<E> {

    private Map<String, Column<E>> idToColumnMap = new HashMap<>();

    /**
     * Instantiates a new CrudGrid for the supplied bean type.
     *
     * @param beanType
     *            the beanType for the item
     *
     */
    public GridPro(Class<E> beanType) {
        super(beanType);
        setup();
    }

    /**
     * Creates a new instance, with page size of 50.
     */
    public GridPro() {
        super();
        setup();
    }

    /**
     * Creates a new instance, with the specified page size.
     * <p>
     * The page size influences the {@link Query#getLimit()} sent by the client,
     * but it's up to the webcomponent to determine the actual query limit,
     * based on the height of the component and scroll position. Usually the
     * limit is 3 times the page size (e.g. 150 items with a page size of 50).
     *
     * @param pageSize
     *            the page size. Must be greater than zero.
     */
    public GridPro(int pageSize) {
        super(pageSize);
        setup();
    }

    private void setup() {
        addItemPropertyChangedListener(e -> {
            EditColumn<E> column = (EditColumn<E>) this.idToColumnMap.get(e.getPath());

            column.getItemUpdater().accept(e.getItem(), e.getSourceItem().get(e.getPath()).asString());
        });
    }

    /**
     * Server-side component for the {@code <vaadin-grid-edit-column>} element.
     *
     * <p>
     * Every added column sends data to the client side regardless of its
     * visibility state. Don't add a new column at all or use
     * {@link GridPro#removeColumn(Column)} to avoid sending extra data.
     * </p>
     *
     * @param <T>
     *            type of the underlying grid this column is compatible with
     */
    @Tag("vaadin-grid-pro-edit-column")
    public static class EditColumn<T> extends Column<T> {

        private ItemUpdater<T, String> itemUpdater;

        /**
         * Constructs a new Column for use inside a Grid.
         *
         * @param grid
         *            the grid this column is attached to
         * @param columnId
         *            unique identifier of this column
         * @param renderer
         *            the renderer to use in this column, must not be
         *            {@code null}
         */
        public EditColumn(GridPro<T> grid, String columnId, Renderer<T> renderer) {
            super(grid, columnId, renderer);
        }

        /**
         * Sets the itemUpdater function that will be called on item changed.
         *
         * @param itemUpdater
         *            the callback function that is called when item is changed.
         *            It receives two arguments: item and newValue.
         * @return this column instance
         */
        protected EditColumn<T> setItemUpdater(ItemUpdater<T, String> itemUpdater) {
            this.itemUpdater = itemUpdater;
            return this;
        }

        /**
         * Gets the itemUpdater function that will be called on item changed.
         *
         * @return the instance of itemUpdater for this column
         */
        protected ItemUpdater<T, String> getItemUpdater() {
            return itemUpdater;
        }

        /**
         * Sets the type of the editor that is used for modifying cell value.
         *
         * @param type
         *            the type of the editor
         * @return this column instance
         *
         * @see EditorType
         */
        protected EditColumn<T> setEditorType(EditorType type) {
            getElement().setProperty("editorType", type == null ? "text" : type.getTypeName());
            return this;
        }

        /**
         * Gets the type of the editor that is used for modifying cell value.
         *
         * @return the editor type
         */
        @Synchronize("editor-type-changed")
        protected String getEditorType() {
            return getElement().getProperty("editorType", "text");
        }

        /**
         * Sets the list of options that is used for select type of the editor.
         *
         * @param options
         *            the list of options
         * @return this column instance
         */
        protected EditColumn<T> setOptions(List<String> options) {
            getElement().setPropertyJson("editorOptions", JsonSerializer.toJson(options));
            return this;
        }

        /**
         * Gets the list of options that is used for select type of the editor.
         *
         * @return the list of options
         */
        @Synchronize("editor-options-changed")
        protected List<String> getOptions() {
            return JsonSerializer.toObjects(String.class,  (JsonArray) getElement().getPropertyRaw("editorOptions"));
        }
    }

    /**
     * Adds a new edit column to this {@link GridPro} with a value provider.
     * <p>
     * This method is based on {@link Grid#addColumn(ValueProvider)}.
     * </p>
     * <p>
     * The instance of {@link EditColumnConfigurator} should be provided as
     * second parameter in order to configure the needed type of the editor for the
     * column. Available static methods to configure the needed editor type are:
     * {@link EditColumnConfigurator#text(ItemUpdater)}
     * {@link EditColumnConfigurator#checkbox(ItemUpdater)}
     * {@link EditColumnConfigurator#select(ItemUpdater, List)}
     * </p>
     *
     * @param valueProvider
     *            the value provider
     * @param columnConfigurator
     *            the instance of {@link EditColumnConfigurator} class which configures
     *            the column to operate with specific type of the editor
     * @return the created column
     * @see Grid#addColumn(ValueProvider)
     * @see EditColumnConfigurator#text(ItemUpdater)
     * @see EditColumnConfigurator#checkbox(ItemUpdater)
     * @see EditColumnConfigurator#select(ItemUpdater, List)
     * @see #removeColumn(Column)
     */
    public EditColumn<E> addEditColumn(ValueProvider<E, ?> valueProvider, EditColumnConfigurator columnConfigurator) {
        Objects.requireNonNull(columnConfigurator);

        EditColumn<E> column = this.addColumn(valueProvider, this::createEditColumn);

        return configureEditColumn(column, columnConfigurator);
    }

    /**
     * Adds a new edit column to this {@link GridPro} with a renderer.
     * <p>
     * This method is based on {@link Grid#addColumn(Renderer)}.
     * </p>
     * <p>
     * The instance of {@link EditColumnConfigurator} should be provided as
     * second parameter in order to configure the needed type of the editor for the
     * column. Available static methods to configure the needed editor type are:
     * {@link EditColumnConfigurator#text(ItemUpdater)}
     * {@link EditColumnConfigurator#checkbox(ItemUpdater)}
     * {@link EditColumnConfigurator#select(ItemUpdater, List)}
     * </p>
     *
     * @param renderer
     *            the renderer used to create the grid cell structure
     * @param columnConfigurator
     *            the instance of {@link EditColumnConfigurator} class which configures
     *            the column to operate with specific type of the editor
     * @return the created column
     * @see Grid#addColumn(Renderer)
     * @see EditColumnConfigurator#text(ItemUpdater)
     * @see EditColumnConfigurator#checkbox(ItemUpdater)
     * @see EditColumnConfigurator#select(ItemUpdater, List)
     * @see #removeColumn(Column)
     */
    public EditColumn<E> addEditColumn(Renderer<E> renderer, EditColumnConfigurator columnConfigurator) {
        Objects.requireNonNull(columnConfigurator);

        EditColumn<E> column = this.addColumn(renderer, this::createEditColumn);

        return configureEditColumn(column, columnConfigurator);
    }

    /**
     * Adds a new edit column for the given property name.
     * <p>
     * This method is based on {@link Grid#addColumn(String)}.
     * </p>
     * <p>
     * <strong>Note:</strong> This method can only be used for a Grid created
     * from a bean type with {@link #GridPro(Class)}.
     * </p>
     * <p>
     * The instance of {@link EditColumnConfigurator} should be provided as
     * second parameter in order to configure the needed type of the editor for the
     * column. Available static methods to configure the needed editor type are:
     * {@link EditColumnConfigurator#text(ItemUpdater)}
     * {@link EditColumnConfigurator#checkbox(ItemUpdater)}
     * {@link EditColumnConfigurator#select(ItemUpdater, List)}
     * </p>
     *
     * @see #removeColumn(Column)
     *
     * @param propertyName
     *            the property name of the new column, not <code>null</code>
     * @param columnConfigurator
     *            the instance of {@link EditColumnConfigurator} class which configures
     *            the column to operate with specific type of the editor
     * @return the created column
     *
     * @see Grid#addColumn(String)
     * @see EditColumnConfigurator#text(ItemUpdater)
     * @see EditColumnConfigurator#checkbox(ItemUpdater)
     * @see EditColumnConfigurator#select(ItemUpdater, List)
     */
    public EditColumn<E> addEditColumn(String propertyName, EditColumnConfigurator columnConfigurator) {
        Objects.requireNonNull(columnConfigurator);

        EditColumn<E> column = this.addColumn(propertyName, this::createEditColumn);

        return configureEditColumn(column, columnConfigurator);
    }

    /**
     * Sets enterNextRow value for this grid.
     *
     * @param enterNextRow
     *            when <code>true</code>, after moving to next editable cell using
     *            Tab / Enter, it will be focused in edit mode
     */
    public void setEnterNextRow(boolean enterNextRow) {
        getElement().setProperty("enterNextRow", enterNextRow);
    }

    /**
     * Gets the enterNextRow value for this grid.
     *
     * @return enterNextRow value
     */
    @Synchronize("enter-next-row-changed")
    public boolean getEnterNextRow() {
        return getElement().getProperty("enterNextRow", false);
    }

    /**
     * Sets keepEditorOpen value for this grid.
     *
     * @param keepEditorOpen
     *            when <code>true</code>, pressing Enter while in cell edit mode
     *            will move focus to the editable cell in the next row
     */
    public void setKeepEditorOpen(boolean keepEditorOpen) {
        getElement().setProperty("keepEditorOpen", keepEditorOpen);
    }

    /**
     * Gets the keepEditorOpen value for this grid.
     *
     * @return keepEditorOpen value
     */
    @Synchronize("keep-editor-open-changed")
    public boolean getKeepEditorOpen() {
        return getElement().getProperty("keepEditorOpen", false);
    }

    private EditColumn<E> configureEditColumn(EditColumn<E> column, EditColumnConfigurator columnConfigurator) {
        column.setEditorType(columnConfigurator.getType());
        column.setItemUpdater(columnConfigurator.getItemUpdater());
        column.setOptions(columnConfigurator.getOptions());

        return column;
    }

    /**
     * Creates a new edit column instance for this {@link GridPro} instance.
     * <p>
     * This method must not return <code>null</code>.
     *
     * @param renderer
     *            the renderer used to create the grid cell structure
     * @param columnId
     *            internal column id
     * @return edit column instance
     * @see Renderer
     */
    protected EditColumn<E> createEditColumn(Renderer<E> renderer, String columnId) {
        EditColumn<E> column = new EditColumn<>(this, columnId, renderer);
        idToColumnMap.put(columnId, column);
        return column;
    }

    /**
     * Event fired when the user starts to edit an existing item.
     *
     * @param <E> the bean type
     */
    @DomEvent("item-property-changed")
    public static class ItemPropertyChangedEvent<E> extends ComponentEvent<GridPro<E>> {

        private E item;
        private JsonObject sourceItem;
        private String path;

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source     the source component
         * @param fromClient <code>true</code> if the event originated from the client
         * @param item       the item to be edited, provided in JSON as internally represented in Grid
         * @param path       item subproperty that was changed
         */
        public ItemPropertyChangedEvent(GridPro<E> source, boolean fromClient,
                                        @EventData("event.detail.item") JsonObject item,
                                        @EventData("event.detail.path") String path) {
            super(source, fromClient);
            this.sourceItem = item;
            this.item = source.getDataCommunicator()
                    .getKeyMapper().get(item.getString("key"));
            this.path = path;
        }

        /**
         * Gets an instance of edited item.
         *
         * @return the instance of edited item
         */
        public E getItem() {
            return item;
        }

        /**
         * Gets an instance of edited item with the relations to the columns.
         *
         * @return the instance of edited item
         */
        public JsonObject getSourceItem() {
            return sourceItem;
        }

        /**
         * Gets the key of the column where item was edited.
         *
         * @return the key of the column
         */
        public String getPath() {
            return path;
        }
    }

    /**
     * Registers a listener to be notified when the user starts to edit an existing item.
     *
     * @param listener a listener to be notified
     * @return a handle that can be used to unregister the listener
     */
    public Registration addItemPropertyChangedListener(ComponentEventListener<ItemPropertyChangedEvent<E>> listener) {
        return ComponentUtil.addListener(this, ItemPropertyChangedEvent.class,
                (ComponentEventListener) listener);
    }
}
