package com.setty.commons.vo.stats;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/11 10:02
 */
@Data
public class StatsVO {
    private String hostId;
    private Integer type;
    private StatsType statsType;
    private List<Integer> indexes;
    private Integer data;
    private Long createTime;
    private List<Object> seq;

    public StatsVO(String hostId, int type, List<Integer> ixSeq) {
        this.hostId = hostId;
        this.type = type;
        this.indexes = ixSeq;
        seq = new ArrayList<>();
        rebuildSeq();
    }

    public void updateData(int data) {
        setData(data);
        rebuildSeq();
    }

    private void rebuildSeq() {
        seq.clear();
        seq.addAll(indexes);
        seq.add(data);
    }

    public StatsType getStatsType() {
        if (statsType != null) {
            return statsType;
        }
        for (StatsType value : StatsType.values()) {
            if (value.getType() == type) {
                statsType = value;
                break;
            }
        }
        return statsType;
    }
}
