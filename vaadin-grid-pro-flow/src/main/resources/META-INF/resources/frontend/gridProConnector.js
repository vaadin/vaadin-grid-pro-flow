// Error handling functions
const tryCatchWrapper = function(originalFunction) {
    return function() {
        try {
            originalFunction.apply(this, arguments);
        } catch (error) {
            logError(error.message);
        }
    }
}

function logError(message) {
    console.error("There seems to be an error in the GridPro:\n" + message + "\n" +
       "Please submit an issue to https://github.com/vaadin/vaadin-grid-pro-flow/issues/new!");
}

window.Vaadin.Flow.gridProConnector = {
  setEditModeRenderer: tryCatchWrapper(function(column, component) {
      column.editModeRenderer = tryCatchWrapper(function(root) {
          root.appendChild(component);
          this._grid._cancelStopEdit();
          component.focus();
      });

      column._setEditorValue = tryCatchWrapper(function(editor, value) {
          // Not needed in case of custom editor as value is set on server-side.
          // Overridden in order to avoid blinking of the cell content.
      })

      column._getEditorValue = tryCatchWrapper(function(editor) {
          // Not needed in case of custom editor as value is set on server-side.
          // Overridden in order to avoid blinking of the cell content.
          return;
      })
  }),

  patchEditModeRenderer: tryCatchWrapper(function(column) {
      column.__editModeRenderer = tryCatchWrapper(function(root, column, rowData) {
          const cell = root.assignedSlot.parentNode;
          const grid = column._grid;

          if (grid.__edited && grid.__edited.model.item.key !== rowData.item.key) {
              grid._stopEdit();
              return;
          }

          const tagName = column._getEditorTagName(cell);
          if (!root.firstElementChild || root.firstElementChild.localName.toLowerCase() !== tagName) {
              root.innerHTML = `
              <${tagName}></${tagName}>
            `;
          }
      });
  })
};
