package back_propagation;

import java.io.*;
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

    public static void main(String[] args)
    {
        training_examples=new Vector<>();
        readfile();
        int epoch=500;
        double learningRate=0.01;
        getrandomweights();
       while(epoch!=0){
            for(int i=0;i<training_examples.size();i++){
                Vector<Double>input=training_examples.get(i).getX();
                Vector<Double>output=feedforward(i);
                Vector<Double>errorout=geterror(output,training_examples.get(i).getOutput());
                Double[]newsecondlayerweights=updateoutlayerweights(errorout,0.01);
                Vector<Double>hiddenlayererror=hiddenlayererror(errorout);
                updateinputweights(hiddenlayererror,input,0.01);
                secondlevelweights=newsecondlayerweights;
                }
           epoch--;

            }
        double[]meansquareerror=meansquareerror();
        for(int i=0;i<meansquareerror.length;i++){
            System.out.println(i+")"+meansquareerror[i]);
          }
        printResult();
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
    private static Vector<Double>hiddenlayererror(Vector<Double>errorout){
        //hiddenlayerout
        //secondlevel weights
        //outerror
        Vector<Double>errorofhiddenlayer=new Vector<>();
        for(int i=0;i<hiddenNodesOutput.length;i++){
            int weightindex=i;
            double error=hiddenNodesOutput[i]*(1-hiddenNodesOutput[i]);
            int w=0;
            for(int j=0;j<errorout.size();j++) {
                w+=errorout.get(j)*secondlevelweights[weightindex];
                weightindex += hiddenNodesOutput.length;
            }
            error*=w;
            errorofhiddenlayer.add(error);

        }
        return errorofhiddenlayer;
    }

    private static void updateinputweights(Vector<Double>error,Vector<Double>input, double learnRate){

        int weight = 0;
        for(int i = 0; i<error.size();i++){

            for(int j =0; j<input.size();j++){

                firstlevelweights[weight]+= learnRate* error.get(i)*input.get(j);
                weight++;
            }
        }


    }

    private static Double[] updateoutlayerweights(Vector<Double>error,double learningRate){
        int weightindex=0;
        Double[]newsecondweights=secondlevelweights;
        for(int i=0;i<error.size();i++){
            for(int j=0;j<hiddenNodesOutput.length;j++){
                newsecondweights[weightindex]+=learningRate*error.get(i)*hiddenNodesOutput[j];
                weightindex++;
            }
        }
        return newsecondweights;
    }

    private static Vector<Double> geterror(Vector<Double>realout,Vector<Double>target){
        Vector<Double>error=new Vector<>(outputNodesNumber);
        for(int i=0;i<outputNodesNumber;i++){
            double err=realout.get(i)*(1-realout.get(i))*(target.get(i)-realout.get(i));
            error.add(err);
        }
        return error;
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
            for(int j=0;j<hiddenNodesOutput.length;j++){
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

    private static void getrandomweights(){
         int firstlevelweightsNumber=inputNodesNumber*hiddenNodesNumber;
         int secondlevelweightsNumber=hiddenNodesNumber*outputNodesNumber;
         firstlevelweights=new Double[firstlevelweightsNumber];
         secondlevelweights=new Double[secondlevelweightsNumber];
         //gets random weights for the first level(between input and hidden layer)
         for(int i=0;i<firstlevelweightsNumber;i++){
             firstlevelweights[i]=(Math.random()*20)-10;
         }
         //gets random weights for the second level(between hidden and output.txt layer)
         for(int i=0;i<secondlevelweightsNumber;i++){
             secondlevelweights[i]=(Math.random()*20)-10;
         }

    }

    private static void readfile(){
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

    private static void printResult(){
        OutputStream file = null;
        try {
            file = new FileOutputStream(new File("C:\\Users\\Hadeer\\Desktop\\backpropagation\\output.txt"));
            String[] newArray = new String[firstlevelweights.length];

            for (int i = 0; i < firstlevelweights.length; i++) {

                file.write(firstlevelweights[i].toString().getBytes());
                file.write("\n".getBytes());

            }
            file.write("\n ".getBytes());
            for(int i=0;i<secondlevelweights.length;i++){
                file.write(secondlevelweights[i].toString().getBytes());
                file.write("\n".getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }}
