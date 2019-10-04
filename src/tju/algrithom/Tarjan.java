package tju.algrithom;


import java.io.*;
import java.util.*;

public class Tarjan {
    //存放公司名的文件
    private static final String COMPANY_PATH = "data/hash.txt";

    //存放结果文件
    private static final String OUT_PATH = "data/Tarjan/Tarjandata.txt";

    private int numOfNode;
    private int[][] graph;//图
    private boolean[] inStack;//节点是否在栈内，因为在stack中寻找一个节点不方便。这种方式查找快
    private Stack<Integer> stack;
    private Map<String, Integer> result; //保存每个节点对应的最大联通子图索引
    private List<String> company; //存放公司名
    private int[] dfn; //存放每个节点的遍历时间戳
    private int[] low; //存放节点在栈中最早连通的时间戳
    private int time;//时间戳
    private int partition; //最大联通子图索引

    /**
     * 构造函数
     *
     * @param graph 数据集的邻接矩阵
     * @throws IOException 抛出异常
     */
    public Tarjan(int[][] graph) throws IOException {
        this.graph = graph;
        this.numOfNode = graph.length;
        this.inStack = new boolean[numOfNode];
        this.stack = new Stack<>();
        this.result = new HashMap<>();
        this.partition = 0;
        this.dfn = new int[numOfNode];
        this.low = new int[numOfNode];
        Arrays.fill(dfn, -1);//将dfn所有元素都置为-1，其中dfn[i]=-1代表i还有没被访问过。
        Arrays.fill(low, -1);

        //从文件中读取公司名并存入数组
        this.company = new ArrayList<>();
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(COMPANY_PATH));
        String line;
        while ((line = lineNumberReader.readLine()) != null) {
            this.company.add(line.split("-->")[0].trim());
        }
        lineNumberReader.close();
    }

    /**
     * 运行Tarjan算法并按照最大联通子图索引排序并进行保存输出
     *
     * @throws IOException 抛出异常
     */
    public void run() throws IOException {
        for (int i = 0; i < numOfNode; i++) {
            if (dfn[i] == -1) {
                tarjan(i);
            }
        }

        //对存放公司和对应联通子图索引的HashMap进行排序
        List<Map.Entry<String, Integer>> list = new ArrayList<>(result.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue));

        //输出HashMap并写入文件
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUT_PATH));
        System.out.println("?node               " + "|" + "             ?tj");
        System.out.println("-----------------------------------------------");
        for (Map.Entry<String, Integer> entry : list) {
            bufferedWriter.write(entry.getKey() + " -----> " + entry.getValue() + "\r\n");
            System.out.println(entry.getKey() + "   |   " + entry.getValue());
        }
        bufferedWriter.close();
        System.out.println("-----------------------------------------------");

    }

    /**
     * Tarjan算法主体（递归）
     *
     * @param current 抛出异常
     */
    private void tarjan(int current) {
        dfn[current] = low[current] = time++;
        inStack[current] = true;
        stack.push(current);

        for (int i = 0; i < graph[current].length; i++) {
            if (graph[current][i] != 1) {
                continue;
            }
            if (dfn[i] == -1) {//-1代表没有被访问
                tarjan(i);
                low[current] = Math.min(low[current], low[i]);
            } else if (inStack[i]) {
                low[current] = Math.min(low[current], dfn[i]);
            }
        }

        if (low[current] == dfn[current]) {
            int j = -1;
            while (current != j) {
                j = stack.pop();
                inStack[j] = false;
                result.put(company.get(j), partition);
            }
            partition++;
        }
    }
}
