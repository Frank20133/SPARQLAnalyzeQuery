package tju.algrithom;

import java.io.*;
import java.util.*;

public class ClossnessCentrality {

    //hash中存放的是公司名称以及索引
    private static final String READ_PATH = "data/hash.txt";

    //存放某节点的单源最短路径
    private static final String ROUTE_PATH = "data/Dijkstra/DijkstraPathData.txt";

    //结果存放路径
    private static final String OUT_PATH = "data/ClossnessCentrality/ClossnessCentralityData.txt";

    /**
     * 计算CC值并降序输出
     *
     * @param data  邻接矩阵
     * @param limit 输出限制
     * @param seq   排序字段
     * @throws IOException 抛出异常
     */
    public void clossnessCentrality(int[][] data, int limit, String seq) throws IOException {
        List<String> node = initCompanyArray();
        Map<String, Double> result = new HashMap<>();

        double nodeLen = node.size() - 1;
        int count = 0;

        for (String aNode : node) {
            new Dijkstra().dijkstra(data, aNode, Integer.MAX_VALUE, "ASC");
            List<Double> edge = getEdge();
            double temp = 0;
            if (edge.size() != 0) {
                double edgeLen = 0;
                for (Double integer : edge) {
                    edgeLen = edgeLen + (1 / integer);
                }
                temp = edgeLen / (nodeLen - 1);
            }
            if (temp != 0) {
                result.put(aNode, temp * 10);
            }
        }

        sortAndPrint(limit, seq, result, count);
    }

    /**
     * 对结果进行排序并打印保存
     *
     * @param limit  输出限制
     * @param seq    排序字段
     * @param result 存放公司以及对应CC值得HashMap
     * @param count  计数
     * @throws IOException 抛出异常
     */
    private void sortAndPrint(int limit, String seq, Map<String, Double> result, int count) throws IOException {
        List<Map.Entry<String, Double>> list = new ArrayList<>(result.entrySet());

        if (seq.equals("ASC")) {
            list.sort(Comparator.comparing(Map.Entry::getValue));
        } else if (seq.equals("DESC")) {
            list.sort(((o1, o2) -> o2.getValue().compareTo(o1.getValue())));
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUT_PATH));
        for (Map.Entry<String, Double> entry : list) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            bufferedWriter.write(entry.getKey() + " -----> " + entry.getValue() + "\r\n");
            System.out.println(count);
            if (++count == limit) {
                break;
            }
        }

        bufferedWriter.close();
    }

    /**
     * 针对某个节点获取它的所有最短路径的边数
     *
     * @return 存放某节点的最短路径的边数
     * @throws IOException 抛出异常
     */
    private List<Double> getEdge() throws IOException {
        List<Double> edgeLen = new ArrayList<>();
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(ROUTE_PATH));
        String line;
        while ((line = lineNumberReader.readLine()) != null) {
            edgeLen.add(Double.parseDouble(line.split("----->")[1].trim()));
        }
        lineNumberReader.close();
        return edgeLen;
    }

    /**
     * 初始化存放公司的数组
     *
     * @return 存有公司名的数组
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
