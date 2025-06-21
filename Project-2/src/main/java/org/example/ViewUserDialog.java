package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewUserDialog extends JDialog {

    public ViewUserDialog(JFrame parent, int userId, String name, String email, String department) {
        super(parent, "User Details", true); // true for modal
        setLayout(new BorderLayout());
        setSize(350, 250);
        setLocationRelativeTo(parent);

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        detailsPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        JTextField idField = new JTextField(String.valueOf(userId));
        idField.setEditable(false);
        idField.setColumns(15);
        detailsPanel.add(idField, gbc);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        detailsPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(name);
        nameField.setEditable(false);
        nameField.setColumns(15);
        detailsPanel.add(nameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        detailsPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(email);
        emailField.setEditable(false);
        emailField.setColumns(15);
        detailsPanel.add(emailField, gbc);

        // Department
        gbc.gridx = 0;
        gbc.gridy = 3;
        detailsPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        JTextField departmentField = new JTextField(department);
        departmentField.setEditable(false);
        departmentField.setColumns(15);
        detailsPanel.add(departmentField, gbc);

        add(detailsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dialog
            }
        });
        buttonPanel.add(okButton);
        add(buttonPanel, BorderLayout.SOUTH);
        // setVisible(true); // Visibility should be set by the caller
    }
}