package org.cm.boot.starter.exception;

import org.cm.boot.starter.config.BaseConstants;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author parkstud@qq.com 2020-05-20
 */
@Data
@NoArgsConstructor
public class ExceptionResponse {
    private Boolean success;
    private BaseConstants.ErrorCode code;
    private String msg;
    private String type;
    private Throwable throwable;

    /**
     * 根据code自动获取描述信息、消息类型
     *
     * @param code 消息编码
     */
    public ExceptionResponse(BaseConstants.ErrorCode code) {
        this.success = false;
        this.code = code;
    }
}
