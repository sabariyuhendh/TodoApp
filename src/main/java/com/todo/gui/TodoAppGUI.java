package com.todo.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

import com.model.Todo;
import com.todo.dao.TodoAppDAO;

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
    }

    private void initializeComponents() {
        setTitle("Todo App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        String[] columnNames = {"ID","Title","Description","Completed","Created At","Updated At"};
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
        setLayout(new BorderLayout(10, 10));

        // Top Panel (Inputs + Buttons + Filters)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        // Input panel with GridBagLayout
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        inputPanel.add(completedCheckBox, gbc);

        topPanel.add(inputPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryComboBox);

        topPanel.add(filterPanel, BorderLayout.NORTH);

        add(topPanel, BorderLayout.NORTH);

        // Table in center
        JScrollPane tableScrollPane = new JScrollPane(todoTable);
        add(tableScrollPane, BorderLayout.CENTER);

        add(new JScrollPane(todoTable), BorderLayout.CENTER);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        statusPanel.add(new JLabel("Select"));
        add(statusPanel, BorderLayout.SOUTH);

    }
    private void setupEventListeners() {
        addButton.addActionListener((e) -> addTodo());
        editButton.addActionListener((e) -> editTodo());
        deleteButton.addActionListener((e -> deleteTodo()));
        refreshButton.addActionListener((e -> refreshTodo()));
    }
    private void addTodo() {
        String title = titleField.getText();
        String description = descriptionArea.getText();
    }
    private void deleteTodo() {

    }
    private void editTodo() {

    }
    private void refreshTodo() {

    }
    private void loadTodo() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
       // List<Todo> todos = todoAppDAO.getTodos();
    }
}
