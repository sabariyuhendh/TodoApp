package com.todo.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.model.Todo;
import com.todo.dao.TodoAppDAO;
import com.todo.util.DatabaseConnection;

import java.util.Date;

public class TodoAppGUI extends JFrame {
    private TodoAppDAO todoAppDAO;
    private JTable todoTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JCheckBox completedCheckBox;
    private JButton addButton, deleteButton, editButton, refreshButton;
    private JComboBox<String> categoryComboBox;

    public TodoAppGUI() {
        todoAppDAO = new TodoAppDAO();
        todoTable = new JTable();
        tableModel = new DefaultTableModel();
        initializeComponents();
        setupComponents();
        setupEventListeners();
        loadTodos();
    }

    private void initializeComponents() {
        setTitle("Todo App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        String[] columnNames = {"ID", "Title", "Description", "Completed", "Created At", "Updated At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        todoTable = new JTable(tableModel);
        todoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Inputs
        titleField = new JTextField(25);

        descriptionArea = new JTextArea(4, 25);
        descriptionArea.setEditable(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        completedCheckBox = new JCheckBox("Completed");

        // Buttons
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        editButton = new JButton("Edit");
        refreshButton = new JButton("Refresh");

        // Filter dropdown
        String[] categoryOptions = {"All", "Completed", "Pending"};
        categoryComboBox = new JComboBox<>(categoryOptions);
    }
    private void setupComponents() {
        setLayout(new BorderLayout());

        // Input panel for title, description, completed checkbox
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(completedCheckBox, gbc);

        // Button panel for Add, Update, Delete, Refresh
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Filter panel for filter label and combo box
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(categoryComboBox);

        // North panel to combine filter, input, and button panels
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(filterPanel, BorderLayout.NORTH);
        northPanel.add(inputPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(todoTable), BorderLayout.CENTER);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Select a todo to edit or delete:"));
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        addButton.addActionListener(e -> addTodo());
        editButton.addActionListener(e -> updateTodo());
        deleteButton.addActionListener(e -> deleteTodo());
        refreshButton.addActionListener(e -> refreshTodo());

        // ✅ Add this listener
        todoTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadSelectedTodo();
            }
        });
    }


    private void addTodo(){
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean completed = completedCheckBox.isSelected();
        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title or Description is empty!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Todo todo = new Todo(title,description);
            todo.setCompleted(completed);
            todoAppDAO.createtodo(todo);

            JOptionPane.showMessageDialog(this,"Todo added succesfully","Success",JOptionPane.INFORMATION_MESSAGE);
            loadTodos();
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error adding todo","Failure",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTodo() {
        int row = todoTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to update", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean completed = completedCheckBox.isSelected();

        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title or Description is empty!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Todo todo = todoAppDAO.getTodoById(id);
            if (todo != null) {
                todo.setTitle(title);
                todo.setDescription(description);
                todo.setCompleted(completed);
                todo.setUpdated_at(java.time.LocalDateTime.now());

                if (todoAppDAO.updateTodo(todo)) {
                    JOptionPane.showMessageDialog(this, "Todo updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTodos();
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteTodo(){
        int row = todoTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try{
            todoAppDAO.deleteTodo(id);
            JOptionPane.showMessageDialog(this,"Todo deleted successfully","Success",JOptionPane.INFORMATION_MESSAGE);

        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        loadTodos();
    }
    private void refreshTodo(){
        loadTodos();
    }
    private void loadTodos(){
        try {
            List<Todo> todos = todoAppDAO.getAllTodos();
            updateTable(todos);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading todos: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    private void updateTable(List<Todo> todos){
        tableModel.setRowCount(0);
        for(Todo todo : todos){
            Object[] row = {todo.getId(), todo.getTitle(), todo.getDescription(), todo.isCompleted(), todo.getCreated_at(), todo.getUpdated_at()};
            tableModel.addRow(row);
        }
    }
    private void loadSelectedTodo(){
        int row  = todoTable.getSelectedRow();
        if(row >= 0){
            String title = tableModel.getValueAt(row, 1).toString();
            String description = tableModel.getValueAt(row, 2).toString();
            boolean completed = Boolean.parseBoolean(tableModel.getValueAt(row, 3).toString());
            titleField.setText(title);
            descriptionArea.setText(description);
            completedCheckBox.setSelected(completed);
        }
    }
}
