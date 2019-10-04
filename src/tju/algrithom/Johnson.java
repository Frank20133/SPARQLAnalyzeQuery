package tju.algrithom;


import java.io.*;
import java.util.*;

public class Johnson {

    //hash中存放的是公司名称以及索引
    private static final String READ_PATH = "data/hash.txt";

    //存放结果路径的文件（Djikstra）
    private static final String ROUTE_PATH = "data/Dijkstra/DijkstraPathData.txt";

    //存放Johnson的结果
    private static final String OUT_PATH = "ata/Johnson/JohnsonData.txt";

    /**
     * 利用Jhonson计算多源最短路径（利用Djikstra实现）
     *
     * @param data  邻接矩阵
     * @param limit 输出限制
     * @param seq   排序字段
     * @throws IOException 抛出异常
     */
    public void jhonson(int[][] data, int limit, String seq) throws IOException {
        //存放所有公司名
        List<String> node = initCompanyArray();
        //存放路径信息和路径长度
        Map<String, Integer> result = new HashMap<>();

        int count = 0;

        //遍历所有节点
        for (String company : node) {
            //对每个节点进行Djikstra求最短路径
            new Dijkstra().dijkstra(data, company, limit, seq);

            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(ROUTE_PATH));
            String line;

            //将路径以及路径长度存入HashMap，方便之后的排序操作
            while ((line = lineNumberReader.readLine()) != null) {
                String[] tmp = line.split("----->");
                result.put(tmp[0].trim(), Integer.parseInt(tmp[1].trim()));
            }
            lineNumberReader.close();
        }

        //对HashMap进行排序并输出写入文件
        sortAndPrint(limit, seq, result, count);
    }

    /**
     * 按照路径长度进行排序并进行输出并写入文件
     *
     * @param limit  输出限制
     * @param seq    排序字段
     * @param result 存放路径和路径长度的HashMap
     * @param count  计数
     * @throws IOException 抛出异常
     */
    private void sortAndPrint(int limit, String seq, Map<String, Integer> result, int count) throws IOException {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(result.entrySet());
        if (seq.equals("ASC")) {
            list.sort(Comparator.comparing(Map.Entry::getValue));
        } else if (seq.equals("DESC")) {
            list.sort(((o1, o2) -> o2.getValue().compareTo(o1.getValue())));
        }

        System.out.println("?path                               " + "|" + "                             ?js");
        System.out.println("--------------------------------------------------------------------------------");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUT_PATH));
        for (Map.Entry<String, Integer> entry : list) {
            System.out.println(entry.getKey() + "   |   " + entry.getValue());
            bufferedWriter.write(entry.getKey() + " -----> " + entry.getValue() + "\r\n");
            if (++count == limit) {
                break;
            }
        }
        bufferedWriter.close();
        System.out.println("--------------------------------------------------------------------------------");
    }

    /**
     * 初始化存放公司名的数组
     *
     * @return 装有公司名的List
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
