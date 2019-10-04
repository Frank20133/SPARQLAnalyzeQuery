package tju.initialization;

import java.io.*;
import java.util.*;


public class GenerateDataMatrix {

    private static final String OUT_PATH = "data/hash.txt";

    private final static int MAX = 100000;

    /**
     * 根据原始数据集构造的矩阵形式表示
     *
     * @return array 数据集的矩阵表示
     * @throws IOException 抛出异常不处理
     * @create 2019/9/29 00:10
     */
    public int[][] createMatrix(String READ_PATH) throws IOException {
        //新建一个map<公司，索引>
        Map<String, Integer> company = new HashMap(4096);

        //存放每一对关系的索引
        ArrayList<ArrayList<Integer>> indexMatrix = new ArrayList<>(4096);

        LineNumberReader addCmp = new LineNumberReader(new FileReader(READ_PATH));
        String line;
        int index = 0;

        while ((line = addCmp.readLine()) != null) {
            //分割完的tmp长度为2
            String[] tmp = line.split("<holder>");

            //初始化索引list
            ArrayList<Integer> tmpIndex = new ArrayList<>();
            for (String cmp : tmp) {
                cmp = cmp.trim().substring(1, cmp.length() - 2);

                //HashMap去重
                if (!company.containsKey(cmp)) {
                    company.put(cmp, index++);
                }

                //获取没对关系中公司对应的索引（2个公司）
                tmpIndex.add(company.get(cmp));
            }
            //添加每一对关系的索引
            indexMatrix.add(tmpIndex);
        }

        //构造邻接矩阵
        int[][] cmpMatrix = new int[company.size()][company.size()];
        for (ArrayList<Integer> anIndexMatrix : indexMatrix) {
            int subject = anIndexMatrix.get(0);
            int object = anIndexMatrix.get(1);
            cmpMatrix[subject][object] = 1;
        }

        //将没有链接到的边权重设为去穷大
        for (int i = 0; i < cmpMatrix.length; i++) {
            for (int j = 0; j < cmpMatrix.length; j++) {
                if ((i != j) && cmpMatrix[i][j] != 1) {
                    cmpMatrix[i][j] = MAX;
                }
            }
        }

        //对HashMap进行升序排序并写入文件
        writeHash(company);

        addCmp.close();
        return cmpMatrix;
    }

    /**
     * 将公司名称以及对应索引写入文件并按照索引进行升序排序（方便最短路径计算）
     *
     * @param company 存放公司名称以及对应的索引
     * @throws IOException 抛出异常
     */
    private void writeHash(Map<String, Integer> company) throws IOException {
        //对邻接矩阵的索引进行升序排序，方便最短路径算法操作
        List<Map.Entry<String, Integer>> list = new ArrayList<>(company.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue));

        FileWriter fileWriter = new FileWriter(OUT_PATH);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for (Map.Entry<String, Integer> entry : list) {
            bufferedWriter.write(entry.getKey() + "-->" + entry.getValue() + "\r\n");
        }

        bufferedWriter.close();
    }

}
