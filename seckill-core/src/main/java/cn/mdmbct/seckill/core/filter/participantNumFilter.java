package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.Participant;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/22 8:17
 * @modified mdmbct
 * @since 0.1
 */
public class participantNumFilter<R> extends Filter<R> {

    private final int participationNum;

    public participantNumFilter(int order, int participationNum) {
        super(order);
        this.participationNum = participationNum;
    }

    @Override
    public String notPassMsg() {
        return "参与次数过多！";
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        return getCurParticipationNum() <= participationNum;
    }

    private int getCurParticipationNum() {
        return 0;
    }

}
