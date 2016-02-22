package com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.node;


import com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.node.NodeChangedEvent;

import java.util.EventListener;

public interface NodeChangedListener extends EventListener {
	public void valueChanged(NodeChangedEvent nce);
}
