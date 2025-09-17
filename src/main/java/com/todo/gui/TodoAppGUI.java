package com.todo.gui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;
import com.todo.dao.TodoAppDAO;
public class TodoAppGUI extends JFrame {
    private TodoAppDAO todoAppDAO;
    private JTable todoTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JCheckBox completedCheckBox;
    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton refreshButton;
    private JComboBox<String> categoryComboBox;

    public  TodoAppGUI() {
        todoAppDAO = new TodoAppDAO();
        todoTable = new JTable();
        tableModel = new DefaultTableModel();
        initializeComponents();
        setupComponents();
    }
    private  void initializeComponents() {
        setTitle("Todo App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 720);
        //setResizable(false);
        setLocationRelativeTo(null);

        String[] columnNames = {"ID","Title","Description","Completed","Created At","Updated At"};
        tableModel = new DefaultTableModel(columnNames,0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        todoTable = new JTable(tableModel);
        todoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        todoTable.getSelectionModel().addListSelectionListener(
            (e) ->{
                if(!e.getValueIsAdjusting()) {
//                    loadSelectedtodo();
                }
            }
        );
        titleField = new JTextField(20);
        descriptionArea = new JTextArea(3,2);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        completedCheckBox = new JCheckBox("Completed");
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        editButton = new JButton("Edit");
        refreshButton = new JButton("Refresh");
        String[] categoryOptions = {"All","Completed","Pending"};
        categoryComboBox = new JComboBox<>(categoryOptions);
        categoryComboBox.addActionListener((e)->{
            //categoryTodos;
        });


    }
    private void setupComponents() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Title"),gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(titleField, gbc);
        add(inputPanel, BorderLayout.NORTH);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Description"),gbc);
        gbc.gridx = 1;
        inputPanel.add(descriptionArea,gbc);
        add(inputPanel, BorderLayout.NORTH);
    }
}
