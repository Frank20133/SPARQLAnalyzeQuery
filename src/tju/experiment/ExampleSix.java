package tju.experiment;

import tju.algrithom.Dijkstra;
import tju.initialization.ConstructQuery;
import tju.initialization.GenerateDataMatrix;

import java.io.IOException;

public class ExampleSix {
    private static final String READ_PATH = "data/data_final.txt";

    public static void main(String[] args) throws IOException {
        //数据集的矩阵表示
        int[][] data = new GenerateDataMatrix().createMatrix(READ_PATH);
        new ConstructQuery().execCONDefaultGraphViaTriples();
        new Dijkstra().dijkstra(data, "安邦财产保险股份有限公司", 3,"ASC");
    }
}
