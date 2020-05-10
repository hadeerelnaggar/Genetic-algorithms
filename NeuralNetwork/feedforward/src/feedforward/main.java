package feedforward;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class main {
    public static Vector<Training> training_examples;
    public static int inputNodesNumber=0;
    public static int hiddenNodesNumber=0;
    public static int outputNodesNumber=0;
    public static int number_of_training=0;
    public static Double[]firstlevelweights;
    public static Double[]secondlevelweights;
    public static double[]hiddenNodesOutput;
    public static void main(String[] args) {
         readfile();
        firstlevelweights=new Double[inputNodesNumber*hiddenNodesNumber];
        secondlevelweights=new Double[hiddenNodesNumber*outputNodesNumber];
         readweightsfile();
         double[]meanerror=meansquareerror();
         for(int i=0;i<meanerror.length;i++){
             System.out.println(meanerror[i]);
         }
    }
    private static double[] meansquareerror(){
        double[]meanserror=new double[training_examples.size()];
        for(int i=0;i<training_examples.size();i++){
            Vector<Double> output=feedforward(i);
            Vector<Double>target=training_examples.get(i).getOutput();
            double meansquareerror=0.0;
            for(int j=0;j<output.size();j++){
                meansquareerror+=Math.pow((target.get(j)-output.get(j)),2);
            }
            meansquareerror/=output.size();
            meanserror[i]=meansquareerror;
        }
        return meanserror;
    }
    private static void readweightsfile(){
        try {
            File myObj = new File("C:\\Users\\Hadeer\\Desktop\\backpropagation\\output.txt");
            Scanner myReader = new Scanner(myObj);
            String weight=myReader.nextLine();
            int i=0;
            while(weight!=" " &&i<firstlevelweights.length){
                firstlevelweights[i]=Double.parseDouble(weight);
                i++;
                weight=myReader.nextLine();
            }
            i=0;
            weight=myReader.nextLine();
            while (myReader.hasNext() && i<secondlevelweights.length){
                secondlevelweights[i]=Double.parseDouble(weight);
                i++;
                weight=myReader.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static void readfile(){
        training_examples=new Vector<>();
        try{
            File myObj = new File("train.txt");
            Scanner myReader = new Scanner(myObj);
            String data = myReader.nextLine();
            String[]numbers=data.split(" ");
            inputNodesNumber=Integer.parseInt(numbers[0]);
            hiddenNodesNumber=Integer.parseInt(numbers[1]);
            outputNodesNumber=Integer.parseInt(numbers[2]);
            number_of_training=Integer.parseInt(myReader.nextLine());
            for(int i=0;i<number_of_training;i++){
                Training train=new Training();
                data=myReader.nextLine();
                data.replace("  "," ");
                String[]s=data.split(" ");
                Vector<Double>x=new Vector<>();
                Vector<Double>y=new Vector<>();
                for(int j=0;j<s.length;j++) {
                    s[j].replace(" ", "");
                    if (!s[j].isEmpty()) {
                        if (x.size() < inputNodesNumber) {
                            x.add(Double.parseDouble(s[j]));
                        }
                        else {
                            y.add(Double.parseDouble(s[j]));
                        }
                    }
                }

                normalize(x);
                normalize(y);
                train.setX(x);
                train.setOutput(y);
                training_examples.add(train);
            }
            myReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    private static void normalize(Vector<Double>input) {
        double max = 0;
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i) > max) {
                max = input.get(i);
            }
        }
        for (int i = 0; i < input.size(); i++) {
            input.set(i, input.get(i) / max);
        }
    }
    private static Vector<Double> feedforward(int trainindex){
        hiddenNodesOutput=new double[hiddenNodesNumber];
        Vector<Double>input=training_examples.get(trainindex).getX();
        int weightIndex=0;
        //multiplies each input with the its weight and adds them to give the output.txt of each hidden layer
        for(int i=0;i<hiddenNodesOutput.length;i++){
            double firstout=0.0;
            for(int j=0;j<input.size();j++){
                firstout+=input.get(j)*firstlevelweights[weightIndex];
                weightIndex++;
            }
            hiddenNodesOutput[i]=sigmoid(firstout);
        }
        Vector<Double>output=new Vector<>();
        int secondweightindex=0;
        double out=0.0;
        //multiply eacg hidden layer output.txt by its weight to get the output.txt of each output.txt node
        for(int i=0;i<outputNodesNumber;i++){
            out=0.0;
            for(int j=0;j<hiddenNodesOutput.length-1;j++){
                out+=hiddenNodesOutput[j]*secondlevelweights[secondweightindex];
                secondweightindex++;
            }
            output.add(sigmoid(out));
        }
        return output;
    }
    private static double sigmoid(double x){
        return 1/(1+Math.exp(-x));
    }
}
