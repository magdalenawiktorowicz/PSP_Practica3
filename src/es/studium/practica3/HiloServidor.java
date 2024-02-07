package es.studium.practica3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HiloServidor extends Thread {
	DataInputStream input;
	DataOutputStream output;
	Socket socket;
	boolean fin = false;
	int numeroSecreto;

	public HiloServidor(Socket socket, int numeroSecreto) {
		this.socket = socket;
		this.numeroSecreto = numeroSecreto;
		try {
			// obtener un flujo de entrada
			input = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void run() {
		// enviar los mensajes anteriores al nuevo cliente
		String texto = Servidor.textAreaChat.getText();
		mandarContenidoChat(texto);
		// mandar apuestas desde el Cliente al Servidor	
		while (!fin && !socket.isClosed()) {
			String cadena = "";
			try {
				cadena = input.readUTF(); // datos obtenidos del cliente
				String[] nombreYapuesta = cadena.split(" ");
				String nombreJugador = nombreYapuesta[0];
				int apuesta = Integer.parseInt(nombreYapuesta[1]);
				if (apuesta == 0) {
					// nuevo jugador
					Servidor.textAreaChat.append("\n" + nombreJugador + " ha entrado en el juego.");
				} else {
					// lógica del programa
					// si alguno adivina el número secreto...
					if (apuesta == numeroSecreto) {
						Servidor.textAreaChat.append("\n" + nombreJugador + " HA GANADO!\nEl número secreto fue: " + numeroSecreto + "\nGAME OVER");
						fin = true;
						Servidor.gameIsOver = true;
					} else if (apuesta > numeroSecreto) {
						// si el número secreto es menor
						Servidor.textAreaChat.append("\n" + nombreJugador + " piensa que el número es el " + apuesta
								+ ", pero el número es menor a " + apuesta + ".");
					} else {
						// si el número secreto es mayor
						Servidor.textAreaChat.append("\n" + nombreJugador + " piensa que el número es el " + apuesta
								+ ", pero el número es mayor a " + apuesta + ".");
					}
				}
				texto = Servidor.textAreaChat.getText();
				mandarContenidoChat(texto);

			} catch (Exception ex) {
				ex.printStackTrace();
				fin = true;
			}
		}
		if (Servidor.gameIsOver) {
			try {
				output.close();
				input.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void mandarContenidoChat(String texto) {
		for (int i = 0; i < Servidor.CONEXIONES; i++) {
			Socket socket = Servidor.tabla[i];
			try {
				// mandar el resultado de la apuesta desde el cliente a todos los demás
				output = new DataOutputStream(socket.getOutputStream());
				output.writeUTF(texto);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
