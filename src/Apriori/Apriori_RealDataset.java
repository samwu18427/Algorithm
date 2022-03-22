package Apriori;

import java.io.*;
import java.util.*;

public class Apriori_RealDataset {
    static List<int[]> ItemSet;
    static List<int[]> AllFS = new ArrayList<>();
    static List<Integer> AllFSC = new ArrayList<>();
    static String FilePath = "RealDataset.txt";
    static String WriteFilePath = "TRD.txt";
    static int numOfItem;
    static int numOfTL;
    static String delim = ";";
    static double minSup = 0.01, minCnf = 0.01;

    public static void transformDB() throws IOException {
        System.err.println("TransformDB");
        String str;
        String[] strArray = {};
        List<String> item = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(FilePath));
        FileWriter fw = new FileWriter(WriteFilePath);

        while ((str = br.readLine()) != null) {
            strArray = str.split(delim);
            for (String i : strArray) {
                if (!item.contains(i)) {
                    item.add(i);
                }
            }
            for (int i = 0; i < strArray.length; ++i) {
                strArray[i] = Integer.toString(item.indexOf(strArray[i]) + 1);      //以item的索引值為ID序列
            }
            //用substring去頭尾括號
            fw.write(Arrays.toString(strArray).substring(1, Arrays.toString(strArray).length() - 1).replace(",", "") + '\n');
        }
        fw.close();
        br.close();
        fw = new FileWriter("ItemId.txt");
        for (int i = 0; i < item.size(); ++i) {
            fw.write(item.get(i) + "," + (i + 1) + '\n');
        }
        fw.close();
        System.err.println("done TransformDB");
    }
    public static void config() throws IOException {
        userInput();
        numOfItem = 0;
        numOfTL = 0;

        transformDB();
        FilePath = WriteFilePath;
        delim = " ";

        BufferedReader br = new BufferedReader(new FileReader(FilePath));

        while (br.ready()) {
            String line = br.readLine();
            numOfTL++;
            StringTokenizer st = new StringTokenizer(line, delim);
            while (st.hasMoreTokens()) {
                int x = Integer.parseInt(st.nextToken());
                if (x + 1 > numOfItem)
                    numOfItem = x + 1;
            }
        }
        System.err.println(numOfItem + " items, " + numOfTL + " transaction lists");
    }

    public static void userInput() {
        Scanner scan=new Scanner(System.in);

        System.out.print("Input minimun support(%):");
        minSup=scan.nextDouble()/100;
        while (minSup<0 || minSup>1){
            System.out.println("Wrong format,Input minimun support(%):");
            minSup=scan.nextDouble()/100;
        }

        System.out.print("Input minimun confidence(%):");
        minCnf=scan.nextDouble()/100;
        while (minCnf<0 || minCnf>1){
            System.out.println("Wrong format,Input minimun support(%):");
            minCnf=scan.nextDouble()/100;
        }

        System.out.print("Input dataSet's path:");
        FilePath=scan.next();

        System.out.print("Input Dataset's delim:");
        delim = scan.next();
    }

    public static void go() throws IOException {
        System.err.println("-------------------Processing--------------------");
        /**creat len-1 Candidate Set---------*/
        ItemSet = new ArrayList<>();
        for (int i = 0; i < numOfItem; i++) {
            int[] Candi = {i};
            ItemSet.add(Candi);
        }

        int Setlen = 1;

        while (ItemSet.size() > 0) {
            findFS();
            System.out.println("found " + ItemSet.size() + " of length=" + Setlen + " frequent set");

            if (ItemSet.size() != 0) {
                ArrangeCS();
                Setlen++;
                System.out.println("found " + ItemSet.size() + " of length=" + Setlen + " candidate set");
            }

        }
        System.out.println(AllFS.size() + " FrequentSet");
//      for (int i = 0; i < AllFS.size(); i++)
//          System.out.println(Arrays.toString(AllFS.get(i))+":"+AllFSC.get(i));

//      AllFS.forEach(arr -> System.out.println(Arrays.toString(arr)));
        OutputAllFS();
        OutpusConfidence();
    }

    public static void findFS() throws IOException {
        List<int[]> FS = new ArrayList<>();
        int count[] = new int[ItemSet.size()];

        BufferedReader br = new BufferedReader(new FileReader(FilePath));

        boolean check;
        boolean[] tlck = new boolean[numOfItem];

        for (int i = 0; i < numOfTL; i++) {
            String line = br.readLine();

            Arrays.fill(tlck, false);
            StringTokenizer st = new StringTokenizer(line, delim);
            while (st.hasMoreTokens()) {
                int parsedVal = Integer.parseInt(st.nextToken());
                tlck[parsedVal] = true;
            }

            for (int j = 0; j < ItemSet.size(); j++) {
                check = true;
                int[] cand = ItemSet.get(j);
                for (int item : cand) {
                    if (!tlck[item]) {
                        check = false;
                        break;
                    }
                }
                if (check)
                    count[j]++;
            }
        }
        br.close();

        for (int i = 0; i < ItemSet.size(); i++) {
            if ((count[i] / (double) (numOfTL)) >= minSup) {
                FS.add(ItemSet.get(i));
                AllFSC.add(count[i]);
            }
        }
        ItemSet = FS;
        AllFS.addAll(FS);
    }

    public static void ArrangeCS() {
        int lenOfItemset = ItemSet.get(0).length;
        Map<String, int[]> Candidate = new HashMap<>();

        for (int i = 0; i < ItemSet.size(); i++) {
            for (int j = i + 1; j < ItemSet.size(); j++) {
                int[] a = ItemSet.get(i);
                int[] b = ItemSet.get(j);
                int[] Candi = new int[lenOfItemset + 1];
                for (int k = 0; k < Candi.length - 1; k++) {
                    Candi[k] = a[k];
                }
                int Nodiff = 0;
                for (int x = 0; x < b.length; x++) {
                    boolean found = false;
                    for (int y = 0; y < a.length; y++) {
                        if (a[y] == b[x]) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        Nodiff++;
                        Candi[Candi.length - 1] = b[x];
                    }
                }
                if (Nodiff == 1) {
                    Arrays.sort(Candi);
                    Candidate.put(Arrays.toString(Candi), Candi);
                }
            }
        }
        ItemSet = new ArrayList<>(Candidate.values());
    }

    public static void OutputAllFS() throws IOException {
        FileWriter fw = new FileWriter("RealDataset_allFrequentSet.txt");
        fw.write("Minimum Support=" + minSup * 100 + "%, Transcations:" + numOfTL + '\n' + '\n');
        System.out.println("Minimum Support=" + minSup * 100 + "%, Transcations:" + numOfTL + '\n');
        for (int i = 0; i < AllFS.size(); i++) {
            fw.write(Arrays.toString(AllFS.get(i)) + " #SUP: " + AllFSC.get(i) + '\n');
        }
        fw.close();
        System.err.println("------------------done WriteAllFS-----------------");
    }

    public static void OutpusConfidence() throws IOException {
        List<String> tmp = new ArrayList<>();
        FileWriter fw = new FileWriter("RealDataset_Confidence.txt");
        fw.write("Minimum Support=" + minSup * 100 + "%, Minimum Confidence=" + minCnf * 100 + "%, Transcations:" + numOfTL);
        System.out.println("Minimum Support=" + minSup * 100 + "%, Minimum Confidence=" + minCnf * 100 + "%, Transcations:" + numOfTL);

        for (int i = 0; i < AllFS.size(); ++i) {
            int[] bb = AllFS.get(i);

            tmp = new ArrayList<>();

            if (bb.length >= 2) {
                System.out.println(Arrays.toString(bb));
                fw.write('\n' + Arrays.toString(bb) + '\n');
                int nbits = 1 << bb.length;
                for (int zz = 0; zz < nbits; ++zz) {
                    int t;
                    String carr = "";
                    for (int ss = 0; ss < bb.length; ++ss) {
                        t = 1 << ss;
                        if ((t & zz) != 0)
                            carr += bb[ss] + " ";
                    }
                    if (carr != "")
                        tmp.add(carr);
                }

                for (int b = 0; b < tmp.size() - 1; b++) {
                    String dd = tmp.get(b);
                    String[] condition = dd.split(delim);

                    int id = 0;
                    String res = "";
                    for (int str1 : bb) {
                        if (!Arrays.asList(condition).contains(Integer.toString(str1))) {
                            res += str1 + " ";
                        }
                    }
                    for (int cc = 0; cc < AllFS.size(); cc++) {

                        String[] CSArr2 = Arrays.stream(AllFS.get(cc)).mapToObj(String::valueOf).toArray(String[]::new);
                        if (Arrays.asList(CSArr2).containsAll(Arrays.asList(condition))) {
                            id = cc;
                            break;
                        }
                    }

                    double confidence = (double) AllFSC.get(i) / AllFSC.get(id);
                    if (confidence >= minCnf) {
                        fw.write(String.join(" ", condition) + " ==> " + res + " #SUP: " + AllFSC.get(id) + " #CONF: " +confidence + '\n');
                        System.out.println(String.join(" ", condition) + " ==> " + res + " #SUP: " + AllFSC.get(id) + " #CONF: " + confidence + " ");
                    }
                }
            }

        }
        fw.close();
        System.err.println("---------------done WriteConfidence---------------");
    }

    public static void main(String args[]) throws IOException {
        config();
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        long mem = rt.totalMemory() - rt.freeMemory();
        long start = System.currentTimeMillis();
        go();
        System.err.println((double)(rt.totalMemory() - rt.freeMemory() - mem) / 1024 / 1024 + " MB");
        System.err.println("Runtime: " + (double) (System.currentTimeMillis() - start) / 1000 + "秒");
    }
}
