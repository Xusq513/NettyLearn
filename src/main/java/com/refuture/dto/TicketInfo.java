package com.refuture.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * <p>
 * 地铁车票信息
 * </p>
 *
 * @author Michael Xu
 * @version 1.0
 * @date 2020/3/25 16:35
 */
@Data
@ToString
@Accessors(chain = true)
public class TicketInfo implements Serializable {

    private static final Long serialVersionUID = 7565838717623951575L;
    /**
     * 手机号码
     */
    private String mobileNo;

    /**
     * 起始站
     */
    private String startStation;

    /**
     * 结束站
     */
    private String endStation;

    /**
     * 入站时间
     */
    private LocalDateTime startTime;

    /**
     * 出站时间
     */
    private LocalDateTime endTime;

    /**
     * 处理时间
     */
    private LocalDateTime dealTime;
}
