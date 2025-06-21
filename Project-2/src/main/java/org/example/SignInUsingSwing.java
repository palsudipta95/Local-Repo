package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignInUsingSwing extends JFrame {

    JTextField usernameField; // Package-private for testing
    JPasswordField passwordField; // Package-private for testing
    private JButton signInButton;
    JButton registerButton; // Package-private for testing

    public SignInUsingSwing() {
        setTitle("Sign In");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Name:"), gbc);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        add(usernameField, gbc);

        // Password label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Sign In button
        signInButton = new JButton("Sign In");
        gbc.gridx = 0;
        gbc.gridy = 2;
        //gbc.gridwidth = 2; // Remove gridwidth to make space for register button
        add(signInButton, gbc);

        // Register button
        this.registerButton = new JButton("Register"); // Made it a field
        gbc.gridx = 1;
        gbc.gridy = 2;
        //gbc.gridwidth = 1; // Explicitly set gridwidth for register button
        add(this.registerButton, gbc);

        // Action listener for Sign In button
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        // Action listener for Register button
        this.registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open RegistrationForm window
                new RegistrationForm().setVisible(true);
            }
        });
    }

    // Made package-private for testing
    void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DBConnection.getConnection()) {
            // Modified SQL to fetch user ID and name along with checking credentials
            String sql = "SELECT id, name, email, department FROM users WHERE name = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("id");
                        String name = rs.getString("name");
                        String email = rs.getString("email");
                        String department = rs.getString("department");
                        JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // Close the sign-in window
                        // Pass user details to the Dashboard
                        new Dashboard(userId, name, email, department).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // For debugging purposes
        }
    }

    // Dashboard class with menu bar
    class Dashboard extends JFrame {
        private int currentUserId;
        private String currentUserName;
        private String currentUserEmail;
        private String currentUserDepartment;

        public Dashboard(int userId, String userName, String userEmail, String userDepartment) {
            this.currentUserId = userId;
            this.currentUserName = userName;
            this.currentUserEmail = userEmail;
            this.currentUserDepartment = userDepartment;

            setTitle("Dashboard");
            setSize(500, 400); // Adjusted size for menu bar
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            JLabel welcomeLabel = new JLabel("Welcome, " + currentUserName + "!", SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
            add(welcomeLabel, BorderLayout.NORTH);

            // Menu Bar
            JMenuBar menuBar = new JMenuBar();

            // User Menu
            JMenu userMenu = new JMenu("User Actions");
            menuBar.add(userMenu);

            // Menu Items
            JMenuItem viewAllUsersItem = new JMenuItem("View All Users");
            viewAllUsersItem.addActionListener(e -> new UserListView().setVisible(true));
            userMenu.add(viewAllUsersItem);

            JMenuItem viewProfileItem = new JMenuItem("View My Profile");
            viewProfileItem.addActionListener(e -> {
                // Fetches the latest data for the current user and displays it
                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "SELECT name, email, department FROM users WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, currentUserId);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                new ViewUserDialog(Dashboard.this, currentUserId, rs.getString("name"), rs.getString("email"), rs.getString("department")).setVisible(true);
                            } else {
                                JOptionPane.showMessageDialog(Dashboard.this, "Could not find your profile information.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(Dashboard.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            userMenu.add(viewProfileItem);

            JMenuItem editProfileItem = new JMenuItem("Edit My Profile");
            editProfileItem.addActionListener(e -> {

                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "SELECT name, email, department FROM users WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, currentUserId);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {

                                EditUserDialog editDialog = new EditUserDialog(Dashboard.this, null, currentUserId, rs.getString("name"), rs.getString("email"), rs.getString("department"), -1);
                                editDialog.setVisible(true); // This is a modal dialog, code below will run after it's closed.

                                if (editDialog.getChangesSavedSuccessfully()) {
                                    // Re-fetch user data to update Dashboard's state and welcome label
                                    try (Connection updateConn = DBConnection.getConnection()) {
                                        String updateSql = "SELECT name, email, department FROM users WHERE id = ?";
                                        try (PreparedStatement updateStmt = updateConn.prepareStatement(updateSql)) {
                                            updateStmt.setInt(1, currentUserId);
                                            try (ResultSet updatedRs = updateStmt.executeQuery()) {
                                                if (updatedRs.next()) {
                                                    currentUserName = updatedRs.getString("name");
                                                    currentUserEmail = updatedRs.getString("email"); // Keep email and dept consistent too
                                                    currentUserDepartment = updatedRs.getString("department");
                                                    welcomeLabel.setText("Welcome, " + currentUserName + "!");
                                                }
                                            }
                                        }
                                    } catch (SQLException refreshEx) {
                                        JOptionPane.showMessageDialog(Dashboard.this, "Error refreshing user data: " + refreshEx.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(Dashboard.this, "Could not find your profile information for editing.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(Dashboard.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            userMenu.add(editProfileItem);

            setJMenuBar(menuBar);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SignInUsingSwing().setVisible(true);
        });
    }
}