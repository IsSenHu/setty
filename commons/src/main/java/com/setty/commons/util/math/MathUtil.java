package com.setty.commons.util.math;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/11 15:00
 */
public class MathUtil {

    /**
     * 求最大公约数
     *
     * @param a 数a
     * @param b 数b
     * @return 最大公约数
     */
    public static int gcd(int a, int b) {
        if (a % b == 0) {
            return b;
        }
        return gcd(b, a % b);
    }

    /**
     * 分解质因数
     *
     * @param num 目标数字
     * @return 质因数数组
     */
    public static int[] decPosFactor(int num) {
        List<Integer> integers = new ArrayList<>();
        int j = 0;
        for (int i = 2; i <= num; i++) {
            if (num % i == 0) {
                integers.add(i);
                num /= i;
                i--;
            }
        }
        int[] ret = new int[integers.size()];
        for (int i = 0; i < integers.size(); i++) {
            ret[i] = integers.get(i);
        }
        return ret;
    }
}
