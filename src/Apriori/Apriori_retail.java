package Apriori;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Apriori_retail {
    static boolean checkFS=false;
    static int MinSup = 881,setlen=1;
    static double MinSupD=0.01,MinCnf=0.01;
    static String FilePath = "src/retail.txt";
    static List<String> TL = new ArrayList<>();                                     //Transaction List
    static List<String> CS = new ArrayList<>();                                     //CS = Candidate Set
    static List<Integer> CSC = new ArrayList<>();                                   //CS = Candidate Set Count
    static List<String> FS = new ArrayList<>();                                     //FS = Frequent Set
    static List<Integer> FSC = new ArrayList<>();                                   //FSC = Frequent Set Count
    static List<String> AllFS = new ArrayList<>();
    static List<Integer> AllFSC = new ArrayList<>();
    static List<String> IFS = new ArrayList<>();

    public static void Config() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Input Minimum Support(%):");
        MinSupD=scan.nextDouble()/100;
        System.out.print("Input Minimum Confidence(%):");
        MinSupD=scan.nextDouble()/100;
        System.out.print("Input DataSet's path:");
        FilePath=scan.next();
    }
    public static void ReadFile() throws IOException {
        System.err.println("---------------Processing ReadFile----------------");
        String str;
        String[] strArr;

        BufferedReader br = new BufferedReader(new FileReader(FilePath));
        FileWriter fw=new FileWriter("cs.txt");
        while ((str = br.readLine()) != null) {
            TL.add(str);
            strArr = str.split(" ");
            for (int i = 0; i < strArr.length; i++) {
                String xx = strArr[i];
                if (!CS.contains(xx)) {     //若CAT沒有category則新增
                    CS.add(xx);
                    CSC.add(1);
                    fw.write(xx+'\n');
                }
                else {                       //若CAT有則Count+1
                    int ID = CS.indexOf(xx);
                    CSC.set(ID, CSC.get(ID) + 1);
                }
            }
        }

        MinSup=(int)(MinSupD*TL.size());
        br.close();
        fw.close();
        System.err.println("------------------done ReadFile-------------------");
        FindFS();
    }
    public static void FindFS() throws IOException {
        FS = new ArrayList<>();
        FSC = new ArrayList<>();

        for (int i = 0; i < CSC.size(); ++i) {
            if (CSC.get(i) >= MinSup) {
                AllFS.add(CS.get(i));
                FS.add(CS.get(i));
                AllFSC.add(CSC.get(i));
                FSC.add(CSC.get(i));
            }
        }
        if(FS.size()==0){
            System.err.println("--------------End, not found FS-------------------");
            checkFS=true;
            return;
        }
        System.err.println(FS.size() + "組 "+"len="+setlen+" Frequent Set");
        System.err.println("------------------done FindFS---------------------");
        ArrangeCS();
    }
    public static void ArrangeCS() throws IOException {
        if(checkFS)
            return;
        //clear CS、CSC----------------------------
        CS = new ArrayList<>();
        CSC = new ArrayList<>();
        //Arrange Candidate Set--------------------
        for (int i = 0; i < FS.size() - 1; i++) {
            for (int j = i + 1; j < FS.size(); j++) {
                String[] set1 = FS.get(i).split(" ");
                String[] set2 = FS.get(j).split(" ");
                String CSname = "";

                for (int k = 0; k < set2.length; k++) {
                    CSname += (set1[k] + " ");
                    if (!FS.get(i).contains(set2[k]))
                        CSname += (set2[k] + " ");
                }
                CSname = CSname.substring(0, CSname.length() - 1);
                //check Candidate Set's Correctness
                String[] CSArr = CSname.split(" ");
                if (CSArr.length == set1.length + 1) {
                    boolean checkCS=false;
                    for (int k=0;k<CS.size();k++) {
                        String[] CSArr2 = CS.get(k).split(" ");
                        if (Arrays.asList(CSArr2).containsAll(Arrays.asList(CSArr))) {
                            checkCS=true;
                            break;
                        }
                    }
                    if(!checkCS){
                        CS.add(CSname);
                        CSC.add(0);
                    }
                }
            }
        }
        if(CS.size()==0){
            System.err.println("-----------------End, not found CS-------------------");
            checkFS=true;
            return;
        }
        System.err.println("----------------done  ArrangeCS-------------------");
        CountCS();
    }
    public static void CountCS() throws IOException {
        if(checkFS)
            return;
        for (int i = 0; i < CS.size(); ++i) {
            String str = CS.get(i);
            int count=CSC.get(i);
            String[] Candi = str.split(" ");

            for (String j : TL) {
                String[] strArr = j.split(" ");
                if (Arrays.asList(strArr).containsAll(Arrays.asList((Candi)))) {
                    count++;
                }
            }
            CSC.set(i,count);
        }
        System.err.println(CS.size() + "組 "+"len="+(++setlen)+" Candidate Set");
        System.err.println("----------------done CountCandi-------------------");
        FindFS();
    }
    public static void go() throws IOException {
        if(!checkFS) {
            FindFS();
            ArrangeCS();
            CountCS();
        }
    }
    public static void WriteAllFS()throws IOException{
        FileWriter fw = new FileWriter("retail_allFrequentSet.txt");
        fw.write("Minimum Support="+MinSupD*100+"%, Transcations:"+TL.size()+'\n'+'\n');
        System.out.println("Minimum Support="+MinSupD*100+"%, Transcations:"+TL.size()+'\n');
        for (int i = 0; i < AllFS.size(); i++) {
            fw.write(AllFS.get(i) + " #SUP: " +AllFSC.get(i)+ '\n');
        }
        fw.close();
        System.err.println("------------------done WriteAllFS-----------------");
    }
    public static void WriteConfidence() throws IOException {
        List<String> tmp = new ArrayList<>();
        FileWriter fw = new FileWriter("retail_Confidence.txt");
        fw.write("Minimum Support="+MinSupD*100+"%, Minimum Confidence="+MinCnf*100+"%, Transcations:"+TL.size());
        System.out.println("Minimum Support="+MinSupD*100+"%, Minimum Confidence="+MinCnf*100+"%, Transcations:"+TL.size());

        for (int i = 0; i < AllFS.size(); ++i) {
            String set = AllFS.get(i);
            String[] bb = set.split("\\s");
            tmp = new ArrayList<>();

            if (bb.length >= 2) {
                System.out.println(set);
                fw.write('\n'+set+'\n');
                int nbits = 1 << bb.length;
                for (int zz = 0; zz < nbits; ++zz) {
                    int t;
                    String carr ="";
                    for (int ss = 0; ss < bb.length; ++ss) {
                        t = 1 << ss;
                        if ((t & zz) != 0)  // 與運算，同為1時才會是1
                            carr += bb[ss]+" ";
                    }
                    if(carr!="")
                        tmp.add(carr);
                }

                for (int b=0;b<tmp.size()-1;b++) {
                    String dd = tmp.get(b);
                    String[] condition = dd.split("\\s");

                    int id = 0;
                    String res = "";
                    for (String str1 : bb) {
                        if (!Arrays.asList(condition).contains(str1)) {
                            res += str1 + " ";
                        }
                    }
                    for (int cc = 0; cc < AllFS.size(); cc++) {
                        String[] CSArr2 = AllFS.get(cc).split(" ");
                        if (Arrays.asList(CSArr2).containsAll(Arrays.asList(condition))) {
                            id = cc;
                            break;
                        }
                    }

                    double confidence = (double)AllFSC.get(i) / AllFSC.get(id);
                    if (confidence >= MinCnf) {
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
        Long start=System.currentTimeMillis();
        Config();
        ReadFile();
        go();
        WriteAllFS();
        WriteConfidence();
        System.err.println((double)(System.currentTimeMillis()-start)/1000+"秒");
    }
}
