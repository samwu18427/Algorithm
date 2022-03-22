package Apriori;

import java.util.*;

public class ArrangeThread implements Runnable {

    private HashMap<String, int[]> CandiSet = new HashMap<>();
    private ArrayList<int[]> ItemSet = new ArrayList<>();
    private int Itemsetlen;
    private int setlen;
    private static int i = -1;
    private boolean state;
    private static int cycle = 0;

    public void InputConfig(ArrayList<int[]> ItemSet, int setlen) {
        this.ItemSet = ItemSet;
        this.setlen = setlen;
        Itemsetlen = this.ItemSet.size();
        state = true;
    }

    @Override
    public void run() {
        while (state && i < Itemsetlen - 2) {
            i++;
            System.out.println(i);
            int[] a = ItemSet.get(i);
            for (int j = i + 1; j < Itemsetlen; ++j) {
                int[] b = ItemSet.get(j);
                int[] candi = new int[setlen + 1];
                System.arraycopy(a, 0, candi, 0, setlen);
                int nodiff = 0;
                for (int x = 0; x < setlen; ++x) {
                    boolean found = false;
                    for (int value : a) {
                        if (b[x] == value) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        nodiff++;
                        System.arraycopy(b, x, candi, setlen, 1);
                    }
                }
                if (nodiff == 1) {
                    Arrays.sort(candi);
                    CandiSet.put(Arrays.toString(candi), candi);
                }
//                System.out.println("i:"+i+", j:"+j+", Itemsetlen:"+Itemsetlen);
            }
            if (++cycle == Itemsetlen - 2) {
                state = false;
//                System.out.println(cycle);
            }
        }
    }

    public HashMap<String, int[]> getCandiSet() {
        try {
            return CandiSet;
        } catch (Exception e) {
            System.out.print("Failed to get CandiSet");
            return null;
        }
    }

    public boolean checkRunState() {
        return state;
    }
}