package Apriori;

import java.io.*;
import java.util.*;

class Apriori_retailV5 {
    final String FilePath = "Dataset/retail.txt";
//    final String FilePath = "Dataset/testdata";
    int TL_count;
    int Item_count;
    int setlen;
    int threadNum = 1;
    double minSup = 0.01;
    double minCnf = 0.01;
    String delim = " ";

    ArrayList<int[]> ItemSet = new ArrayList<>();
    ArrayList<int[]> AllFS = new ArrayList<>();
    ArrayList<Integer> AllFScount = new ArrayList<>();

    void config() throws IOException {
        TL_count = 0;
        Item_count = 0;

        BufferedReader br = new BufferedReader(new FileReader(FilePath));

        while (br.ready()) {
            String line = br.readLine();
            TL_count++;
            StringTokenizer st = new StringTokenizer(line, delim);

            while (st.hasMoreTokens()) {
                int x = Integer.parseInt(st.nextToken());
                if (Item_count < x)
                    Item_count = x;
            }
        }
        br.close();
        System.err.println(Item_count + " items, " + TL_count + " transaction lists");
    }

    void findFS() throws IOException {
        int[] count = new int[ItemSet.size()];
        boolean[] TLcheck = new boolean[Item_count];

        BufferedReader br = new BufferedReader(new FileReader(FilePath));

        while (br.ready()) {
            String line = br.readLine();
            StringTokenizer st = new StringTokenizer(line, delim);
            if (st.countTokens() < setlen) {
                continue;
            }
            Arrays.fill(TLcheck, false);
            while (st.hasMoreTokens()) {
                int pivot = Integer.parseInt(st.nextToken()) - 1;
                TLcheck[pivot] = true;
            }

            for (int i = 0; i < ItemSet.size(); ++i) {
                boolean SetCheck = true;
                for (int item : ItemSet.get(i)) {
                    if (!TLcheck[item]) {
                        SetCheck = false;
                        break;
                    }
                }
                if (SetCheck) {
                    ++count[i];
                }
            }
        }
        br.close();

        /**select count>=minSup add->FS*/
        ArrayList<int[]> FS = new ArrayList<>();
        for (int i = 0; i < ItemSet.size(); ++i) {
            if ((count[i] / (double) (TL_count)) >= minSup) {
                FS.add(ItemSet.get(i));
                AllFScount.add(count[i]);
            }
        }
        ItemSet = FS;
        AllFS.addAll(FS);
    }

    void ArrangeCS() throws InterruptedException {
        HashMap<String, int[]> CandidateSet;
        Thread[] ATList = new Thread[threadNum];
        ArrangeThread AT = new ArrangeThread();
        AT.InputConfig(ItemSet, setlen);

        for (int i = 0; i < threadNum; i++) {
            Thread a = new Thread(AT);
            a.start();
            ATList[i] = a;
        }
        do {
            Thread.sleep(10);
        } while (AT.checkRunState());
        for (Thread a : ATList) {
            a.interrupt();
        }
        CandidateSet = AT.getCandiSet();
//        System.out.println(CandidateSet.size());
        ItemSet = new ArrayList<>(CandidateSet.values());
        System.out.println(ItemSet.size());
//        System.out.println(Arrays.toString(ItemSet.get(0)));
        ++setlen;
    }

    void CheckCS() {
        for (int i = 0; i < ItemSet.size(); ++i) {
            int[] arr = ItemSet.get(i);
            System.out.println("i:"+i+Arrays.toString(arr));
            int abit = 1 << setlen;
            List<int[]> tmp = new ArrayList<>();
            for (int aa = 0; aa < abit; ++aa) {
                String candi = "";
                for (int bbit = 0; bbit < setlen; ++bbit) {
                    int bb = 1 << bbit;
                    if ((bb & aa) != 0)
                        candi += arr[bbit] + delim;
                }
                if (candi != "") {
                    String[] strarr = candi.split(delim);
                    int[] candi_subset = new int[strarr.length];
                    for (int j = 0; j < candi_subset.length; ++j) {
                        candi_subset[j] = Integer.parseInt(strarr[j]);
                    }
                    tmp.add(candi_subset);
                }
            }
            int check = 0;
            for (int[] candi_subset : tmp) {
                for (int[] fs : AllFS) {
                    if (Arrays.equals(fs, candi_subset)) {
                        check++;
                    }
                }
            }
            /**exclude superset itself*/
            if (check < tmp.size() - 1) {
                ItemSet.remove(i);
                i--;
            }
        }
    }

