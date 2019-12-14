package com.tvd12.ezyhttp.server.core.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.server.core.handler.UncaughtExceptionHandler;

public class ExceptionHandlerManager extends EzyLoggable {

	protected final List<Object> exceptionHandlers;
	protected final Map<Class<?>, UncaughtExceptionHandler> uncaughtExceptionHandlers;
	
	public ExceptionHandlerManager() {
		this.exceptionHandlers = new ArrayList<>();
		this.uncaughtExceptionHandlers = new HashMap<>();
	}
	
	public void addExceptionHandler(Object exceptionHandler) {
		this.exceptionHandlers.add(exceptionHandler);
	}
	
	public void addExceptionHandlers(List<Object> exceptionHandlers) {
		this.exceptionHandlers.addAll(exceptionHandlers);
	}
	
	public List<Object> getExceptionHandlerList() {
		return new ArrayList<>(exceptionHandlers);
	}
	
	public UncaughtExceptionHandler getUncaughtExceptionHandler(
			Class<?> exceptionClass) {
		UncaughtExceptionHandler hanlder = uncaughtExceptionHandlers.get(exceptionClass);
		return hanlder;
	}
	
	public void addUncaughtExceptionHandler(
			Class<?> exceptionClass, UncaughtExceptionHandler handler) {
		this.uncaughtExceptionHandlers.put(exceptionClass, handler);
		this.logger.info("add exception handler for: " + exceptionClass.getName());
	}
	
	public void addUncaughtExceptionHandlers(
			Map<Class<?>, UncaughtExceptionHandler> handlers) {
		for(Class<?> exceptionClass : handlers.keySet())
			addUncaughtExceptionHandler(exceptionClass, handlers.get(exceptionClass));
	}
}
