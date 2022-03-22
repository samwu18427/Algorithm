package eclat;

import java.util.*;

class Node {
    String ItemId;
    String LastItemId;
    ArrayList<Integer> Id_Trans;

    Node(String str1, String str2, ArrayList<Integer> AL1) {
        ItemId = str1;
        LastItemId = str2;
        Id_Trans = AL1;
    }
}

public class eclat {
    static List<String> Dataset = new ArrayList<>();
    static String delim = " ";
    static int itemNum = 5;
    static int MinSup = 3;

    static void SetDataSet() {
        Dataset.add("1 2 4 5");
        Dataset.add("2 3 5");
        Dataset.add("1 2 4 5");
        Dataset.add("1 2 3 5");
        Dataset.add("1 2 3 4 5");
        Dataset.add("2 3 4");
    }

    static void eclat() {
        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList[] Tidset = new ArrayList[itemNum];
        for (int i = 0; i < Tidset.length; i++)
            Tidset[i] = new ArrayList<>();

        for (int line = 1; line < Dataset.size(); line++)
            for (String strArr : Dataset.get(line - 1).split(delim))
                Tidset[Integer.parseInt(strArr) - 1].add(line);

        for (int i = 0; i < Tidset.length; i++)
            nodes.add(new Node(String.valueOf(i + 1), String.valueOf(i + 1), Tidset[i]));
        getFI(nodes);
    }

    static void getFI(ArrayList<Node> node) {
        for (int i = 0; i < node.size(); i++) {
            ArrayList<Node> tmp = new ArrayList<>();
            for (int j = i + 1; j < node.size(); j++) {
                String newItem = node.get(i).ItemId + delim + node.get(j).LastItemId;
                ArrayList<Integer> transaction = intersect(node.get(i).Id_Trans, node.get(j).Id_Trans);
                if (transaction.size() >= MinSup) {
                    System.out.println("item : " + newItem + " Transaction : " + transaction);
                    tmp.add(new Node(newItem, node.get(j).LastItemId, transaction));
                }
            }
            getFI(tmp);
        }
    }

    static ArrayList<Integer> intersect(ArrayList<Integer> x, ArrayList<Integer> y) {
        ArrayList<Integer> a = new ArrayList<>(x);
        a.retainAll(y);
        return a;
    }

    public static void main(String[] args) {
        SetDataSet();
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        long start_memory = rt.totalMemory() - rt.freeMemory();
        long start_time = System.currentTimeMillis();
        eclat();
        System.err.println((float) (rt.totalMemory() - rt.freeMemory() - start_memory) / 1024 / 1024 + " MB");
        System.err.println("Execution time: " + (double) (System.currentTimeMillis() - start_time) / 1000 + "秒");
    }
}