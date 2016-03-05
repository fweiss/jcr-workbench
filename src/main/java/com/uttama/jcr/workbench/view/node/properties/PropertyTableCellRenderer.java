package com.uttama.jcr.workbench.view.node.properties;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class PropertyTableCellRenderer
extends DefaultTableCellRenderer {
    private static final CheckBoxRenderer checkBoxRenderer = new CheckBoxRenderer();
    public PropertyTableCellRenderer() {
        super();
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        //return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        return checkBoxRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
    private static class CheckBoxRenderer
    extends JCheckBox
    implements TableCellRenderer {
        private static final Border focusBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
        public CheckBoxRenderer() {
            super();
            setHorizontalAlignment(JLabel.LEFT);
            setBorderPainted(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            setSelected(value != null && ((String) value).equals("true"));
            setBorder(hasFocus ? focusBorder : noFocusBorder);
            return this;
        }
    }
}
