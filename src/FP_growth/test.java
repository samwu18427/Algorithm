package FP_growth;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class test {
    static String filepath = "Dataset/testdata";
    static String delim = " ";
    static int MinSup=3;

    static List<List<String>> readfile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        List<List<String>> TL = new ArrayList<>();
        while (br.ready())
            TL.add(Arrays.asList(br.readLine().split(delim)));
        return TL;
    }

    static void fp_growth(List<List<String>> TL, List<String> pattern) {
        List<TreeNode> HT = creatHT(TL);
        List<List<String>>orderTL=sortTL(TL,HT);

    }

    static List<TreeNode> creatHT(List<List<String>>TL){
        List<TreeNode>HT=new ArrayList<>();
        Map<String,TreeNode>itemset=new HashMap<>();
        for(List<String>list:TL)
            for(String id:list)
                if (!itemset.containsKey(id))
                    itemset.put(id,new TreeNode(id,1,null));
                else
                    itemset.get(id).count++;
        for (Map.Entry<String,TreeNode> entry:itemset.entrySet())
            if (entry.getValue().count>=MinSup)
                HT.add(entry.getValue());
        Collections.sort(HT);
        return HT;
    }

    static List<List<String>> sortTL(List<List<String>>TL,List<TreeNode>HT){
        List<List<String>>orderTL=new ArrayList<>();
        for(List<String>list :TL){
            List<String>temp=new ArrayList<>();
            for(TreeNode header:HT)
                if (list.contains(header.ID))
                    temp.add(header.ID);
            orderTL.add(temp);
        }
        return orderTL;
    }

    public static void main(String[] args) throws IOException {
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        long start_memory = rt.totalMemory() - rt.freeMemory();
        long start_time = System.currentTimeMillis();
        fp_growth(readfile(), null);
        System.err.println((float) (rt.totalMemory() - rt.freeMemory() - start_memory) / 1024 / 1024 + "MB");
        System.err.println("Execution time: " + (double) (System.currentTimeMillis() - start_time) / 1000 + "秒");
    }
}
