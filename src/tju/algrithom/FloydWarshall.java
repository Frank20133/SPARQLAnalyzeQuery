package tju.algrithom;


import java.io.*;
import java.util.*;

public class FloydWarshall {

    //hash中存放的是公司名称以及索引
    private static final String READ_PATH = "data/hash.txt";

    //hash中存放的是公司名称以及索引
    private static final String OUT_PATH = "data/FloydWarshall/FloydWarshallData.txt";

    private final static int MAX = 100000;

    /**
     * 计算弗洛伊德最短路径
     *
     * @param data  邻接矩阵
     * @param limit 输出限制
     * @param seq   排序字段
     * @throws IOException 抛出异常
     */
    public void floydWarshall(int[][] data, int limit, String seq) throws IOException {
        //存放公司名称
        List<String> node = initCompanyArray();

        //存放结果路径以及边数
        Map<StringBuilder, Integer> result = new HashMap<>();

        //控制输出
        int count = 0;

        //初始化路径数组
        int[][] path = new int[data.length][data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                path[i][j] = -1;
            }
        }

        //迭代更新邻接矩阵和路径数组
        for (int m = 0; m < data.length; m++) {
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data.length; j++) {
                    if (data[i][m] + data[m][j] < data[i][j]) {
                        data[i][j] = data[i][m] + data[m][j];
                        //记录经由哪个点到达
                        path[i][j] = m;
                    }
                }
            }
        }

        //寻找路径
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                if (i != j) {
                    if (data[i][j] != MAX) {
                        StringBuilder route = new StringBuilder();
                        route.append(node.get(i)).append("->");
                        findPath(path, node, route, i, j);
                        route.append(node.get(j));
                        result.put(route, data[i][j]);
                    }
                }
            }
        }


        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUT_PATH));
        //对结果进行排序
        List<Map.Entry<StringBuilder, Integer>> entry = sortHash(result, seq);

        //输出并写入文件
        System.out.println("?path                               " + "               |               " + "               ?fw");
        System.out.println("-----------------------------------------------------------------------------------------------");
        for (Map.Entry<StringBuilder, Integer> e : entry) {
            System.out.println(e.getKey() + "   |   " + e.getValue());
            bufferedWriter.write(e.getKey() + " -----> " + e.getValue() + "\r\n");
            if (++count == limit) {
                break;
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------------");
        bufferedWriter.close();
    }

    /**
     * @param result 要进行排序的HashMap
     * @param seq    排序字段
     * @return 返回排序好的结果数组
     */
    private List<Map.Entry<StringBuilder, Integer>> sortHash(Map<StringBuilder, Integer> result, String seq) {
        List<Map.Entry<StringBuilder, Integer>> entry = new ArrayList<>(result.entrySet());
        if (seq.equals("ASC")) {
            entry.sort(Comparator.comparing(Map.Entry::getValue));
        } else if (seq.equals("DESC")) {
            entry.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        }
        return entry;
    }

    /**
     * @return 读取文件并将公司名存入数组
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

    /**
     * 递归寻找路径点
     *
     * @param path  路径数组
     * @param node  存放公司名
     * @param route 添加路径
     * @param i     公司索引
     * @param j     公司索引
     */
    private static void findPath(int[][] path, List<String> node, StringBuilder route, int i, int j) {
        int m = path[i][j];
        if (m == -1) {
            return;
        }

        findPath(path, node, route, i, m);
        route.append(node.get(m)).append("->");
        findPath(path, node, route, m, j);
    }
}
