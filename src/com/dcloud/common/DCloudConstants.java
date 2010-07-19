package com.dcloud.common;

/*
 * Task states: 
 * 0 - submission pending
 * 1 - submitted. results pending
 * 2 - execution done. results ready for consumption
 * 3 - can be cleared from pool
 */

import java.io.Serializable;

public interface DCloudConstants extends Serializable{
	public static final int SUB_PENDING = 0;
	public static final int SUBMITTED = 1;
	public static final int EXEC_DONE = 2;
	public static final int TRASH = 3;
	public static final int FAILED = -1;
}
