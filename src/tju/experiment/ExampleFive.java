package tju.experiment;

import tju.algrithom.ShortestPath;
import tju.initialization.ConstructQuery;
import tju.initialization.GenerateDataMatrix;

import java.io.IOException;

public class ExampleFive {
    private static final String READ_PATH = "data/data_final.txt";

    public static void main(String[] args) throws IOException {
        //数据集的矩阵表示
        int[][] data = new GenerateDataMatrix().createMatrix(READ_PATH);
        new ConstructQuery().execCONDefaultGraphViaTriples();
        new ShortestPath().shortestPath(data,"安邦财产保险股份有限公司","招商银行股份有限公司", 1,"ASC");
    }
}
