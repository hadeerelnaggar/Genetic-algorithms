package knapsack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args) throws FileNotFoundException {
       genetics_algorithm_knapsack();
    }
    private static void genetics_algorithm_knapsack() throws FileNotFoundException {
        int number_of_population=30;
        int iterations;
        double pm=0.2;
        double pc=0.9;
        //read input
        Test_Case [] testcases;
        testcases=readInputFile();
        if(testcases==null)
            return;
        //loop on test cases
       for(int i=0;i<testcases.length;i++)
       {
            String[] generation;
            ArrayList<String>new_generation=new ArrayList<>();
            ArrayList<String>sub_generation=new ArrayList<>();
            int max_fit=0;
            //init population
            generation=generate_population(testcases[i].getNumber_of_items(),number_of_population);
            /**print generation
            System.out.println("generation");
            for(int k=0;k<generation.length;k++){
                System.out.print(generation[k]+" ");
            }
            System.out.println();**/
            String max_chromosome=generation[0];
            iterations=1500;
             do {
                //calculate fitness
                int[] fitness_array = new int[number_of_population];
                for (int j = 0; j < number_of_population; j++) {
                    fitness_array[j] = fitness_function(generation[j], testcases[i].getItems());
                    if(fitness_array[j]>=max_fit && checkweightofchromosome(generation[j],testcases[i].getSize_of_knapsack(),testcases[i].getItems()))
                    {
                        max_fit=fitness_array[j];
                        max_chromosome=generation[j];
                    }
                }
                //select
                 for(int k=0;k<generation.length;k++)
                 {
                     sub_generation.add(generation[k]);
                 }
                 //select half population by rolete wheel selection
                 //adding the selected ones in new_generation
                 //sub_genration saves the unselected ones
                 for(int k=0;k<generation.length/2;k++){
                     while(true){
                         String select = rouletteWheelSelection(generation, testcases[i].getItems());
                         //System.out.println("selection"+select);
                         if (!new_generation.contains(select))
                         {
                             new_generation.add(select);
                             sub_generation.remove(select);
                             break;
                         }
                     }
                 }
                 for(int k=0;k<new_generation.size()/2;k+=2)
                 {
                     String []offsprings;
                     offsprings=singlePointCrossOver(new_generation.get(k),new_generation.get(k+1),pc);
                     new_generation.remove(offsprings[0]);
                     new_generation.remove(offsprings[1]);
                     new_generation.addAll(Arrays.asList(offsprings));
                 }
                /** for(int k=0;k<2;k++){
                     while(true){
                         String select = rouletteWheelSelection(generation, testcases[i].getItems());
                         //System.out.println("selection"+select);
                         if (!new_generation.contains(select)){
                             new_generation.add(select);
                             sub_generation.remove(select);
                             break;
                         }
                     }
                 }
                //cross over on the other half that is not selected by rollette wheel which are stored in sub_generation list
                 for(int k=0;k<sub_generation.size()/2;k+=2){
                     String []offsprings;
                     offsprings=singlePointCrossOver(sub_generation.get(k),sub_generation.get(k+1),pc);
                     sub_generation.remove(offsprings[0]);
                     sub_generation.remove(offsprings[1]);
                     sub_generation.addAll(Arrays.asList(offsprings));
                 }
                 //adding the new offsprings to the new generation list**/
                 new_generation.addAll(sub_generation);
                //mutation
                for(int j=0;j<number_of_population;j++){
                    new_generation.set(j,mutation(new_generation.get(j),pm));
                }
                //check termination condition
                iterations--;
                for(int j=0;j<generation.length;j++) {
                    generation[j] = new_generation.get(j);
                }

                /**print generation
                for(int k=0;k<generation.length;k++){
                     System.out.print(generation[k]+" ");
                 }
                 System.out.println();**/


                new_generation=new ArrayList<>();
                sub_generation=new ArrayList<>();
             }
            while(iterations!=0);//repeat
           System.out.println("result of case number "+(i+1)+" is "+max_fit);
           //System.out.println("maximum chromosome is "+max_chromosome);
           System.out.println("------------------------------------------------------------------------");
        }
    }
    private static String [] generate_population(int number_of_items,int number_of_population)
    {
        String [] generation=new String[number_of_population];
        char one='1';
        char zero='0';
        for(int i=0;i<number_of_population;i++) {
            // to delete the null value in the string
            generation[i]=new String();
            generation[i]="";

            for(int j=0;j<number_of_items;j++){
                double random_number=Math.random();

                if(random_number>=0.5)
                {
                    generation[i]+=one;
                }
                else
                {
                    generation[i]+=zero;
                }

            }
        }

        return generation;
    }
    /** fitness function return the summation of items accepted in the knapsack **/
    private static int fitness_function(String chromosome ,Items[] items)
    {
        int fitness=0;
        for(int i=0;i<chromosome.length();i++)
        {
            if(chromosome.charAt(i)=='1')
            {
                fitness+=items[i].getBenefit();
            }

        }
        return fitness;
    }


    /** evaluate the fitness function if its feasible for the bag **/
    private static boolean checkweightofchromosome(String chromosome,int weightOfTheBag,Items[] items)
    {
        int weight=0;
        for(int i=0;i<chromosome.length();i++){
            if(chromosome.charAt(i)=='1')
                weight+=items[i].getWeight();
        }
        return (weightOfTheBag>=weight);
    }
    /** Single CrossOver method after x bits **/
    private static String [] singlePointCrossOver(String firstOne,String secondOne,double pc)
    {
        int afterHowManyBits=(int)((firstOne.length()-2)*Math.random()+1);
        double random_number=Math.random();

        String[] newOffsprings= new String[2];

        int lengthofstring=firstOne.length();
        if(random_number<=pc) {
            newOffsprings[0] = firstOne.substring(0, afterHowManyBits) + secondOne.substring(afterHowManyBits, lengthofstring);
            newOffsprings[1] = secondOne.substring(0, afterHowManyBits) + firstOne.substring(afterHowManyBits, lengthofstring);
        }
        else{
            newOffsprings[0]=firstOne;
            newOffsprings[1]=secondOne;
        }
        return newOffsprings;
    }

    /** Mutation method**/
    private static String mutation(String chromosome,double pm){
        double random_number=Math.random();
        int random_index;
        if(random_number<=pm){
            random_index= (int)((chromosome.length()-1)*Math.random());
            chromosome=flipbit(chromosome,random_index);
        }
        return chromosome;
    }
    private static String flipbit(String chromosome,int index){
        String new_chromosome="";
        String first_substring=chromosome.substring(0,index);
        String second_substring=chromosome.substring(index+1,chromosome.length());
        if(chromosome.charAt(index)=='1'){
            new_chromosome=first_substring+'0'+second_substring;
        }
        else{
            new_chromosome=first_substring+'1'+second_substring;
        }
        return new_chromosome;

    }


    /** Return String from random number according to the Roulette wheel fitness For Each chromosome array corresponding to the
     * generation array so if the the fitness For Each chromosome[x] so the returned value generation[x]
     * @param generation population array
     * @param items array of objects each with weight and benefit
     * @return the chosen String based on roulette Wheel Selection
     */

    private static String rouletteWheelSelection(String[]generation,Items[] items)
    {
        int [] fitnessForEachchromosome=new int [generation.length];
        int totalSum=0;
        int counter=0;
        for(int i=0;i<generation.length;i++)
        {
         fitnessForEachchromosome[i]=fitness_function(generation[i],items);
         totalSum+=fitnessForEachchromosome[i];
        }

        double random_number=totalSum*Math.random();

        for(int i=0;i<generation.length;i++)
        {

            if(random_number<=(fitnessForEachchromosome[i]+counter))
                return generation[i];
            else
                counter+=fitnessForEachchromosome[i];


        }


        return "";
    }

    // read input from file
    // tested
    private static Test_Case[] readInputFile() throws FileNotFoundException {
        File file = new File("C:\\Users\\Hadeer\\Desktop\\GeneticsAssignment1\\input_example.txt");
        int number_of_testcases = 0;
        int number_of_items = 0;
        int size_of_knapsack = 0;
        Test_Case[] testcases;
        Items[] items;
        if (file.exists()) {
            Scanner reader = new Scanner(file);
            number_of_testcases = Integer.parseInt(reader.nextLine());
            reader.nextLine();
            testcases = new Test_Case[number_of_testcases];
            for (int i = 0; i < number_of_testcases; i++) {

                number_of_items = Integer.parseInt(reader.nextLine());
                size_of_knapsack = Integer.parseInt(reader.nextLine());
                items = new Items[number_of_items];
                for (int j = 0; j < number_of_items; j++) {
                    items[j] = new Items();
                    items[j].setWeight(Integer.parseInt(reader.next()));
                    items[j].setBenefit(Integer.parseInt(reader.next()));
                }
                testcases[i] = new Test_Case(number_of_items, size_of_knapsack, items);
                if (reader.hasNext()) {
                    reader.nextLine();
                    reader.nextLine();
                }
            }
            /*
            System.out.println("number of test cases:"+ number_of_testcases);
            for(int i=0;i<testcases.length;i++)
            {
                System.out.println("number of items:"+testcases[i].getNumber_of_items());
                System.out.println("size of knapsack:"+testcases[i].getSize_of_knapsack());
                for(int j=0;j<testcases[i].getNumber_of_items();j++){
                    System.out.print(testcases[i].items[j].getWeight()+" ");
                    System.out.println(testcases[i].items[j].getBenefit()+" ");
                }
                }
            */
            return testcases;
        } else {
            System.out.println("file doesn't exist");
        }
        return null;
    }

}
