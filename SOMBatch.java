package sombatch;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.StringTokenizer;

public class SOMBatch {
    public final static int numOfNeuron = 81;
    public final static double neighbourhoodSize = 9.0;
    public final static int instanceSize = 5000;
    public final static int attributeSize = 40;
    public final static double decay = 40;
    public final static String filename = "waveform-5000.txt";
    public final static String outputfilename = "weight-matrix.txt";
    public final static int epoch = 1;
    static double[][] neuron = new double[numOfNeuron][attributeSize];
    static double[][] hckX = new double[numOfNeuron][attributeSize];
    static double[] hck = new double[numOfNeuron];
    
    
    static double computeDistance(double[] vector1,double[] vector2){
        double distance=0.0;
        for(int i=0;i<attributeSize;i++){
            distance+=Math.pow((vector1[i]-vector2[i]),2.0);
        }
        distance=Math.sqrt(distance);
        return distance;
    }
    
    static int computeBMU(double[] vector){
        int bestNeuron = 0;
        double bestDistance=Double.MAX_VALUE;
        double distance;
        for(int i=0;i<numOfNeuron;i++){
            distance=computeDistance(vector,neuron[i]);
            if(distance < bestDistance){
                bestDistance=distance;
                bestNeuron=i;
            }
        }
        return bestNeuron;
    }
    
    public static void main(String[] args) throws Exception{
        int i,j,k,e;
        
        StringTokenizer idata;
        double[] vector;
        
        // generate random initial neuron data
        for(i=0;i<numOfNeuron;i++){
            for(j=0;j<attributeSize;j++){
                neuron[i][j]=(Math.random()*2)-1.0;
            }
        }
        
        for(e=0;e<epoch;e++){
            System.out.println("running epoch "+e);

        // initialize numerator and denominator
        for(i=0;i<numOfNeuron;i++){
            hck[i]=0;
            for(j=0;j<attributeSize;j++){
                hckX[i][j]=0;
            }
        }
        
        Scanner sc = new Scanner(new FileInputStream(filename));
        // start looping through dataset
        for(i=0;i<instanceSize;i++){
            idata = new StringTokenizer(sc.nextLine(),",");
            vector = new double[attributeSize];
            for(j=0;j<attributeSize;j++){
                vector[j]=Double.parseDouble(idata.nextToken());
            }
            int BMU = computeBMU(vector);
            double tempHck;
            double tempWidth;
            double tempUp;
            
            for(j=0;j<numOfNeuron;j++){
                tempUp=-(Math.pow((computeDistance(neuron[BMU],neuron[j])),2.0));
                tempWidth = 2 * Math.pow((neighbourhoodSize * Math.exp((-(double)i/decay))),2.0);
                tempHck=Math.exp(tempUp/tempWidth);
                hck[j]+=tempHck;
                for(k=0;k<attributeSize;k++){
                    hckX[j][k]+=(vector[k]*tempHck);
                }
            //    System.out.println("BMU: "+BMU+",Current: "+j+",Hck: "+tempHck);
            }
        //    System.out.println("Width: "+(neighbourhoodSize * Math.exp(((double)-i/(double)(instanceSize/2)))));
        }
        
        sc.close();
        // update weight
        for(i=0;i<numOfNeuron;i++){
            for(j=0;j<attributeSize;j++){
                neuron[i][j]=(hckX[i][j]/hck[i]);
            }
        }
        
        // compute Quantization Error
        Scanner sc1 = new Scanner(new FileInputStream(filename));
        double totalError=0.0;
         for(i=0;i<instanceSize;i++){
            idata = new StringTokenizer(sc1.nextLine(),",");
            vector = new double[attributeSize];
            for(j=0;j<attributeSize;j++){
                vector[j]=Double.parseDouble(idata.nextToken());
            }
            int BMU = computeBMU(vector);
            totalError+=(computeDistance(neuron[BMU],vector));
         }
         System.out.println("Total Error: "+totalError);
         System.out.println("Average Error: "+totalError/(double)instanceSize);
 
        }
        
        // print out the weight matrix
        PrintWriter pw = new PrintWriter(new FileOutputStream(outputfilename));
        for(i=0;i<numOfNeuron;i++){
            for(j=0;j<attributeSize;j++){
              //  System.out.print(neuron[i][j]+",");
                pw.print(neuron[i][j]+",");
            }
            pw.println();
            //System.out.println();
        }
        pw.close();
    }
}
