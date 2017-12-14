/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package antrian_datanfc;

/**
 *
 * @author jengcool
 */
import java.nio.ByteBuffer;
import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

public class NFCcard {

    private TerminalFactory factory;
    private List<CardTerminal> terminals;
    private CardTerminal terminal;
    private Card card;
    public CardChannel cardChannel;

    public NFCcard() throws CardException {
        factory = TerminalFactory.getDefault();
        terminals = factory.terminals().list();
        terminal = terminals.get(0);
        card = terminal.connect("*");
        cardChannel = card.getBasicChannel();
//        cardChannel.transmit( new CommandAPDU(new byte[] { (byte)0xE0, (byte)0x00, (byte)0x00, (byte)0x21, (byte)0x01,(byte)0x77 }));
    }

    public String send(byte[] cmd, CardChannel channel) {

        String res = "";

        byte[] baResp = new byte[258];
        ByteBuffer bufCmd = ByteBuffer.wrap(cmd);
        ByteBuffer bufResp = ByteBuffer.wrap(baResp);

        // output = The length of the received response APDU
        int output = 0;

        try {

            output = channel.transmit(bufCmd, bufResp);
        } catch (CardException ex) {
            ex.printStackTrace();
        }

        for (int i = 0; i < output; i++) {
            res += String.format("%02X", baResp[i]);
            // The result is formatted as a hexadecimal integer
        }

        return res;
    }

    
    
    private void CekRead(){
    
    System.out.println("auth:"+send(new byte[]{(byte) 0xFF,
            (byte) 0x88, (byte) 0x00, (byte) 0x04, (byte) 0x60, (byte) 0x00},cardChannel));

    //FF D6 00 04 10 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
    //68 65 6c 6c 6f 77 6f 72 6c 64 21
//    System.out.println("writedata:"+send(new byte[]{(byte) 0xFF,
//            (byte) 0xD6, (byte) 0x00, (byte) 0x04, (byte) 0x10, 
//            (byte) 0x00,(byte) 0x01,(byte) 0x02,(byte) 0x03,(byte) 0x04,(byte) 0x05 
//            ,(byte) 0x06,(byte) 0x07,(byte) 0x08,(byte) 0x09,(byte) 0x0A,(byte) 0x0B,(byte) 0x0C,(byte) 0x0D,
//            (byte) 0x0E,(byte) 0x0F},cardChannel));

   
    
//System.out.println("uid:"+send( new byte[] { (byte)0xFF, 
//            (byte)0xCA, (byte)0x00, (byte)0x00, (byte)0x00 },cardChannel));
//
//
//
//System.out.println("data block:"+send( new byte[] { (byte) 0xFF, (byte) 0x00, (byte) 0x00,
// (byte) 0x00, (byte) 0x05, (byte) 0xD4, (byte) 0x40,
// (byte) 0x00, (byte) 0x30, (byte) 0x01},cardChannel));

//FF B0 00 04 10
    }
    
    
    public String getCardID() throws CardException {

        byte[] baResp = new byte[258];
        ByteBuffer bufResp = ByteBuffer.wrap(baResp);

        String cardID = "";
//        ResponseAPDU answer=cardChannel.transmit( new CommandAPDU(new byte[] { (byte)0xFF, 
//            (byte)0xCA, (byte)0x00, (byte)0x00, (byte)0x00 }));
//        


        



//FF 88 00 04 60 00
//FF B0 00 04 10
        ResponseAPDU answer = cardChannel.transmit(new CommandAPDU(0xFF, 0xB0, 0x00, 0x04, 0x10));

        //block 4  sebanyak 16 byte
        //FF B0 00 04 10
//        ResponseAPDU answer=cardChannel.transmit( new CommandAPDU(new byte[] { (byte)0xFF, 
//            (byte)0xB0, (byte)0x00, (byte)0x04, (byte)0x10 }));
        System.out.println("" + String.format("%02X",answer.getSW1()) + " " + String.format("%02X",answer.getSW2()));

        if (answer.getSW1() == 0x90 && answer.getSW2() == 0x00) {
            System.out.println("sukses");
        } else if (answer.getSW1() == 0x63 && answer.getSW2() == 0x00) {
            System.out.println("gagal");
        }

        byte r[] = answer.getData();
        for (int i = 0; i < r.length; i++) {
            cardID += String.format("%02X",r[i]);
        }
        return cardID;
    }

    public static void main(String[] args) throws CardException {

        NFCcard c = new NFCcard();
        c.CekRead();
//        System.out.println(c.getCardID());
//c.CekRead();
    }

}
