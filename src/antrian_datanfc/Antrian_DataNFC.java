/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package antrian_datanfc;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 *
 * @author jengcool
 */
public class Antrian_DataNFC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
//        String b="asal";
//        byte[] as=b.getBytes(StandardCharsets.US_ASCII);
//        System.out.println(as[1]);

        int[] kecuali = new int[15];
        HashMap trailer = new HashMap();
        int a = 0;
        //data value 720 byte
        for (int i = 7; i < 64; i = i + 4) {
//           System.out.println(i);
            kecuali[a] = i;

            trailer.put(a, a);
//           System.out.println("kecuali : "+kecuali[a]);
            a++;
        }

        if (trailer.get(7) == null) {
            System.out.println("inul");

        }

        HashMap allblock = new HashMap();
        for (int c = 4; c < 63; c++) {
            allblock.put(c, c);
        }

        int d=0;
        int c=26;
        int b=c/16;
        //cek apa ada sisa bagi klo ada add 1
        
        if(c%16>0){
          System.out.println("ada sisa bagi");
           d=b+1;
        }
        
        
        
//        
//        for(int k=0;k<d;k++){
//          
//            System.out.println(kecuali[k]);
//        
//        }
//        System.out.println(b);
//        String l="tesdua";
//        System.out.println(l.substring(0, 5));
//        
//        int u=74/16;
//        System.out.println(u);
    }

}
