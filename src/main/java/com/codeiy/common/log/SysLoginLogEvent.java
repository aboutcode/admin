package com.codeiy.common.log;

import com.codeiy.system.entity.SysLoginInfo;
import org.springframework.context.ApplicationEvent;

public class SysLoginLogEvent extends ApplicationEvent {

    public SysLoginLogEvent(SysLoginInfo source) {
        super(source);
    }

}
