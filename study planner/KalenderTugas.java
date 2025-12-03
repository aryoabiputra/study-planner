import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

public class KalenderTugas extends JFrame {
    private LocalDate currentDate = LocalDate.now();
    private LocalDate selectedDate = LocalDate.now();
    private JPanel calendarPanel;
    private JPanel taskPanel;
    private JLabel monthYearLabel;
    private JComboBox<String> viewModeCombo;
    private Map<LocalDate, List<Task>> tasksByDate;
    
    // Mode tampilan
    private enum ViewMode {
        HARIAN, MINGGUAN
    }
    private ViewMode currentMode = ViewMode.HARIAN;

    public KalenderTugas() {
        setTitle("Kalender Tugas");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Inisialisasi data contoh
        initializeSampleData();
        
        // Setup UI
        setupUI();
        
        setVisible(true);
    }

    private void initializeSampleData() {
        tasksByDate = new HashMap<>();
        
        // Tambah beberapa tugas contoh
        addTask(LocalDate.now(), "Mengerjakan Tugas UAS", "Pemrograman Java", 
                LocalDate.now().plusDays(3), "Tinggi");
        addTask(LocalDate.now().plusDays(1), "Membuat Laporan", "Basis Data", 
                LocalDate.now().plusDays(5), "Sedang");
        addTask(LocalDate.now().plusDays(3), "Quiz Online", "Struktur Data", 
                LocalDate.now().plusDays(3), "Tinggi");
        addTask(LocalDate.now().plusDays(5), "Presentasi Kelompok", "Pemrograman Web", 
                LocalDate.now().plusDays(7), "Tinggi");
        addTask(LocalDate.now().plusDays(2), "Review Materi", "Algoritma", 
                LocalDate.now().plusDays(4), "Rendah");
    }

    private void addTask(LocalDate date, String title, String subject, LocalDate deadline, String priority) {
        tasksByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(new Task(title, subject, deadline, priority));
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(248, 249, 250));
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(800);
        splitPane.setBorder(null);
        splitPane.setDividerSize(1);
        
        // Calendar Panel (kiri)
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        calendarPanel = new JPanel();
        calendarPanel.setBackground(Color.WHITE);
        leftPanel.add(calendarPanel, BorderLayout.CENTER);
        
