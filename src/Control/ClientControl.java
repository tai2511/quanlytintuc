/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Dto.Request;
import Dto.Response;

public class ClientControl {
	private Socket mySocket;
	private String serverHost = "localhost";
	private int serverPort = 7777;

	public ClientControl() {
	}

	public Response connectAndGetData(Request request) {
		openConnection();
		sendData(request);
		Response result = receiveData();
		closeConnection();
		return result;
	}

	public Socket openConnection() {
		try {
			mySocket = new Socket(serverHost, serverPort);
			if (mySocket == null)
				System.out.println("Khong the mo ket noi");
			else
				System.out.println("Mo ket noi thanh cong");
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return mySocket;
	}

	public boolean sendData(Request request) {
		System.out.println("Gui du lieu");
		try {
			ObjectOutputStream oos = new ObjectOutputStream(mySocket.getOutputStream());
			oos.writeObject(request);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	public Response receiveData() {
		Response result = null;
		System.out.println("Nhan du lieu");
		try {
			ObjectInputStream ois = new ObjectInputStream(mySocket.getInputStream());
			result = (Response) ois.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return result;
	}


	public boolean closeConnection() {
		try {
			mySocket.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}
