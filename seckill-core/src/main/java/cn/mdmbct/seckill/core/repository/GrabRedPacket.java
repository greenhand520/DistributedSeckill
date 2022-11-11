package cn.mdmbct.seckill.core.repository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

    /**
     * 持续时间 单位：秒
     */
    private final long ttl;

}
