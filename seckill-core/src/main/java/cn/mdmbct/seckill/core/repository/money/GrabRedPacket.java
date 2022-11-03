package cn.mdmbct.seckill.core.repository.money;

import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 抢红包配置
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/19 22:58
 * @modified mdmbct
 * @since 0.1
 */
@RequiredArgsConstructor
public class GrabRedPacket {

    /**
     * 抢红包活动id
     */
    private final String id;

    private final List<RedPacket> packets;

    /**
     * 开始时间
     */
    private final long startTime;
}
