package tju.algrithom;

import java.io.*;
import java.util.*;

public class BellmanFord {

    //hash中存放的是公司名称以及索引
    private static final String READ_PATH = "data/hash.txt";

    //存放结果路径的文件（针对单源算法）
    private static final String OUT_PATH = "data/BellmanFord/BellmanFordData.txt";

    //最大值定义
    private final static int MAX = 100000;

    /**
     * 计算指定节点的BellFord最短路径
     *
     * @param data        邻接矩阵
     * @param companyName 指定的节点
     * @param limit       输出限制
     * @param seq         排序字段
     * @throws IOException 抛出异常
     */
    public void bellmanFord(int[][] data, String companyName, int limit, String seq) throws IOException {
        // distance则是记录还未求出最短路径的顶点(以及该顶点到起点s的距离)，与 flag配合使用,flag[i] == true 表示U中i顶点已被移除
        int[] distance = new int[data.length];

        // 前驱顶点数组,即，prev[i]的值是"顶点vs"到"顶点i"的最短路径所经历的全部顶点中，位于"顶点i"之前的那个顶点。
        int[] prev = new int[data.length];

        //存放所有的公司名
        String[] vertexes = new String[data.length];

        int count = 0;

        initCompanyArray(vertexes);

        //起点公司的索引
        int vs = 0;
        for (String name : vertexes) {
            if (name.equals(companyName)) {
                break;
            }
            ++vs;
        }

        for (int i = 0; i < vertexes.length; i++) {
            distance[i] = MAX; //初始化为无穷大

            prev[i] = -1; //初始化为无效索引值
        }
        distance[vs] = 0; //从节点vs到其他节点的最短路径

        for (int T = 0; T < data.length - 1; T++) { //进行nodeNum-1此迭代计算
            boolean update = false;
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data.length; j++) {
                    if (i != j) {
                        if (data[i][j] > 0) {
                            if (distance[j] > distance[i] + data[i][j]) {
                                update = true;
                                distance[j] = distance[i] + data[i][j];
                                prev[j] = i;
                            }
                        }
                    }
                }
            }
            if (!update) {
                break;
            }
        }

        //寻找路径名
        Map<StringBuilder, Integer> result = searchPath(distance, prev, vertexes, vs);

        //对路径长度进行排序并输出
        sortAndSave(limit, seq, count, result);
    }

    /**
     * 针对某个节点搜寻路径点名称
     *
     * @param distance 起点到各点的距离
     * @param prev     存放某点的前驱结点
     * @param vertexes 存放节点名称
     * @param vs       起点索引
     * @return 返回存放路径以及路径长度的HashMap
     */
    private Map<StringBuilder, Integer> searchPath(int[] distance, int[] prev, String[] vertexes, int vs) {
        Map<StringBuilder, Integer> result = new HashMap<>();
        for (int i = 0; i < vertexes.length; i++) {
            if (distance[i] == MAX) {
                continue;
            }
            StringBuilder printPath = new StringBuilder();

            List<String> path = new ArrayList<>();
            int j = i;
            while (true) {
                if (j == -1) {
                    path.remove(path.size() - 1);
                    path.add(vertexes[vs]);
                    if (path.size() == 2 && path.get(0).equals(path.get(path.size() - 1))) {
                        path.remove(path.size() - 1);
                    }
                    break;
                }
                path.add(vertexes[j]);


                j = prev[j];
            }

            for (int x = path.size() - 1; x >= 0; x--) {
                if (x == 0) {
                    printPath.append(path.get(x));
                } else {
                    printPath.append(path.get(x)).append("->");
                }
            }
            result.put(printPath, distance[i]);
        }
        return result;
    }

    /**
     * 对结果数组进行排序输出并保存
     *
     * @param limit  输出限制
     * @param seq    排序字段
     * @param count  计数
     * @param result 存放路径+边数
     * @throws IOException 抛出异常
     */
    private void sortAndSave(int limit, String seq, int count, Map<StringBuilder, Integer> result) throws IOException {
        List<Map.Entry<StringBuilder, Integer>> list = new ArrayList<>(result.entrySet());
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUT_PATH));

        if (seq.equals("ASC")) {
            list.sort(Comparator.comparing(Map.Entry::getValue));
        } else if (seq.equals("DESC")) {
            list.sort(((o1, o2) -> o2.getValue().compareTo(o1.getValue())));
        }
        for (Map.Entry<StringBuilder, Integer> entry : list) {
            if (entry.getValue() != 0) {
                System.out.println(entry.getKey() + "   :   " + entry.getValue());
                bufferedWriter.write(entry.getKey() + " -----> " + entry.getValue() + "\r\n");
                if (++count == limit) {
                    break;
                }
            }
        }

        bufferedWriter.close();
    }

    /**
     * 初始化公司数组
     *
     * @param vertexes 存放公司名的数组
     * @throws IOException 抛出异常
     */
    private void initCompanyArray(String[] vertexes) throws IOException {
        LineNumberReader lineNumberReader = new LineNumberReader((new FileReader(READ_PATH)));
        String line;
        int idx = 0;
        while ((line = lineNumberReader.readLine()) != null) {
            //分割完的tmp长度为2,第一项为公司，第二项为索引
            String[] tmp = line.split("-->");

            vertexes[idx++] = tmp[0];
        }

        lineNumberReader.close();
    }
}
