package es.studium.practica3;

import java.awt.Color;
import java.awt.Font;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Servidor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblNumeroSecreto;
	private JTextField textFieldNumeroSecreto;
	static JTextArea textAreaChat;
	static JTextField mensaje = new JTextField("");
	static JTextField mensaje2 = new JTextField("");
	private JScrollPane scrollPane;

	Font font1 = new Font("Segoe UI", Font.BOLD, 20);
	Font font2 = new Font("Segoe UI", Font.PLAIN, 14);

	static ServerSocket servidor;
	static final int PUERTO = 44444;
	static int CONEXIONES = 0;
	// establecer máximo 10 conexiones a clientes
	static Socket[] tabla = new Socket[10];
	// obtener un número aleatorio entre 1 y 100
	static Random random = new Random();
	static final int numeroSecreto = random.nextInt(100)+1;
	
	static boolean gameIsOver = false;

	public Servidor() {
		setResizable(false);
		setTitle("Juego - Servidor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 330);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(249, 250, 222));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 63, 516, 220);
		contentPane.add(scrollPane);

		textAreaChat = new JTextArea();
		textAreaChat.setFont(font2);
		textAreaChat.setText("Bienvenido al juego! Intenta adivinar el número secreto!\nEscribe un número entre 1 y 100.");
		scrollPane.setViewportView(textAreaChat);
		textAreaChat.setBackground(Color.white);
		textAreaChat.setEditable(false);

		lblNumeroSecreto = new JLabel("Número secreto:");
		lblNumeroSecreto.setBounds(105, 8, 167, 35);
		lblNumeroSecreto.setFont(font1);
		contentPane.add(lblNumeroSecreto);

		textFieldNumeroSecreto = new JTextField();
		textFieldNumeroSecreto.setBackground(new Color(219, 243, 255));
		textFieldNumeroSecreto.setEditable(false);
		textFieldNumeroSecreto.setBounds(290, 10, 71, 35);
		textFieldNumeroSecreto.setColumns(15);
		textFieldNumeroSecreto.setFont(font1);
		textFieldNumeroSecreto.setText(numeroSecreto + "");
		contentPane.add(textFieldNumeroSecreto);
	}

	public static void main(String[] args) throws Exception {
		// abrir un servidor
		servidor = new ServerSocket(PUERTO);
		System.out.println("Servidor iniciado...");
		// crear un objeto de la clase Servidor
		Servidor servidorPantalla = new Servidor();
		servidorPantalla.setVisible(true);

		while (CONEXIONES < 10) {
			Socket socket;
			try {
				// el servidor espera al cliente y cuando se conecta, crea un socket
				socket = servidor.accept();
			} catch (SocketException socketEx) {
				break;
			}
			// añadir un nuevo socket a la tabla
			tabla[CONEXIONES] = socket;
			CONEXIONES++;
			// lanzar el hilo por cada cliente conectado
			HiloServidor hilo = new HiloServidor(socket, numeroSecreto);
			hilo.start();
		}
	}
}
