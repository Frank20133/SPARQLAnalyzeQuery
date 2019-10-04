package tju.algrithom;

import java.io.*;

public class ShortestPath {

    //hash中存放的是公司名称以及索引
    private static final String READ_PATH = "data/Dijkstra/DijkstraPathData.txt";

    //存放结果路径的文件（针对单源算法）
    private static final String OUT_PATH = "data/ShortestPath/ShortestPathData.txt";

    /**
     * @param data      邻接矩阵
     * @param startName 起点
     * @param endName   终点
     * @param seq       排序字段
     * @throws IOException 抛出异常
     */
    public void shortestPath(int[][] data, String startName, String endName, int limit, String seq) throws IOException {
        new Dijkstra().dijkstra(data, startName, limit, seq);

        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(READ_PATH));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUT_PATH));
        String line;
        while ((line = lineNumberReader.readLine()) != null) {
            String[] tmp = line.split("----->");
            String path = tmp[0].trim();
            String lastCompany = path.substring(path.lastIndexOf(">") + 1);
            if (lastCompany.equals(endName)) {
                bufferedWriter.write(path + "\r\n");
            }
        }

        bufferedWriter.close();
        lineNumberReader.close();
    }
}
