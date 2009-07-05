package com.uttama.jcr.workbench.events;

import com.uttama.jcr.workbench.model.RepositoryModel;

public class RepositoryModelEvent {
	private final RepositoryModel repositoryModel;
	public RepositoryModelEvent(RepositoryModel repositoryModel) {
		this.repositoryModel = repositoryModel;
	}
	public RepositoryModel getModel() {
		return this.repositoryModel;
	}

}
