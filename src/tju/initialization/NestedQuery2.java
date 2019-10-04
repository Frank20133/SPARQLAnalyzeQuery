package tju.initialization;


import tju.algrithom.DegreeCentrality;
import tju.algrithom.FloydWarshall;
import tju.algrithom.PageRank;

import java.io.IOException;

/**
 * It's the class for Nested SPARQL Analyze Query of PageRank + Degree Centrality + Floyd-Warshall
 */
public class NestedQuery2 {
    private static final String READ_PATH = "data/data_final.txt";
    private static final String ALGORITHM_PATH1 = "data/PageRank/PageRankKeyData.txt";
    private static final String ALGORITHM_PATH2 = "data/DegreeCentrality/DegreeCentralityData.txt";
    public static final String SUB_GRAPH = "data/SubGraph/Subgraph.txt";

    public static void main(String[] args) throws IOException {
        int[][] originalData = new GenerateDataMatrix().createMatrix(READ_PATH);

        //得出ALGORITHM_PATH1
        new PageRank().pagerank(originalData, 20, 0.85, 100, "DESC");

        //得出ALGORITHM_PATH2
        new DegreeCentrality().degreeCentrality(originalData, "in", 100, "DESC");

        //得出SUB_GRAPH
        new ExtractGraph().generateSubgraph(ALGORITHM_PATH1, ALGORITHM_PATH2);

        int[][] subgraphData = new GenerateDataMatrix().createMatrix(SUB_GRAPH);
        new FloydWarshall().floydWarshall(subgraphData, 100, "ASC");

    }
}
