package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditUserDialog extends JDialog {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField departmentField;
    private JButton saveButton;
    private JButton cancelButton;

    private UserListView userListViewInstance; // Might be null if called from Dashboard
    private int userId;
    private int rowIndex; // Will be -1 if not from UserListView
    private JFrame parentFrame; // To show messages correctly
    private boolean changesSavedSuccessfully = false; // Flag to indicate save status

    public EditUserDialog(JFrame parent, UserListView userListView, int userId, String currentName, String currentEmail, String currentDepartment, int rowIndex) {
        super(parent, "Edit User", true); // true for modal
        this.parentFrame = parent;
        this.userListViewInstance = userListView;
        this.userId = userId;
        this.rowIndex = rowIndex;

        setLayout(new BorderLayout());
        setSize(400, 200);
        setLocationRelativeTo(parent);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5)); // rows, cols, hgap, vgap
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField(currentName);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField(currentEmail);
        formPanel.add(emailField);

        formPanel.add(new JLabel("Department:"));
        departmentField = new JTextField(currentDepartment);
        formPanel.add(departmentField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dialog
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveChanges() {
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newDepartment = departmentField.getText().trim();

        if (newName.isEmpty() || newEmail.isEmpty() || newDepartment.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call method in UserListView to update data if available
        if (userListViewInstance != null) {
            userListViewInstance.updateUser(userId, newName, newEmail, newDepartment, rowIndex);
        } else {
            // Called from Dashboard, perform direct DB update
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE users SET name = ?, email = ?, department = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, newName);
                    pstmt.setString(2, newEmail);
                    pstmt.setString(3, newDepartment);
                    pstmt.setInt(4, userId);

                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(this, "Profile updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        this.changesSavedSuccessfully = true;
                        // If called from Dashboard, parentFrame is Dashboard. It will check getChangesSavedSuccessfully()
                    } else {
                        JOptionPane.showMessageDialog(this, "User not found or could not be updated.", "Update Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error during update: " + ex.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred during update: " + ex.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        dispose(); // Close the dialog
    }

    public boolean getChangesSavedSuccessfully() {
        return changesSavedSuccessfully;
    }
}