package tju.algrithom;

import java.io.*;
import java.util.*;

public class PageRank {

    //读取公司索引文件
    private static final String READ_PATH = "data/hash.txt";

    //存放PageRank结果，只保存Key
    private static final String OUT_PATH = "data/PageRank/PageRankKeyData.txt";

    private final static int MAX = 100000;

    /**
     * @param matrix    公司的邻接矩阵
     * @param iteration PageRank计算的迭代次数
     * @param d         衰减因子
     * @param limit     输出限制
     * @param seq       排序字段
     * @throws IOException 抛出异常
     * @create: 2019/9/30 08:10
     */
    public void pagerank(int[][] matrix, int iteration, double d, int limit, String seq) throws IOException {
        double[] linkOut = new double[matrix.length];
        double[] pr = new double[matrix.length];

        int limitCount = 0;
        long startTine = System.currentTimeMillis();

        //计算每个节点的出度
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] != MAX) {
                    linkOut[i] += matrix[i][j];
                }
            }
        }

        //初始化pr数组，默认为（1-d）
        for (int i = 0; i < matrix.length; i++) {
            pr[i] = (1 - d);
        }

        //开始迭代计算PR值
        for (int i = 0; i < iteration; i++) {
            pr = calculatePr(matrix, pr, linkOut, d);
        }

        //将PR值和对应的公司名存入HashMap，方便后面输出
        Map<String, Double> cmpPr = new HashMap<>();
        buildPrCompanyHash(READ_PATH, pr, cmpPr);

        //将Hash存入List并进行降序排序
        List<Map.Entry<String, Double>> result = sortHash(cmpPr, seq);

        long endTime = System.currentTimeMillis();


        FileWriter fileWriter = new FileWriter(OUT_PATH);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        System.out.println("PageRank迭代" + iteration + "轮的计算时间为：" + (endTime - startTine) + "ms");

        System.out.println("------------------------------------------------------------");
        System.out.println("?node               |                ?pr                    ");
        System.out.println("------------------------------------------------------------");
        for (Map.Entry<String, Double> res : result) {
            System.out.println(res.getKey() + "    |    " + res.getValue());
            bufferedWriter.write(res.getKey() + " -----> " + res.getValue() + "\r\n");
            if (++limitCount == limit) {
                break;
            }
        }
        System.out.println("------------------------------------------------------------");
        bufferedWriter.close();
    }

    /**
     * 对PR值进行迭代计算
     *
     * @param matrix  邻接矩阵
     * @param pr      上一轮迭代后的PR数组
     * @param linkOut 各个节点的出度数组
     * @param d       衰减因子
     * @return 返回下一轮迭代后的PR数组
     */
    private double[] calculatePr(int[][] matrix, double[] pr, double[] linkOut, double d) {
        //初始化新的PR数组
        double[] factor = new double[pr.length];

        //更新每个节点的PR值
        for (int i = 0; i < pr.length; i++) {
            double num = 0;
            for (int j = 0; j < pr.length; j++) {
                if ((i != j) && ((matrix[j][i] != 0) && (matrix[j][i] != MAX))) {
                    num = num + pr[j] / linkOut[j];
                }
            }
            factor[i] = 1 - d + d * num;
        }

        return factor;
    }

    /**
     * 对各个节点的PR值进行降序排序
     *
     * @param cmpPr 关联公司及PR值的HashMap
     * @param seq   排序字段
     * @return 返回排序好的HashMap
     */
    private List<Map.Entry<String, Double>> sortHash(Map<String, Double> cmpPr, String seq) {
        List<Map.Entry<String, Double>> result = new ArrayList<>(cmpPr.entrySet());
        if (seq.equals("ASC")) {
            result.sort((Comparator.comparing(Map.Entry::getValue)));
        } else if (seq.equals("DESC")) {
            result.sort(((o1, o2) -> o2.getValue().compareTo(o1.getValue())));
        }
        return result;
    }

    /**
     * 关联公司名称以及对应的PR值
     *
     * @param hashFile 读取存放公司及其索引的文件
     * @param pr       计算好的PR数组
     * @param cmpPr    为了关联公司名称和PR值得HashMap
     * @throws IOException 抛出异常
     */
    private void buildPrCompanyHash(String hashFile, double[] pr, Map<String, Double> cmpPr) throws IOException {
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(hashFile));
        String line;
        while ((line = lineNumberReader.readLine()) != null) {
            //分割完的tmp长度为2,第一项为公司，第二项为索引
            String[] tmp = line.split("-->");

            //hashMap中第一项为公司，第二项为公司对应的PR值
            cmpPr.put(tmp[0], pr[Integer.parseInt(tmp[1])]);
        }

        lineNumberReader.close();
    }
}
