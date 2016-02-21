package com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.repository;

public interface RepositoryModelListener {
	void nodeStatusChanged(RepositoryModelEvent rpe);
    void namespacesChanged(RepositoryModelEvent rpe);
}
