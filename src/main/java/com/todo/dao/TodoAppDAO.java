package com.todo.dao;

import com.model.Todo;
import com.todo.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TodoAppDAO {
    public List<Todo> getTodos() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        List<Todo> todos = new ArrayList<Todo>();
        try(Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from todos order by created_at desc ");
            ResultSet rs = stmt.executeQuery();
        )
        {
            while (rs.next()) {
                Todo todo = new Todo();
                todo.setId(rs.getInt("id"));
                todo.setTitle(rs.getString("title"));
                todo.setDescription(rs.getString("description"));
                todo.setCompleted(rs.getBoolean("completed"));
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                todo.setCreated_at(createdAt);
                LocalDateTime updatedAt =  rs.getTimestamp("updated_at").toLocalDateTime();
                todo.setUpdated_at(updatedAt);
                todos.add(todo);
            }
        }
        return todos;
    }
}
