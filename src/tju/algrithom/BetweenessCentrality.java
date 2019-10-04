package tju.algrithom;

import java.io.*;
import java.util.*;

public class BetweenessCentrality {

    //hash中存放的是公司名称以及索引
    private static final String READ_PATH = "data/hash.txt";
    //存放所有最短路径的文件
    private static final String PATH_PATH = "data/FloydWarshall/FloydWarshallSortAllData.txt";
    //存放结果的文件
    private static final String OUT_PATH = "ata/BetweenessCentrality/BetweenessCentralityData.txt";

    /**
     * 计算各个节点BC值并进行排序输出
     *
     * @param limit 输出限制
     * @param seq   排序字段
     * @throws IOException 抛出异常
     */
    public void betweenessCentrality(int limit, String seq) throws IOException {
        List<String> allNode = InitNodeList();
        double[] nodeBCvalue = new double[allNode.size()];

        Random random = new Random();
        Set<String> node = new HashSet<>();

        Map<String, Double> result = new HashMap<>();

        int count = 0;
        int cnt = 0;

        List<String> path = InitPathArray();
        while (node.size() != allNode.size()) {
            int idx = random.nextInt(allNode.size());
            if (!node.contains(allNode.get(idx))) {
                System.out.println(cnt++);
                node.add(allNode.get(idx));
                for (int j = 0; j < path.size(); j++) {
                    double temp = 0;
                    //BC公式分子
                    double SPcontainsNode = 0;
                    //取出单条路径
                    String s = path.get(j);
                    //BC式子分母
                    double SP = 0;
                    ++SP;

                    String[] split = s.split("->");
                    if (!split[0].equals(allNode.get(idx)) && !split[split.length - 1].equals(allNode.get(idx))) {
                        if (Arrays.asList(split).contains(allNode.get(idx))) {
                            ++SPcontainsNode;
                        }
                    }

                    temp = SPcontainsNode / SP;
                    nodeBCvalue[idx] += temp;
                }
                result.put(allNode.get(idx), nodeBCvalue[idx]);
            }
        }

        //排序并保存输出
        List<Map.Entry<String, Double>> list = sortHash(seq, result);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUT_PATH));

        System.out.println("?path                               " + "               |               " + "               ?bc");
        System.out.println("------------------------------------------------------------------------------------------------");
        for (Map.Entry<String, Double> entry : list) {
            System.out.println(entry.getKey() + "       |      " + entry.getValue());
            bufferedWriter.write(entry.getKey() + " -----> " + entry.getValue() + "\r\n");
            if (++count == limit) {
                break;
            }
        }
        bufferedWriter.close();
        System.out.println("------------------------------------------------------------------------------------------------");

    }

    /**
     * 对结果进行排序
     *
     * @param seq    排序字段
     * @param result 待排序HashMap
     * @return 返回排序好的结果数组
     */
    private List<Map.Entry<String, Double>> sortHash(String seq, Map<String, Double> result) {
        List<Map.Entry<String, Double>> list = new ArrayList<>(result.entrySet());
        if (seq.equals("ASC")) {
            list.sort(Comparator.comparing(Map.Entry::getValue));
        } else if (seq.equals("DESC")) {
            list.sort(((o1, o2) -> o2.getValue().compareTo(o1.getValue())));
        }
        return list;
    }

    /**
     * 初始化存放所有最短路径的数组
     *
     * @return 返回装有所有最短路径的数组
     * @throws IOException 抛出异常
     */
    private List<String> InitPathArray() throws IOException {
        List<String> path = new ArrayList<>();
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(PATH_PATH));
        String line;
        while ((line = lineNumberReader.readLine()) != null) {
            path.add(line.split("----->")[0].trim());
        }
        lineNumberReader.close();
        return path;
    }

    /**
     * 初始化存放公司名的数组
     *
     * @return 返回存放公司名的数组
     * @throws IOException 抛出异常
     */
    private List<String> InitNodeList() throws IOException {
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(READ_PATH));
        List<String> node = new ArrayList<>();
        String line;
        while ((line = lineNumberReader.readLine()) != null) {
            node.add(line.split("-->")[0].trim());
        }

        lineNumberReader.close();

        return node;
    }
}
