package com.company;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Main {

    private static  FuzzySet [] fuzzySets;
    private static String [] outRules;
    private static FuzzySet outputSet;
    private static Vector<Vector<FuzzificationOutput>> fuzzifactionOutput;  //same length of fuzzySet
    private static Vector<InferenceOutput>inferenceOutput;
    private static Map<String,Double> centroids;

    public static void main(String[] args) throws IOException
    {
        prepare();
        fuzzification();
        System.out.println();
        System.out.println("fuzzification output:");
        printFuzzyOutput();
        inferenceOutput=new Vector<>();
        for(int i=0;i<outRules.length;i++){
            splitString(outRules[i]);
        }
        printinferenceoutput();
        centroids=new HashMap<>();
        for(int i=0;i<outputSet.getMembership_Functions().length;i++){
            double cenroid=getcentroid(outputSet.getMembership_Functions()[i]);
            centroids.put(outputSet.getMembership_Functions()[i].getName(),cenroid);
        }
        System.out.println("centroids are:");
        System.out.println(centroids);
        double predicted=0.0;
        double base=0.0;
        for(int i=0;i<inferenceOutput.size();i++){
            String name=inferenceOutput.get(i).getMemberFunctionName();
            double inferencevalue=inferenceOutput.get(i).getValue();
            double centroid=centroids.get(name);
            predicted+=inferencevalue*centroid;
            base+=inferencevalue;
        }
        predicted/=base;
        System.out.println("prediction is:"+predicted);

    }
    private static void printinferenceoutput()
    {
        System.out.println("Rule Inference output");
        for(int i=0;i<inferenceOutput.size();i++){
            System.out.println(inferenceOutput.get(i).getValue()+inferenceOutput.get(i).getMemberFunctionName());
        }
    }
    private static double getcentroid(Membership_Function member)
    {
        double centroid=0.0;
        double area=0.0;
        for(int i=0;i<member.getPoints().length-1;i++){
               area+=(member.getPoints()[i]*getY(member.getType(),i+1))-(member.getPoints()[i+1]*getY(member.getType(),i));
        }
        area*=0.5;
        for(int i=0;i<member.getPoints().length-1;i++){
            double w=(member.getPoints()[i]*getY(member.getType(),i+1))-(member.getPoints()[i+1]*getY(member.getType(),i));
            centroid+=(member.getPoints()[i]+member.getPoints()[i+1])*w;
        }
        centroid*=1/(6*area);
        return centroid;
    }
    private static void prepare() throws IOException
    {
        File file=new File("C:\\Users\\Hadeer\\Desktop\\Assignment_3\\Inputnew.txt");
        BufferedReader br= null;
        br = new BufferedReader(new FileReader(file));
        String st="";
        st=br.readLine();
        int loopNumber=Integer.parseInt(st);
        fuzzySets = new FuzzySet[loopNumber];  //numberOfObjects;
        Membership_Function [] membership_function;
        for(int i =0;i<loopNumber;i++)
        {
            st=br.readLine();
            String[] splitingName= st.split(" ");
            fuzzySets[i]= new FuzzySet();
            fuzzySets[i].setName(splitingName[0]);
            fuzzySets[i].setCrispInput(Integer.parseInt(splitingName[1]));
            st=br.readLine();
            int numberOfMemberFunction=Integer.parseInt(st);
            membership_function= new Membership_Function[numberOfMemberFunction];
            fuzzySets[i].setNumberOfMemberFunction(numberOfMemberFunction);
            for(int j=0;j<numberOfMemberFunction;j++)
            {
                st=br.readLine();
                splitingName= st.split(" ");
                membership_function[j]=new Membership_Function();
                membership_function[j].setName(splitingName[0]);
                membership_function[j].setType(splitingName[1]);

                st=br.readLine();
                splitingName= st.split(" ");
                int[] points = Arrays.stream(splitingName).mapToInt(Integer::parseInt).toArray();   //numberofPoints
                membership_function[j].setPoints(points);


            }

            fuzzySets[i].setMembership_Functions(membership_function);
        }

        printObject(fuzzySets);
        st=br.readLine();
        outputSet = new FuzzySet();
        outputSet.setName(st);
        st=br.readLine();
        int nomf=Integer.parseInt(st);
        String[] splitingName;
        Membership_Function[] object =new Membership_Function[5] ;
        for( int i=0;i<nomf;i++)
        {
            st=br.readLine();
            splitingName= st.split(" ");
            object[i]=new Membership_Function();
            object[i].setName(splitingName[0]);
            object[i].setType(splitingName[1]);

            st=br.readLine();
            splitingName= st.split(" ");
            int[] points = Arrays.stream(splitingName).mapToInt(Integer::parseInt).toArray();   //numberofPoints
            object[i].setPoints(points);

        }
        outputSet.setMembership_Functions(object);

        int numberOfRules=Integer.parseInt(br.readLine());
        outRules=new String [numberOfRules];
        for(int i=0;i<numberOfRules;i++)
        {
            outRules[i]=br.readLine();
        }
    }
    private static void printObject(FuzzySet[] object)
    {
        for(int i=0;i<object.length;i++)
        {
            System.out.println(object[i].getName()+" "+ object[i].getCrispInput());
            Membership_Function [] objects=object[i].getMembership_Functions();
            for(int j=0;j<object[i].getNumberOfMemberFunction();j++)
            {
                Membership_Function obj = objects[j];
                System.out.println(obj.getName()+" "+ obj.getType());
                System.out.println(Arrays.toString(obj.getPoints()));
            }
        }
    }
    private static void printFuzzyOutput()
    {
        for(int i=0;i<fuzzifactionOutput.size();i++)
        {

            for(int j=0;j<fuzzifactionOutput.get(i).size();j++)
            {
                System.out.println(fuzzifactionOutput.get(i).get(j).getFuzzySetName()+" "+fuzzifactionOutput.get(i).get(j).getMembership_FunctionName()+ " "+fuzzifactionOutput.get(i).get(j).getValue());
            }
            System.out.println(" ");
        }

    }
    private static double getEquationOfLine(double x1,double y1,double x2,double y2,double crispPoint)
    {
        double slope = (y2 - y1)/( x2 - x1);
        double B = y1 - (slope * x1);
        double yOut=(slope*crispPoint)+B;


        return yOut;
    }
    private static void fuzzification()
    {
        fuzzifactionOutput= new Vector<>();
        for (int i=0;i<fuzzySets.length;i++)
        {

            String fuzzySetName= fuzzySets[i].getName();

            Vector<FuzzificationOutput>FuzzyOutput= new Vector<>();
            int crispInput = fuzzySets[i].getCrispInput();
            Membership_Function [] membership_functions=fuzzySets[i].getMembership_Functions();

            for(int j=0;j<membership_functions.length;j++)
            {
                String nameOfMembership= membership_functions[j].getName();
                int index=-1;
                int []points=membership_functions[j].getPoints();
                for(int k=0;k<points.length-1;k++)
                {
                    if(crispInput>=points[k]&& crispInput<=points[k+1])
                    {
                        index=k;
                        break;
                    }
                }
                if(index==-1)
                {

                    FuzzyOutput.add(new FuzzificationOutput(fuzzySetName,nameOfMembership,0.0));
                }
                else
                {
                    int x1=points[index],x2=points[index+1];
                    int y1=0,y2=0;
                    y1=getY(membership_functions[j].getType(),index);
                    y2=getY(membership_functions[j].getType(),index+1);
                    FuzzyOutput.add(new FuzzificationOutput(fuzzySetName,nameOfMembership,getEquationOfLine(x1,y1,x2,y2,crispInput)));
                }

            }
            fuzzifactionOutput.add(FuzzyOutput);  //add answer each of MembershipFunction

        }

    }
    private static void splitString (String rule)
    {
        rule=rule.replaceAll(" ","");
        String [] arrOfRules = rule.split("then");
        boolean checkIfAnd=arrOfRules[0].contains("AND");
        Vector<Double> numbersForAnd;
        Vector<Double> numbersForOR;
        String [] arrayOfAnd= arrOfRules[0].split("AND");
        numbersForAnd=new Vector<Double>();
        for(int i=0;i<arrayOfAnd.length;i++)
        {
                if(arrayOfAnd[i].contains("OR"))
                {
                    String []arrayOfOr =arrayOfAnd[i].split("OR");
                    numbersForOR=new Vector<>();
                        for(int j=0;j<arrayOfOr.length;j++){
                        numbersForOR.add(calculateString(arrayOfOr[j]));
                    }
                    numbersForOR.sort(Double::compareTo);
                    numbersForAnd.add(numbersForOR.get(numbersForOR.size()-1));
                }
                else
                {
                    numbersForAnd.add(calculateString(arrayOfAnd[i]));
                }
        }
        Double ruleanswer=numbersForAnd.get(0);
        InferenceOutput out=new InferenceOutput(arrOfRules[1].split("=")[1],ruleanswer);
        inferenceOutput.add(out);
    }
    private static double calculateString(String rule)
    {

        String [] split = rule.split("=");
        for(int i=0;i<fuzzifactionOutput.size();i++)
        {
            for(int j=0;j<fuzzifactionOutput.get(i).size();j++)
            {
                if(split[0].equals(fuzzifactionOutput.get(i).get(j).getFuzzySetName()) && split[1].equals(fuzzifactionOutput.get(i).get(j).getMembership_FunctionName()))
                {
                    return fuzzifactionOutput.get(i).get(j).getValue();

                }
            }
        }
          return 12.0;

    }
    private static int getY(String type,int index)
    {
        if(type.equals("triangle"))
        {

            if(index==0 || index==2) {
                return 0;
            }
            else
            {
                return 1;
            }
        }
        else{
            if(index==0 || index==3)
            {
               return 0;
            }
            else if (index==1 || index==2)
            {
               return 1;
            }
        }
        return Integer.parseInt(null);
    }
}


