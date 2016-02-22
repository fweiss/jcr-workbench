package com.uttama.jcr.workbench.model.node;


import java.util.EventListener;

public interface NodeChangedListener extends EventListener {
    public void valueChanged(NodeChangedEvent nce);
}
