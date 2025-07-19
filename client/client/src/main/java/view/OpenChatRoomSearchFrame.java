package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OpenChatRoomSearchFrame extends JFrame {
    private Point initialClick;
    private JButton exit, hide;
    private JTextField searchField;
    private JButton searchBtn;
    private JList<String[]> chatList;

    public OpenChatRoomSearchFrame() {
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. 최상단 커스텀 top 패널
        JPanel top = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        top.setLayout(new BorderLayout());
        top.setOpaque(false);
        top.setPreferredSize(new Dimension(0, 32));

        JLabel titleLabel = new JLabel("오픈채팅 목록");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        top.add(titleLabel, BorderLayout.WEST);

        JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightBtnPanel.setOpaque(false);

        ImageIcon exListIcon = new ImageIcon("./image/exit.png");
        Image scaledexListImg = exListIcon.getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH);
        exit = new JButton(new ImageIcon(scaledexListImg));
        exit.setPreferredSize(new Dimension(20, 20));
        exit.setMargin(new Insets(0, 0, 0, 0));
        exit.setOpaque(false);
        exit.setContentAreaFilled(false);
        exit.setBorderPainted(false);
        exit.addActionListener(e -> dispose()); // System.exit(0) 대신 창만 닫기

        ImageIcon mnListIcon = new ImageIcon("./image/minimize.png");
        Image scaledmnListImg = mnListIcon.getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH);
        hide = new JButton(new ImageIcon(scaledmnListImg));
        hide.setPreferredSize(new Dimension(30, 20));
        hide.setMargin(new Insets(0, 0, 0, 0));
        hide.setOpaque(false);
        hide.setContentAreaFilled(false);
        hide.setBorderPainted(false);
        hide.addActionListener(e -> setState(JFrame.ICONIFIED));

        rightBtnPanel.add(hide);
        rightBtnPanel.add(exit);
        top.add(rightBtnPanel, BorderLayout.EAST);

        top.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        top.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - initialClick.x;
                int dy = e.getY() - initialClick.y;
                Point p = getLocation();
                setLocation(p.x + dx, p.y + dy);
            }
        });
        add(top, BorderLayout.NORTH);

        // 2. 검색 영역
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(120, 23));
        searchField.setBackground(Color.WHITE);
        searchField.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon searchIcon = new ImageIcon("./image/search.png");
        Image scaledSearchImg = searchIcon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
        searchBtn = new JButton(new ImageIcon(scaledSearchImg));
        searchBtn.setPreferredSize(new Dimension(32, 28));
        searchBtn.setBackground(Color.WHITE);
        searchBtn.setBorderPainted(false);
        searchBtn.setContentAreaFilled(false);
        searchBtn.setOpaque(true);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> {
            String input = searchField.getText().trim();
            if(input.isEmpty()) {
                JOptionPane.showMessageDialog(null, "오픈채팅방 이름을 입력해주세요.");
            }
        });
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(new JSeparator(), BorderLayout.CENTER);

        // 3. 채팅방 목록 리스트
        String[][] dummyData = new String[10][3];
        for (int i = 0; i < 10; i++) {
            dummyData[i][0] = "오픈 채팅방(익명) " + (i + 1);
            dummyData[i][1] = "최근 메시지 미리보기 " + (i + 1);
            dummyData[i][2] = "./profile/chatRoom/test.jpg";
        }

        chatList = new JList<>(dummyData);
        // 공통 ChatRoomCellRenderer
        chatList.setCellRenderer(new ChatRoomCellRenderer());
        chatList.setFixedCellHeight(70);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane chatListScroll = new JScrollPane(chatList);
        chatListScroll.setBorder(BorderFactory.createLineBorder(new Color(0xD3D3D3)));

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(centerPanel, BorderLayout.NORTH);
        listPanel.add(chatListScroll, BorderLayout.CENTER);

        add(listPanel, BorderLayout.CENTER);

        chatList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int idx = chatList.locationToIndex(e.getPoint());
                    if (idx >= 0) {
                        openChatRoomWindow((String[]) chatList.getModel().getElementAt(idx));
                    }
                }
            }
        });
    }

    private void openChatRoomWindow(String[] data) {
        JFrame chatWindow = new JFrame(data[0]);
        chatWindow.setSize(400, 500);
        chatWindow.setLocationRelativeTo(this);
        chatWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel chatingPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel(data[0], SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        JTextArea messageArea = new JTextArea("여기에 메시지가 표시됩니다...");
        messageArea.setEditable(false);

        JPanel sendPanel = new JPanel(new BorderLayout(5, 0));
        JTextField messageInput = new JTextField();
        JButton send = new JButton("전송");
        sendPanel.add(messageInput, BorderLayout.CENTER);
        sendPanel.add(send, BorderLayout.EAST);

        chatingPanel.add(sendPanel, BorderLayout.SOUTH);
        chatingPanel.add(title, BorderLayout.NORTH);
        chatingPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        chatWindow.add(chatingPanel);

        chatWindow.setVisible(true);
    }
}
