package com.uttama.jcr.workbench.model.node.version;

import javax.jcr.RepositoryException;
import javax.jcr.version.Version;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NodeVersionModel extends AbstractTableModel {
    final private static int NAME_COLUMN = 0;
    final private static int CREATED_COLUMN = 1;
    final private static String[] columnNames = { "Name", "Created" };

    private List<Version> versions = new ArrayList<>();

    public void setVersions(List<Version> versions) {
        this.versions = versions;
//        fireNodeVersionModelChanged(new NodeVersionModelEvent(this));
        fireTableDataChanged();
    }

    /* TableModel */

    @Override
    public int getRowCount() {
        return versions.size();
    }
    @Override
    public int getColumnCount() {
        return 2;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar created = null;
        Version version = versions.get(rowIndex);
        Object cell = null;
        try {
            switch (columnIndex) {
                case NAME_COLUMN:
                    cell = version.getName();
                    break;
                case CREATED_COLUMN:
                    created = version.getCreated();
                    cell = format1.format(created.getTime());
                    break;
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return cell;
   }

    /* NodeVersionModelListener */

    private List<NodeVersionModelListener> nodeVersionModelListeners = new ArrayList<>();
    public void addNodeVersionModelListener(NodeVersionModelListener listener) {
        nodeVersionModelListeners.add(listener);
    }
    private void fireNodeVersionModelChanged(NodeVersionModelEvent nvme) {
        for (NodeVersionModelListener listener : nodeVersionModelListeners) {
            listener.versionsChanged(nvme);
        }
    }

}
