/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpip;

import View.ClientView;

/**
 *
 * @author LENPOVO
 */
public class TCPIP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //ServerView view1       = new ServerView();
        ClientView view = new ClientView();
        view.setVisible(true);
    }
    
}
