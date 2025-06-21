package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationUsingSwing extends JFrame {

    private JTextField nameField, emailField;
    private JPasswordField passwordField;
    private JRadioButton maleBtn, femaleBtn;
    private JButton registerBtn;

    public RegistrationUsingSwing() {
        setTitle("Registration Form");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name Label and Field
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Name:"), gbc);
        nameField = new JTextField();
        gbc.gridx = 1;
        add(nameField, gbc);

        // Email Label and Field
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Email:"), gbc);
        emailField = new JTextField();
        gbc.gridx = 1;
        add(emailField, gbc);

        // Password Label and Field
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField();
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Gender:"), gbc);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        maleBtn = new JRadioButton("Male");
        femaleBtn = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleBtn);
        genderGroup.add(femaleBtn);
        genderPanel.add(maleBtn);
        genderPanel.add(femaleBtn);
        gbc.gridx = 1;
        add(genderPanel, gbc);

        // Register Button
        registerBtn = new JButton("Register");
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(registerBtn, gbc);

        // Register button action
        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String gender = maleBtn.isSelected() ? "Male" : (femaleBtn.isSelected() ? "Female" : "Not Selected");

                JOptionPane.showMessageDialog(RegistrationUsingSwing.this,
                        "Name: " + name + "\nEmail: " + email + "\nGender: " + gender,
                        "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RegistrationUsingSwing().setVisible(true);
        });
    }
}