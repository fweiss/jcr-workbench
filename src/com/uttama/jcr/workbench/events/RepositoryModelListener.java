package com.uttama.jcr.workbench.events;

public interface RepositoryModelListener {
	void nodeStatusChanged(RepositoryModelEvent rpe);
    void namespacesChanged(RepositoryModelEvent rpe);
}
