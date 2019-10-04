package tju.initialization;


import tju.algrithom.Louvain;

import java.io.IOException;

public class Main {

    private static final String READ_PATH = "data/data_final.txt";

    public static void main(String[] args) throws IOException {
        //数据集的矩阵表示
        int[][] data = new GenerateDataMatrix().createMatrix(READ_PATH);
        //new PageRank().pagerank(data, 20, 0.85,500,"DESC");
        //new Dijkstra().dijkstra(data, "安邦财产保险股份有限公司", 5000,"ASC");
        //ShortestPath().shortestPath(data,"中国证券金融股份有限公司","中央汇金资产管理有限责任公司", "ASC");
        //BellmanFord().bellmanFord(data,"和谐健康保险股份有限公司", 300, "ASC");
        //new FloydWarshall().floydWarshall(data,20, "ASC");
        //new BetweenessCentrality().betweenessCentrality(100,"DESC");
        //new ClossnessCentrality().clossnessCentrality(data, Integer.MAX_VALUE, "DESC");
        //new DegreeCentrality().degreeCentrality(data, "out", 10, "DESC");

        //Louvain算法
        //new GenerateFileForLouvain().generateFileForLouvain();
        new Louvain().louvain();

        //Tarjan算法
        //new Tarjan(data).run();
    }


}
