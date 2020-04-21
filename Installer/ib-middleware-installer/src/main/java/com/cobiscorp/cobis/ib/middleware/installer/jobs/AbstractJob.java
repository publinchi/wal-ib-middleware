package com.cobiscorp.cobis.ib.middleware.installer.jobs;

import lombok.Delegate;

import com.izforge.izpack.util.AbstractUIProcessHandler;

public abstract class AbstractJob implements Job, AbstractUIProcessHandler{		

	@SuppressWarnings("unused")
	private final String simpleName;

	@Delegate
	protected AbstractUIProcessHandler handler;

	public AbstractJob() {
		simpleName = getClass().getSimpleName();
	}

	public AbstractJob(String simpleName) {
		this.simpleName = simpleName;
	}
	
	public boolean run(AbstractUIProcessHandler handler, String[] args) {
		this.handler = handler;
		return execute(args);
	}

	protected abstract boolean execute(String[] args);
	
}