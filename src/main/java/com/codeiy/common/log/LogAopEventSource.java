package com.codeiy.common.log;

import com.codeiy.system.entity.SysLog;
import lombok.Data;

/**
 * spring event log
 *
 * @author lengleng
 * @date 2023/8/11
 */
@Data
public class LogAopEventSource extends SysLog {

	/**
	 * 参数重写成object
	 */
	private Object body;

}
