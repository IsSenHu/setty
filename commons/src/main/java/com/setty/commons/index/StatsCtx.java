package com.setty.commons.index;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.setty.commons.vo.stats.StatsType;
import com.setty.commons.vo.stats.StatsVO;
import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * @author HuSen
 * create on 2019/7/11 9:58
 */
@Getter
public class StatsCtx {

    /**
     * 统计类型 => 统计节点
     * */
    private Map<StatsType, AbstractStatsNode> roots = new HashMap<>();

    private SetMultimap<StatsType, List<Object>> statsSeqIxs = HashMultimap.create();

    @Getter
    private static abstract class AbstractStatsNode {

        private BiFunction<Integer, Integer, Integer> dataUpdater;

        AbstractStatsNode(BiFunction<Integer, Integer, Integer> dataUpdater) {
            this.dataUpdater = dataUpdater;
        }

        /**
         * 得到当前的统计数据
         *
         * @return 统计数据
         */
        public abstract int getStatsData();

        StatsVO load(StatsVO statsVO) {
            return load(statsVO, 0);
        }

        /**
         * 加载 StatsVO
         *
         * @param statsVO 统计VO
         * @param layer   深度
         * @return StatsVO
         */
        protected abstract StatsVO load(StatsVO statsVO, int layer);

        /**
         * 更新统计数据
         *
         * @param ixs    索引
         * @param data   数据
         * @param svoCre 获取 StatsVO 函数
         * @return Pair<StatsVO, Boolean>
         */
        protected abstract Pair<StatsVO, Boolean> updateStatsData(Iterator<Integer> ixs, Integer data, Supplier<StatsVO> svoCre);

        /**
         * 获取统计数据
         *
         * @param ixSeq 索引
         * @return 统计数据
         */
        int getStatsData(List<Integer> ixSeq) {
            for (int i = ixSeq.size() - 1; i >= 0; i--) {
                Integer ix = ixSeq.get(i);
                if (ix >= 0) {
                    break;
                } else {
                    ixSeq.remove(i);
                }
            }
            return getStatsData(ixSeq, 0);
        }

        /**
         * 获取统计数据
         *
         * @param ixSeq 索引
         * @param layer 深度
         * @return 统计数据
         */
        protected abstract int getStatsData(List<Integer> ixSeq, int layer);

        /**
         * 计算新的统计数据
         *
         * @param oldVal 旧的值
         * @param newVal 新的值
         * @return 新的统计结果
         */
        Integer calNewStatsData(Integer oldVal, Integer newVal) {
            return dataUpdater.apply(oldVal, newVal);
        }
    }

    private static class StatsIndexNode extends AbstractStatsNode {

        private int data;

        private Map<Integer, AbstractStatsNode> subNodes;

        StatsIndexNode(BiFunction<Integer, Integer, Integer> dataUpdater) {
            super(dataUpdater);
            subNodes = new HashMap<>();
        }

        @Override
        public int getStatsData() {
            return data;
        }

        @Override
        protected StatsVO load(StatsVO statsVO, int layer) {
            this.data = calNewStatsData(this.data, statsVO.getData());
            List<Integer> ixSeq = statsVO.getIndexes();
            if (layer < ixSeq.size()) {
                Integer ix = ixSeq.get(layer);
                AbstractStatsNode subNode = subNodes.get(ix);
                int nextLayer = layer + 1;
                if (subNode == null) {
                    BiFunction<Integer, Integer, Integer> dataUpdater = getDataUpdater();
                    if (nextLayer < ixSeq.size()) {
                        subNode = new StatsIndexNode(dataUpdater);
                    } else {
                        subNode = new StatsDataNode(dataUpdater);
                    }
                    subNodes.put(ix, subNode);
                }
                return subNode.load(statsVO, nextLayer);
            } else {
                return null;
            }
        }

        @Override
        protected Pair<StatsVO, Boolean> updateStatsData(Iterator<Integer> ixs, Integer data, Supplier<StatsVO> svoCre) {
            this.data = calNewStatsData(this.data, data);
            if (ixs.hasNext()) {
                Integer ix = ixs.next();
                AbstractStatsNode subNode = subNodes.get(ix);
                if (subNode == null) {
                    BiFunction<Integer, Integer, Integer> dataUpdater = getDataUpdater();
                    if (ixs.hasNext()) {
                        subNode = new StatsIndexNode(dataUpdater);
                    } else {
                        subNode = new StatsDataNode(dataUpdater);
                    }
                    subNodes.put(ix, subNode);
                }
                return subNode.updateStatsData(ixs, data, svoCre);
            } else {
                return null;
            }
        }

