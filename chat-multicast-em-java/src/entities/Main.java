package entities;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Scanner;

public class Main {
	private static final String SAIR = "Sair";
	private static String nome;
	static volatile boolean acabado = false;
	static MulticastSocket mSocket = null;

	public static void main(String[] args) {

		try (Scanner sc = new Scanner(System.in)) {

			System.out.println("IP: 239.0.0.0");
			// ip fixo
			InetAddress grupo = InetAddress.getByName("239.0.0.0");

			System.out.println("PORT: 1234");
			// porta fixa
			int port = Integer.parseInt("1234");

			System.out.print("\nEntre com seu nome: ");
			nome = sc.nextLine();

			mSocket = new MulticastSocket(port);
			mSocket.joinGroup(grupo);

			Thread t = new Thread(new lerThread(mSocket, grupo, port));
			t.start();

			// envia para o grupo atual
			System.out.println("\nComece a digitar suas mensagens...\n");

			while (true) {
				String mensagem = sc.nextLine();
				// se o usuário digitar Sair fecha o socket
				if (mensagem.equalsIgnoreCase(Main.SAIR)) {
					acabado = true;
					mSocket.leaveGroup(grupo);
					mSocket.close();
					break;
				}

				mensagem = nome + ": " + mensagem;
				byte[] buffer = mensagem.getBytes();
				DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, grupo, port);
				mSocket.send(datagram);
			}
		} catch (SocketException socketEx) {
			System.out.println("Erro: Na criação do socket!" + socketEx.getMessage());
			socketEx.printStackTrace();
		} catch (IOException e) {
			System.out.println("Erro: Leitura/Escrita para o socket!" + e.getMessage());
			e.printStackTrace();
		}
	}

	public static String getNome() {
		return nome;
	}

	public static void setNome(String nome) {
		Main.nome = nome;
	}
}
