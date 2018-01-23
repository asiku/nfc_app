/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package antrian_datanfc;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author jengcool
 */
public class Frm_utama extends javax.swing.JFrame {

    /**
     * Creates new form Frm_utama
     */
    private Card card;
    public CardChannel cardChannel;

    public Frm_utama() {
        initComponents();
//
        Timer timer = new Timer();  //At this line a new Thread will be created
        MyTask task = new MyTask();
        

        timer.scheduleAtFixedRate(task, 0, 1000);
        txt_write_data.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                jmlkar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                jmlkar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                jmlkar();
            }
        });

    }

    
    class bacan extends Thread{ 
      public void run(){
//          ResultMifar1k();
jButton1.setText("tss");
      }
    } 
    
    class MyTask extends TimerTask {

        public void run() {

            ///////////////////This fix applied after reading thread at http://stackoverflow.com/a/16987873/1411888
//            try {
//                Class pcscterminal
//                        = Class.forName("sun.security.smartcardio.PCSCTerminals");
//                Field contextId = pcscterminal.getDeclaredField("contextId");
//                contextId.setAccessible(true);
//
//                if (contextId.getLong(pcscterminal) != 0L) {
//                    Class pcsc
//                            = Class.forName("sun.security.smartcardio.PCSC");
//
//                    Method SCardEstablishContext = pcsc.getDeclaredMethod(
//                            "SCardEstablishContext", new Class[]{Integer.TYPE});
//                    SCardEstablishContext.setAccessible(true);
//
//                    Field SCARD_SCOPE_USER
//                            = pcsc.getDeclaredField("SCARD_SCOPE_USER");
//                    SCARD_SCOPE_USER.setAccessible(true);
//
//                    long newId = ((Long) SCardEstablishContext.invoke(pcsc, new Object[]{Integer.valueOf(SCARD_SCOPE_USER.getInt(pcsc))})).longValue();
//                    contextId.setLong(pcscterminal, newId);
//                }
//            } catch (Exception ex) {
//            }
//            
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

            TerminalFactory factory = null;
            List<CardTerminal> terminals = null;
            try {
                factory = TerminalFactory.getDefault();

                terminals = factory.terminals().list();
            } catch (Exception ex) { //
                Logger.getLogger(Frm_utama.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (factory != null && factory.terminals() != null && terminals
                    != null && terminals.size() > 0) {
                try {

                    CardTerminal terminal = terminals.get(0);

                    if (terminal != null) {

                        System.out.println(terminal);
                        lbl_stat_nfcreader.setText("" + terminal);

                        if (terminal.isCardPresent()) {
                            card = terminal.connect("*");
                            cardChannel = card.getBasicChannel();

                            System.out.println("Card");
                            lbl_stat_nfc.setText("card");

//                            bacan t=new bacan();
//                            t.start();
                            
                           
                        } else {
                            System.out.println("No Card");
                            lbl_stat_nfc.setText("No card");
                        }

                    } else {
                        System.out.println("No terminal");
                    }

                    terminal = null;
                } catch (Exception e) {
                    Logger.getLogger(Frm_utama.class.getName()).log(Level.SEVERE, null, e);
                }
                factory = null;

                terminals = null;
                
               
                
                Runtime.getRuntime().gc();

            } else {
                System.out.println("No terminal");
            }

        }// end run
    }

    
    public void otentkasi_mifare1k(byte blok) {
    String otk = send(new byte[]{(byte) 0xFF,
            (byte) 0x88, (byte) 0x00, blok, (byte) 0x60, (byte) 0x00}, cardChannel);
        if (otk.equals("9000")) {
            System.out.println("sukses Authentikasi");
            lbl_stat_otentik.setText("Berhasil Di Otentikasi");
        } else {
            System.out.println("gagal Authentikasi");
            lbl_stat_otentik.setText("Gagal Otentikasi Device Tidak Bisa Baca dan Write Code:" + otk);
        }
    }
    
    public void loadkey(){
        
     // password baru wolaaa=77 6f 6c 61 61 61   
    
    String otk = send(new byte[]{(byte) 0xFF,
            (byte) 0x82, (byte) 0x00, (byte) 0x00, (byte) 0x06, 
            (byte) 0x77,(byte) 0x6F,(byte) 0x6C,(byte) 0x61,(byte) 0x61,(byte) 0x61}, cardChannel);
        if (otk.equals("9000")) {
            System.out.println("sukses Authentikasi");
            lbl_stat_otentik.setText("Berhasil Di Otentikasi");
        } else {
            System.out.println("gagal Authentikasi");
            lbl_stat_otentik.setText("Gagal Otentikasi Device Tidak Bisa Baca dan Write Code:" + otk);
        }
    }
    
    public void otentikasi() throws CardException {

//        System.out.println("auth:" + send(new byte[]{(byte) 0xFF,
//            (byte) 0x88, (byte) 0x00, (byte) 0x04, (byte) 0x60, (byte) 0x00}, cardChannel));

        //parameter ke 4 autentikasi perblok
        
        //loadkey non default password
        loadkey();
        
        String otk = send(new byte[]{(byte) 0xFF,
            (byte) 0x88, (byte) 0x00, (byte) 0x04, (byte) 0x60, (byte) 0x00}, cardChannel);
        if (otk.equals("9000")) {
            System.out.println("sukses Authentikasi");
            lbl_stat_otentik.setText("Berhasil Di Otentikasi");
        } else {
            System.out.println("gagal Authentikasi");
            lbl_stat_otentik.setText("Gagal Otentikasi Device Tidak Bisa Baca dan Write Code:" + otk);
        }
        
        
    }

    private void jmlkar() {

        lbl_jml.setText("Karakter " + txt_write_data.getText().length() + " jml");

    }

    
    private void ResultMifar1k() {
         Mifare_1kMap mifare = new Mifare_1kMap();
         String b = "";
         for (int i = 0; i < mifare.blocks.length; i++) {
            otentkasi_mifare1k(mifare.blocks[i]);
             try {
                 b=b+BacadataMifare1k(mifare.blocks[i]);
             } catch (CardException ex) {
                 Logger.getLogger(Frm_utama.class.getName()).log(Level.SEVERE, null, ex);
             }
         }
         
         this.txt_baca_data.setText(b);

    
    }
    
       private String BacadataMifare1k(byte blok) throws CardException {

         
        String cardID = "";


        ResponseAPDU answer = cardChannel.transmit(new CommandAPDU(0xFF, 0xB0, 0x00, blok, 0x10));
        
        System.out.println("" + String.format("%02X", answer.getSW1()) + " " + String.format("%02X", answer.getSW2()));

        if (answer.getSW1() == 0x90 && answer.getSW2() == 0x00) {
            System.out.println("sukses");
        } else if (answer.getSW1() == 0x63 && answer.getSW2() == 0x00) {
            System.out.println("gagal");
        }

        byte r[] = answer.getData();
        for (int i = 0; i < r.length; i++) {
            cardID += String.format("%02X", r[i]);
        }
        
        StringBuilder rst = new StringBuilder();
        for (int i = 0; i < cardID.length(); i += 2) {
            String str = cardID.substring(i, i + 2);
            rst.append((char) Integer.parseInt(str, 16));
        }
        
        System.out.println("result :"+ rst.toString());
       
//        this.txt_baca_data.setText(rst.toString());
        return rst.toString();
    }
    
 
    
    public void Bacadata2() throws CardException {

//        otentikasi();

         Mifare_1kMap mifare = new Mifare_1kMap();
         
         otentkasi_mifare1k(mifare.blocks[3]);
         
        String cardID = "";
//        ResponseAPDU answer = cardChannel.transmit(new CommandAPDU(0xFF, 0xB0, 0x00, 0x04, 0x10));

        ResponseAPDU answer = cardChannel.transmit(new CommandAPDU(0xFF, 0xB0, 0x00, mifare.blocks[3], 0x10));
        
        System.out.println("" + String.format("%02X", answer.getSW1()) + " " + String.format("%02X", answer.getSW2()));

        if (answer.getSW1() == 0x90 && answer.getSW2() == 0x00) {
            System.out.println("sukses");
        } else if (answer.getSW1() == 0x63 && answer.getSW2() == 0x00) {
            System.out.println("gagal");
        }

        byte r[] = answer.getData();
        for (int i = 0; i < r.length; i++) {
            cardID += String.format("%02X", r[i]);
        }
        
        StringBuilder rst = new StringBuilder();
        for (int i = 0; i < cardID.length(); i += 2) {
            String str = cardID.substring(i, i + 2);
            rst.append((char) Integer.parseInt(str, 16));
        }
        
        
        this.txt_baca_data.setText(rst.toString());

    }
    
    
    public String Bacadata(byte block) throws CardException {

        
        String cardID = "";
        ResponseAPDU answer = cardChannel.transmit(new CommandAPDU(0xFF, 0xB0, 0x00, block, 0x10));

        System.out.println("" + String.format("%02X", answer.getSW1()) + " " + String.format("%02X", answer.getSW2()));

        if (answer.getSW1() == 0x90 && answer.getSW2() == 0x00) {
            System.out.println("sukses");
        } else if (answer.getSW1() == 0x63 && answer.getSW2() == 0x00) {
            System.out.println("gagal");
        }

        byte r[] = answer.getData();
        for (int i = 0; i < r.length; i++) {
            cardID += String.format("%02X", r[i]);
        }
//        this.txt_baca_data.setText(cardID);

//   byte[] s = DatatypeConverter.parseHexBinary(res);
//    System.out.println(new String(s));

 StringBuilder rst = new StringBuilder();
        for (int i = 0; i < cardID.length(); i += 2) {
            String str = cardID.substring(i, i + 2);
            rst.append((char) Integer.parseInt(str, 16));
        }

        return rst.toString();

    }

//    private boolean Cektrailer(int cek) {
//
//        boolean at = false;
//
//        HashMap trailer = new HashMap();
//
//        int a = 0;
//        for (int i = 7; i < 64; i = i + 4) {
//            a++;
//            trailer.put(a, i);
//
//        }
//
//        Set set = trailer.entrySet();
//
//        Iterator ite = set.iterator();
//
//        while (ite.hasNext()) {
//
//            Map.Entry me = (Map.Entry) ite.next();
//
//            if (me.getValue().equals(cek)) {
//                at = true;
//                break;
//            } else {
//                at = false;
//                break;
//            }
//
//        }
//
//        return at;
//    }

    private void InitTulis() {

        if (this.txt_write_data.getText().length() <= 720) {

//            otentikasi();
            int len = this.txt_write_data.getText().length();

            String txt = this.txt_write_data.getText();

            int bagi = len / 16;

            if (len % 16 > 0) {
//            bagi = bagi + 1;
                int addkar = (bagi * 16) + 16;
                int awal = addkar - len;
                StringBuilder spasi = new StringBuilder();

//                System.out.println("add:" + awal);
                for (int i = 0; i < awal; i++) {
                    spasi.append(" ");
                }
                txt = txt + spasi.toString();
//                System.out.println("txt:" + txt.length());
                bagi = txt.length() / 16;

            }

            Mifare_1kMap mifare = new Mifare_1kMap();

            //mapping block selesai tinggal data string yg blon
            int txtbagi = 0;
            int txtc = 0;
            for (int i = 0; i < bagi; i++) {
                txtc++;
//                System.out.println("txt:" + txt.substring(txtbagi * 16, i * 16).getBytes(StandardCharsets.US_ASCII));
//                System.out.print(i + ":");
//                System.out.println(mifare.blocks[i]);

//                System.out.println(txtc+" "+txt);
//System.out.println(txt.substring(txtbagi*16, txtc*16));
               
                otentkasi_mifare1k(mifare.blocks[i]);
                this.TulisData(mifare.blocks[i], (byte) 0x10, txt.substring(txtbagi * 16, txtc * 16));

                txtbagi++;

            }

//        this.txt_write_data.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "text melebihi kapasitas kartu!");
            StringBuilder txt = new StringBuilder(txt_write_data.getText());

            txt_write_data.setText(txt.substring(0, 720));
        }
    }

    private void TulisData(byte blok, byte ukuran, String data)  {
        if (this.txt_write_data.getText().length() <= 710) {
            byte[] opcode = new byte[5 + data.length()];

            opcode[0] = (byte) 0xFF;
            opcode[1] = (byte) 0xD6;
            opcode[2] = (byte) 0x00;
            opcode[3] = blok;
            opcode[4] = ukuran;

            System.out.println(data.length());

            for (int i = 0; i < data.length(); i++) {
                //edit here  
                opcode[5 + i] = (byte) data.charAt(i);
                System.out.println("tes baca: " + i);

            }
//
            System.out.println("tulis:"+send(opcode, cardChannel));
        } else {

            JOptionPane.showMessageDialog(null, "text melebihi kapasitas kartu!");
            StringBuilder txt = new StringBuilder(txt_write_data.getText());

            txt_write_data.setText(txt.substring(0, 702));
// txt_write_data.setEditable(false);
        }
    }

    
    
    private void TulisTes(byte blok, byte ukuran, String data)  {
        
            byte[] opcode = new byte[5 + data.length()];

            opcode[0] = (byte) 0xFF;
            opcode[1] = (byte) 0xD6;
            opcode[2] = (byte) 0x00;
            opcode[3] = blok;
            opcode[4] = ukuran;

            System.out.println(data.length());

            for (int i = 0; i < data.length(); i++) {
                //edit here  
                opcode[5 + i] = (byte) data.charAt(i);
                System.out.println("tes baca: " + i);

            }
//
            System.out.println("tulis:"+send(opcode, cardChannel));
        
    }

    private byte[] DataAscii() throws CardException {

        byte[] bytes;

        String tmp = txt_write_data.getText();
//        for(int i=0;i<txt_write_data.getText().length();i++){
        bytes = tmp.getBytes(StandardCharsets.US_ASCII);
//        }

        send(new byte[]{(byte) 0xFF,
            (byte) 0xD6, (byte) 0x00, (byte) 0x04, (byte) 0x10,
            (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C, (byte) 0x0D,
            (byte) 0x0E, (byte) 0x0F}, cardChannel);

        return null;
    }

    public String send(byte[] cmd, CardChannel channel)  {

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


//        StringBuilder rst = new StringBuilder();
//        for (int i = 0; i < res.length(); i += 2) {
//            String str = res.substring(i, i + 2);
//            rst.append((char) Integer.parseInt(str, 16));
//        }

     
        
        return res;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lbl_stat_nfc = new javax.swing.JLabel();
        lbl_stat_nfcreader = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lbl_stat_otentik = new javax.swing.JLabel();
        bt_baca_data = new javax.swing.JButton();
        bt_baca_data1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        lbl_jml = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_write_data = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_baca_data = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        bt_bacakartu = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_stat_nfc.setText("Card Belum Konek");

        lbl_stat_nfcreader.setText("Reader Belum Konek");

        jLabel2.setText("Baca Data");

        lbl_stat_otentik.setText("otentikasi");

        bt_baca_data.setText("Baca Blok 4");
        bt_baca_data.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_baca_dataActionPerformed(evt);
            }
        });

        bt_baca_data1.setText("Proses Write");
        bt_baca_data1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_baca_data1ActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Tulis Data [ Khusus Card Mifare 1k] batas Max input 702 karakter\n"));

        lbl_jml.setText("Karakter: 0 jml");

        txt_write_data.setColumns(20);
        txt_write_data.setLineWrap(true);
        txt_write_data.setRows(5);
        txt_write_data.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_write_dataKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(txt_write_data);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl_jml)
                        .addGap(0, 760, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(lbl_jml)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txt_baca_data.setColumns(20);
        txt_baca_data.setLineWrap(true);
        txt_baca_data.setRows(5);
        jScrollPane2.setViewportView(txt_baca_data);

        jButton1.setText("tes baca");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        bt_bacakartu.setText("Baca Kartu");
        bt_bacakartu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_bacakartuActionPerformed(evt);
            }
        });

        jButton2.setText("tes write ganti password");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_stat_otentik, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_stat_nfc, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_stat_nfcreader, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(bt_bacakartu)
                        .addGap(18, 18, 18)
                        .addComponent(bt_baca_data1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bt_baca_data, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61))))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(lbl_stat_nfcreader)
                        .addGap(18, 18, 18)
                        .addComponent(lbl_stat_nfc)
                        .addGap(18, 18, 18)
                        .addComponent(lbl_stat_otentik))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(bt_bacakartu)
                        .addComponent(bt_baca_data1)
                        .addComponent(jButton2)
                        .addComponent(jButton1))
                    .addComponent(bt_baca_data))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bt_baca_dataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_baca_dataActionPerformed
        // TODO add your handling code here:
        if (lbl_stat_nfc.getText().equals("card")) {
            Mifare_1kMap p = new Mifare_1kMap();
            String b = "";
            try {
                otentikasi();
            } catch (CardException ex) {
                Logger.getLogger(Frm_utama.class.getName()).log(Level.SEVERE, null, ex);
            }
//            byte[] read_four_to_seven = new byte[]{(byte) 0xFF, (byte) 0x00, (byte) 0x00,
//                (byte) 0x00, (byte) 0x05, (byte) 0x0D4, (byte) 0x40, (byte) 0x01,
//                (byte) 0x30, (byte) 0x04, (byte) 0x07 };
           
//                System.out.println("Read : " + send(read_four_to_seven, cardChannel));
            
                 

         Mifare_1kMap mifare = new Mifare_1kMap();
         
         
         
        String cardID = "";
//        ResponseAPDU answer = cardChannel.transmit(new CommandAPDU(0xFF, 0xB0, 0x00, 0x04, 0x10));

        ResponseAPDU answer = null;
            try {
                answer = cardChannel.transmit(new CommandAPDU(0xFF, 0xB0, 0x00, 0x04, 0x10));
            } catch (CardException ex) {
                Logger.getLogger(Frm_utama.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        System.out.println("" + String.format("%02X", answer.getSW1()) + " " + String.format("%02X", answer.getSW2()));

        if (answer.getSW1() == 0x90 && answer.getSW2() == 0x00) {
            System.out.println("sukses");
        } else if (answer.getSW1() == 0x63 && answer.getSW2() == 0x00) {
            System.out.println("gagal");
        }

        byte r[] = answer.getData();
        for (int i = 0; i < r.length; i++) {
            cardID += String.format("%02X", r[i]);
        }
        
        StringBuilder rst = new StringBuilder();
        for (int i = 0; i < cardID.length(); i += 2) {
            String str = cardID.substring(i, i + 2);
            rst.append((char) Integer.parseInt(str, 16));
        }
        
        
        this.txt_baca_data.setText(rst.toString());

        }

    }//GEN-LAST:event_bt_baca_dataActionPerformed

    private void txt_write_dataKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_write_dataKeyTyped
        // TODO add your handling code here:
//        if(evt.getKeyCode()==KeyEvent.VK_ESCAPE){
//           txt_write_data.setEditable(true);
//        }
    }//GEN-LAST:event_txt_write_dataKeyTyped

    private void bt_baca_data1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_baca_data1ActionPerformed
        
            // TODO add your handling code here:
