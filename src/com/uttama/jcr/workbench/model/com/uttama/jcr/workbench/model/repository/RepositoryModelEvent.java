package com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.repository;

import com.uttama.jcr.workbench.model.com.uttama.jcr.workbench.model.repository.RepositoryModel;

public class RepositoryModelEvent {
	private final RepositoryModel repositoryModel;
	public RepositoryModelEvent(RepositoryModel repositoryModel) {
		this.repositoryModel = repositoryModel;
	}
	public RepositoryModel getModel() {
		return this.repositoryModel;
	}

}
