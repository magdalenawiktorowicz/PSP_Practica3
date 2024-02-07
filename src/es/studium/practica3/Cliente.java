package es.studium.practica3;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class Cliente extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField textFieldApuesta;
	JTextArea textAreaChat;
	JButton btnEnviar;
	private JScrollPane scrollPane;
	Font font1 = new Font("Segoe UI", Font.BOLD, 20);
	Font font2 = new Font("Segoe UI", Font.PLAIN, 14);

	static String nombreJugador;

	Socket socket;
	int puerto;
	DataInputStream input;
	DataOutputStream output;
	boolean repetir = true;

	public Cliente(Socket socket, String nombre) {
		this.socket = socket;
		nombreJugador = nombre;

		setResizable(false);
		setTitle("Juego");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 330);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(249, 250, 222));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		textAreaChat = new JTextArea();
		textAreaChat.setFont(font2);
		textAreaChat.setBackground(Color.white);
		textAreaChat.setEditable(false);
		textAreaChat.setBounds(10, 10, 416, 240);
		scrollPane = new JScrollPane(textAreaChat);
		scrollPane.setBounds(10, 10, 516, 223);
		contentPane.add(scrollPane);
		JLabel lblApuesta = new JLabel("Tu apuesta:");
		lblApuesta.setBounds(70, 245, 121, 35);
		lblApuesta.setFont(font1);
		contentPane.add(lblApuesta);
		textFieldApuesta = new JTextField();
		textFieldApuesta.setBounds(199, 245, 71, 35);
		textFieldApuesta.setColumns(15);
		textFieldApuesta.setFont(font2);
		contentPane.add(textFieldApuesta);
		btnEnviar = new JButton("Enviar");
		btnEnviar.addActionListener(this);
		btnEnviar.setBackground(new Color(210, 228, 247));
		btnEnviar.setFont(font1);
		btnEnviar.setBounds(304, 245, 121, 35);
		contentPane.add(btnEnviar);
	}

	public String getNombreJugador() {
		return nombreJugador;
	}

	public static void main(String[] args) throws Exception {
		int puerto = 44444;
		Socket socket = null;

		// mostrar el diálogo para obtener el nombre del juegador
		ClienteLogin loginDialog = new ClienteLogin(null);
		nombreJugador = loginDialog.getName();
		if (nombreJugador == null) {
			System.exit(0);
		}

		try {
			// crear un nuevo socket
			socket = new Socket("127.0.0.1", puerto);
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Imposible conectar con el servidor \n" + ex.getMessage(),
					"<<Mensaje de Error:1>>", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		// mostrar la pantalla del cliente
		Cliente cliente = new Cliente(socket, nombreJugador);
		cliente.setVisible(true);
		cliente.ejecutar();
	}

	// método para comunicarse con el servidor
	void ejecutar() {
		try {
			// un flujo para obtener datos y otro flujo para mandar datos
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			// mandar al servidor el nombre del jugador y un 0 para indicar que ha entrado
			// en el chat por primera vez
			output.writeUTF(this.getNombreJugador() + " " + 0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		String texto = "";
		// mientras sigue la comunicación abierta
		while (repetir) {
			try {
				// seguir leyendo del servidor
				texto = input.readUTF();
				// y actualizar el textArea de la ventana Cliente
				textAreaChat.setText(texto);
			} catch (IOException ex) {
				ex.printStackTrace();
				repetir = false;
			}
			// controlar la finalización del juego
			if (Servidor.gameIsOver || textAreaChat.getText().endsWith("GAME OVER")) {
				// deshabilitar el textField y el botón
				textFieldApuesta.setEnabled(false);
				btnEnviar.removeActionListener(this);
	            btnEnviar.setEnabled(false);
			}

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnEnviar && !socket.isClosed() && !Servidor.gameIsOver) {
			// controlar si la apuesta es un número
			int apuesta = validarApuesta(textFieldApuesta.getText());
			// si la función validarApuesta() devuelve un -1, input incorrecto
			if (apuesta == -1) {
				JOptionPane.showMessageDialog(null, "Número introducido es incorrecto.", "<<Mensaje de Error:3>>",
						JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					// si el número es correcto (1-100)
					// limpiar el textField
					textFieldApuesta.setText("");
					// mandar al servidor el nombre del jugador y el número
					output.writeUTF(nombreJugador + " " + apuesta);
					// deshabilitar el botón
					btnEnviar.setEnabled(false);
					// establecer un timer a 3 segundos
					Timer timer = new Timer(3000, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							// habilitar el botón
							btnEnviar.setEnabled(true);
						}
					});
					timer.setRepeats(false);
					// empezar el bloqueo del botón
					timer.start();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} else {
			try {
				input.close();
				output.close();
				socket.close();
				setVisible(false);
				dispose();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	private int validarApuesta(String text) {
		// establecer una expresión regular - un número entre 1-9, un número con dos dígitos o un 100
		String regex = "([1-9]|[1-9][0-9]|100)";
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(text);
		if ((!text.isBlank()) && matcher.matches()) {
			int numero = Integer.parseInt(text);
			if (numero >= 1 && numero <= 100) {
				return numero;
			}
		}
		// en el caso de un input incorrecto, devolver -1
		return -1;
	}

}
