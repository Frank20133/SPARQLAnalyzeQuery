package tju.algrithom;

import java.io.*;
import java.util.*;

public class Dijkstra {

    //hash中存放的是公司名称以及索引
    private static final String READ_PATH = "data/hash.txt";

    //存放结果路径的文件（针对单源算法）
    private static final String OUT_PATH = "data/Dijkstra/DijkstraPathData.txt";

    private final static int MAX = 100000;

    /**
     * 单源的dijkstra最短路径，只给定起点
     *
     * @param data        数据集的邻接矩阵
     * @param companyName 传入起点公司名称
     * @param seq         排序字段
     * @throws IOException 抛出异常不处理
     * @create: 2019/10/01 08:10
     */
    public void dijkstra(int[][] data, String companyName, int limit, String seq) throws IOException {
        // flag[i]=true表示"顶点vs"到"顶点i"的最短路径已成功获取
        boolean[] flag = new boolean[data.length];

        // U则是记录还未求出最短路径的顶点(以及该顶点到起点s的距离)，与 flag配合使用,flag[i] == true 表示U中i顶点已被移除
        int[] U = new int[data.length];

        // 前驱顶点数组,即，prev[i]的值是"顶点vs"到"顶点i"的最短路径所经历的全部顶点中，位于"顶点i"之前的那个顶点。
        int[] prev = new int[data.length];

        //存放所有的公司名
        String[] vertexes = new String[data.length];

        //将所有的公司名放入数组
        initCompanyName(vertexes);

        //起点公司的索引
        int vs = 0;
        for (String name : vertexes) {
            if (name.equals(companyName)) {
                break;
            }
            ++vs;
        }

        // 步骤一：初始时，S中只有起点vs；U中是除vs之外的顶点，并且U中顶点的路径是"起点vs到该顶点的路径"。
        for (int i = 0; i < vertexes.length; i++) {
            flag[i] = false; // 顶点i的最短路径还没获取到。
            U[i] = data[vs][i]; // 顶点i与顶点vs的初始距离为"顶点vs"到"顶点i"的权。也就是邻接矩阵vs行的数据。

            prev[i] = vs; //顶点i的前驱顶点为0
        }

        // 将vs从U中“移除”（U与flag配合使用）
        flag[vs] = true;
        U[vs] = 0;
        // 步骤一结束

        //步骤四：重复步骤二三，直到遍历完所有顶点。
        // 遍历vertexes.length-1次；每次找出一个顶点的最短路径。
        int k = 0;
        for (int i = 1; i < vertexes.length; i++) {
            // 步骤二：从U中找出路径最短的顶点，并将其加入到S中（如果vs顶点到x顶点还有更短的路径的话，那么
            // 必然会有一个y顶点到vs顶点的路径比前者更短且没有加入S中
            // 所以，U中路径最短顶点的路径就是该顶点的最短路径）
            // 即，在未获取最短路径的顶点中，找到离vs最近的顶点(k)。
            int min = MAX;
            for (int j = 0; j < vertexes.length; j++) {
                if (!flag[j] && U[j] < min) {
                    min = U[j];
                    k = j;
                }
            }

            //步骤二结束


            //步骤三：更新U中的顶点和顶点对应的路径
            //标记"顶点k"为已经获取到最短路径（更新U中的顶点，即将k顶点对应的flag标记为true）
            flag[k] = true;

            //修正当前最短路径和前驱顶点（更新U中剩余顶点对应的路径）
            //即，当已经"顶点k的最短路径"之后，更新"未获取最短路径的顶点的最短路径和前驱顶点"。
            for (int j = 0; j < vertexes.length; j++) {
                //以k顶点所在位置连线其他顶点，判断其他顶点经过最短路径顶点k到达vs顶点是否小于目前的最短路径，是，更新入U，不是，不做处理
                int tmp = (data[k][j] == MAX ? MAX : (min + data[k][j]));
                if (!flag[j] && (tmp < U[j])) {
                    U[j] = tmp;
                    //更新 j顶点的最短路径前驱顶点为k
                    prev[j] = k;
                }
            }
            //步骤三结束
        }
        //步骤四结束

        // 对路径的长度进行升序排序
        List<Map.Entry<StringBuilder, Integer>> result = sortByPathLength(U, prev, vertexes, vs, seq);

        //打印结果
        printAndWriteResult(result, limit);

    }

    /**
     * 打印最终的结果并将结果保存到文件，路径信息+边数
     *
     * @param result 传入按照边数排序好的HashMap
     * @throws IOException 抛出异常不处理
     */
    private void printAndWriteResult(List<Map.Entry<StringBuilder, Integer>> result, int limit) throws IOException {

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUT_PATH));

        System.out.println("?path                " + "                         |              " + "?dj");
        System.out.println("---------------------------------------------------------------------------");
        int count = 0;
        for (Map.Entry<StringBuilder, Integer> print : result) {
            if (print.getValue() != 0) {
                if (++count == limit) {
                    break;
                }
                System.out.println(print.getKey() + "       |       " + print.getValue());
                bufferedWriter.write(print.getKey() + " -----> " + print.getValue() + "\r\n");
            }
        }
        System.out.println("---------------------------------------------------------------------------");

        bufferedWriter.close();
    }

    /**
     * 按照边数对结果进行升序排序
     *
     * @param u        到vs节点的路径长度
     * @param prev     存放当前节点索引的前驱节点索引
     * @param vertexes 存放公司名称
     * @param vs       给定的起点公司索引
     * @param seq      排序字段
     * @return 返回排序好的HashMap
     */
    private List<Map.Entry<StringBuilder, Integer>> sortByPathLength(int[] u, int[] prev, String[] vertexes, int vs, String seq) {
        Map<StringBuilder, Integer> result = new HashMap<>();
        for (int i = 0; i < vertexes.length; i++) {
            if (u[i] == MAX) {
                continue;
            }
            StringBuilder printPath = new StringBuilder();

            List<String> path = new ArrayList<>();
            int j = i;
            while (true) {
                path.add(vertexes[j]);

                if (j == vs) {
                    path.remove(path.size() - 1);
                    path.add(vertexes[vs]);
                    if (path.size() == 2 && path.get(0).equals(path.get(path.size() - 1))) {
                        path.remove(path.size() - 1);
                    }
                    break;
                }

                j = prev[j];
            }

            for (int x = path.size() - 1; x >= 0; x--) {
                if (x == 0) {
                    printPath.append(path.get(x));
                } else {
                    printPath.append(path.get(x)).append("->");
                }
            }
            result.put(printPath, u[i]);
        }
        List<Map.Entry<StringBuilder, Integer>> list = new ArrayList<>(result.entrySet());
        if (seq.equals("ASC")) {
            list.sort(Comparator.comparing(Map.Entry::getValue));
        } else if (seq.equals("DESC")) {
            list.sort(((o1, o2) -> o2.getValue().compareTo(o1.getValue())));
        }
        return list;
    }

    /**
     * 初始化公司名称数组
     *
     * @param vertexes 存放公司名称的字符串数组
     * @throws IOException 抛出异常
     */
    private void initCompanyName(String[] vertexes) throws IOException {
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

