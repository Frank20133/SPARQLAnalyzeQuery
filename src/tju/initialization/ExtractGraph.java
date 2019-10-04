package tju.initialization;

import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ExtractGraph {
    private final String ORIGINAL_GRAPH = "data/data_final.txt";
    private final String SUB_GRAPH = "data/SubGraph/Subgraph.txt";

    /**
     * 根据给定的公司名set，抽取出在data_final中仅包含这些点的子图
     *
     * @param algorithmGraph 算法结果文件
     * @throws IOException
     */
    public void generateSubgraph(String algorithmGraph) throws IOException {
        Set<String> companySet = extractCompany(algorithmGraph);
        generateSubgraph0(companySet);
    }

    /**
     * 根据给定的两个公司名set，抽取出在data_final中仅包含这些点的子图
     *
     * @param algorithmGraph1 算法1的结果文件
     * @param algorithmGraph2 算法2的结果文件
     * @throws IOException
     */
    public void generateSubgraph(String algorithmGraph1, String algorithmGraph2) throws IOException {
        Set<String> companySet = combineCompany(algorithmGraph1, algorithmGraph2);
        generateSubgraph0(companySet);
    }

    private void generateSubgraph0(Set<String> companySet) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(ORIGINAL_GRAPH));
        BufferedWriter bw = new BufferedWriter(new FileWriter(SUB_GRAPH));
        String originalGraphLine;

        while ((originalGraphLine = br.readLine()) != null) {
            //存data_final中每一行公司的名字
            Set<String> companyByLine = new HashSet(2);
            String[] companys = originalGraphLine.split("<holder>");

            for (String company : companys) {
                //去掉前后<>
                String norCompany = company.substring(company.indexOf("<") + 1, company.indexOf(">"));
                companyByLine.add(norCompany);
            }

            if (!Collections.disjoint(companySet, companyByLine)) {
                bw.write(originalGraphLine + "\r\n");
            }
        }

        //注意Subgraph文件的最后有一个空行
        bw.close();
        br.close();
    }

    /**
     * 根据算法结果中的所有公司抽取出公司名集合
     *
     * @param algorithmGraph 算法结果文件
     * @return Hashset  包含算法结果中所有公司名的一个set
     * @throws IOException
     */
    private Set<String> extractCompany(String algorithmGraph) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(algorithmGraph));
        String algorithmGraphLine;

        //存原始图中所有公司的名字
        Set<String> companySet = new HashSet();
        while ((algorithmGraphLine = br.readLine()) != null) {
            String[] lineArr = algorithmGraphLine.split("----->");

            //取出----->左边的字符串
            String lineArrFirst = lineArr[0].trim();
            String[] companyNames = lineArrFirst.split("->");

            //将----->左边所有的公司加入到companySet中
            for (String companeName : companyNames) {
                companySet.add(companeName);
            }
        }
        //   System.out.println(companySet);
        br.close();
        return companySet;
    }

    /**
     * 根据两个算法的结果抽取并合并出一个公司名集合
     *
     * @param algorithmGraph1 算法1的结果文件
     * @param algorithmGraph2 算法2的结果文件
     * @return HashSet 包含算法结果中所有公司名的一个set
     * @throws IOException
     */
    private Set<String> combineCompany(String algorithmGraph1, String algorithmGraph2) throws IOException {
        Set<String> companySet1 = extractCompany(algorithmGraph1);
        Set<String> companySet2 = extractCompany(algorithmGraph2);

        //取交集
        companySet1.addAll(companySet2);
        return companySet1;
    }
}
