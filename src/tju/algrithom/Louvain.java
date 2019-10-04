package tju.algrithom;

import java.io.*;
import java.util.*;

public class Louvain implements Cloneable {

    //Louvain所需的文件
    private static final String DATA_PATH = "data/Louvain/louvain_intermediate_data.txt";

    //存放公司名的文件
    private static final String COMPANY_PATH = "data/hash.txt";

    //存放结果的文件
    private static final String OUT_PATH = "data/Louvain/LouvainData.txt";

    private int n; // 结点个数
    private int m; // 边数
    private int cluster[]; // 结点i属于哪个簇
    private Edge edge[]; // 邻接表
    private int head[]; // 头结点下标
    private int top; // 已用E的个数
    private double resolution; // 1/2m 全局不变
    private double node_weight[]; // 结点的权值
    private double[] cluster_weight; // 簇的权值

    private int global_n; // 最初始的n
    private int global_cluster[]; // 最后的结果，i属于哪个簇
    private Edge[] new_edge;   //新的邻接表
    private int[] new_head;
    private int new_top = 0;

    private Edge global_edge[];   //全局初始的临接表  只保存一次，永久不变，不参与后期运算
    private int global_head[];
    private int global_top = 0;

    /**
     * 初始化边
     *
     * @param u      起始节点
     * @param v      目标节点
     * @param weight 边的权重
     */
    private void addEdge(int u, int v, double weight) {
        if (edge[top] == null)
            edge[top] = new Edge();
        edge[top].v = v;
        edge[top].weight = weight;
        edge[top].next = head[u];
        head[u] = top++;
    }

    /**
     * 重构图时初始化边
     *
     * @param u      起始节点
     * @param v      目标节点
     * @param weight 边的权重
     */
    private void addNewEdge(int u, int v, double weight) {
        if (new_edge[new_top] == null)
            new_edge[new_top] = new Edge();
        new_edge[new_top].v = v;
        new_edge[new_top].weight = weight;
        new_edge[new_top].next = new_head[u];
        new_head[u] = new_top++;
    }

    /**
     * 初始化最终的边
     *
     * @param u      起始节点
     * @param v      目标节点
     * @param weight 边的权重
     */
    private void addGlobalEdge(int u, int v, double weight) {
        if (global_edge[global_top] == null)
            global_edge[global_top] = new Edge();
        global_edge[global_top].v = v;
        global_edge[global_top].weight = weight;
        global_edge[global_top].next = global_head[u];
        global_head[u] = global_top++;
    }

    /**
     * 读取数据文件并进行相应的初始化
     */
    private void init() {
        try {
            String encoding = "UTF-8";
            File file = new File(DATA_PATH);
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt;
                lineTxt = bufferedReader.readLine();

                ////// 预处理部分
                String cur2[] = lineTxt.split(" ");
                global_n = n = Integer.parseInt(cur2[0]);
                m = Integer.parseInt(cur2[1]);
                m *= 2;  //删除
                edge = new Edge[m];
                head = new int[n];
                for (int i = 0; i < n; i++)
                    head[i] = -1;
                top = 0;

                global_edge = new Edge[m];
                global_head = new int[n];
                for (int i = 0; i < n; i++)
                    global_head[i] = -1;
                global_top = 0;
                global_cluster = new int[n];
                for (int i = 0; i < global_n; i++)
                    global_cluster[i] = i;
                node_weight = new double[n];
                // 总边权值
                double totalEdgeWeight = 0.0;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    String cur[] = lineTxt.split(" ");
                    int u = Integer.parseInt(cur[0]);
                    int v = Integer.parseInt(cur[1]);
                    double curw;
                    if (cur.length > 2) {
                        curw = Double.parseDouble(cur[2]);
                    } else {
                        curw = 1.0;
                    }
                    addEdge(u, v, curw);
                    addEdge(v, u, curw);  //删除
                    addGlobalEdge(u, v, curw);
                    addGlobalEdge(v, u, curw);  //删除
                    totalEdgeWeight += 2 * curw;  //只加curw
                    node_weight[u] += curw;
                    if (u != v) {
                        node_weight[v] += curw;  //删除
                    }
                }
                resolution = 1 / totalEdgeWeight;
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }

