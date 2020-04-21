package com.cobiscorp.cobis.ib.middleware.installer.jobs;
import com.izforge.izpack.util.AbstractUIProcessHandler;

public interface Job {	
	boolean run(AbstractUIProcessHandler handler, String[] args);
}