        // Task Panel (kanan)
        taskPanel = new JPanel();
        taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));
        taskPanel.setBackground(new Color(248, 249, 250));
        taskPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JScrollPane taskScrollPane = new JScrollPane(taskPanel);
        taskScrollPane.setBorder(null);
        taskScrollPane.getViewport().setBackground(new Color(248, 249, 250));
        taskScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(taskScrollPane);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Tampilkan kalender awal
        updateCalendarView();
        updateTaskPanel();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel.setBackground(new Color(99, 102, 241));
        
        // Navigation
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        navPanel.setOpaque(false);
        
        JButton prevBtn = createIconButton("Prev");
        JButton todayBtn = createPrimaryButton("Hari Ini");
        JButton nextBtn = createIconButton("Next");
        
        monthYearLabel = new JLabel();
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        monthYearLabel.setForeground(Color.WHITE);
        updateMonthYearLabel();
        
        prevBtn.addActionListener(e -> navigateMonth(-1));
        nextBtn.addActionListener(e -> navigateMonth(1));
        todayBtn.addActionListener(e -> goToToday());
        
        navPanel.add(prevBtn);
        navPanel.add(nextBtn);
        navPanel.add(Box.createHorizontalStrut(10));
        navPanel.add(todayBtn);
        navPanel.add(Box.createHorizontalStrut(20));
        navPanel.add(monthYearLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        viewModeCombo = new JComboBox<>(new String[]{"Tampilan Bulanan", "Tampilan Mingguan"});
        viewModeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        viewModeCombo.setBackground(new Color(129, 140, 248));
        viewModeCombo.setForeground(Color.BLACK);
        viewModeCombo.setBorder(new EmptyBorder(8, 15, 8, 15));
        viewModeCombo.setFocusable(false);
        viewModeCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Custom renderer untuk dropdown items
        viewModeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                label.setOpaque(true);
                label.setBorder(new EmptyBorder(8, 15, 8, 15));
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                if (isSelected) {
                    label.setBackground(new Color(99, 102, 241));
                    label.setForeground(Color.WHITE);
                } else if (index == -1) {
                    label.setBackground(new Color(129, 140, 248));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(new Color(30, 41, 59));
                }
                return label;
            }
        });
        
        viewModeCombo.addActionListener(e -> changeViewMode());
        
        JButton addTaskBtn = createAccentButton("+ Tambah Tugas");
        addTaskBtn.addActionListener(e -> showAddTaskDialog());
        
        rightPanel.add(viewModeCombo);
        rightPanel.add(addTaskBtn);
        
        panel.add(navPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JButton createIconButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(new Color(129, 140, 248));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(165, 180, 252));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(129, 140, 248));
            }
        });
        
        return btn;
    }
    
    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(new Color(129, 140, 248));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(165, 180, 252));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(129, 140, 248));
            }
        });
        
        return btn;
    }
    
    private JButton createAccentButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBackground(new Color(16, 185, 129));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(52, 211, 153));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(16, 185, 129));
            }
        });
        
        return btn;
    }



    private void updateMonthYearLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy",  new Locale("id", "ID"));
        monthYearLabel.setText(currentDate.format(formatter));
    }

    private void navigateMonth(int direction) {
        currentDate = currentDate.plusMonths(direction);
        updateMonthYearLabel();
        updateCalendarView();
    }

    private void goToToday() {
        currentDate = LocalDate.now();
        selectedDate = LocalDate.now();
        updateMonthYearLabel();
        updateCalendarView();
        updateTaskPanel();
    }

    private void changeViewMode() {
        int index = viewModeCombo.getSelectedIndex();
        currentMode = (index == 0) ? ViewMode.HARIAN : ViewMode.MINGGUAN;
        updateCalendarView();
    }

    private void updateCalendarView() {
        calendarPanel.removeAll();
        
        if (currentMode == ViewMode.HARIAN) {
            createMonthlyCalendar();
        } else {
            createWeeklyCalendar();
        }
        
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            
            if (icon.getIconWidth() <= 0) {
                icon = new ImageIcon(path);
            }
            
            if (icon.getIconWidth() > 0) {
                Image scaledImage = icon.getImage().getScaledInstance(
                    width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + path);
        }
        
        return new ImageIcon();
    }
    
    private JLabel createIconLabel(String iconPath, int size, String fallbackEmoji) {
        ImageIcon icon = loadIcon(iconPath, size, size);
        
        if (icon.getIconWidth() > 0) {
            return new JLabel(icon);
        }
        
        JLabel label = new JLabel(fallbackEmoji);
        label.setFont(new Font("Segoe UI", Font.PLAIN, size));
        return label;
    }

    private void createMonthlyCalendar() {
        calendarPanel.setLayout(new BorderLayout(0, 10));
        calendarPanel.setBackground(Color.WHITE);
        
        // Header hari
        JPanel headerPanel = new JPanel(new GridLayout(1, 7, 2, 2));
        headerPanel.setBackground(Color.WHITE);
        String[] days = {"Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
        for (String day : days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setForeground(new Color(100, 116, 139));
            label.setBorder(new EmptyBorder(10, 5, 10, 5));
            headerPanel.add(label);
        }
        
        // Grid tanggal
        JPanel gridPanel = new JPanel(new GridLayout(0, 7, 8, 8));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        LocalDate firstDay = currentDate.withDayOfMonth(1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = currentDate.lengthOfMonth();
        
        for (int i = 0; i < startDayOfWeek; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(Color.WHITE);
            gridPanel.add(emptyPanel);
        }
        
        // Tambah tanggal
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentDate.withDayOfMonth(day);
            JPanel dayPanel = createDayPanel(date);
            gridPanel.add(dayPanel);
        }
        
        calendarPanel.add(headerPanel, BorderLayout.NORTH);
        calendarPanel.add(gridPanel, BorderLayout.CENTER);
    }

    private JPanel createDayPanel(LocalDate date) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(12, 8, 12, 8)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setPreferredSize(new Dimension(100, 85));
        
        boolean isToday = date.equals(LocalDate.now());
        boolean isSelected = date.equals(selectedDate);
        boolean isCurrentMonth = date.getMonth() == currentDate.getMonth();
        
        // Styling berdasarkan status
        if (isSelected) {
            panel.setBackground(new Color(99, 102, 241));
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(99, 102, 241), 2),
                new EmptyBorder(11, 7, 11, 7)
            ));
        } else if (isToday) {
            panel.setBackground(new Color(224, 231, 255));
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(99, 102, 241), 1),
                new EmptyBorder(11, 7, 11, 7)
            ));
        }
        
        // Label tanggal
        JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        if (isSelected) {
            dayLabel.setForeground(Color.WHITE);
        } else if (isToday) {
            dayLabel.setForeground(new Color(99, 102, 241));
        } else if (!isCurrentMonth) {
            dayLabel.setForeground(new Color(203, 213, 225));
        } else {
            dayLabel.setForeground(new Color(30, 41, 59));
        }
        
        panel.add(dayLabel, BorderLayout.NORTH);
        
        // Tanda visual jika ada tugas
        if (tasksByDate.containsKey(date) && !tasksByDate.get(date).isEmpty()) {
            JPanel indicatorPanel = new JPanel();
            indicatorPanel.setLayout(new BoxLayout(indicatorPanel, BoxLayout.Y_AXIS));
            indicatorPanel.setOpaque(false);
            indicatorPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
            
            List<Task> tasks = tasksByDate.get(date);
            int displayCount = Math.min(tasks.size(), 3);
            
            for (int i = 0; i < displayCount; i++) {
                JPanel taskIndicator = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
                taskIndicator.setOpaque(false);
                
                JPanel colorDot = new JPanel();
                colorDot.setPreferredSize(new Dimension(8, 8));
                colorDot.setBackground(getPriorityColor(tasks.get(i).priority));
                colorDot.setBorder(BorderFactory.createLineBorder(
                    isSelected ? Color.WHITE : getPriorityColor(tasks.get(i).priority), 1));
                
                JLabel taskLabel = new JLabel(" " + tasks.get(i).title);
                taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                
                if (isSelected) {
                    taskLabel.setForeground(Color.WHITE);
                } else {
                    taskLabel.setForeground(new Color(71, 85, 105));
                }
                
                String text = tasks.get(i).title;
                if (text.length() > 12) {
                    text = text.substring(0, 12) + "...";
                }
                taskLabel.setText(" " + text);
                
                taskIndicator.add(colorDot);
                taskIndicator.add(taskLabel);
                indicatorPanel.add(taskIndicator);
            }
            
            if (tasks.size() > 3) {
                JLabel moreLabel = new JLabel("+" + (tasks.size() - 3) + " lainnya");
                moreLabel.setFont(new Font("Segoe UI", Font.ITALIC, 9));
                moreLabel.setForeground(isSelected ? Color.WHITE : new Color(148, 163, 184));
                moreLabel.setBorder(new EmptyBorder(3, 0, 0, 0));
                indicatorPanel.add(moreLabel);
            }
            
            panel.add(indicatorPanel, BorderLayout.CENTER);
        }
        
        // Click event
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectedDate = date;
                updateCalendarView();
                updateTaskPanel();
            }
            
            public void mouseEntered(MouseEvent e) {
                if (!date.equals(selectedDate)) {
                    panel.setBackground(new Color(241, 245, 249));
                }
            }
            
            public void mouseExited(MouseEvent e) {
                if (!date.equals(selectedDate)) {
                    if (date.equals(LocalDate.now())) {
                        panel.setBackground(new Color(224, 231, 255));
                    } else {
                        panel.setBackground(Color.WHITE);
                    }
                }
            }
        });
        
        return panel;
    }

    private void createWeeklyCalendar() {
        calendarPanel.setLayout(new BorderLayout());
        
        // Hitung minggu dari tanggal terpilih
        LocalDate startOfWeek = selectedDate.with(DayOfWeek.MONDAY);
        
        JPanel headerPanel = new JPanel(new GridLayout(1, 7));
        JPanel contentPanel = new JPanel(new GridLayout(1, 7, 5, 5));
        contentPanel.setBackground(Color.WHITE);
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            
            // Header
            String dayName = date.getDayOfWeek() .getDisplayName(TextStyle.SHORT, new Locale("id", "ID"));
            JLabel headerLabel = new JLabel(dayName + " " + date.getDayOfMonth(), SwingConstants.CENTER);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
            headerLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
            
            if (date.equals(LocalDate.now())) {
                headerLabel.setForeground(new Color(63, 81, 181));
            }
            
            headerPanel.add(headerLabel);
            
            // Content
            JPanel dayColumn = createWeekDayColumn(date);
            contentPanel.add(dayColumn);
        }
        
        calendarPanel.add(headerPanel, BorderLayout.NORTH);
        calendarPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createWeekDayColumn(LocalDate date) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        if (date.equals(selectedDate)) {
            panel.setBackground(new Color(227, 242, 253));
            panel.setBorder(BorderFactory.createLineBorder(new Color(63, 81, 181), 2));
        }
        
        if (tasksByDate.containsKey(date)) {
            List<Task> tasks = tasksByDate.get(date);
            for (Task task : tasks) {
                JPanel taskItem = new JPanel(new BorderLayout());
                taskItem.setBackground(getPriorityColor(task.priority).brighter().brighter());
                taskItem.setBorder(new EmptyBorder(5, 5, 5, 5));
                taskItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                
                JLabel titleLabel = new JLabel("<html><b>" + task.title + "</b></html>");
                titleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                JLabel subjectLabel = new JLabel(task.subject);
                subjectLabel.setFont(new Font("Arial", Font.PLAIN, 10));
                
                taskItem.add(titleLabel, BorderLayout.NORTH);
                taskItem.add(subjectLabel, BorderLayout.CENTER);
                
                panel.add(taskItem);
                panel.add(Box.createVerticalStrut(3));
            }
        } else {
            JLabel emptyLabel = new JLabel("Tidak ada tugas", SwingConstants.CENTER);
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 11));
            panel.add(Box.createVerticalGlue());
            panel.add(emptyLabel);
            panel.add(Box.createVerticalGlue());
        }
        
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectedDate = date;
                updateCalendarView();
                updateTaskPanel();
            }
        });
        
        return panel;
    }

    private void updateTaskPanel() {
        taskPanel.removeAll();
        taskPanel.setBackground(new Color(248, 249, 250));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", new Locale("id", "ID"));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel(selectedDate.format(formatter));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 41, 59));
        
        JLabel countLabel = new JLabel();
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countLabel.setForeground(new Color(100, 116, 139));
        
        int taskCount = tasksByDate.containsKey(selectedDate) ? 
                       tasksByDate.get(selectedDate).size() : 0;
        countLabel.setText(taskCount + " tugas");
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(countLabel, BorderLayout.SOUTH);
        
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        taskPanel.add(headerPanel);
        
        if (tasksByDate.containsKey(selectedDate) && !tasksByDate.get(selectedDate).isEmpty()) {
            List<Task> tasks = tasksByDate.get(selectedDate);
            
            for (Task task : tasks) {
                JPanel taskCard = createTaskCard(task);
                taskCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                taskPanel.add(taskCard);
                taskPanel.add(Box.createVerticalStrut(12));
            }
        } else {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setOpaque(false);
            emptyPanel.setBorder(new EmptyBorder(40, 20, 40, 20));
            
            JLabel emptyIcon = createIconLabel("/icons/calendar.png", 64, "📅");
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel emptyLabel = new JLabel("Tidak ada tugas", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            emptyLabel.setForeground(new Color(148, 163, 184));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel emptySubLabel = new JLabel("Tambahkan tugas baru untuk tanggal ini", SwingConstants.CENTER);
            emptySubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            emptySubLabel.setForeground(new Color(203, 213, 225));
            emptySubLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            emptyPanel.add(emptyIcon);
            emptyPanel.add(Box.createVerticalStrut(10));
            emptyPanel.add(emptyLabel);
            emptyPanel.add(Box.createVerticalStrut(5));
            emptyPanel.add(emptySubLabel);
            
            emptyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            taskPanel.add(emptyPanel);
        }
        
        taskPanel.add(Box.createVerticalGlue());
        taskPanel.revalidate();
        taskPanel.repaint();
    }

    private JPanel createTaskCard(Task task) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 250, 252));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                    new EmptyBorder(20, 20, 20, 20)
                ));
            }
            
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                    new EmptyBorder(20, 20, 20, 20)
                ));
            }
        });
        
        JPanel priorityBar = new JPanel();
        priorityBar.setBackground(getPriorityColor(task.priority));
        priorityBar.setPreferredSize(new Dimension(4, 0));
        card.add(priorityBar, BorderLayout.WEST);
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Title
        JLabel titleLabel = new JLabel(task.title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(30, 41, 59));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel subjectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        subjectPanel.setOpaque(false);
        subjectPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subjectIcon = createIconLabel("/icons/book.png", 16, "📚 ");
        
        JLabel subjectLabel = new JLabel(task.subject);
        subjectLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subjectLabel.setForeground(new Color(100, 116, 139));
        
        subjectPanel.add(subjectIcon);
        subjectPanel.add(subjectLabel);
        
        JPanel deadlinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        deadlinePanel.setOpaque(false);
        deadlinePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));
      
        JLabel deadlineIcon = createIconLabel("/icons/clock.png", 16, "⏰ ");
        
        JLabel deadlineLabel = new JLabel("Deadline: " + task.deadline.format(formatter));
        deadlineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Cek jika deadline dekat
        long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), task.deadline);
        if (daysUntil < 0) {
            deadlineLabel.setText("Deadline: " + task.deadline.format(formatter) + " (Terlewat)");
            deadlineLabel.setForeground(new Color(220, 38, 38));
            deadlineLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        } else if (daysUntil == 0) {
            deadlineLabel.setText("Deadline: Hari ini");
            deadlineLabel.setForeground(new Color(234, 88, 12));
            deadlineLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        } else if (daysUntil <= 2) {
            deadlineLabel.setText("Deadline: " + task.deadline.format(formatter) + 
                                 " (" + daysUntil + " hari lagi)");
            deadlineLabel.setForeground(new Color(234, 88, 12));
            deadlineLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        } else {
            deadlineLabel.setForeground(new Color(100, 116, 139));
        }
        
        deadlinePanel.add(deadlineIcon);
        deadlinePanel.add(deadlineLabel);
        
        JPanel priorityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        priorityPanel.setOpaque(false);
        priorityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel priorityBadge = new JLabel(task.priority);
        priorityBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        priorityBadge.setForeground(Color.WHITE);
        priorityBadge.setOpaque(true);
        priorityBadge.setBackground(getPriorityColor(task.priority));
        priorityBadge.setBorder(new EmptyBorder(4, 12, 4, 12));
        
        priorityPanel.add(priorityBadge);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(subjectPanel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(deadlinePanel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(priorityPanel);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }

    private Color getPriorityColor(String priority) {
        switch (priority.toLowerCase()) {
            case "tinggi":
                return new Color(239, 68, 68);
            case "sedang":
                return new Color(251, 146, 60);
            case "rendah":
                return new Color(34, 197, 94);
            default:
                return new Color(148, 163, 184);
        }
    }

    private void showAddTaskDialog() {
        JDialog dialog = new JDialog(this, "Tambah Tugas Baru", true);
        dialog.setSize(450, 650);
        dialog.setLocationRelativeTo(this);
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
        
        JComboBox<String> priorityCombo = new JComboBox<>(
            new String[]{"Tinggi", "Sedang", "Rendah"});
        priorityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priorityCombo.setBackground(Color.WHITE);
        priorityCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateSpinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        JSpinner deadlineSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor deadlineEditor = new JSpinner.DateEditor(deadlineSpinner, "dd/MM/yyyy");
        deadlineSpinner.setEditor(deadlineEditor);
        deadlineSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deadlineSpinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        // Add fields dengan labels
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
            new EmptyBorder(10, 20, 10, 20)
        ));
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
            if (titleField.getText().trim().isEmpty() || 
                subjectField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Judul dan Mata Kuliah harus diisi!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Date date = (Date) dateSpinner.getValue();
            Date deadline = (Date) deadlineSpinner.getValue();
            
            LocalDate taskDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate deadlineDate = deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            addTask(taskDate, titleField.getText(), subjectField.getText(), deadlineDate, (String) priorityCombo.getSelectedItem());
            
            updateCalendarView();
            updateTaskPanel();
            dialog.dispose();
            
            JOptionPane.showMessageDialog(this, "Tugas berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        mainPanel.add(buttonPanel);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        return field;
    }
    
    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(71, 85, 105));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(field);
        panel.add(Box.createVerticalStrut(15));
    }

    // Inner class untuk Task
    private static class Task {
        String title;
        String subject;
        LocalDate deadline;
        String priority;
        
        Task(String title, String subject, LocalDate deadline, String priority) {
            this.title = title;
            this.subject = subject;
            this.deadline = deadline;
            this.priority = priority;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new KalenderTugas();
        });
    }
}