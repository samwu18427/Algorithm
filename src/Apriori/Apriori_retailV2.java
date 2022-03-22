package Apriori;

import java.io.*;
import java.util.*;

public class Apriori_retailV2 {
    static List<int[]> ItemSet=new ArrayList<>();
    static List<int[]> AllFS= new ArrayList<>();                        //All frequent set
    static List<Integer> AllFSC= new ArrayList<>();                     //All frequent set count
    static String FilePath="retail.txt";
    static int numOfItem;                                               //Number of item
    static int TL;                                                      //Transcation List
    static String delim=" ";
    static double minSup=0.01,minCnf=0.01;

    /**configure all setting */
    public static void config() throws IOException {
//      userInput();
        numOfItem =0;
        TL =0;
        BufferedReader br=new BufferedReader(new FileReader(FilePath));

        while(br.ready()){
            String line=br.readLine();
            TL++;
            StringTokenizer st=new StringTokenizer(line,delim);
            while(st.hasMoreTokens()){
                int x=Integer.parseInt(st.nextToken());
                if(x+1>numOfItem)
                    numOfItem=x;
            }
        }
        System.err.println(numOfItem+" items, "+ TL +" transaction lists");
    }

    /**user setting*/
    public static void userInput(){
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
    }

    /**start algorithm*/
    public static void go() throws IOException {
        System.err.println("-------------------Processing--------------------");
        /**creat len-1 Candidate Set---------*/
        ItemSet = new ArrayList<>();
        for(int i=0; i<numOfItem; i++)
        {
            int[] Candi = {i};
            ItemSet.add(Candi);
        }

        int Setlen=1;

        /**find frequent set & arrange candidate set, until nothing output*/
        while(ItemSet.size()>0){
            findFS();
            System.out.println("found "+ItemSet.size()+" of length="+Setlen+" frequent set");

            if(ItemSet.size()!=0){
                ArrangeCS();
                Setlen++;
                System.out.println("found "+ItemSet.size()+" of length="+Setlen+" candidate set");
            }
        }

        System.out.println(AllFS.size()+" FrequentSet");
//      for (int i = 0; i < AllFS.size(); i++)
//          System.out.println(Arrays.toString(AllFS.get(i))+":"+AllFSC.get(i));

//      AllFS.forEach(arr -> System.out.println(Arrays.toString(arr)));
        OutputAllFS();
        OutpusConfidence();
    }
    public static void findFS() throws IOException {
        List<int[]> FS=new ArrayList<>();
        int count[]=new int[ItemSet.size()];

        BufferedReader br=new BufferedReader(new FileReader(FilePath));

        boolean check;
        boolean[] tlck=new boolean[numOfItem];

        for(int i = 0; i< TL; i++) {
            String line = br.readLine();
            /**判斷line包含的元素，存在=>tlck[元素]=true*/
            Arrays.fill(tlck, false);
            StringTokenizer st = new StringTokenizer(line, delim);
            while (st.hasMoreTokens()) {
                int parsedVal = Integer.parseInt(st.nextToken());
                tlck[parsedVal] = true;
            }

            /**Read Candidate set，if tlck[Candi]=false => continue next Candi
             * if tlck[Candi]=true => count[Candi]++ */
            for (int j = 0; j < ItemSet.size(); j++) {
                check = true;
                int[] candi = ItemSet.get(j);
                for (int item : candi) {
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

        /**select count>=minSup add->FS*/
        for(int i = 0; i< ItemSet.size(); i++){
            if((count[i]/(double)(TL))>=minSup){
                FS.add(ItemSet.get(i));
                AllFSC.add(count[i]);
            }
        }

        /**FS arrange -> len+1 candidate set
         * FS add ->AllFS*/
        ItemSet =FS;
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
        FileWriter fw = new FileWriter("retail_allFrequentSetV2.txt");
        fw.write("Minimum Support="+minSup*100+"%, Transcations:"+ TL +'\n'+'\n');
        System.out.println("Minimum Support="+minSup*100+"%, Transcations:"+ TL +'\n');
        for (int i = 0; i < AllFS.size(); i++) {
            fw.write(Arrays.toString(AllFS.get(i)) + " #SUP: " +AllFSC.get(i)+ '\n');
        }
        fw.close();
        System.err.println("------------------done WriteAllFS-----------------");
    }
    public static void OutpusConfidence() throws IOException {
        FileWriter fw = new FileWriter("retail_ConfidenceV2.txt");
        fw.write("Minimum Support="+minSup*100+"%, Minimum Confidence="+minCnf*100+"%, Transcations:"+ TL);
        System.out.println("Minimum Support="+minSup*100+"%, Minimum Confidence="+minCnf*100+"%, Transcations:"+ TL);

        for (int i = 0; i < AllFS.size(); ++i) {
            int[] bb=AllFS.get(i);
            List<String> tmp = new ArrayList<>();                       //save rest of combine set

            if (bb.length >= 2) {
                System.out.println(Arrays.toString(bb));
                fw.write('\n'+Arrays.toString(bb)+'\n');
                int nbits = 1 << bb.length;
                for (int zz = 0; zz < nbits; ++zz) {
                    int t;
                    String carr ="";
                    for (int ss = 0; ss < bb.length; ++ss) {
                        t = 1 << ss;
                        if ((t & zz) != 0)
                            carr += bb[ss]+" ";
                    }
                    if(carr!="")
                        tmp.add(carr);
                }

                for (int b=0;b<tmp.size()-1;b++) {
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

                    double confidence = (double)AllFSC.get(i) / AllFSC.get(id);
                    if (confidence >= minCnf) {
                        fw.write(String.join(" ",condition) + " ==> " + res + " #SUP: "+AllFSC.get(id) +" #CONF: "+ confidence+'\n');
                        System.out.println(String.join(" ", condition) + " ==> " + res + " #SUP: " + AllFSC.get(id) + " #CONF: " + confidence+" ");
                    }
                }
            }

        }
        fw.close();
        System.err.println("---------------done WriteConfidence---------------");
    }
    public static void main(String args[]) throws IOException {
        config();
        Runtime rt=Runtime.getRuntime();
        rt.gc();
        long mem=rt.totalMemory()-rt.freeMemory();
        long start =System.currentTimeMillis();
        go();
        System.err.println((double)(rt.totalMemory()-rt.freeMemory()-mem)/1024/1024+" MB");
        System.err.println("Runtime: "+(double) (System.currentTimeMillis() - start) / 1000 +"秒");
    }
}
