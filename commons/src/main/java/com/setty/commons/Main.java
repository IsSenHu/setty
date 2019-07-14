package com.setty.commons;

import com.google.common.collect.Lists;
import com.setty.commons.index.StatsCtx;
import com.setty.commons.vo.stats.StatsType;
import com.setty.commons.vo.stats.StatsVO;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author HuSen
 * create on 2019/7/11 11:14
 */
public class Main {
    public static void main(String[] args) {
        StatsCtx ctx = new StatsCtx();
        StatsVO statsVO = new StatsVO("husen", 1, Lists.newArrayList(1, 2, 3, 4));
        statsVO.setCreateTime(System.nanoTime());
        statsVO.setData(2019);

        StatsVO vo = ctx.loadStatsVO(statsVO, (oldVal, newVal) -> {
            long temp = oldVal + newVal;
            return (int) (temp < Integer.MAX_VALUE ? temp : Integer.MAX_VALUE);
        });
        int statsData = ctx.getStatsData(StatsType.TEST, Lists.newArrayList(1, 2, 3, 4));
        System.out.println(statsData);

        Pair<StatsVO, Boolean> pair = ctx.updateStatsData(StatsType.TEST, "husen", Lists.newArrayList(1, 2, 3, 4), 10, (oldVal, newVal) -> {
            long temp = oldVal + newVal;
            return (int) (temp < Integer.MAX_VALUE ? temp : Integer.MAX_VALUE);
        });

        System.out.println(pair.getRight());
        statsData = ctx.getStatsData(StatsType.TEST, Lists.newArrayList(), 10);
        System.out.println(statsData);
    }
}
