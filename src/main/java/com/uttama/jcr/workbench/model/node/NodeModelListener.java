package com.uttama.jcr.workbench.model.node;


import java.util.EventListener;

public interface NodeModelListener extends EventListener {
    public void valueChanged(NodeModelEvent nce);
}
