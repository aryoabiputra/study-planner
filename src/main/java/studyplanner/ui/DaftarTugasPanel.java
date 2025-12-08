package studyplanner.ui;

import studyplanner.dao.MatkulDAO;
import studyplanner.dao.TugasDAO;
import studyplanner.model.Matkul;
import studyplanner.model.Tugas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DaftarTugasPanel extends JPanel {

    private JPanel cardsContainer;
    private JComboBox<String> statusCombo;
    private JComboBox<Matkul> matkulCombo;
    private JComboBox<String> prioritasCombo;
    private JComboBox<String> sortCombo;
    private JButton tambahBtn;
    private JButton editBtn;
    private JButton hapusBtn;
    private JLabel selectedLabel;

    private List<Tugas> semuaTugas = new ArrayList<>();
    private Tugas tugasTerpilih = null;

    public DaftarTugasPanel() {
        initUi();
        muatDataAwal();
        pasangListener();
    }

    private void initUi() {
        setLayout(new BorderLayout(16, 16));
        setBackground(Color.WHITE);

        // ====== TOP BAR (judul + filter + tombol) ======
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("Daftar Tugas");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        top.add(title, BorderLayout.WEST);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        controlPanel.setOpaque(false);

        // Filter status
        statusCombo = new JComboBox<>(new String[] {
                "Semua status", "Todo", "Doing", "Done"
        });

        // Filter prioritas
        prioritasCombo = new JComboBox<>(new String[] {
                "Semua prioritas",
                "1 (Tertinggi)",
                "2",
                "3",
                "4",
                "5 (Terendah)"
        });

        // Filter mata kuliah
        matkulCombo = new JComboBox<>();
        matkulCombo.addItem(null); // index 0 = semua matkul
        for (Matkul m : MatkulDAO.getAll()) {
            matkulCombo.addItem(m);
        }
        matkulCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Semua mata kuliah");
                } else {
                    setText(((Matkul) value).getNamaMatkul());
                }
                return this;
            }
        });
        matkulCombo.setSelectedIndex(0);

        // Sort
        sortCombo = new JComboBox<>(new String[] {
                "Sort: Deadline", "Sort: Prioritas"
        });

        // Tombol CRUD
        tambahBtn = new JButton("Tambah");
        editBtn = new JButton("Edit");
        hapusBtn = new JButton("Hapus");

        controlPanel.add(statusCombo);
        controlPanel.add(matkulCombo);
        controlPanel.add(prioritasCombo);
        controlPanel.add(sortCombo);
        controlPanel.add(tambahBtn);
        controlPanel.add(editBtn);
        controlPanel.add(hapusBtn);

        top.add(controlPanel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // ====== AREA CARD ======
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(new Color(245, 245, 245));
        cardsContainer.setOpaque(true);

        JScrollPane scroll = new JScrollPane(cardsContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // ====== STATUS BAR (info tugas terpilih) ======
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        selectedLabel = new JLabel("Tugas dipilih: -");
        bottom.add(selectedLabel, BorderLayout.WEST);
        add(bottom, BorderLayout.SOUTH);
    }

    private void muatDataAwal() {
        semuaTugas = TugasDAO.getAll(); // sudah ORDER BY deadline ASC di DAO
        terapkanFilterDanSort();
    }

    private void pasangListener() {
        statusCombo.addActionListener(e -> terapkanFilterDanSort());
        matkulCombo.addActionListener(e -> terapkanFilterDanSort());
        prioritasCombo.addActionListener(e -> terapkanFilterDanSort());
        sortCombo.addActionListener(e -> terapkanFilterDanSort());

        tambahBtn.addActionListener(e -> onTambah());
        editBtn.addActionListener(e -> onEdit());
        hapusBtn.addActionListener(e -> onHapus());
    }

    private void terapkanFilterDanSort() {
        List<Tugas> filtered = new ArrayList<>();

        String statusDipilih = (String) statusCombo.getSelectedItem();
        String prioritasDipilih = (String) prioritasCombo.getSelectedItem();
        Matkul matkulDipilih = (Matkul) matkulCombo.getSelectedItem();
        String sortDipilih = (String) sortCombo.getSelectedItem();

        Integer prioritasFilter = null;
        if (prioritasDipilih != null && !prioritasDipilih.startsWith("Semua")) {
            prioritasFilter = Integer.parseInt(prioritasDipilih.substring(0, 1));
        }

        for (Tugas t : semuaTugas) {

            // Filter status
            if (statusDipilih != null && !statusDipilih.startsWith("Semua")) {
                if (t.getStatus() == null || !t.getStatus().equalsIgnoreCase(statusDipilih)) {
                    continue;
                }
            }

            // Filter mata kuliah
            if (matkulDipilih != null) {
                if (t.getMatkulId() == null || !t.getMatkulId().equals(matkulDipilih.getIdMatkul())) {
                    continue;
                }
            }

            // Filter prioritas
            if (prioritasFilter != null) {
                if (t.getPrioritas() != prioritasFilter) {
                    continue;
                }
            }

            filtered.add(t);
        }

        // Sorting
        if (sortDipilih != null && sortDipilih.contains("Prioritas")) {
            filtered.sort(Comparator.comparingInt(Tugas::getPrioritas));
        } else {
            filtered.sort(Comparator.comparing(
                    t -> t.getDeadline() == null ? "" : t.getDeadline()));
        }

        tampilkanCards(filtered);
    }

    private void tampilkanCards(List<Tugas> data) {
        cardsContainer.removeAll();
        tugasTerpilih = null;
        selectedLabel.setText("Tugas dipilih: -");

        for (Tugas t : data) {
            Matkul m = null;
            if (t.getMatkulId() != null) {
                m = MatkulDAO.findById(t.getMatkulId());
            }
            String namaMatkul = (m != null) ? m.getNamaMatkul() : "-";

            TugasCardPanel card = new TugasCardPanel();
            card.setData(t, namaMatkul);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
            card.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            card.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    tugasTerpilih = t;
                    selectedLabel.setText("Tugas dipilih: " + t.getJudul());
                }
            });

            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            wrapper.add(card, BorderLayout.CENTER);

            cardsContainer.add(wrapper);
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    // ============= HANDLER TOMBOL =============

    private void onTambah() {
        showAddTaskDialogLikeCalendar();
    }

    private void onEdit() {
        if (tugasTerpilih == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih dulu tugas dengan klik salah satu card.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        TaskFormDialog dialog = new TaskFormDialog(owner, tugasTerpilih);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            Tugas hasil = dialog.getTugasResult();
            // pastikan ID tetap sama
            hasil.setIdTugas(tugasTerpilih.getIdTugas());

            TugasDAO.update(hasil);
            semuaTugas = TugasDAO.getAll();
            terapkanFilterDanSort();
        }
    }

    private void onHapus() {
        if (tugasTerpilih == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih dulu tugas dengan klik salah satu card.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus tugas '" + tugasTerpilih.getJudul() + "'?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            TugasDAO.delete(tugasTerpilih.getIdTugas());
            semuaTugas = TugasDAO.getAll();
            terapkanFilterDanSort();
        }
    }

    // ==== Dialog Tambah Tugas: tampilan mirip KalenderPanel ====
    private void showAddTaskDialogLikeCalendar() {
        // cari Frame induk dari panel ini
        java.awt.Window w = SwingUtilities.getWindowAncestor(this);
        java.awt.Frame owner = (w instanceof java.awt.Frame) ? (java.awt.Frame) w : null;

        JDialog dialog = new JDialog(owner, "Tambah Tugas Baru", true); // modal
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Title
        JLabel titleHeader = new JLabel("Tambah Tugas Baru");
        titleHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleHeader.setForeground(new Color(30, 41, 59));
        titleHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleHeader);
        mainPanel.add(Box.createVerticalStrut(20));

        // Form fields
        JTextField titleField = createStyledTextField("Judul Tugas");
        JTextField subjectField = createStyledTextField("Mata Kuliah");

        JComboBox<String> priorityCombo = new JComboBox<>(new String[] { "Tinggi", "Sedang", "Rendah" });
        priorityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priorityCombo.setBackground(Color.WHITE);
        priorityCombo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(10, 12, 10, 12)));

        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateSpinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(8, 12, 8, 12)));

        JSpinner deadlineSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor deadlineEditor = new JSpinner.DateEditor(deadlineSpinner, "dd/MM/yyyy");
        deadlineSpinner.setEditor(deadlineEditor);
        deadlineSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deadlineSpinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(8, 12, 8, 12)));

        // label + field
        addFormField(mainPanel, "Judul Tugas *", titleField);
        addFormField(mainPanel, "Mata Kuliah *", subjectField);
        addFormField(mainPanel, "Tanggal", dateSpinner);
        addFormField(mainPanel, "Deadline", deadlineSpinner);
        addFormField(mainPanel, "Prioritas", priorityCombo);

        mainPanel.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelBtn = new JButton("Batal");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelBtn.setForeground(new Color(100, 116, 139));
        cancelBtn.setBackground(Color.WHITE);
        cancelBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(10, 20, 10, 20)));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton saveBtn = new JButton("Simpan Tugas");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBackground(new Color(99, 102, 241));
        saveBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.setBorderPainted(false);

        saveBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                saveBtn.setBackground(new Color(129, 140, 248));
            }

            public void mouseExited(MouseEvent e) {
                saveBtn.setBackground(new Color(99, 102, 241));
            }
        });

        saveBtn.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty()
                    || subjectField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Judul dan Mata Kuliah harus diisi!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date date = (Date) dateSpinner.getValue();
            Date deadline = (Date) deadlineSpinner.getValue();

            LocalDate taskDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate deadlineDate = deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            try {
                Tugas tugasBaru = new Tugas();
                tugasBaru.setJudul(titleField.getText().trim());
                tugasBaru.setDeskripsi("");
                tugasBaru.setDeadline(deadlineDate.toString()); // "yyyy-MM-dd"
                tugasBaru.setMatkulId(null);
                tugasBaru.setSelesai(false);

                String now = LocalDate.now().toString();
                tugasBaru.setTanggalDibuat(now);
                tugasBaru.setUpdatedAt(now);
                tugasBaru.setTipe("tugas");
                tugasBaru.setPrioritas(
                        switch ((String) priorityCombo.getSelectedItem()) {
                            case "Tinggi" -> 1;
                            case "Sedang" -> 2;
                            case "Rendah" -> 3;
                            default -> 2;
                        });
                tugasBaru.setStatus("TODO");

                TugasDAO.insert(tugasBaru);

                // refresh daftar tugas
                semuaTugas = TugasDAO.getAll();
                terapkanFilterDanSort();

                dialog.dispose();

                JOptionPane.showMessageDialog(
                        DaftarTugasPanel.this,
                        "Tugas berhasil ditambahkan!",
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                        "Gagal menyimpan tugas ke database:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        mainPanel.add(buttonPanel);

        dialog.setContentPane(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    // ===================== FORM DIALOG (untuk EDIT) =====================
    private static class TaskFormDialog extends JDialog {
        private boolean saved = false;

        private JTextField judulField;
        private JComboBox<Matkul> matkulCombo;
        private JComboBox<String> tipeCombo;
        private JComboBox<Integer> prioritasCombo;
        private JComboBox<String> statusCombo;
        private JTextField deadlineField; // format yyyy-MM-dd

        private Tugas existing;

        public TaskFormDialog(Window owner, Tugas existing) {
            super(owner, "Form Tugas", ModalityType.APPLICATION_MODAL);
            this.existing = existing;

            initUi();
            if (existing != null) {
                isiDariExisting();
            }

            pack();
            setLocationRelativeTo(owner);
        }

        private void initUi() {
            JPanel main = new JPanel(new BorderLayout(10, 10));
            main.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            JLabel title = new JLabel(existing == null ? "Tambah Tugas" : "Edit Tugas");
            title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
            main.add(title, BorderLayout.NORTH);

            JPanel form = new JPanel();
            form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

            judulField = createStyledTextField("");
            deadlineField = createStyledTextField("yyyy-MM-dd");

            matkulCombo = new JComboBox<>();
            matkulCombo.addItem(null);
            for (Matkul m : MatkulDAO.getAll()) {
                matkulCombo.addItem(m);
            }
            matkulCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value == null) {
                        setText("Tidak ada / umum");
                    } else {
                        setText(((Matkul) value).getNamaMatkul());
                    }
                    return this;
                }
            });

            tipeCombo = new JComboBox<>(new String[] { "tugas", "kuis", "ujian", "project", "lainnya" });
            prioritasCombo = new JComboBox<>(new Integer[] { 1, 2, 3, 4, 5 });
            statusCombo = new JComboBox<>(new String[] { "Todo", "Doing", "Done" });

            addFormField(form, "Judul Tugas *", judulField);
            addFormField(form, "Mata Kuliah", matkulCombo);
            addFormField(form, "Tipe", tipeCombo);
            addFormField(form, "Prioritas (1-5)", prioritasCombo);
            addFormField(form, "Status", statusCombo);
            addFormField(form, "Deadline (yyyy-MM-dd)", deadlineField);

            main.add(form, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelBtn = new JButton("Batal");
            JButton saveBtn = new JButton("Simpan");

            cancelBtn.addActionListener(e -> {
                saved = false;
                dispose();
            });

            saveBtn.addActionListener(e -> {
                if (judulField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Judul tugas wajib diisi.",
                            "Validasi",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (deadlineField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Deadline wajib diisi (format yyyy-MM-dd).",
                            "Validasi",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String now = LocalDate.now().toString();

                if (existing == null) {
                    existing = new Tugas();
                    existing.setTanggalDibuat(now);
                }

                existing.setJudul(judulField.getText().trim());
                existing.setDeadline(deadlineField.getText().trim());

                Matkul m = (Matkul) matkulCombo.getSelectedItem();
                existing.setMatkulId(m != null ? m.getIdMatkul() : null);

                existing.setTipe((String) tipeCombo.getSelectedItem());

                Integer prior = (Integer) prioritasCombo.getSelectedItem();
                if (prior != null) {
                    existing.setPrioritas(prior);
                }

                existing.setStatus((String) statusCombo.getSelectedItem());
                existing.setUpdatedAt(now);

                saved = true;
                dispose();
            });

            buttonPanel.add(cancelBtn);
            buttonPanel.add(saveBtn);
            main.add(buttonPanel, BorderLayout.SOUTH);

            setContentPane(main);
        }

        private void isiDariExisting() {
            if (existing == null)
                return;

            if (existing.getJudul() != null) {
                judulField.setText(existing.getJudul());
            }
            if (existing.getDeadline() != null) {
                deadlineField.setText(existing.getDeadline());
            }

            if (existing.getMatkulId() != null) {
                for (int i = 0; i < matkulCombo.getItemCount(); i++) {
                    Matkul m = matkulCombo.getItemAt(i);
                    if (m != null && existing.getMatkulId().equals(m.getIdMatkul())) {
                        matkulCombo.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                matkulCombo.setSelectedIndex(0);
            }

            if (existing.getTipe() != null) {
                tipeCombo.setSelectedItem(existing.getTipe());
            }

            if (existing.getStatus() != null) {
                statusCombo.setSelectedItem(existing.getStatus());
            }

            // prioritas default 3 kalau 0
            int p = existing.getPrioritas();
            if (p < 1 || p > 5)
                p = 3;
            prioritasCombo.setSelectedItem(p);
        }

        public boolean isSaved() {
            return saved;
        }

        public Tugas getTugasResult() {
            return existing;
        }
    }

    // ===================== HELPER UI =====================

    private static JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(8, 12, 8, 12)));
        // placeholder tidak diimplementasikan, cuma param nama saja
        return field;
    }

    private static void addFormField(JPanel parent, String labelText, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(4, 4));
        row.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        row.add(lbl, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);

        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(row);
        parent.add(Box.createVerticalStrut(12));
    }
}
