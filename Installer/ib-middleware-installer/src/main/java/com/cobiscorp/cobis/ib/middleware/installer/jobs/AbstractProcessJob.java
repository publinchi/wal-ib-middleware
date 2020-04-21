package com.cobiscorp.cobis.ib.middleware.installer.jobs;

public abstract class AbstractProcessJob extends AbstractJob{
	
	@Override
	protected boolean execute(String[] args) {		
		return true;
	}
}