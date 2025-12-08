/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package studyplanner.ui;

/**
 *
 * @author aryop
 */
import studyplanner.dao.TugasDAO;
import studyplanner.ui.TugasCardPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.Box;

public class BerandaPanel extends javax.swing.JPanel {

    /**
     * Creates new form BerandaPanel
     */
    public BerandaPanel() {
        initComponents();
        setupResponsiveLayout();
        applyModernStyles();
        muatRingkasanDummy();
        isiDeadlineDariDatabase();
    }

    private void setupResponsiveLayout() {
        // Root Beranda: header + isi (scroll)
        this.removeAll();
        this.setLayout(new BorderLayout());

        // ===== HEADER "Beranda" di atas =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 24, 8, 24));
        header.add(jLabel4, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ===== SCROLL isi utama di CENTER =====
        add(jScrollPane2, BorderLayout.CENTER);

        // Panel di dalam scroll (panelUtama)
        panelUtama.setOpaque(false);
        panelUtama.removeAll(); // <-- kosongkan dulu
        panelUtama.setLayout(new BoxLayout(panelUtama, BoxLayout.Y_AXIS));
        panelUtama.setBorder(new EmptyBorder(0, 24, 24, 24));

        // Biar kartu ikut lebar
        Dimension prefRingkasan = ringkasanPanel.getPreferredSize();
        ringkasanPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, prefRingkasan.height));
        ringkasanPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Dimension prefDeadline = deadlinePanel.getPreferredSize();
        deadlinePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, prefDeadline.height));
        deadlinePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // *** URUTKAN DI SINI ***
        panelUtama.add(ringkasanPanel); // Ringkasan di atas
        panelUtama.add(Box.createVerticalStrut(16));
        panelUtama.add(deadlinePanel); // Mendekati Deadline di bawah
    }

    private void applyModernStyles() {
        Color bgMain = new Color(248, 249, 250); // abu muda (sama kalender)
        Color cardBg = Color.WHITE;
        Color border = new Color(226, 232, 240);
        Color textDark = new Color(30, 41, 59);
        Color textSoft = new Color(71, 85, 105);

        // Background utama
        this.setBackground(bgMain);
        jScrollPane2.getViewport().setBackground(bgMain);
        jScrollPane2.setBorder(null);

        // Judul "Beranda"
        jLabel4.setForeground(textDark);
        jLabel4.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));

        // ===== Kartu Ringkasan =====
        ringkasanPanel.setBackground(cardBg);
        ringkasandalamPanel.setOpaque(false);
        ringkasanPanel.setBorder(
                javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(border),
                        new EmptyBorder(16, 16, 16, 16)));

        jLabel10.setForeground(textDark); // "Ringkasan"
        jLabel5.setForeground(textSoft); // "Total Tugas"
        jLabel7.setForeground(textSoft); // "Mendekati Deadline"
        jLabel8.setForeground(textSoft); // "Tugas Selesai"

        totalTugasLabel.setForeground(textDark);
        deadlineLabel.setForeground(new Color(234, 88, 12)); // oranye
        selesaiLabel.setForeground(new Color(16, 185, 129)); // hijau

        // ===== Kartu Mendekati Deadline =====
        deadlinePanel.setBackground(cardBg);
        deadlinePanel.setBorder(
                javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(border),
                        new EmptyBorder(16, 16, 16, 16)));
    }

    private void isiDeadlineDariDatabase() {
        // bersihkan isi panel
        deadlinePanel.removeAll();

        // layout vertikal untuk card
        deadlinePanel.setLayout(new javax.swing.BoxLayout(deadlinePanel, javax.swing.BoxLayout.Y_AXIS));

        // judul
        javax.swing.JLabel title = new javax.swing.JLabel("Mendekati Deadline");
        title.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        title.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 24, 8, 8));
        title.setAlignmentX(LEFT_ALIGNMENT);
        deadlinePanel.add(title);

        // ambil tugas yang mendekati deadline (misal 7 hari ke depan)
        java.util.List<studyplanner.model.Tugas> daftarTugas = TugasDAO.getMendekatiDeadline(7);

        if (daftarTugas.isEmpty()) {
            javax.swing.JLabel kosong = new javax.swing.JLabel("Belum ada tugas yang mendekati deadline.");
            kosong.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 12));
            kosong.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 24, 8, 8));
            kosong.setAlignmentX(LEFT_ALIGNMENT);
            deadlinePanel.add(kosong);
        } else {
            java.time.LocalDate today = java.time.LocalDate.now();

            for (studyplanner.model.Tugas t : daftarTugas) {
                String deadlineStr = t.getDeadline();
                if (deadlineStr == null || deadlineStr.isBlank()) {
                    continue;
                }

                java.time.LocalDate dl;
                try {
                    dl = java.time.LocalDate.parse(deadlineStr); // format "yyyy-MM-dd"
                } catch (Exception ex) {
                    continue;
                }

                long days = java.time.temporal.ChronoUnit.DAYS.between(today, dl);
                String infoHari;
                if (days < 0) {
                    infoHari = " (terlewat)";
                } else if (days == 0) {
                    infoHari = " (hari ini)";
                } else if (days == 1) {
                    infoHari = " (1 hari lagi)";
                } else {
                    infoHari = " (" + days + " hari lagi)";
                }

                // bikin card pakai judul tugas asli
                TugasCardPanel card = new TugasCardPanel();
                card.setJudulTugas(t.getJudul() + infoHari);
                card.setAlignmentX(LEFT_ALIGNMENT);
                deadlinePanel.add(card);
            }
        }

        // refresh tampilan
        deadlinePanel.revalidate();
        deadlinePanel.repaint();
    }

    private void muatRingkasanDummy() {
        // Versi pakai database beneran:
        int total = TugasDAO.countTotalTugas();
        int mendekati = TugasDAO.countMendekatiDeadline(3); // 3 hari ke depan
        int selesai = TugasDAO.countSelesai();

        // Tampilkan ke label
        totalTugasLabel.setText(String.valueOf(total));
        deadlineLabel.setText(String.valueOf(mendekati));
        selesaiLabel.setText(String.valueOf(selesai));
    }

    public void refreshData() {
        muatRingkasanDummy(); // ini sudah pakai DB
        isiDeadlineDariDatabase(); // method yang tadi kita buat
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        panelUtama = new javax.swing.JPanel();
        ringkasanPanel = new javax.swing.JPanel();
        ringkasandalamPanel = new javax.swing.JPanel();
        totalTugasLabel = new javax.swing.JLabel();
        deadlineLabel = new javax.swing.JLabel();
        selesaiLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        deadlinePanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(21, 129, 191));

        jScrollPane2.setBorder(null);
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(1280, 720));

        panelUtama.setBackground(new java.awt.Color(21, 129, 191));

        ringkasanPanel.setBackground(new java.awt.Color(61, 182, 177));

        ringkasandalamPanel.setBackground(new java.awt.Color(204, 229, 207));
        ringkasandalamPanel.setLayout(new java.awt.GridLayout(2, 3));

        totalTugasLabel.setFont(new java.awt.Font("Segoe UI", 0, 50)); // NOI18N
        totalTugasLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalTugasLabel.setText("0");
        ringkasandalamPanel.add(totalTugasLabel);

        deadlineLabel.setFont(new java.awt.Font("Segoe UI", 0, 50)); // NOI18N
        deadlineLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        deadlineLabel.setText("0");
        ringkasandalamPanel.add(deadlineLabel);

        selesaiLabel.setFont(new java.awt.Font("Segoe UI", 0, 50)); // NOI18N
        selesaiLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        selesaiLabel.setText("0");
        ringkasandalamPanel.add(selesaiLabel);

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Total Tugas");
        ringkasandalamPanel.add(jLabel5);

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Mendekati Deadline");
        ringkasandalamPanel.add(jLabel7);

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Tugas Selesai");
        ringkasandalamPanel.add(jLabel8);

        jLabel10.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        jLabel10.setText("Ringkasan");

        javax.swing.GroupLayout ringkasanPanelLayout = new javax.swing.GroupLayout(ringkasanPanel);
        ringkasanPanel.setLayout(ringkasanPanelLayout);
        ringkasanPanelLayout.setHorizontalGroup(
                ringkasanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(ringkasanPanelLayout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(jLabel10)
                                .addContainerGap(520, Short.MAX_VALUE))
                        .addComponent(ringkasandalamPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        ringkasanPanelLayout.setVerticalGroup(
                ringkasanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                ringkasanPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ringkasandalamPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 98,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(13, Short.MAX_VALUE)));

        deadlinePanel.setBackground(new java.awt.Color(61, 182, 177));

        jLabel6.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        jLabel6.setText("Mendekati Deadline");

        javax.swing.GroupLayout deadlinePanelLayout = new javax.swing.GroupLayout(deadlinePanel);
        deadlinePanel.setLayout(deadlinePanelLayout);
        deadlinePanelLayout.setHorizontalGroup(
                deadlinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(deadlinePanelLayout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(jLabel6)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        deadlinePanelLayout.setVerticalGroup(
                deadlinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(deadlinePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel6)
                                .addContainerGap(141, Short.MAX_VALUE)));

        javax.swing.GroupLayout panelUtamaLayout = new javax.swing.GroupLayout(panelUtama);
        panelUtama.setLayout(panelUtamaLayout);
        panelUtamaLayout.setHorizontalGroup(
                panelUtamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelUtamaLayout.createSequentialGroup()
                                .addGroup(panelUtamaLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(deadlinePanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(ringkasanPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(44, Short.MAX_VALUE)));
        panelUtamaLayout.setVerticalGroup(
                panelUtamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelUtamaLayout.createSequentialGroup()
                                .addComponent(ringkasanPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27,
                                        Short.MAX_VALUE)
                                .addComponent(deadlinePanel, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)));

        jScrollPane2.setViewportView(panelUtama);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Beranda");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel4)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 625,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(18, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 339,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(26, Short.MAX_VALUE)));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel deadlineLabel;
    private javax.swing.JPanel deadlinePanel;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelUtama;
    private javax.swing.JPanel ringkasanPanel;
    private javax.swing.JPanel ringkasandalamPanel;
    private javax.swing.JLabel selesaiLabel;
    private javax.swing.JLabel totalTugasLabel;
    // End of variables declaration//GEN-END:variables
}
