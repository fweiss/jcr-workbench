package com.uttama.jcr.workbench.view.swing;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


/**
 * Customize a JTable with per-row cell editor. The default JTable implementation only
 * permits per-column cell renderer and editor types. For JCR properties, we'll need a cell editor
 * based the property type (STRING, BOOLEAN, DATE, etc).
 *
 */
public class PropertyTable
extends JTable {
	private final static PropertyTableCellRenderer tableCellRenderer = new PropertyTableCellRenderer();
	public PropertyTable(TableModel dataModel, TableColumnModel columnModel) {
		super(dataModel, columnModel);
	}
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		TableCellRenderer renderer = super.getCellRenderer(row, column);
		if (column == 2) {
			Object value = getModel().getValueAt(row, 1);
			if (value instanceof String && value.toString().equals("Boolean"))
				renderer =  tableCellRenderer;
		}
		return renderer;
	}
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		return super.getCellEditor(row, column);
	}
}
