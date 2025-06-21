package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistrationForm extends JFrame {

    JTextField IDField, nameField, emailField, usernameField; // Package-private for testing
    JPasswordField passwordField; // Package-private for testing
    JComboBox<String> departmentCombo; // Package-private for testing
    JButton signInBtn; // Package-private for testing

    public RegistrationForm() {
        setTitle("User Registration");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("ID:"), gbc);

        gbc.gridx = 1;
        IDField = new JTextField(20);
        panel.add(IDField, gbc);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        // Department dropdown
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Department:"), gbc);

        gbc.gridx = 1;
        String[] departments = {"CSE", "EEE", "BBA", "Civil", "Textile"};
        departmentCombo = new JComboBox<>(departments);
        panel.add(departmentCombo, gbc);

        // Register Button
        gbc.gridx = 0; // Changed gridx to 0 to make space for Sign In button
        gbc.gridy = 6;
        JButton registerBtn = new JButton("Register");
        panel.add(registerBtn, gbc);

        // Sign In Button
        gbc.gridx = 1;
        gbc.gridy = 6;
        this.signInBtn = new JButton("Sign In"); // Made it a field
        panel.add(this.signInBtn, gbc);

        // Button listener for Register button
        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        // Button listener for Sign In button
        this.signInBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close current registration form
                new SignInUsingSwing().setVisible(true); // Open sign-in form
            }
        });

        add(panel);
        setVisible(true);
    }

    // Made package-private for testing
    void registerUser() {
        String name = nameField.getText();
        String id = IDField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = String.valueOf(passwordField.getPassword());
        String department = (String) departmentCombo.getSelectedItem();

        if (name.isEmpty() || id.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || department == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO users (name, id, username, email, password, department) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, id);
            stmt.setString(3, username);
            stmt.setString(4, email);
            stmt.setString(5, password);
            stmt.setString(6, department);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful!");
            clearFields();
        } catch (SQLIntegrityConstraintViolationException dup) {
            JOptionPane.showMessageDialog(this, "Email already registered.");
        } catch (Exception ex) {
            ex.printStackTrace();  // TEMP for debugging - remove in production
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage());
        }

    }

    // Made package-private for testing
    void clearFields() {
        nameField.setText("");
        IDField.setText("");
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        departmentCombo.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegistrationForm::new);
    }
}