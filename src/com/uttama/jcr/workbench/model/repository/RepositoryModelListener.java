package com.uttama.jcr.workbench.model.repository;

import java.util.EventListener;

public interface RepositoryModelListener extends EventListener {
    void nodeStatusChanged(RepositoryModelEvent rpe);
    void namespacesChanged(RepositoryModelEvent rpe);
    void configurationChanged(RepositoryModelEvent rpe);
}
