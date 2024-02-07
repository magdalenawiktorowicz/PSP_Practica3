package es.studium.practica3;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class ClienteLogin extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	Font font1 = new Font("Segoe UI", Font.BOLD, 20);
	Font font2 = new Font("Segoe UI", Font.PLAIN, 16);
	private JTextField textFieldNombre;
	private JButton btnAceptar;
	String name;

	public ClienteLogin(Frame parent) {
		super(parent, "Login", true);
		setResizable(false);
		setTitle("Juego");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parent);
		setBounds(100,100, 310, 210);
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(null);
		contentPane.setBackground(new Color(249, 250, 222));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		JLabel lblNombre = new JLabel("Nombre:");
		lblNombre.setBounds(29, 38, 85, 36);
		lblNombre.setFont(font1);

		textFieldNombre = new JTextField();
		textFieldNombre.setBounds(136, 39, 137, 35);
		textFieldNombre.setColumns(15);
		textFieldNombre.setFont(font2);

		btnAceptar = new JButton("Aceptar");
		btnAceptar.setBounds(74, 104, 137, 35);
		btnAceptar.addActionListener(this);
		btnAceptar.setBackground(new Color(210, 228, 247));
		btnAceptar.setFont(font1);
		contentPane.add(lblNombre);
		contentPane.add(textFieldNombre);
		contentPane.add(btnAceptar);
		setContentPane(contentPane);
		setVisible(true);
	}

	public String getName() {
		return name;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAceptar) {
			if ((textFieldNombre.getText() != null) && !(textFieldNombre.getText().isBlank())) {
				// obtener el nombre del jugador
				name = textFieldNombre.getText();
				dispose();
				
			} else {
				// marcar el campo que el usuario tiene que rellenar
				Border border = BorderFactory.createLineBorder(Color.red, 2);
				textFieldNombre.setBorder(border);

			}
		}
		
	}

}
