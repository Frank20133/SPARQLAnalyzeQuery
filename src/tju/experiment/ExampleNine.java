package tju.experiment;

import tju.algrithom.Dijkstra;
import tju.algrithom.PageRank;
import tju.initialization.ExtractGraph;
import tju.initialization.GenerateDataMatrix;

import java.io.IOException;

/**
 * It's the class for Nested SPARQL Analyze Query of Dijkstra + PageRank
 */
public class ExampleNine {
    private static final String READ_PATH = "data/data_final.txt";
    private static final String ALGORITHM_PATH = "data/Dijkstra/DijkstraPathData.txt";
    public static final String SUB_GRAPH = "data/SubGraph/Subgraph.txt";

    public static void main(String[] args) throws IOException {
        int[][] originalData = new GenerateDataMatrix().createMatrix(READ_PATH);

        //得出ALGORITHM_PATH
        new Dijkstra().dijkstra(originalData, "安邦财产保险股份有限公司", 15000,"ASC");

        //得出SUB_GRAPH
        new ExtractGraph().generateSubgraph(ALGORITHM_PATH);

        int[][] subgraphData = new GenerateDataMatrix().createMatrix(SUB_GRAPH);
        new PageRank().pagerank(subgraphData, 20, 0.85, 1, "DESC");

    }
}
