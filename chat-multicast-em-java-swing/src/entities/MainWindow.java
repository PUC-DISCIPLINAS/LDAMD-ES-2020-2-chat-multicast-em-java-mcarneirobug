package entities;

import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextPane;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MainWindow {

	private JFrame frmMulticastChat;
	private JTextField TxtNome;
	private JTextField TxtMensagem;
	private JTextField TxtIP;
	private JTextField TxtPorta;
	private JTextPane TxtChatPane;
	private JButton btnEnviarMensagem;

	private MulticastThread mThread;

	public MainWindow() {
		inicializar();
	}

	private void inicializar() {

		setfrmMulticastChat(new JFrame());
		getfrmMulticastChat().setTitle("Multicast - Chat");
		getfrmMulticastChat().setResizable(false);
		getfrmMulticastChat().setBounds(100, 100, 440, 525);
		getfrmMulticastChat().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getfrmMulticastChat().getContentPane().setLayout(null);

		JPanel nomePanel = new JPanel();
		nomePanel.setBounds(146, 0, 398, 30);
		getfrmMulticastChat().getContentPane().add(nomePanel);
		nomePanel.setLayout(null);

		JLabel labelNome = new JLabel("Nome:");
		labelNome.setBounds(150, 8, 99, 14);
		nomePanel.add(labelNome);

		TxtNome = new JTextField();
		TxtNome.setText("Cliente");
		TxtNome.setBounds(190, 5, 86, 25);
		nomePanel.add(TxtNome);
		TxtNome.setColumns(10);

		JPanel janelaChat = new JPanel();
		janelaChat.setBounds(0, 41, 544, 287);
		getfrmMulticastChat().getContentPane().add(janelaChat);
		janelaChat.setLayout(new BorderLayout(0, 0));

		TxtChatPane = new JTextPane();
		TxtChatPane.setEditable(false);
		TxtChatPane.setBackground(new Color(119, 122, 122));
		janelaChat.add(TxtChatPane, BorderLayout.CENTER);

		JPanel mensagemPanel = new JPanel();
		mensagemPanel.setBounds(0, 339, 544, 49);
		getfrmMulticastChat().getContentPane().add(mensagemPanel);
		mensagemPanel.setLayout(null);

		TxtMensagem = new JTextField();
		TxtMensagem.setBounds(0, 11, 473, 20);
		mensagemPanel.add(TxtMensagem);
		TxtMensagem.setColumns(10);

		JPanel ipPortaPanel = new JPanel();
		ipPortaPanel.setBounds(10, 387, 217, 90);
		getfrmMulticastChat().getContentPane().add(ipPortaPanel);
		ipPortaPanel.setLayout(null);

		TxtIP = new JTextField();
		TxtIP.setText("239.0.0.0");
		TxtIP.setBounds(0, 40, 86, 20);
		ipPortaPanel.add(TxtIP);
		TxtIP.setColumns(10);

		TxtPorta = new JTextField();
		TxtPorta.setText("1234");
		TxtPorta.setBounds(121, 40, 86, 20);
		ipPortaPanel.add(TxtPorta);
		TxtPorta.setColumns(10);

		JLabel ipLabel = new JLabel("IP:");
		ipLabel.setBounds(0, 15, 86, 14);
		ipPortaPanel.add(ipLabel);

		JLabel portaLabel = new JLabel("Porta:");
		portaLabel.setBounds(121, 15, 46, 14);
		ipPortaPanel.add(portaLabel);

		JPanel btnPanel = new JPanel();
		btnPanel.setBounds(238, 388, 306, 107);
		getfrmMulticastChat().getContentPane().add(btnPanel);
		btnPanel.setLayout(null);

		JButton btnEntrarChat = new JButton("ENTRAR CHAT");
		btnEntrarChat.setBounds(0, 11, 80, 27);
		btnEntrarChat.setBackground(new Color(255, 255, 255));
		btnPanel.add(btnEntrarChat);
		btnEntrarChat.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				entrarChat(evt);
			}
		});

		btnEnviarMensagem = new JButton("ENVIAR");
		btnEnviarMensagem.setBounds(90, 11, 80, 27);
		btnEnviarMensagem.setBackground(new Color(255, 255, 255));
		btnPanel.add(btnEnviarMensagem);
		btnEnviarMensagem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				enviarMensagem(evt);
			}
		});
		btnEnviarMensagem.setEnabled(false);

		JButton btnSairChat = new JButton("SAIR DO CHAT");
		btnSairChat.setBounds(0, 50, 170, 27);
		btnSairChat.setBackground(new Color(255, 255, 255));
		btnPanel.add(btnSairChat);
		btnSairChat.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				sairChat(evt);
			}
		});
	}

	/**
	 * Getter and Setter
	 * 
	 * @return
	 */
	
	public JFrame getfrmMulticastChat() {
		return frmMulticastChat;
	}

	public void setfrmMulticastChat(JFrame frmMulticastChat) {
		this.frmMulticastChat = frmMulticastChat;
	}

	private void entrarChat(ActionEvent evt) {
		btnEnviarMensagem.setEnabled(true);
		String ip = TxtIP.getText();
		int porta = Integer.parseInt(TxtPorta.getText());
		try {
			mThread = new MulticastThread(porta, ip, this);
			new Thread(mThread).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void enviarMensagem(ActionEvent evt) {
		String mensagem = "\n" + TxtNome.getText() + ": " + TxtMensagem.getText();
		mThread.enviar(mensagem);
		TxtMensagem.setText(""); // assim que enviar mensagem ficar em branco
	}

	/**
	 * Assim que sair do chat btnEnviarMensagem 
	 * fica desabilitado
	 * @param evt
	 */
	private void sairChat(ActionEvent evt) {
		btnEnviarMensagem.setEnabled(false);
		mThread.parar();
	}

	public void receberMensagem(String s) {
		TxtChatPane.setText(TxtChatPane.getText() + s);
	}
}

class MulticastThread implements Runnable {

	private final MulticastSocket socket;
	private volatile boolean executando = true;
	private final InetAddress ip;
	private final int porta;
	private final MainWindow messenger;

	public MulticastThread(int porta, String ip, MainWindow mf) throws IOException {
		this.socket = new MulticastSocket(porta);
		this.ip = InetAddress.getByName(ip);
		this.porta = porta;
		this.socket.joinGroup(this.ip);
		this.messenger = mf;
	}

	public void enviar(String mensagem) {
		try {
			byte[] buffer = mensagem.getBytes();
			DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, ip, porta);
			socket.send(datagram);
		} catch (IOException e) {
			System.out.println("Erro: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void parar() {
		try {
			socket.leaveGroup(ip);
			executando = false;
		} catch (IOException e) {
			System.out.println("Erro: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		DatagramPacket datagram;
		while (executando) {
			try {
				byte[] buffer = new byte[1000];
				datagram = new DatagramPacket(buffer, buffer.length);
				socket.receive(datagram);
				String mensagem = new String(datagram.getData(), "8859_1");
				messenger.receberMensagem(mensagem);
			} catch (IOException e) {
				System.out.println("Erro: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
