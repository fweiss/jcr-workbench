package com.uttama.jcr.workbench.model.repository;

public class RepositoryModelEvent {
    private final RepositoryModel repositoryModel;
    public RepositoryModelEvent(RepositoryModel repositoryModel) {
        this.repositoryModel = repositoryModel;
    }
    public RepositoryModel getModel() {
        return this.repositoryModel;
    }

}