//        byte[] baResp = new byte[258];
//        TulisData((byte) 0x00, (byte) 0x00, baResp);

InitTulis();
        

    }//GEN-LAST:event_bt_baca_data1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            // TODO add your handling code here:
            
//            otentikasi();
            Bacadata2();
        } catch (CardException ex) {
            Logger.getLogger(Frm_utama.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void bt_bacakartuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_bacakartuActionPerformed
         
            ResultMifar1k();
            
    }//GEN-LAST:event_bt_bacakartuActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        String otk = send(new byte[]{(byte) 0xFF,
            (byte) 0x88, (byte) 0x00, (byte) 0x07, (byte) 0x60, (byte) 0x00}, cardChannel);
        if (otk.equals("9000")) {
            System.out.println("sukses Authentikasi");
            lbl_stat_otentik.setText("Berhasil Di Otentikasi");
        } else {
            System.out.println("gagal Authentikasi");
            lbl_stat_otentik.setText("Gagal Otentikasi Device Tidak Bisa Baca dan Write Code:" + otk);
        }
        
       this.TulisTes((byte)0x07, (byte) 0x10, "wolaaaÿiaaaaaa");
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Frm_utama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Frm_utama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Frm_utama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Frm_utama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Frm_utama().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bt_baca_data;
    private javax.swing.JButton bt_baca_data1;
    private javax.swing.JButton bt_bacakartu;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl_jml;
    private javax.swing.JLabel lbl_stat_nfc;
    private javax.swing.JLabel lbl_stat_nfcreader;
    private javax.swing.JLabel lbl_stat_otentik;
    private javax.swing.JTextArea txt_baca_data;
    private javax.swing.JTextArea txt_write_data;
    // End of variables declaration//GEN-END:variables
}
