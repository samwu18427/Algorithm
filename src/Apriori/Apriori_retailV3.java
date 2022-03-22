package Apriori;

import java.io.*;
import java.util.*;

public class Apriori_retailV3 {
    static List<int[]> itemset;
    static List<int[]> AllFS= new ArrayList<>();                        //All frequent set
    static List<Integer> AllFSC= new ArrayList<>();                     //All frequent set count
    static String FilePath="Dataset/retail.txt";
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
                    numOfItem=x+1;
            }
        }
        System.err.println(numOfItem+" items, "+ TL +" transaction lists");
    }//end config

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
    }//end userInput
    /**start algorithm*/
    public static void go() throws IOException {
        System.err.println("-------------------Processing--------------------");
        /**creat len-1 Candidate Set---------*/
        itemset = new ArrayList<>();
        for(int i=0; i<numOfItem; i++)
        {
            int[] Candi = {i};
            itemset.add(Candi);
        }

        int Setlen=1;

        /**find frequent set & arrange candidate set, until nothing output*/
        while(itemset.size()>0){
            findFS();
            System.out.println("found "+ itemset.size()+" of length="+Setlen+" frequent set");

            if(itemset.size()!=0){
                ArrangeCS();
                CheckCS();
                Setlen++;
                System.out.println("found "+ itemset.size()+" of length="+Setlen+" candidate set");
            }
        }

        System.out.println(AllFS.size()+" FrequentSet");
//      for (int i = 0; i < AllFS.size(); i++)
//          System.out.println(Arrays.toString(AllFS.get(i))+":"+AllFSC.get(i));

//      AllFS.forEach(arr -> System.out.println(Arrays.toString(arr)));
        OutputAllFS();
        OutpusConfidence();
    }//end go
    public static void findFS() throws IOException {
        List<int[]> FS=new ArrayList<>();
        int count[]=new int[itemset.size()];

        BufferedReader br=new BufferedReader(new FileReader(FilePath));

        boolean check;
        boolean[] tlck=new boolean[numOfItem];

        for(int i = 0; i< TL; i++) {
            String line = br.readLine();
            int TlLenCount=0;
            /**spilt line's element，=>tlck[element]=true*/
            Arrays.fill(tlck, false);
            StringTokenizer st = new StringTokenizer(line, delim);
            while (st.hasMoreTokens()) {
                int parsedVal = Integer.parseInt(st.nextToken());
                tlck[parsedVal] = true;
                TlLenCount++;
            }
            if(TlLenCount<itemset.get(0).length)
                continue;

            /**Read Candidate set，if tlck[Candi]=false => continue next Candi
             * if tlck[Candi]=true => count[Candi]++ */
            for (int j = 0; j < itemset.size(); j++) {
                check = true;
                int[] candi = itemset.get(j);
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
        for(int i = 0; i< itemset.size(); i++){
            if((count[i]/(double)(TL))>=minSup){
                FS.add(itemset.get(i));
                AllFSC.add(count[i]);
            }
        }

        /**FS arrange -> len+1 candidate set
         * FS add ->AllFS*/
        itemset =FS;
        AllFS.addAll(FS);
    }//end findFS
    public static void ArrangeCS() {
        int lenOfItemset = itemset.get(0).length;
        Map<String, int[]> Candidate = new HashMap<>();

        for (int i = 0; i < itemset.size(); i++) {
            for (int j = i + 1; j < itemset.size(); j++) {
                int[] a = itemset.get(i);
                int[] b = itemset.get(j);
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
        itemset = new ArrayList<>(Candidate.values());
    }//end ArrangeCS
    public static void CheckCS(){
        List<int[]> del = new ArrayList<>();
        for(int i=0;i< itemset.size();++i) {
            int[] bb = itemset.get(i);
            List<String> tmp = new ArrayList<>();
            //find candidate set's all subset add->tmp
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
            //for loop tmp & AllFS ->check contain
            int check = 0;
            for (int j = 0; j < tmp.size() - 1; ++j) {
                String str = tmp.get(j);
                String[] qq = str.split(delim);

                for (int[] CSArr1 : AllFS) {
                    String[] CSArr2 = Arrays.stream(CSArr1).mapToObj(String::valueOf).toArray(String[]::new);
                    if (Arrays.asList(CSArr2).containsAll(Arrays.asList(qq))) {
                        check++;
                        break;
                    }
                }
            }
            if (check < tmp.size() - 1) {
                itemset.remove(i);
                i--;
            }
        }
    }//end CheckCS
    public static void OutputAllFS() throws IOException {
        FileWriter fw = new FileWriter("retail_allFrequentSetV2.txt");
        fw.write("Minimum Support="+minSup*100+"%, Transcations:"+ TL +'\n'+'\n');
        System.out.println("Minimum Support="+minSup*100+"%, Transcations:"+ TL +'\n');
        for (int i = 0; i < AllFS.size(); i++) {
            fw.write(Arrays.toString(AllFS.get(i)) + " #SUP: " +AllFSC.get(i)+ '\n');
        }
        fw.close();
        System.err.println("------------------done WriteAllFS-----------------");
    }//end OutputAllFS
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
    }//end OutpusConfidence
    public static void main(String args[]) throws IOException {
        config();
        Runtime rt=Runtime.getRuntime();
        rt.gc();
        long mem=rt.totalMemory()-rt.freeMemory();
        long start =System.currentTimeMillis();
        go();
        System.err.println((double)(rt.totalMemory()-rt.freeMemory()-mem)/1024/1024+" MB");
        System.err.println("Runtime: "+(double) (System.currentTimeMillis() - start) / 1000 +"秒");
    }//end main
}//end class
