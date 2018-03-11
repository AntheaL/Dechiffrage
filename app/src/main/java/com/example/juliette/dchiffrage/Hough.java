package com.example.juliette.dchiffrage;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import java.util.ArrayList;

public class Hough {

    private int width,height;
    // max Rho walue (= length of the diagonal)
    private double maxRho;
    // size of the accumulators array
    private int maxIndexTheta,maxIndexRho;
    // accumulators array
    int[][] acc;
    Bitmap image;

    public Hough(Bitmap b) {
        this.image=b;
        this.width=b.getWidth();
        this.height=b.getHeight();
        this.maxRho = Math.sqrt( width*width + height*height );
        this.maxIndexTheta=360; // precision : 1 degree by cell
        this.maxIndexRho=(int)(1+this.maxRho); // precision : 1 pixel by cell
        this.acc = new int[maxIndexTheta][maxIndexRho];
        for(int i = 0; i<maxIndexTheta; i++) {
            for(int j=0;j<maxIndexRho;j++) acc[i][j] = 0;
        }
    }

    public void ajouter(int x,int y) {
        // use origin = center of the image
        x-=width/2;	y-=height/2;
        for(int indexTheta=0; indexTheta<maxIndexTheta; indexTheta+=1) {
            double theta = ((double)indexTheta/maxIndexTheta)*Math.PI;
            double rho = x*Math.cos(theta) + y*Math.sin(theta);
            int indexRho   = (int) (0.5 + (rho/this.maxRho + 0.5)*this.maxIndexRho );
            acc[indexTheta][indexRho]++;
        }
    }

    // convert (rho,theta) to (a,b) such that Y=a.X+b
    public double[] rhotheta_to_ab(double rho, double theta) {
        double a=0,b=0;
        if(Math.sin(theta)!=0) {
            a = -Math.cos(theta)/Math.sin(theta);
            b = rho/Math.sin(theta)+height/2-a*width/2; // use origin = (0,0)
        } else {
            a=Double.MAX_VALUE;
            b=0;
        }
        return new double[] {a,b};
    }

    public void accumuler() {
        int pixel;
        for(int i=0; i<width; i++) {
            for(int j=0; j<height; j++) {
                pixel = image.getPixel(i,j);
                if(Color.red(pixel)<20&&Color.blue(pixel)<20&&Color.green(pixel)<20) {
                    ajouter(i,j);
                }
            }
        }
    }

    public int[] winner() {
        // parsing the accumulators for max accumulator
        double max=0;
        int winrho=0, wintheta=0;
        for(int r=0;r<maxIndexRho;r++) {
            for(int t=0;t<maxIndexTheta;t++) {
                if (acc[t][r]<max) continue;
                max=acc[t][r];
                winrho=r;
                wintheta=t;
            }
        }

        // indexes -> (rho,theta)
        // double rho   = ((double)winrho/this.maxIndexRho - 0.5)*this.maxRho;
        // double theta = ((double)wintheta/this.maxIndexTheta)*Math.PI;

        return new int[] {winrho,wintheta};
    }

    public Bitmap visionner() {
        accumuler();
        int[] array = new int[maxIndexRho*maxIndexTheta];
        int[] win = winner();
        for(int r=0;r<maxIndexRho;r++) {
            for (int t = 0; t < maxIndexTheta; t++) array[t*maxIndexRho+r]=acc[t][r]*255/acc[win[1]][win[0]];
        }
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(array, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public void RotateBitmap() {
        double angle = winner()[1]/maxIndexTheta*Math.PI;
        Matrix matrix = new Matrix();
        matrix.postRotate((float) angle);
        image =  Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
    }


        //  public Bitmap filter() {}
        // filtre acc pour ne garder que les barres de mesure et de portée


  //  public double getHeight() {
  //      Bitmap btm = filter();
        // renvoie la hauteur (index) d'une mesure
  //
  //  }


  //  public ArrayList<int[]> cut() {
  //      ArrayList<int[]> L = new ArrayList<>();
  //          // renvoie la liste des mesures données par les 2 limites horizontales
  //          // (hauteur d'image égale à  3*getHeight())
  //  }
}
