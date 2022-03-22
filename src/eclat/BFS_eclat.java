package eclat;

import java.util.*;

public class BFS_eclat {
    static List<String> DataSet = new ArrayList<>();
    static Map<String, ArrayList<String>> ItemInfo = new HashMap<>();   //<item,item行數>
    static Map<String, ArrayList<String>> AllFS = new LinkedHashMap<>(); //LinkedHashMap才有順序
    static Map<String, ArrayList<String>> Itemset = new HashMap<>();
    static String delim = " ";
    static int MinSup = 3;

    static void SetDataSet() {
        DataSet.add("1 2 4 5");
        DataSet.add("2 3 5");
        DataSet.add("1 2 4 5");
        DataSet.add("1 2 3 5");
        DataSet.add("1 2 3 4 5");
        DataSet.add("2 3 4");
    }

    static void eclat() {
        for (int i = 1; i <= DataSet.size(); i++) {
            String str = DataSet.get(i - 1);
            String[] tmp = str.split(delim);
            for (String s : tmp) {
                if (!ItemInfo.containsKey(s))
                    ItemInfo.put(s, new ArrayList<>());
                ItemInfo.get(s).add(Integer.toString(i));
            }
        }
        Itemset = new HashMap<>(ItemInfo);
        while (Itemset.size() > 1)
            getFI();
        PrintAns();
    }

    static void getFI() {
        Map<String, ArrayList<String>> tempItemset = new HashMap<>();
        Object[] SetKey = Itemset.keySet().toArray();
        ArrayList<String> tempArrayList;
        for (int i = 0; i < SetKey.length - 1; i++) {
            for (int j = i + 1; j < SetKey.length; j++) {
                int count = 0;
                tempArrayList=new ArrayList<>();
                Object[] a = Itemset.get(SetKey[i]).toArray();
                Object[] b = Itemset.get(SetKey[j]).toArray();
                for (Object str1 : a)
                    for (Object str2 : b)
                        if (str1.equals(str2)) {
                            count++;
                            tempArrayList.add((String) str1);
                            break;
                        }
                if (count >= MinSup) {
                    String x = (String) SetKey[i];
                    String y = (String) SetKey[j];
                    String[] xx = x.split(delim);
                    String[] yy = y.split(delim);
                    String[] result = new String[xx.length + 1];
                    int diff = 0;
                    for (int xx_i = 0; xx_i < xx.length; xx_i++) {
                        result[xx_i] = xx[xx_i];
                        if (!xx[xx_i].equals(yy[xx_i])) {
                            result[result.length - 1] = yy[xx_i];
                            diff++;
                        }
                    }
                    if (diff > 1)
                        continue;
                    Arrays.sort(result);
                    String zz = Arrays.toString(result);
                    zz = zz.substring(1, zz.length() - 1).replace(",", "");
                    tempItemset.put(zz, tempArrayList);
                }
            }
        }
        Itemset = tempItemset;
        AllFS.putAll(Itemset);
    }

    static void PrintAns() {
        System.out.println("Minimum Support: " + MinSup);
        for (Map.Entry entry : AllFS.entrySet())
            System.out.println("item : " + entry.getKey() + " Transaction : " + entry.getValue());
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