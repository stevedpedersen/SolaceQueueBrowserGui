package com.solace.psg.queueBrowser.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class QueueSelectorDialog {
    String selectedOption = "";

    public String selectQueues(JFrame parentFrame, String[] queues) {
        // Create a JDialog
        JDialog dialog = new JDialog(parentFrame, "Queue Selection", true);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setLocation(parentFrame.getLocation().x + 10, parentFrame.getLocation().y + 10);
        dialog.setIconImage(parentFrame.getIconImage());

        dialog.setSize(500, 600);
        dialog.setLayout(new BorderLayout());
        
        // Add a label with instructions
        JLabel instructionLabel = new JLabel("Search and select the destination queue, then click 'OK':");
        instructionLabel.setHorizontalAlignment(SwingConstants.LEFT);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        dialog.add(instructionLabel, BorderLayout.NORTH);

        // Create search field
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(20);
        searchField.setToolTipText("Type to filter queues by name");
        
        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchInputPanel.add(searchLabel);
        searchInputPanel.add(searchField);
        searchPanel.add(searchInputPanel, BorderLayout.CENTER);
        
        dialog.add(searchPanel, BorderLayout.NORTH);

        // Create table model with queue names
        String[] columnNames = {"Queue Name"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Populate table with all queues
        for (String queue : queues) {
            tableModel.addRow(new Object[]{queue});
        }

        // Create table with row sorter for filtering
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);
        
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        
        // Enable search filtering
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().trim();
                if (searchText.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 0));
                }
            }
        });

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(480, 450));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    selectedOption = (String) tableModel.getValueAt(modelRow, 0);
                }
                dialog.dispose();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedOption = "";
                dialog.dispose();
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Enable double-click to select
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        int modelRow = table.convertRowIndexToModel(row);
                        selectedOption = (String) tableModel.getValueAt(modelRow, 0);
                        dialog.dispose();
                    }
                }
            }
        });
        
        // Enable Enter key to select
        table.getInputMap().put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0), "select");
        table.getActionMap().put("select", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    selectedOption = (String) tableModel.getValueAt(modelRow, 0);
                    dialog.dispose();
                }
            }
        });

        // Focus on search field and select first row if available
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                searchField.requestFocus();
                if (table.getRowCount() > 0) {
                    table.setRowSelectionInterval(0, 0);
                }
            }
        });

        dialog.setVisible(true);
        
        return selectedOption != null ? selectedOption : "";
    }
}
