package tju.algrithom;


import java.io.*;
import java.util.*;

public class DegreeCentrality {

    //hash中存放的是公司名称以及索引
    private static final String READ_PATH = "data/hash.txt";

    //结果路径输出文件
    private static final String OUT_PATH = "data/DegreeCentrality/DegreeCentralityData.txt";

    private final static int MAX = 100000;

    /**
     * 计算度中心性
     *
     * @param data   邻接矩阵
     * @param degree 度的类型
     * @param limit  输出限制
     * @param seq    排序字段
     * @throws IOException 抛出异常
     */
    public void degreeCentrality(int[][] data, String degree, int limit, String seq) throws IOException {
        //存放公司的数组
        List<String> node = initCompanyArray();
        //存放(公司：度中心性)的HashMap
        Map<String, Integer> result = new HashMap<>();

        int count = 0;

        if (degree.equals("in")) {
            //计算入度
            for (int i = 0; i < data.length; i++) {
                int in = 0;
                for (int j = 0; j < data[i].length; j++) {
                    if (data[j][i] != 0 && data[j][i] != MAX) {
                        in += data[j][i];
                    }
                }
                result.put(node.get(i), in);
            }
        } else if (degree.equals("out")) {
            //计算出度
            for (int i = 0; i < data.length; i++) {
                int out = 0;
                for (int j = 0; j < data[i].length; j++) {
                    if (data[i][j] != 0 && data[i][j] != MAX) {
                        out += data[i][j];
                    }
                }
                result.put(node.get(i), out);
            }
        }

        //对HashMap排序
        List<Map.Entry<String, Integer>> list = sortHash(seq, result);

        //打印结果并保存
        printAndSave(limit, count, list);

    }

    /**
     * 打印并写入文件
     *
     * @param limit 输出限制
     * @param count 计数
     * @param list  存放公司名和对应度的list
     * @throws IOException 抛出异常
     */
    private void printAndSave(int limit, int count, List<Map.Entry<String, Integer>> list) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUT_PATH));
        System.out.println("name                " + "|" + "             followers");
        System.out.println("------------------------------------------------------");
        for (Map.Entry<String, Integer> entry : list) {
            System.out.println(entry.getKey() + "   |   " + entry.getValue());
            bufferedWriter.write(entry.getKey() + " -----> " + entry.getValue() + "\r\n");
            if (++count == limit) {
                break;
            }
        }
        System.out.println("------------------------------------------------------");
        bufferedWriter.close();
    }

    /**
     * 对结果按照度中心性进行排序
     *
     * @param seq    排序字段
     * @param result 存放公司名以及度中心性的HashMap
     * @return 返回排好序的list
     */
    private List<Map.Entry<String, Integer>> sortHash(String seq, Map<String, Integer> result) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(result.entrySet());
        if (seq.equals("ASC")) {
            list.sort(Comparator.comparing(Map.Entry::getValue));
        } else if (seq.equals("DESC")) {
            list.sort(((o1, o2) -> o2.getValue().compareTo(o1.getValue())));
        }
        return list;
    }

    /**
     * 初始化公司数组
     *
     * @return 返回初始化好的数组，里面存放的公司名
     * @throws IOException 抛出异常
     */
    private List<String> initCompanyArray() throws IOException {
        List<String> node = new ArrayList<>();
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(READ_PATH));
        String line;
        while ((line = lineNumberReader.readLine()) != null) {
            node.add(line.split("-->")[0].trim());
        }
        lineNumberReader.close();
        return node;
    }
}
