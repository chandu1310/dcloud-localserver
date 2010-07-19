package com.dcloud.common;

import java.io.Serializable;

public class DCloudRequest implements Serializable {
	private Class taskClass;
	private String taskClassName;
	private Object taskClassInstance;
	
	public String getTaskClassName() {
		return taskClassName;
	}
	public void setTaskClassName(String taskClassName) {
		this.taskClassName = taskClassName;
	}
	public Class getTaskClass() {
		return taskClass;
	}
	public void setTaskClass(Class taskClass) {
		this.taskClass = taskClass;
	}
	public Object getTaskClassInstance() {
		return taskClassInstance;
	}
	public void setTaskClassInstance(Object taskClassInstance) {
		this.taskClassInstance = taskClassInstance;
	}
	
}
