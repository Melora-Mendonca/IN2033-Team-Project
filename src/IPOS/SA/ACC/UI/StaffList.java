package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Model.Staff;
import IPOS.SA.ACC.Service.StaffService;
import IPOS.SA.UI.BaseFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StaffList extends BaseFrame {

    private final StaffService staffService;

    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel statusLabel;

    public StaffList(String fullname, String role) {
        super(fullname, role, "Staff List");
        this.staffService = new StaffService();
        buildContent();
        loadStaffData();
    }

    @Override
    protected String getHeaderTitle() {
        return "Staff List";
    }

    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout(0, 0));
        CenterPanel.setBackground(new Color(245, 247, 250));

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(17, 24, 39));
        topBar.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(new Color(17, 24, 39));

        JLabel searchLabel = new JLabel("Search by Staff ID, Name, or Username:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton searchButton  = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        styleBtn(searchButton);
        styleBtn(refreshButton);

        searchButton.addActionListener(e  -> searchStaff());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadStaffData();
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        topBar.add(searchPanel, BorderLayout.WEST);

        // TABLE
        String[] columns = {
                "Staff ID", "Username", "First Name", "Surname",
                "Email", "Phone", "Role", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        staffTable = new JTable(tableModel);
        staffTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        staffTable.setRowHeight(30);
        staffTable.setShowGrid(false);
        staffTable.setFillsViewportHeight(true);
        staffTable.getTableHeader().setReorderingAllowed(false);
        staffTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        staffTable.getTableHeader().setBackground(new Color(17, 24, 39));
        staffTable.getTableHeader().setForeground(Color.WHITE);

        // Colour code inactive staff
        staffTable.setDefaultRenderer(Object.class,
                new javax.swing.table.DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable t, Object val,
                                                                   boolean sel, boolean foc, int row, int col) {
                        Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                        if (!sel) {
                            String status = tableModel.getValueAt(row, 7).toString();
                            c.setBackground(status.equals("Inactive")
                                    ? new Color(255, 240, 240) : Color.WHITE);
                        }
                        return c;
                    }
                }
        );

        JScrollPane scroll = new JScrollPane(staffTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // ── BOTTOM BUTTONS ────────────────────────────────────
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(17, 24, 39));
        bottomPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(17, 24, 39));

        JButton viewDetailsButton = new JButton("View / Edit Staff");
        JButton createButton      = new JButton("Create Staff Account");
        styleBtn(viewDetailsButton);
        styleBtn(createButton);

        viewDetailsButton.addActionListener(e -> {
            int row = staffTable.getSelectedRow();
            if (row >= 0) {
                String staffId = tableModel.getValueAt(row, 0).toString();
                dispose();
                new StaffAccountManagement(fullname, role, "MANAGE", staffId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a staff member to view details.");
            }
        });

        createButton.addActionListener(e -> {
            dispose();
            new StaffAccountManagement(fullname, role, "CREATE");
        });

        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(createButton);

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(Color.WHITE);

        bottomPanel.add(buttonPanel,  BorderLayout.WEST);
        bottomPanel.add(statusLabel,  BorderLayout.EAST);

        CenterPanel.add(topBar,      BorderLayout.NORTH);
        CenterPanel.add(scroll,      BorderLayout.CENTER);
        CenterPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    // DATA METHODS
    private void loadStaffData() {
        try {
            List<Staff> staffList = staffService.getStaffList();
            tableModel.setRowCount(0);

            for (Staff s : staffList) {
                tableModel.addRow(new Object[]{
                        s.getStaffId(),
                        s.getUsername(),
                        s.getFirstName(),
                        s.getSurName(),
                        s.getEmail(),
                        s.getPhone(),
                        s.getRole(),
                        s.isActive() ? "Active" : "Inactive"
                });
            }
            statusLabel.setText(staffList.size() + " staff members loaded");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading staff data: " + ex.getMessage());
            statusLabel.setText("Error loading data");
        }
    }

    private void searchStaff() {
        String search = searchField.getText().trim().toLowerCase();
        if (search.isEmpty()) { loadStaffData(); return; }

        try {
            List<Staff> all      = staffService.getStaffList();
            List<Staff> filtered = new ArrayList<>();

            for (Staff s : all) {
                if (s.getStaffId().toLowerCase().contains(search)   ||
                        s.getUsername().toLowerCase().contains(search)   ||
                        s.getFirstName().toLowerCase().contains(search)  ||
                        s.getSurName().toLowerCase().contains(search)    ||
                        s.getEmail().toLowerCase().contains(search)) {
                    filtered.add(s);
                }
            }

            tableModel.setRowCount(0);
            for (Staff s : filtered) {
                tableModel.addRow(new Object[]{
                        s.getStaffId(),
                        s.getUsername(),
                        s.getFirstName(),
                        s.getSurName(),
                        s.getEmail(),
                        s.getPhone(),
                        s.getRole(),
                        s.isActive() ? "Active" : "Inactive"
                });
            }
            statusLabel.setText(filtered.size() + " staff member(s) found");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching staff: " + ex.getMessage());
        }
    }

    // HELPER METHODS
    private void styleBtn(JButton btn) {
        btn.setBackground(new Color(30, 70, 90));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
}
