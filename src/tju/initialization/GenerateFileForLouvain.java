package tju.initialization;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GenerateFileForLouvain {

    //数据集文件
    private static final String DATA_PATH = "data/data_final.txt";

    //公司名存放文件
    private static final String COMPANY_PATH = "data/hash.txt";

    //Louvain所需的文件
    private static final String OUT_PATH = "data/louvain_intermediate_data.txt";

    public void generateFileForLouvain() throws IOException {
        //存放公司名及对应索引
        Map<String, Integer> company = new HashMap<>(4096);

        //存放每一对关系的索引
        ArrayList<ArrayList<Integer>> indexMatrix = new ArrayList<>(4096);

        //初始化company
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(COMPANY_PATH));
        String line;
        while ((line = lineNumberReader.readLine()) != null) {
            company.put(line.split("-->")[0].trim(), Integer.parseInt(line.split("-->")[1].trim()));
        }
        lineNumberReader.close();

        //读取原始数据集并获取节点的索引并存入indexMatrix
        LineNumberReader lineNumberReader1 = new LineNumberReader(new FileReader(DATA_PATH));
        String line1;
        while ((line1 = lineNumberReader1.readLine()) != null) {
            //分割完的tmp长度为2
            String[] tmp = line1.split("<holder>");

            //初始化索引list
            ArrayList<Integer> tmpIndex = new ArrayList<>();
            for (String cmp : tmp) {
                cmp = cmp.trim().substring(1, cmp.length() - 2);

                //获取没对关系中公司对应的索引（2个公司）
                tmpIndex.add(company.get(cmp));
            }
            //添加每一对关系的索引
            indexMatrix.add(tmpIndex);
        }
        lineNumberReader1.close();

        indexMatrix.sort((o1, o2) -> {
            if (o1.get(0).equals(o2.get(0))) {
                return Integer.compare(o1.get(1), o2.get(1));
            }
            return Integer.compare(o1.get(0), o2.get(0));
        });


        //遍历indexMatrix并将边的关系写入文件
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUT_PATH));
        //第一行写入节点个数以及边的个数
        bufferedWriter.write(String.valueOf(company.size()) + " " + String.valueOf(indexMatrix.size()) + "\r\n");
        for (ArrayList<Integer> arrayList : indexMatrix) {
            for (Integer index : arrayList) {
                bufferedWriter.write(String.valueOf(index) + " ");
            }
            bufferedWriter.write(String.valueOf(1) + "\r\n");
        }
        bufferedWriter.close();

    }
}
