package com.yupi.usercenter.common;

import java.util.List;

/**
 * @Author xuao
 * @Date 2024/11/3 14:19
 * @Description: 计算两个字符串的需要经过多少次操作才会一致。数字越小，相似度越高
 */
public  class MinDistanceUtils {

        public static int minDistance(String word1, String word2) {
            int m = word1.length();
            int n = word2.length();
            int[][] dp = new int[m + 1][n + 1];

            for (int i = 0; i <= m; i++) {
                dp[i][0] = i;
            }

            for (int j = 0; j <= n; j++) {
                dp[0][j] = j;
            }

            for (int i = 1; i <= m; i++) {
                for (int j = 1; j <= n; j++) {
                    if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                        dp[i][j] = dp[i - 1][j - 1];
                    } else {
                        dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                    }
                }
            }
            return dp[m][n];
        }

    public static int minDistance(List<String> tags1, List<String> tags2) {
        int m = tags1.size();
        int n = tags2.size();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (tags1.get(i - 1).equals(tags2.get(j - 1))) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

}
