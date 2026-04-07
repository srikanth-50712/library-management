// File: src/AdminDashboard.java
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    private String adminName;
    private DefaultTableModel studentModel;
    private DefaultTableModel bookModel;
    private JTable studentTable;
    private JTable bookTable;

    static final Color BG     = new Color(18, 32, 47);
    static final Color HEADER = new Color(20, 40, 60);
    static final Color GOLD   = new Color(212, 175, 55);
    static final Color ACCENT = new Color(100, 160, 220);
    static final Color CARD   = new Color(26, 46, 68);
    static final Color WHITE  = new Color(240, 240, 245);
    static final Color GRAY   = new Color(160, 175, 190);
    static final Color GREEN  = new Color(46, 160, 100);
    static final Color RED    = new Color(200, 60, 60);

    public AdminDashboard(String name) {
        this.adminName = name;
        setTitle("Admin Dashboard - Library Management System");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER);
        header.setPreferredSize(new Dimension(1000, 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GOLD));

        JLabel titleLbl = new JLabel("  Library Management - Admin Panel");
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 20));
        titleLbl.setForeground(GOLD);
        header.add(titleLbl, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 18));
        rightHeader.setOpaque(false);
        JLabel userLbl = new JLabel("Admin: " + adminName);
        userLbl.setForeground(WHITE);
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rightHeader.add(userLbl);

        JButton logoutBtn = smallBtn("Logout", RED);
        logoutBtn.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        rightHeader.add(logoutBtn);
        header.add(rightHeader, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(30, 55, 80));
        tabs.setForeground(Color.WHITE);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("TabbedPane.selected", new Color(212, 175, 55));
        UIManager.put("TabbedPane.selectedForeground", Color.BLACK);
        UIManager.put("TabbedPane.foreground", Color.WHITE);
        UIManager.put("TabbedPane.background", new Color(30, 55, 80));
        SwingUtilities.updateComponentTreeUI(tabs);

        tabs.addTab("  Add Student  ", createAddStudentTab());
        tabs.addTab("  View / Delete Students  ", createViewStudentsTab());
        tabs.addTab("  Add Book  ", createAddBookTab());
        tabs.addTab("  View Books  ", createViewBooksTab());

        // Set tab colors manually with bright visible text
        for (int i = 0; i < tabs.getTabCount(); i++) {
            JLabel tabLabel = new JLabel(tabs.getTitleAt(i), SwingConstants.CENTER);
            tabLabel.setForeground(new Color(255, 220, 80));
            tabLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            tabs.setTabComponentAt(i, tabLabel);
        }

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createAddStudentTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 90, 120), 1, true),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Add New Student");
        title.setFont(new Font("Georgia", Font.BOLD, 18));
        title.setForeground(GOLD);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);
        gbc.gridwidth = 1;

        JTextField sidField   = styledField("e.g. STU001");
        JTextField nameField  = styledField("Full Name");
        JTextField emailField = styledField("Email address");
        JTextField deptField  = styledField("e.g. Computer Science");
        JTextField phoneField = styledField("Phone number");

        addRow(card, gbc, "Student ID:", sidField, 1);
        addRow(card, gbc, "Full Name:", nameField, 2);
        addRow(card, gbc, "Email:", emailField, 3);
        addRow(card, gbc, "Department:", deptField, 4);
        addRow(card, gbc, "Phone:", phoneField, 5);

        JButton addBtn = styledBtn("Add Student", GREEN);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 5, 8);
        card.add(addBtn, gbc);

        JLabel statusLbl = new JLabel("", SwingConstants.CENTER);
        statusLbl.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        gbc.gridy = 7;
        card.add(statusLbl, gbc);

        panel.add(card);

        addBtn.addActionListener(e -> {
            String sid   = sidField.getText().trim();
            String sname = nameField.getText().trim();
            String email = emailField.getText().trim();
            String dept  = deptField.getText().trim();
            String phone = phoneField.getText().trim();

            if (sid.isEmpty() || sname.isEmpty()) {
                statusLbl.setForeground(RED);
                statusLbl.setText("Student ID and Name are required!");
                return;
            }
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO students (student_id, full_name, email, department, phone) VALUES (?,?,?,?,?)"
                );
                ps.setString(1, sid); ps.setString(2, sname);
                ps.setString(3, email); ps.setString(4, dept); ps.setString(5, phone);
                ps.executeUpdate();
                statusLbl.setForeground(GREEN);
                statusLbl.setText("Student added successfully!");
                sidField.setText(""); nameField.setText(""); emailField.setText("");
                deptField.setText(""); phoneField.setText("");
            } catch (SQLIntegrityConstraintViolationException ex) {
                statusLbl.setForeground(RED);
                statusLbl.setText("Student ID already exists!");
            } catch (SQLException ex) {
                statusLbl.setForeground(RED);
                statusLbl.setText("Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel createViewStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topBar.setBackground(BG);

        JTextField searchField = styledField("Search by name or ID...");
        searchField.setPreferredSize(new Dimension(250, 35));
        JButton searchBtn  = styledBtn("Search", ACCENT);
        JButton refreshBtn = styledBtn("Refresh", new Color(60, 90, 120));
        JButton deleteBtn  = styledBtn("Delete Selected", RED);

        topBar.add(searchField);
        topBar.add(searchBtn);
        topBar.add(refreshBtn);
        topBar.add(deleteBtn);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Student ID", "Full Name", "Email", "Department", "Phone"};
        studentModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable = styledTable(studentModel);
        JScrollPane scroll = new JScrollPane(studentTable);
        scroll.getViewport().setBackground(new Color(22, 40, 58));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 90, 120)));
        panel.add(scroll, BorderLayout.CENTER);

        loadStudents(null);

        searchBtn.addActionListener(e -> loadStudents(searchField.getText().trim()));
        refreshBtn.addActionListener(e -> { searchField.setText(""); loadStudents(null); });

        deleteBtn.addActionListener(e -> {
            int row = studentTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a student!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) studentModel.getValueAt(row, 0);
            String name = (String) studentModel.getValueAt(row, 2);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete student: " + name + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DBConnection.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE id=?");
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Student deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadStudents(null);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private void loadStudents(String search) {
        studentModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps;
            if (search == null || search.isEmpty()) {
                ps = conn.prepareStatement("SELECT * FROM students ORDER BY added_at DESC");
            } else {
                ps = conn.prepareStatement("SELECT * FROM students WHERE full_name LIKE ? OR student_id LIKE ?");
                ps.setString(1, "%" + search + "%");
                ps.setString(2, "%" + search + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studentModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("student_id"),
                    rs.getString("full_name"), rs.getString("email"),
                    rs.getString("department"), rs.getString("phone")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private JPanel createAddBookTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 90, 120), 1, true),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Add New Book");
        title.setFont(new Font("Georgia", Font.BOLD, 18));
        title.setForeground(GOLD);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);
        gbc.gridwidth = 1;

        JTextField titleField    = styledField("Book title");
        JTextField authorField   = styledField("Author name");
        JTextField isbnField     = styledField("ISBN number");
        JTextField categoryField = styledField("e.g. Programming");
        JTextField qtyField      = styledField("Number of copies");

        addRow(card, gbc, "Book Title:", titleField, 1);
        addRow(card, gbc, "Author:", authorField, 2);
        addRow(card, gbc, "ISBN:", isbnField, 3);
        addRow(card, gbc, "Category:", categoryField, 4);
        addRow(card, gbc, "Quantity:", qtyField, 5);

        JButton addBtn = styledBtn("Add Book", GREEN);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 5, 8);
        card.add(addBtn, gbc);

        JLabel statusLbl = new JLabel("", SwingConstants.CENTER);
        statusLbl.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        gbc.gridy = 7;
        card.add(statusLbl, gbc);

        panel.add(card);

        addBtn.addActionListener(e -> {
            String t   = titleField.getText().trim();
            String a   = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            String cat = categoryField.getText().trim();
            int qty = 1;
            try { qty = Integer.parseInt(qtyField.getText().trim()); } catch (Exception ignored) {}

            if (t.isEmpty() || a.isEmpty()) {
                statusLbl.setForeground(RED);
                statusLbl.setText("Title and Author are required!");
                return;
            }
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO books (title, author, isbn, category, quantity, available) VALUES (?,?,?,?,?,?)"
                );
                ps.setString(1, t); ps.setString(2, a); ps.setString(3, isbn);
                ps.setString(4, cat); ps.setInt(5, qty); ps.setInt(6, qty);
                ps.executeUpdate();
                statusLbl.setForeground(GREEN);
                statusLbl.setText("Book added successfully!");
                titleField.setText(""); authorField.setText(""); isbnField.setText("");
                categoryField.setText(""); qtyField.setText("");
            } catch (SQLException ex) {
                statusLbl.setForeground(RED);
                statusLbl.setText("Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel createViewBooksTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topBar.setBackground(BG);

        JTextField searchField = styledField("Search by title or author...");
        searchField.setPreferredSize(new Dimension(250, 35));
        JButton searchBtn  = styledBtn("Search", ACCENT);
        JButton refreshBtn = styledBtn("Refresh", new Color(60, 90, 120));

        topBar.add(searchField);
        topBar.add(searchBtn);
        topBar.add(refreshBtn);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Author", "ISBN", "Category", "Total Qty", "Available"};
        bookModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        bookTable = styledTable(bookModel);
        JScrollPane scroll = new JScrollPane(bookTable);
        scroll.getViewport().setBackground(new Color(22, 40, 58));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 90, 120)));
        panel.add(scroll, BorderLayout.CENTER);

        loadBooks(null);

        searchBtn.addActionListener(e -> loadBooks(searchField.getText().trim()));
        refreshBtn.addActionListener(e -> { searchField.setText(""); loadBooks(null); });

        return panel;
    }

    private void loadBooks(String search) {
        bookModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps;
            if (search == null || search.isEmpty()) {
                ps = conn.prepareStatement("SELECT * FROM books ORDER BY title");
            } else {
                ps = conn.prepareStatement("SELECT * FROM books WHERE title LIKE ? OR author LIKE ?");
                ps.setString(1, "%" + search + "%");
                ps.setString(2, "%" + search + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("title"), rs.getString("author"),
                    rs.getString("isbn"), rs.getString("category"),
                    rs.getInt("quantity"), rs.getInt("available")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void addRow(JPanel p, GridBagConstraints g, String label, JTextField field, int row) {
        g.gridwidth = 1;
        g.gridx = 0; g.gridy = row;
        g.insets = new Insets(8, 8, 8, 8);
        p.add(makeLabel(label), g);
        g.gridx = 1; g.ipadx = 150;
        p.add(field, g);
        g.ipadx = 0;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(GRAY);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }

    private JTextField styledField(String tip) {
        JTextField f = new JTextField(15);
        f.setBackground(new Color(35, 60, 85));
        f.setForeground(WHITE);
        f.setCaretColor(GOLD);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 90, 120), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        f.setToolTipText(tip);
        return f;
    }

    private JButton styledBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return btn;
    }

    private JButton smallBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(new Color(22, 40, 58));
        table.setForeground(WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setGridColor(new Color(40, 65, 90));
        table.setSelectionBackground(new Color(50, 100, 150));
        table.setSelectionForeground(WHITE);
        table.getTableHeader().setBackground(HEADER);
        table.getTableHeader().setForeground(GOLD);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        return table;
    }
}