        @Override
        protected int getStatsData(List<Integer> ixSeq, int layer) {
            if (layer < ixSeq.size()) {
                Integer ix = ixSeq.get(layer);
                layer++;
                if (ix >= 0) {
                    AbstractStatsNode subNode = subNodes.get(ix);
                    if (subNode == null) {
                        return 0;
                    }
                    return subNode.getStatsData(ixSeq, layer);
                } else {
                    Collection<AbstractStatsNode> nodes = subNodes.values();
                    int ret = 0;
                    for (AbstractStatsNode node : nodes) {
                        ret = calNewStatsData(ret, node.getStatsData(ixSeq, layer));
                    }
                    return ret;
                }
            } else {
                return getStatsData();
            }
        }
    }

    private static class StatsDataNode extends AbstractStatsNode {

        private StatsVO statsVO;

        StatsDataNode(BiFunction<Integer, Integer, Integer> dataUpdater) {
            super(dataUpdater);
        }

        @Override
        public int getStatsData() {
            return statsVO != null ? statsVO.getData() : 0;
        }

        @Override
        protected StatsVO load(StatsVO statsVO, int layer) {
            if (this.statsVO == null) {
                this.statsVO = statsVO;
                return null;
            } else if (this.statsVO.getCreateTime() > statsVO.getCreateTime()) {
                StatsVO delete = this.statsVO;
                this.statsVO = statsVO;
                return delete;
            } else {
                return null;
            }
        }

        @Override
        protected Pair<StatsVO, Boolean> updateStatsData(Iterator<Integer> ixs, Integer data, Supplier<StatsVO> svoCre) {
            StatsVO curStats = this.statsVO;
            Boolean updated;
            if (curStats == null) {
                this.statsVO = curStats = svoCre.get();
                updated = Boolean.TRUE;
            } else {
                data = calNewStatsData(curStats.getData(), data);
                updated = curStats.getData() != data.intValue();
            }
            curStats.updateData(data);
            return ImmutablePair.of(curStats, updated);
        }

        @Override
        protected int getStatsData(List<Integer> ixSeq, int layer) {
            return getStatsData();
        }
    }

    public StatsVO loadStatsVO(StatsVO statsVO, BiFunction<Integer, Integer, Integer> dataUpdater) {
        // 获取当前统计VO的索引
        List<Integer> ixSeq = statsVO.getIndexes();

        // 获取当前统计VO所属的统计节点
        StatsType statsType = statsVO.getStatsType();
        AbstractStatsNode root = gocStatsRootNode(statsType, ixSeq, dataUpdater);

        // 加载该节点下的统计VO 返回旧的数据
        StatsVO delete = root.load(statsVO);

        // 存放新的 统计类型和索引的映射关系
        statsSeqIxs.put(statsType, statsVO.getSeq());
        // 移除旧的 统计类型和索引的映射关系
        if (delete != null) {
            statsSeqIxs.remove(statsType, delete.getSeq());
        }
        return delete;
    }

    public Pair<StatsVO, Boolean> updateStatsData(StatsType statsType, String hostId, List<Integer> ixSeq, Integer data, BiFunction<Integer, Integer, Integer> dataUpdater) {
        AbstractStatsNode root = gocStatsRootNode(statsType, ixSeq, dataUpdater);
        Pair<StatsVO, Boolean> ret = root.updateStatsData(ixSeq.iterator(), data, () -> new StatsVO(hostId, statsType.getType(), ixSeq));
        statsSeqIxs.put(statsType, ret.getLeft().getSeq());
        return ret;
    }

    public int getStatsData(StatsType type, List<Integer> qryIxSeq) {
        return getStatsData(type, qryIxSeq, 0);
    }

    public int getStatsData(StatsType type, List<Integer> qryIxSeq, int defaultVal) {
        AbstractStatsNode node = roots.get(type);
        if (node == null) {
            return defaultVal;
        }
        return node.getStatsData(qryIxSeq);
    }

    private AbstractStatsNode gocStatsRootNode(StatsType type, List<Integer> ixSeq, BiFunction<Integer, Integer, Integer> dataUpdater) {
        return roots.computeIfAbsent(type, opType -> ixSeq.size() > 0 ? new StatsIndexNode(dataUpdater) : new StatsDataNode(dataUpdater));
    }
}
