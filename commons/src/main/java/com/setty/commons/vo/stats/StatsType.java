package com.setty.commons.vo.stats;

import lombok.Getter;

/**
 * @author HuSen
 * create on 2019/7/11 10:50
 */
@Getter
public enum StatsType {
    //
    TEST(1);

    StatsType(int type) {
        this.type = type;
    }

    private int type;
}
