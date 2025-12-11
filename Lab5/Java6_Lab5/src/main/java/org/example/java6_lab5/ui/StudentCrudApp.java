package org.example.java6_lab5.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.java6_lab5.client.HttpClient;
import org.example.java6_lab5.model.Student;
import org.example.java6_lab5.model.StudentMap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.HttpURLConnection;

public class StudentCrudApp extends JFrame {
    // Firebase URL - MUST include .json in all endpoints
    private static final String HOST = "https://lab6java5ts00680-default-rtdb.asia-southeast1.firebasedatabase.app/students.json";
    private ObjectMapper mapper = new ObjectMapper();
    
    // Form components
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtMark;
    private JRadioButton rdMale;
    private JRadioButton rdFemale;
    private JButton btnCreate;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnReset;
    private JTable table;
    private DefaultTableModel tableModel;
    
    // Store current Firebase key for update/delete
    private String currentFirebaseKey = null;
    
    public StudentCrudApp() {
        initComponents();
        loadStudents();
    }
    
    private void initComponents() {
        setTitle("Student Management - Firebase");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Student Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // ID
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        txtId = new JTextField(20);
        panel.add(txtId, gbc);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        txtName = new JTextField(20);
        panel.add(txtName, gbc);
        
        // Mark
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Mark:"), gbc);
        gbc.gridx = 1;
        txtMark = new JTextField(20);
        panel.add(txtMark, gbc);
        
        // Gender
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rdMale = new JRadioButton("Male", true);
        rdFemale = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rdMale);
        genderGroup.add(rdFemale);
        genderPanel.add(rdMale);
        genderPanel.add(rdFemale);
        panel.add(genderPanel, gbc);
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnCreate = new JButton("Create");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnReset = new JButton("Reset");
        
        btnCreate.addActionListener(e -> createStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnReset.addActionListener(e -> resetForm());
        
        buttonPanel.add(btnCreate);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnReset);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Student List"));
        
        // Include Firebase Key column
        String[] columns = {"Firebase Key", "ID", "Name", "Mark", "Gender"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editStudent();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadStudents() {
        try {
            // IMPORTANT: Add .json for Firebase
            String url = HOST + "/students.json";
            System.out.println("[INFO] Loading from: " + url);
            
            HttpURLConnection connection = HttpClient.openConnection("GET", url);
            byte[] response = HttpClient.readData(connection);
            
            String responseString = new String(response, "UTF-8");
            System.out.println("[INFO] Response: " + responseString.substring(0, Math.min(100, responseString.length())));
            
            // Check for empty or null response
            if (responseString.trim().isEmpty() || responseString.equals("null")) {
                System.out.println("[INFO] Database is empty");
                tableModel.setRowCount(0);
                JOptionPane.showMessageDialog(this,
                    "No students in Firebase database yet.\n" +
                    "Add some students to get started!",
                    "Empty Database", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Parse as StudentMap (Firebase returns map structure)
            StudentMap studentMap = mapper.readValue(response, StudentMap.class);
            
            tableModel.setRowCount(0);
            for (String key : studentMap.keySet()) {
                Student student = studentMap.get(key);
                Object[] row = {
                    key,  // Firebase key (-N1, -N2, etc.)
                    student.getId(),
                    student.getName(),
                    student.getMark(),
                    student.isGender() ? "Male" : "Female"
                };
                tableModel.addRow(row);
            }
            
            System.out.println("[SUCCESS] Loaded " + studentMap.size() + " students");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading students: " + e.getMessage() + "\n\n" +
                "Check console for details.",
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void editStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        
        try {
            // Get Firebase key from first column
            currentFirebaseKey = (String) table.getValueAt(selectedRow, 0);
            
            // IMPORTANT: Add .json for Firebase
            String url = HOST + "/students/" + currentFirebaseKey + ".json";
            System.out.println("[INFO] Loading student: " + url);
            
            HttpURLConnection connection = HttpClient.openConnection("GET", url);
            byte[] response = HttpClient.readData(connection);
            
            Student student = mapper.readValue(response, Student.class);
            
            txtId.setText(student.getId());
            txtName.setText(student.getName());
            txtMark.setText(String.valueOf(student.getMark()));
            if (student.isGender()) {
                rdMale.setSelected(true);
            } else {
                rdFemale.setSelected(true);
            }
            
            System.out.println("[SUCCESS] Loaded student: " + student.getName());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading student: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void createStudent() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter Student ID!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Student student = getFormData();
            
            // IMPORTANT: Add .json for Firebase
            String url = HOST + "/students.json";
            System.out.println("[INFO] Creating student at: " + url);
            
            HttpURLConnection connection = HttpClient.openConnection("POST", url);
            byte[] data = mapper.writeValueAsBytes(student);
            byte[] responseBytes = HttpClient.writeData(connection, data);
            
            // Firebase returns {"name": "generated-key"}
            String responseJson = new String(responseBytes, "UTF-8");
            System.out.println("[INFO] Firebase response: " + responseJson);
            
            JOptionPane.showMessageDialog(this,
                "Student created successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            resetForm();
            loadStudents();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error creating student: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void updateStudent() {
        if (currentFirebaseKey == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a student to update!",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Student student = getFormData();
            
            // IMPORTANT: Add .json for Firebase
            String url = HOST + "/students/" + currentFirebaseKey + ".json";
            System.out.println("[INFO] Updating student at: " + url);
            
            HttpURLConnection connection = HttpClient.openConnection("PUT", url);
            byte[] data = mapper.writeValueAsBytes(student);
            HttpClient.writeData(connection, data);
            
            JOptionPane.showMessageDialog(this,
                "Student updated successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            resetForm();
            loadStudents();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error updating student: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void deleteStudent() {
        if (currentFirebaseKey == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a student to delete!",
                "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this student?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // IMPORTANT: Add .json for Firebase
                String url = HOST + "/students/" + currentFirebaseKey + ".json";
                System.out.println("[INFO] Deleting student at: " + url);
                
                HttpURLConnection connection = HttpClient.openConnection("DELETE", url);
                HttpClient.readData(connection);
                
                JOptionPane.showMessageDialog(this,
                    "Student deleted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                resetForm();
                loadStudents();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting student: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void resetForm() {
        txtId.setText("");
        txtName.setText("");
        txtMark.setText("");
        rdMale.setSelected(true);
        currentFirebaseKey = null;
    }
    
    private Student getFormData() throws NumberFormatException {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        double mark = Double.parseDouble(txtMark.getText().trim());
        boolean gender = rdMale.isSelected();
        
        if (id.isEmpty() || name.isEmpty()) {
            throw new IllegalArgumentException("ID and Name cannot be empty!");
        }
        
        return new Student(id, name, mark, gender);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentCrudApp().setVisible(true);
        });
    }
}