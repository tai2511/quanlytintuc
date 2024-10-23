/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Control.ServerControl;

/**
 *
 * @author LENPOVO
 */
public class ServerView {
    public ServerView()
    {
    	showMessage("TCP server is running...");
        new ServerControl();

    }


    public void showMessage(String msg)
    {
        System.out.println(msg);
    }
}