    /**
     * 初始化簇（每个节点的初始簇为自己）
     */
    private void init_cluster() {
        cluster = new int[n];
        for (int i = 0; i < n; i++) { // 一个结点一个簇
            cluster[i] = i;
        }
    }

    /**
     * 在对每一个节点进行迭代的过程中通过判断delta_q的
     * 最大值来判断该节点是否能加入簇
     *
     * @param i 节点的索引
     * @return 返回加入簇的布尔值
     */
    private boolean try_move_i(int i) { // 尝试将i加入某个簇
        double[] edgeWeightPerCluster = new double[n];
        for (int j = head[i]; j != -1; j = edge[j].next) {
            int l = cluster[edge[j].v]; // l是nodeid所在簇的编号
            edgeWeightPerCluster[l] += edge[j].weight;
        }
        int bestCluster = -1; // 最优的簇号下标(先默认是自己)
        double maxx_deltaQ = 0.0; // 增量的最大值
        boolean[] vis = new boolean[n];
        cluster_weight[cluster[i]] -= node_weight[i];
        for (int j = head[i]; j != -1; j = edge[j].next) {
            int l = cluster[edge[j].v]; // l代表領接点的簇号
            if (vis[l]) // 一个領接簇只判断一次
                continue;
            vis[l] = true;
            double cur_deltaQ = edgeWeightPerCluster[l];
            cur_deltaQ -= node_weight[i] * cluster_weight[l] * resolution;
            if (cur_deltaQ > maxx_deltaQ) {
                bestCluster = l;
                maxx_deltaQ = cur_deltaQ;
            }
            edgeWeightPerCluster[l] = 0;
        }
        // 误差
        double eps = 1e-14;
        if (maxx_deltaQ < eps) {
            bestCluster = cluster[i];
        }
        cluster_weight[bestCluster] += node_weight[i];
        if (bestCluster != cluster[i]) { // i成功移动了
            cluster[i] = bestCluster;
            return true;
        }
        return false;
    }

    /**
     * 在每一次迭代完成后重构图，重构每一个簇内的边权重以及
     * 簇之间的权重，并将每个簇看成一个新的节点
     */
    private void rebuildGraph() { // 重构图
        /// 先对簇进行离散化
        int[] change = new int[n];
        int change_size = 0;
        boolean vis[] = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (vis[cluster[i]])
                continue;
            vis[cluster[i]] = true;
            change[change_size++] = cluster[i];
        }
        int[] index = new int[n]; // index[i]代表 i号簇在新图中的结点编号
        for (int i = 0; i < change_size; i++)
            index[change[i]] = i;
        int new_n = change_size; // 新图的大小
        new_edge = new Edge[m];
        new_head = new int[new_n];
        new_top = 0;
        double new_node_weight[] = new double[new_n]; // 新点权和
        for (int i = 0; i < new_n; i++)
            new_head[i] = -1;

