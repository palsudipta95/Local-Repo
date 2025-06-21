package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer; // Added import
import java.awt.*;
import java.sql.*;

public class UserListView extends JFrame {

    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserListView() {
        setTitle("Registered Users");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();
        userTable = new JTable(tableModel);

        // Define table columns
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Email");
        tableModel.addColumn("Department");
        tableModel.addColumn("Edit");
        tableModel.addColumn("Delete");

        fetchUserData();

        // Set custom renderer and editor for button columns
        userTable.getColumn("Edit").setCellRenderer(new ButtonRenderer());
        userTable.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(), "Edit", userTable, this));
        userTable.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        userTable.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), "Delete", userTable, this));

        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private void fetchUserData() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, name, email, department FROM users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String department = rs.getString("department");

                tableModel.addRow(new Object[]{id, name, email, department, "Edit", "Delete"});
            }

        } catch (SQLException ex) {
            // ex.printStackTrace(); // Removed
            JOptionPane.showMessageDialog(this, "Database error while loading user data: " + ex.getMessage(), "Data Load Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // ex.printStackTrace(); // Removed
            JOptionPane.showMessageDialog(this, "An unexpected error occurred while loading user data: " + ex.getMessage(), "Data Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateUser(int userId, String newName, String newEmail, String newDepartment, int rowIndex) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE users SET name = ?, email = ?, department = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newName);
                pstmt.setString(2, newEmail);
                pstmt.setString(3, newDepartment);
                pstmt.setInt(4, userId);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    // Update the table model
                    tableModel.setValueAt(newName, rowIndex, 1); // Column 1 for Name
                    tableModel.setValueAt(newEmail, rowIndex, 2); // Column 2 for Email
                    tableModel.setValueAt(newDepartment, rowIndex, 3); // Column 3 for Department
                    JOptionPane.showMessageDialog(this, "User updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "User not found or could not be updated.", "Update Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            // ex.printStackTrace(); // Removed
            JOptionPane.showMessageDialog(this, "Database error during update: " + ex.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // ex.printStackTrace(); // Removed
            JOptionPane.showMessageDialog(this, "An unexpected error occurred during update: " + ex.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteUser(int userId, int rowIndex) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    // Delay removal to allow cell editor to finish
                    SwingUtilities.invokeLater(() -> {
                        tableModel.removeRow(rowIndex);
                    });
                    JOptionPane.showMessageDialog(this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "User not found or could not be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error during deletion: " + ex.getMessage(), "Deletion Failed", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred during deletion: " + ex.getMessage(), "Deletion Failed", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserListView::new);
    }
}

// Custom TableCellRenderer for displaying buttons
class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

// Custom TableCellEditor for handling button clicks
class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private int row;
    private JTable table;
    private UserListView userListViewInstance;

    public ButtonEditor(JCheckBox checkBox, String actionCommand, JTable table, UserListView userListViewInstance) {
        super(checkBox);
        this.table = table;
        this.userListViewInstance = userListViewInstance;
        button = new JButton();
        button.setOpaque(true);
        button.setActionCommand(actionCommand);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row; // Keep track of the current row being edited
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            String actionCommand = button.getActionCommand();
            if ("Edit".equals(actionCommand)) {
                // Retrieve current data for the user
                Object idObj = table.getModel().getValueAt(row, 0);
                String currentName = table.getModel().getValueAt(row, 1).toString();
                String currentEmail = table.getModel().getValueAt(row, 2).toString();
                String currentDepartment = table.getModel().getValueAt(row, 3).toString();

                if (idObj != null) {
                    try {
                        int userId = Integer.parseInt(idObj.toString());
                        // userListViewInstance is the UserListView JFrame, which can be parent
                        EditUserDialog editDialog = new EditUserDialog(userListViewInstance, userListViewInstance, userId, currentName, currentEmail, currentDepartment, row);
                        editDialog.setVisible(true);
                    } catch (NumberFormatException ex) {
                        // System.err.println("Error parsing user ID for edit: " + idObj); // Removed
                        JOptionPane.showMessageDialog(button, "Invalid user ID format for editing: " + idObj, "ID Format Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(button, "Could not determine user ID for editing. The ID is missing.", "Missing ID Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if ("Delete".equals(actionCommand)) {
                // Retrieve the ID of the user in the selected row.
                Object idObj = table.getModel().getValueAt(row, 0); // Assuming ID is in the first column
                if (idObj != null) {
                    try {
                        int userId = Integer.parseInt(idObj.toString());
                        // Show a confirmation dialog
                        int confirmation = JOptionPane.showConfirmDialog(
                                button, // Parent component
                                "Are you sure you want to delete user ID: " + userId + "?",
                                "Confirm Deletion",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);

                        if (confirmation == JOptionPane.YES_OPTION) {
                            userListViewInstance.deleteUser(userId, row);
                        }
                    } catch (NumberFormatException ex) {
                        // System.err.println("Error parsing user ID: " + idObj); // Removed
                        JOptionPane.showMessageDialog(button, "Invalid user ID format for deletion: " + idObj, "ID Format Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(button, "Could not determine user ID for deletion. The ID is missing.", "Missing ID Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}