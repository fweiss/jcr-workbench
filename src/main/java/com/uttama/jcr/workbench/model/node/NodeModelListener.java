package com.uttama.jcr.workbench.model.node;


import java.util.EventListener;

public interface NodeModelListener extends EventListener {
    void valueChanged(NodeModelEvent nme);
    void versionHistoryChanged(NodeModelEvent nme);
}
