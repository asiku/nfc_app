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

    class MyTask extends TimerTask {

        public void run() {

            ///////////////////This fix applied after reading thread at http://stackoverflow.com/a/16987873/1411888
            try {
                Class pcscterminal
                        = Class.forName("sun.security.smartcardio.PCSCTerminals");
                Field contextId = pcscterminal.getDeclaredField("contextId");
                contextId.setAccessible(true);

                if (contextId.getLong(pcscterminal) != 0L) {
                    Class pcsc
                            = Class.forName("sun.security.smartcardio.PCSC");

                    Method SCardEstablishContext = pcsc.getDeclaredMethod(
                            "SCardEstablishContext", new Class[]{Integer.TYPE});
                    SCardEstablishContext.setAccessible(true);

                    Field SCARD_SCOPE_USER
                            = pcsc.getDeclaredField("SCARD_SCOPE_USER");
                    SCARD_SCOPE_USER.setAccessible(true);

                    long newId = ((Long) SCardEstablishContext.invoke(pcsc, new Object[]{Integer.valueOf(SCARD_SCOPE_USER.getInt(pcsc))})).longValue();
                    contextId.setLong(pcscterminal, newId);
                }
            } catch (Exception ex) {
            }
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

    public void otentikasi() {

        System.out.println("auth:" + send(new byte[]{(byte) 0xFF,
            (byte) 0x88, (byte) 0x00, (byte) 0x04, (byte) 0x60, (byte) 0x00}, cardChannel));

        //9000
        String otk = send(new byte[]{(byte) 0xFF,
            (byte) 0x88, (byte) 0x00, (byte) 0x04, (byte) 0x60, (byte) 0x00}, cardChannel);
        if (otk.equals("9000")) {
            lbl_stat_otentik.setText("Berhasil Di Otentikasi");
        } else {
            lbl_stat_otentik.setText("Gagal Otentikasi Device Tidak Bisa Baca dan Write Code:" + otk);
        }
    }

    private void jmlkar() {

        lbl_jml.setText("Karakter " + txt_write_data.getText().length() + " jml");

    }

    public void Bacadata() throws CardException {

        otentikasi();
        String cardID = "";
        ResponseAPDU answer = cardChannel.transmit(new CommandAPDU(0xFF, 0xB0, 0x00, 0x05, 0x10));

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
        this.txt_baca_data.setText(cardID);

    }

    private boolean Cektrailer(int cek) {

        boolean at = false;

        HashMap trailer = new HashMap();

        int a = 0;
        for (int i = 7; i < 64; i = i + 4) {
            a++;
            trailer.put(a, i);

        }

        Set set = trailer.entrySet();

        Iterator ite = set.iterator();

        while (ite.hasNext()) {

            Map.Entry me = (Map.Entry) ite.next();

            if (me.getValue().equals(cek)) {
                at = true;
                break;
            } else {
                at = false;
                break;
            }

        }

        return at;
    }

    private void InitTulis() {

        if (this.txt_write_data.getText().length() <= 710) {
            
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
int txtc=0;
            for (int i = 0; i < bagi; i++) {
txtc++;
//                System.out.println("txt:" + txt.substring(txtbagi * 16, i * 16).getBytes(StandardCharsets.US_ASCII));
//                System.out.print(i + ":");
//                System.out.println(mifare.blocks[i]);

//                System.out.println(txtc+" "+txt);
//System.out.println(txt.substring(txtbagi*16, txtc*16));
             this.TulisData(mifare.blocks[i], (byte)0x10, txt.substring(txtbagi*16, txtc*16));
             
             txtbagi++;
             
            }

//        this.txt_write_data.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "text melebihi kapasitas kartu!");
            StringBuilder txt = new StringBuilder(txt_write_data.getText());

            txt_write_data.setText(txt.substring(0, 710));
        }
    }

    private void TulisData(byte blok, byte ukuran, String data) {
        if (this.txt_write_data.getText().length() <= 710) {
            byte[] opcode = new byte[5+data.length()];

            opcode[0] = (byte) 0xFF;
            opcode[1] = (byte) 0xD6;
            opcode[2] = (byte) 0x00;
            opcode[3] = blok;
            opcode[4] = ukuran;

System.out.println(data.length());

            for (int i = 0; i < data.length(); i++) {
                //edit here  
                opcode[5 + i] = (byte) data.charAt(i);
                System.out.println("tes baca: "+i);

            }
//
            send(opcode, cardChannel);
        } else {

            JOptionPane.showMessageDialog(null, "text melebihi kapasitas kartu!");
            StringBuilder txt = new StringBuilder(txt_write_data.getText());

            txt_write_data.setText(txt.substring(0, 702));
// txt_write_data.setEditable(false);
        }
    }

    private byte[] DataAscii() {

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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_stat_nfc.setText("Card Belum Konek");

        lbl_stat_nfcreader.setText("Reader Belum Konek");

        jLabel2.setText("Baca Data");

        lbl_stat_otentik.setText("otentikasi");

        bt_baca_data.setText("Baca");
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
                    .addComponent(lbl_jml)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 671, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        txt_baca_data.setRows(5);
        jScrollPane2.setViewportView(txt_baca_data);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_stat_otentik, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_stat_nfc, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_stat_nfcreader, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addComponent(bt_baca_data)
                                .addGap(27, 27, 27)
                                .addComponent(bt_baca_data1, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel2)))
                        .addGap(0, 4, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2)))
                .addContainerGap())
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bt_baca_data)
                    .addComponent(bt_baca_data1))
                .addGap(22, 22, 22))
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
            try {
                Bacadata();
            } catch (CardException ex) {
                Logger.getLogger(Frm_utama.class.getName()).log(Level.SEVERE, null, ex);
            }

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
