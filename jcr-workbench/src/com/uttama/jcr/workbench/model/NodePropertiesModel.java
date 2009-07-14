package com.uttama.jcr.workbench.model;

import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.PropertyDefinition;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class NodePropertiesModel
extends AbstractTableModel {
	private final static int NAME_COLUMN = 0;
	private final static int TYPE_COLUMN = 1;
	private final static int VALUE_COLUMN = 2;
	static String columnNames[] = { "Name", "Type", "Value" };
	Node node;
	Vector<Property> properties;
	public static TableColumnModel getTableColumnModel() {
		TableColumnModel tableColumnModel = new DefaultTableColumnModel();
		for (int i=0; i<columnNames.length; i++) {
			TableColumn column = new TableColumn(i);
			column.setHeaderValue(columnNames[i]);
			tableColumnModel.addColumn(column);
		}
		return tableColumnModel;
	}
	public final static SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	public void setNode(Node node) {
		this.node = node;
		try {
			this.properties = filterProperties(node);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fireTableChanged(new TableModelEvent(this));
	}
	private Vector<Property> filterProperties(Node node)
	throws RepositoryException {
		Vector<Property> properties = new Vector<Property>();
		PropertyIterator iter = node.getProperties();
		while (iter.hasNext()) {
			Property property = iter.nextProperty();
			String name = property.getName();
			if ( ! name.equals("jcr:primaryType"))
				properties.add(property);
		}
		return properties;
	}
	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		try {
			return (int) properties.size();
		} catch (NullPointerException e) {
			return 0;
		}
	}
	@Override
	public boolean isCellEditable(int row, int col) {
		return col == 2;
	}
	@Override
	public Object getValueAt(int row, int column) {
		PropertyIterator iterator;
		try {
			iterator = node.getProperties();
			iterator.skip(row);
			//Property property = iterator.nextProperty();
			Property property = properties.get(row);
			switch (column) {
			case NAME_COLUMN:
				return property.getName();
			case TYPE_COLUMN:
				return PropertyType.nameFromValue(property.getType());
			case VALUE_COLUMN:
				PropertyDefinition definition = property.getDefinition();
				boolean isMultiple = definition.isMultiple();
				return isMultiple ? getValues(property) : getValue(property.getValue(), property);
			default:
				return null;
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public static String getValue(Value value, Property property)
	throws RepositoryException {
		int type = value.getType();
		switch (type) {
		case PropertyType.BOOLEAN:
			return value.getBoolean() ? "true" : "false";
		case PropertyType.DATE:
			//return DateFormat.getDateInstance().format(value.getDate().getTime());
			return dateFormat.format(value.getDate().getTime());
		case PropertyType.DOUBLE:
			return "" + value.getDouble();
		case PropertyType.LONG:
			return Long.toString(value.getLong());
		case PropertyType.STRING:
			return value.getString();
		case PropertyType.NAME:
			return value.getString();
		case PropertyType.BINARY:
			return "length: " + property.getLength();
		case PropertyType.PATH:
			return value.getString();
		case PropertyType.REFERENCE:
			return value.getString();
		default:
			return "huh?";
		}
	}
	public static String getValues(Property property)
	throws RepositoryException {
		Value values[] = property.getValues();
		StringBuffer sb = new StringBuffer();
		for (Value value : values) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(getValue(value, property));
		}
		return sb.toString();
	}
	public void fireTableDataChanged() {
		try {
			this.properties = filterProperties(node);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.fireTableDataChanged();
	}
}
