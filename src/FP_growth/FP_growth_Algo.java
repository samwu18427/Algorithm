package FP_growth;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class FP_growth_Algo {
    static int MinSup = 4000;
    static String delim = " ";
    static String FilePath = "Dataset/mushrooms.txt";

    static List<List<String>> readFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(FilePath));
        List<List<String>> TL = new ArrayList<>();
        while ((br.ready()))
            TL.add(Arrays.asList(br.readLine().split(delim)));
        br.close();
        return TL;
    }

    static void FP_growth(List<List<String>> TL, List<String> pattern) {
        ArrayList<TreeNode> HeaderTable = creatHeaderTable(TL);
        creatTree(TL, HeaderTable);
        if (pattern != null)
            for (TreeNode header : HeaderTable) {
                System.out.print(header.ID);
                for (String rest : pattern)
                    System.out.print(" " + rest);
                System.out.println(" SUP:" + header.count);
            }
        for (TreeNode header : HeaderTable) {
            List<String> NextPattern = new LinkedList<>();
            NextPattern.add(header.ID);
            if (pattern != null)
                NextPattern.addAll(pattern);
            List<List<String>> candiset = new LinkedList<>();
            TreeNode NextSameIDnode = header.NextSameIDlink;
            while (NextSameIDnode != null) {
                int count = NextSameIDnode.count;
                List<String> ParentNodes = new ArrayList<>();
                TreeNode node = NextSameIDnode;
                while (!(node = node.parent).ID.equals("root"))
                    ParentNodes.add(node.ID);
                while (count-- > 0)
                    candiset.add(ParentNodes);
                NextSameIDnode = NextSameIDnode.NextSameIDlink;
            }
            FP_growth(candiset, NextPattern);
        }
    }

    static ArrayList<TreeNode> creatHeaderTable(List<List<String>> TL) {
        if (TL.size() == 0)
            return null;
        ArrayList<TreeNode> HTnode = new ArrayList<>();
        Map<String, TreeNode> itemset = new HashMap<>();
        for (List<String> transaction : TL)
            for (String ID : transaction)
                if (!itemset.containsKey(ID))
                    itemset.put(ID, new TreeNode(ID, 1, null));
                else
                    itemset.get(ID).count++;
        for (Map.Entry<String, TreeNode> entry : itemset.entrySet())
            if (entry.getValue().count >= MinSup)
                HTnode.add(entry.getValue());
        Collections.sort(HTnode);
        return HTnode;
    }

    static LinkedList<String> sortTL(List<String> TL, ArrayList<TreeNode> HTnode) {
        Map<String, Integer> map = new HashMap<>();
        for (String id : TL)
            for (int i = 0; i < HTnode.size(); i++) {
                TreeNode node = HTnode.get(i);
                if (node.ID.equals(id))
                    map.put(id, i);
            }
        List<Entry<String, Integer>> temp = new ArrayList<>(map.entrySet());
        temp.sort(Map.Entry.comparingByValue());
        Collections.reverse(temp);
        LinkedList<String> orderTL = new LinkedList<>();
        for (Entry<String, Integer> entry : temp)
            orderTL.add(entry.getKey());
        return orderTL;
    }

    static void creatTree(List<List<String>> TL, ArrayList<TreeNode> HTnode) {
        TreeNode TreeRoot = new TreeNode("root", -1, null);
        for (List<String> transaction : TL) {
            LinkedList<String> orderTL = sortTL(transaction, HTnode);
            TreeNode node = TreeRoot;
            TreeNode subNode;
            if (TreeRoot.child != null)
                while ((subNode = node.getChildID(orderTL.peek())) != null) {
                    subNode.count++;
                    node = subNode;
                    orderTL.poll();
                }
            addNode(node, orderTL, HTnode);
        }
    }

    static void addNode(TreeNode ParentNode, LinkedList<String> orderTL, ArrayList<TreeNode> HTnode) {
        while (orderTL.size() > 0) {
            String ID = orderTL.poll();
            TreeNode ChildeNode = new TreeNode(ID, 1, ParentNode);
            ParentNode.child.add(ChildeNode);
            for (TreeNode Hnode : HTnode)
                if (Hnode.ID.equals(ID)) {
                    while (Hnode.NextSameIDlink != null)
                        Hnode = Hnode.NextSameIDlink;
                    Hnode.NextSameIDlink = ChildeNode;
                    break;
                }
            addNode(ChildeNode, orderTL, HTnode);
        }
    }

    public static void main(String[] args) throws IOException {
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        long start_memory = rt.totalMemory() - rt.freeMemory();
        long start_time = System.currentTimeMillis();
        FP_growth(readFile(), null);
        System.err.println((float) (rt.totalMemory() - rt.freeMemory() - start_memory) / 1024 / 1024 + " MB");
        System.err.println("Execution time: " + (double) (System.currentTimeMillis() - start_time) / 1000 + "秒");
    }
}