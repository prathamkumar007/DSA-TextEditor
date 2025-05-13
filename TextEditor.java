import java.sql.*;
import java.util.Stack;

public class TextEditor {
    private Node head;
    private final Stack<Action> undoStack = new Stack<>();
    private final Stack<Action> redoStack = new Stack<>();

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/text_editor", "root", "MySql@007"
        );
    }

    public void insertLine(int index, String line) {
        if (index < 0) {
            System.out.println("Invalid index. Must be >= 1.");
            return;
        }

        Node newNode = new Node(line);

        if (index == 0 || head == null) {
            newNode.next = head;
            head = newNode;
        } else {
            Node prev = getNodeAt(index - 1);
            if (prev == null) return;
            newNode.next = prev.next;
            prev.next = newNode;
        }

        try (Connection conn = getConnection()) {
            PreparedStatement shift = conn.prepareStatement(
                "UPDATE document SET line_index = line_index + 1 WHERE line_index >= ? ORDER BY line_index DESC"
            );
            shift.setInt(1, index);
            shift.executeUpdate();

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO document (line_index, content) VALUES (?, ?)"
            );
            ps.setInt(1, index);
            ps.setString(2, line);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        undoStack.push(new Action("insert", index, line));
        redoStack.clear();
    }

    public void deleteLine(int index) {
        if (index < 0 || head == null) {
            System.out.println("Invalid index.");
            return;
        }

        Node deletedNode;
        if (index == 0) {
            deletedNode = head;
            head = head.next;
        } else {
            Node prev = getNodeAt(index - 1);
            if (prev == null || prev.next == null) return;
            deletedNode = prev.next;
            prev.next = prev.next.next;
        }

        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM document WHERE line_index = ?"
            );
            ps.setInt(1, index);
            ps.executeUpdate();

            PreparedStatement shift = conn.prepareStatement(
                "UPDATE document SET line_index = line_index - 1 WHERE line_index > ?"
            );
            shift.setInt(1, index);
            shift.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        undoStack.push(new Action("delete", index, deletedNode.line));
        redoStack.clear();
    }

    public void undo() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo.");
            return;
        }

        Action last = undoStack.pop();
        if (last.type.equals("insert")) {
            deleteLineInternal(last.index);
        } else if (last.type.equals("delete")) {
            insertLineInternal(last.index, last.content);
        }
        redoStack.push(last);
    }

    public void redo() {
        if (redoStack.isEmpty()) {
            System.out.println("Nothing to redo.");
            return;
        }

        Action next = redoStack.pop();
        if (next.type.equals("insert")) {
            insertLineInternal(next.index, next.content);
        } else if (next.type.equals("delete")) {
            deleteLineInternal(next.index);
        }
        undoStack.push(next);
    }

    public void display() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM document ORDER BY line_index")) {

            int count = 0;
            while (rs.next()) {
                System.out.println((rs.getInt("line_index") + 1) + ": " + rs.getString("content"));
                count++;
            }
            if (count == 0) System.out.println("Document is empty.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Node getNodeAt(int index) {
        Node current = head;
        while (index > 0 && current != null) {
            current = current.next;
            index--;
        }
        return current;
    }

    private void insertLineInternal(int index, String line) {
        Node newNode = new Node(line);
        if (index == 0 || head == null) {
            newNode.next = head;
            head = newNode;
        } else {
            Node prev = getNodeAt(index - 1);
            if (prev == null) return;
            newNode.next = prev.next;
            prev.next = newNode;
        }
    
        try (Connection conn = getConnection()) {
            PreparedStatement shift = conn.prepareStatement(
                "UPDATE document SET line_index = line_index + 1 WHERE line_index >= ? ORDER BY line_index DESC"
            );
            shift.setInt(1, index);
            shift.executeUpdate();
    
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO document (line_index, content) VALUES (?, ?)"
            );
            ps.setInt(1, index);
            ps.setString(2, line);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private void deleteLineInternal(int index) {
        if (head == null) return;
        if (index == 0) {
            head = head.next;
        } else {
            Node prev = getNodeAt(index - 1);
            if (prev == null || prev.next == null) return;
            prev.next = prev.next.next;
        }
    
        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM document WHERE line_index = ?"
            );
            ps.setInt(1, index);
            ps.executeUpdate();
    
            PreparedStatement shift = conn.prepareStatement(
                "UPDATE document SET line_index = line_index - 1 WHERE line_index > ?"
            );
            shift.setInt(1, index);
            shift.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void loadFromDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM document ORDER BY line_index")) {
    
            head = null;
            Node tail = null;
    
            while (rs.next()) {
                String line = rs.getString("content");
                Node newNode = new Node(line);
                if (head == null) {
                    head = newNode;
                    tail = newNode;
                } else {
                    tail.next = newNode;
                    tail = newNode;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