        ArrayList<Integer>[] nodeInCluster = new ArrayList[new_n]; // 代表每个簇中的节点列表
        for (int i = 0; i < new_n; i++)
            nodeInCluster[i] = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            nodeInCluster[index[cluster[i]]].add(i);
        }
        for (int u = 0; u < new_n; u++) { // 将同一个簇的挨在一起分析。可以将visindex数组降到一维
            boolean visindex[] = new boolean[new_n]; // visindex[v]代表新图中u节点到v的边在临街表是第几个（多了1，为了初始化方便）
            double delta_w[] = new double[new_n]; // 边权的增量
            for (int i = 0; i < nodeInCluster[u].size(); i++) {
                int t = nodeInCluster[u].get(i);
                for (int k = head[t]; k != -1; k = edge[k].next) {
                    int j = edge[k].v;
                    int v = index[cluster[j]];
                    if (u != v) {
                        if (!visindex[v]) {
                            addNewEdge(u, v, 0);
                            visindex[v] = true;
                        }
                        delta_w[v] += edge[k].weight;
                    }
                }
                new_node_weight[u] += node_weight[t];
            }
            for (int k = new_head[u]; k != -1; k = new_edge[k].next) {
                int v = new_edge[k].v;
                new_edge[k].weight = delta_w[v];
            }
        }

        // 更新答案
        int[] new_global_cluster = new int[global_n];
        for (int i = 0; i < global_n; i++) {
            new_global_cluster[i] = index[cluster[global_cluster[i]]];
        }
        System.arraycopy(new_global_cluster, 0, global_cluster, 0, global_n);
        top = new_top;
        if (m >= 0) System.arraycopy(new_edge, 0, edge, 0, m);
        for (int i = 0; i < new_n; i++) {
            node_weight[i] = new_node_weight[i];
            head[i] = new_head[i];
        }
        n = new_n;
        init_cluster();
    }

    /**
     * 打印和保存结果
     *
     * @throws IOException 抛出异常
     */
    private void printResult() throws IOException {
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(COMPANY_PATH));
        List<String> list = new ArrayList<>();
        Map<String, Integer> result = new HashMap<>();
        String line;
        while ((line = lineNumberReader.readLine()) != null) {
            list.add(line.split("-->")[0].trim());
        }
        lineNumberReader.close();


        for (int i = 0; i < global_n; i++) {
            result.put(list.get(i), global_cluster[i]);
        }

        List<Map.Entry<String, Integer>> list1 = new ArrayList<>(result.entrySet());
        list1.sort(Comparator.comparing(Map.Entry::getValue));

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUT_PATH));

        System.out.println("?path               " + "|" + "               ?lv");
        System.out.println("-------------------------------------------------");
        for (Map.Entry<String, Integer> entry : list1) {
            bufferedWriter.write(entry.getKey() + " -----> " + entry.getValue() + "\r\n");
            System.out.println(entry.getKey() + "   |   " + entry.getValue());
        }
        System.out.println("--------------------------------------------------");
        bufferedWriter.close();
    }

    /**
     * Louvain主算法，在每次迭代的过程中判断每个簇是否有新节点加入
     * 如每个簇稳定（没有新节点加入）停止迭代计算
     *
     * @throws IOException 抛出异常
     */
    public void louvain() throws IOException {
        init();
        init_cluster();
        int count = 0; // 迭代次数
        boolean update_flag; // 标记是否发生过更新
        do { // 第一重循环，每次循环rebuild一次图
            //    print(); // 打印簇列表
            count++;
            cluster_weight = new double[n];
            for (int j = 0; j < n; j++) { // 生成簇的权值
                cluster_weight[cluster[j]] += node_weight[j];
            }
            int[] order = new int[n]; // 生成随机序列
            for (int i = 0; i < n; i++)
                order[i] = i;
            Random random = new Random();
            for (int i = 0; i < n; i++) {
                int j = random.nextInt(n);
                int temp = order[i];
                order[i] = order[j];
                order[j] = temp;
            }
            int enum_time = 0; // 枚举次数，到n时代表所有点已经遍历过且无移动的点
            int point = 0; // 循环指针
            update_flag = false; // 是否发生过更新的标记
            do {
                int i = order[point];
                point = (point + 1) % n;
                if (try_move_i(i)) { // 对i点做尝试
                    enum_time = 0;
                    update_flag = true;
                } else {
                    enum_time++;
                }
            } while (enum_time < n);
            // 最大迭代次数
            int iteration_time = 20;
            if (count > iteration_time || !update_flag) // 如果没更新过或者迭代次数超过指定值
                break;
            rebuildGraph(); // 重构图
        } while (true);

        printResult();
    }

}

//表示一条边
class Edge implements Cloneable {
    int v;     //v表示连接点的编号,w表示此边的权值
    double weight;
    int next;    //next负责连接和此点相关的边

    Edge() {
    }

    public Object clone() {
        Edge temp = null;
        try {
            temp = (Edge) super.clone();   //浅复制
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return temp;
    }
}