    void OutputAllFS() throws IOException {
        FileWriter fw = new FileWriter("retail_allFrequentSetV4.txt");
        fw.write("Minimum Support=" + minSup * 100 + "%, Transcations:" + TL_count + '\n' + '\n');
        for (int i = 0; i < AllFS.size(); i++) {
            fw.write(Arrays.toString(AllFS.get(i)) + " #SUP: " + AllFScount.get(i) + '\n');
        }
        fw.close();
        System.err.println("------------------done WriteAllFS-----------------");
    }//end OutputAllFS

    void OutputConfidence() throws IOException {
        FileWriter fw = new FileWriter("retail_ConfidenceV4.txt");
        fw.write("Minimum Support=" + minSup * 100 + "%, Minimum Confidence=" + minCnf * 100 + "%, Transaction:" + TL_count);
        for (int i = 0; i < AllFS.size(); ++i) {
            int[] frequentSet = AllFS.get(i);
            List<int[]> tmp = new ArrayList<>();                       //save rest of combine set

            if (frequentSet.length >= 2) {
                System.out.println(Arrays.toString(frequentSet));
                fw.write('\n' + Arrays.toString(frequentSet) + '\n');
                int abit = 1 << frequentSet.length;
                for (int aa = 0; aa < abit; ++aa) {
                    String candi = "";
                    for (int bbit = 0; bbit < frequentSet.length; ++bbit) {
                        int bb = 1 << bbit;
                        if ((bb & aa) != 0)
                            candi += frequentSet[bbit] + delim;
                    }
                    if (!candi.equals("")) {
                        String[] strarr = candi.split(delim);
                        int[] candi_subset = new int[strarr.length];
                        for (int j = 0; j < candi_subset.length; ++j) {
                            candi_subset[j] = Integer.parseInt(strarr[j]);
                        }
                        tmp.add(candi_subset);
                    }
                }
                for (int[] condition : tmp) {
                    int id = 0;
                    String res = "";
                    for (int str1 : frequentSet) {
                        if (Arrays.stream(condition).noneMatch(x -> x == str1)) {
                            res += str1 + " ";
                        }
                    }
                    for (int cc = 0; cc < AllFS.size(); cc++) {
                        if (Arrays.equals(AllFS.get(cc), condition)) {
                            id = cc;
                            break;
                        }
                    }

                    double confidence = (double) AllFScount.get(i) / AllFScount.get(id);
                    if (confidence >= minCnf) {
                        fw.write(Arrays.toString(condition) + " ==> " + res + " #SUP: " + AllFScount.get(id) + " #CONF: " + confidence + '\n');
                        System.out.println(Arrays.toString(condition) + " ==> " + res + " #SUP: " + AllFScount.get(id) + " #CONF: " + confidence + " ");
                    }
                }
            }

        }
        fw.close();
        System.err.println("---------------done WriteConfidence---------------");
    }

    void run() throws IOException, InterruptedException {
        ItemSet = new ArrayList<>();
        System.err.println("-------------------Processing--------------------");
        for (int i = 0; i < Item_count; ++i) {
            int[] arr = {i};
            ItemSet.add(arr);
        }
        setlen = 1;
        while (ItemSet.size() > 0) {
            findFS();
            System.out.println("found " + ItemSet.size() + " of length=" + setlen + " frequent set");
            if (ItemSet.size() != 0) {
                System.out.println(ItemSet.size());
                ArrangeCS();
                CheckCS();
                System.out.println("found " + ItemSet.size() + " of length=" + setlen + " candidate set");
            }
        }
        System.out.println(AllFS.size() + " FrequentSet");
        System.out.println("Minimum Support=" + minSup * 100 + "%, Minimum Confidence=" + minCnf * 100 + "%, Transactions:" + TL_count);
        OutputAllFS();
        OutputConfidence();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Apriori_retailV5 ap = new Apriori_retailV5();
        ap.config();
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        long mem = rt.totalMemory() - rt.freeMemory();
        long start = System.currentTimeMillis();
        ap.run();
        System.err.println((double) (rt.totalMemory() - rt.freeMemory() - mem) / 1024 / 1024 + " MB");
        System.err.println("Runtime: " + (double) (System.currentTimeMillis() - start) / 1000 + "秒");

    }
}