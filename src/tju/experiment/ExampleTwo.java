package tju.experiment;

import tju.algrithom.BetweenessCentrality;
import tju.initialization.ConstructQuery;
import tju.initialization.GenerateDataMatrix;

import java.io.IOException;

public class ExampleTwo {
    private static final String READ_PATH = "data/data_final.txt";

    public static void main(String[] args) throws IOException {
        //数据集的矩阵表示
        int[][] data = new GenerateDataMatrix().createMatrix(READ_PATH);
        new ConstructQuery().execCONDefaultGraphViaTriples();
        new BetweenessCentrality().betweenessCentrality(1,"DESC");
    }
}
