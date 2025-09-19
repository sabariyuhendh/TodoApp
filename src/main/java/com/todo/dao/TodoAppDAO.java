package com.todo.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.model.Todo;
import com.todo.util.DatabaseConnection;

public class TodoAppDAO {

    private static final String SELECT_ALL_TODOS = "SELECT * FROM todos ORDER BY created_at DESC";
    private static final String INSERT_TODO = "INSERT INTO todos (title, description, completed, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_TODO_BY_ID = "SELECT * FROM todos WHERE id = ?";
    private static final String UPDATE_TODO = "UPDATE todos SET title = ?, description = ?, completed = ?, updated_at = ? WHERE id = ?";
    private  static final String DELETE_TODO = "DELETE FROM todos WHERE id = ?";
    // Create a New Todo
    public int createtodo(Todo todo) throws SQLException {
        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_TODO, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, todo.getTitle());
            stmt.setString(2, todo.getDescription());
            stmt.setBoolean(3, todo.isCompleted());
            stmt.setTimestamp(4, Timestamp.valueOf(todo.getCreated_at()));
            stmt.setTimestamp(5, todo.getUpdated_at() != null ? Timestamp.valueOf(todo.getUpdated_at()) : null);

            int rows = stmt.executeUpdate();
            if (rows == 0) throw new SQLException("Creating todo failed, no rows affected.");

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean updateTodo(Todo todo) throws SQLException {
        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(UPDATE_TODO)
        ) {
            stmt.setString(1, todo.getTitle());
            stmt.setString(2, todo.getDescription());
            stmt.setBoolean(3, todo.isCompleted());
            stmt.setTimestamp(4, Timestamp.valueOf(todo.getUpdated_at()));
            stmt.setInt(5, todo.getId());

            return stmt.executeUpdate() > 0;
        }
    }
    public boolean deleteTodo(int id) throws SQLException{
        try(
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_TODO)
        ){
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }

    }
    private Todo getTodoRow(ResultSet res) throws SQLException {
        return new Todo(
                res.getInt("id"),
                res.getString("title"),
                res.getString("description"),
                res.getBoolean("completed"),
                res.getTimestamp("created_at").toLocalDateTime(),
                res.getTimestamp("updated_at") != null ? res.getTimestamp("updated_at").toLocalDateTime() : null
        );
    }

    public List<Todo> getAllTodos() throws SQLException {
        List<Todo> todos = new ArrayList<>();
        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_TODOS);
                ResultSet res = stmt.executeQuery()
        ) {
            while (res.next()) {
                todos.add(getTodoRow(res));
            }
        }
        return todos;
    }

    public Todo getTodoById(int id) throws SQLException {
        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_TODO_BY_ID)
        ) {
            stmt.setInt(1, id);
            try (ResultSet res = stmt.executeQuery()) {
                if (res.next()) {
                    return getTodoRow(res);
                }
            }
        }
        return null;
    }
}
