/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package antrian_datanfc;

import java.util.HashMap;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author jengcool
 */
public class BuatTes {
    
     public static void main(String[] args) {
     
//     int d=0;
//       HashMap allblock = new HashMap();
//        for (int c = 4; c < 63; c++) {
//            d++;
//            allblock.put(d, c);
//            
//        System.out.println(d);
//        
//        }
//        
//        System.out.println("size: "+allblock.size());
        
//        String txt="abcdefghijklmnopxabcdefghijklmnop";
//        System.out.println((byte) txt.charAt(0));
//
//          System.out.println(txt.substring(16, 16*2));
//        for (int c = 0; c < 5; c++) {
//        System.out.println(c);
//        
//        }
String hex = "75546f7272656e745c436f6d706c657465645c6e667375635f6f73745f62795f6d757374616e675c50656e64756c756d2d392c303030204d696c65732e6d7033006d7033006d7033004472756d202620426173730050656e64756c756d00496e2053696c69636f00496e2053696c69636f2a3b2a0050656e64756c756d0050656e64756c756d496e2053696c69636f303038004472756d2026204261737350656e64756c756d496e2053696c69636f30303800392c303030204d696c6573203c4d757374616e673e50656e64756c756d496e2053696c69636f3030380050656e64756c756d50656e64756c756d496e2053696c69636f303038004d50330000";
    byte[] s = DatatypeConverter.parseHexBinary(hex);
    System.out.println(new String(s));
     }
}
