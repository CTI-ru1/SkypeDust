/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.uberdust.skypedust;

import eu.uberdust.skypedust.ui.SkypeDustApp;

/**
 *
 * @author carnage
 */
public class SkypeDustMain {

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
            java.util.logging.Logger.getLogger(SkypeDustApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SkypeDustApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SkypeDustApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SkypeDustApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        if(args.length==0){
            
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                new SkypeDustApp().setVisible(true);
                }
            });
        }
        else{
            switch(args[0]){
                case "start":
                    System.out.println("Starting Daemon");
                    break;
                case "stop":
                    System.out.println("Stopping Daemon");
                    break;
                case "restart":
                    System.out.println("Restarting Daemon");
                    break;
                case "cmd":
                    new SkypedustCmd().start();
                    break;
                default:
                    System.out.println("Unknown command");
                    break;
            }
        }
    }
}
