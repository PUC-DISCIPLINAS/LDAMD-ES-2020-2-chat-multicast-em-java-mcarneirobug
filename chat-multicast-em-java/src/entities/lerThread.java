package entities;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class lerThread implements Runnable  {
	private MulticastSocket mSocket;
	private InetAddress grupo;
	private int port;

	// constructor
	lerThread(MulticastSocket mSocket, InetAddress grupo, int port) {
		this.mSocket = mSocket;
		this.grupo = grupo;
		this.port = port;
	}

	public void run() {
		while (!Main.acabado) {

			byte[] buffer = new byte[1000];
			DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, grupo, port);
			String mensagem;
			try {

				mSocket.receive(datagram);
				mensagem = new String(buffer, 0, datagram.getLength(), "8859_1");

				if (!mensagem.startsWith(Main.getNome())) {
					System.out.println(mensagem);
				}

			} catch (IOException e) {
				System.out.println("Fechamento do socket!");
			}

		}
	}
}
