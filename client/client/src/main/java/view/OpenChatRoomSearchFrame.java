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
                // 완전 흰색 배경
                int width = getWidth();
                int height = getHeight();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, width, height);
            }
        };
        // top: 좌측엔 타이틀, 우측엔 최소화 닫기
        top.setLayout(new BorderLayout());
        top.setOpaque(false);
        top.setPreferredSize(new Dimension(0, 32));

        // 좌측 타이틀
        JLabel titleLabel = new JLabel("오픈채팅 목록");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        top.add(titleLabel, BorderLayout.WEST);

        // 우측 버튼 패널
        JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightBtnPanel.setOpaque(false);

        // 닫기 버튼
        ImageIcon exListIcon = new ImageIcon("./image/exit.png");
        Image scaledexListImg = exListIcon.getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH);
        exit = new JButton(new ImageIcon(scaledexListImg));
        exit.setFont(new Font("Arial", Font.BOLD, 10));
        exit.setOpaque(false);
        exit.setContentAreaFilled(false);
        exit.setBorderPainted(false);
        exit.setPreferredSize(new Dimension(20, 20));
        exit.setMargin(new Insets(0, 0, 0, 0));
        exit.addActionListener(e -> System.exit(0));

        // 최소화 버튼
        ImageIcon mnListIcon = new ImageIcon("./image/minimize.png");
        Image scaledmnListImg = mnListIcon.getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH);
        hide = new JButton(new ImageIcon(scaledmnListImg));
        hide.setFont(new Font("Arial", Font.BOLD, 20));
        hide.setFocusPainted(false);
        hide.setOpaque(false);
        hide.setContentAreaFilled(false);
        hide.setBorderPainted(false);
        hide.setPreferredSize(new Dimension(30, 20));
        hide.setMargin(new Insets(0, 0, 0, 0));
        hide.addActionListener(e -> setState(JFrame.ICONIFIED));

        rightBtnPanel.add(hide);
        rightBtnPanel.add(exit);
        top.add(rightBtnPanel, BorderLayout.EAST);

        // 창 드래그 이동
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

        // 2. 검색 영역 (텍스트필드 + 검색Btn)
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
			//TODO 오픈채팅방 이름입력에 따른 검색버튼입니다, 이름검색에 따른 오픈채팅방 리스트 표기 구현필요
		});
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        // ------ Center 영역 패널로 그룹핑 ------
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // [여기서 리스트와 검색창 사이 구분선!]
        centerPanel.add(new JSeparator(), BorderLayout.CENTER);

        // 3. 채팅방 목록 리스트 (더미 데이터 + 커스텀 렌더러 예시)
        String[][] dummyData = new String[10][3];
        for (int i = 0; i < 10; i++) {
            dummyData[i][0] = "오픈 채팅방(익명) " + (i + 1);
            dummyData[i][1] = "최근 메시지 미리보기 " + (i + 1);
            dummyData[i][2] = "./profile/chatRoom/test.jpg";
        }

        chatList = new JList<>(dummyData);
        chatList.setCellRenderer(new ChatRoomCellRenderer());
        chatList.setFixedCellHeight(70);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane chatListScroll = new JScrollPane(chatList);
        // 리스트에 얇은 회색 테두리
        chatListScroll.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createLineBorder(new Color(0xD3D3D3)), // 연회색
              BorderFactory.createEmptyBorder(0,0,0,0)
        ));

        // 리스트가 separator 아래로 오도록
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.add(centerPanel, BorderLayout.NORTH);         // 검색 + separator
        listPanel.add(chatListScroll, BorderLayout.CENTER);     // 리스트

        add(listPanel, BorderLayout.CENTER);

        // 더블클릭 채팅방 열기
        chatList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int idx = chatList.locationToIndex(e.getPoint());
                if (idx >= 0) {
                    String[] roomData = (String[]) chatList.getModel().getElementAt(idx);
                    if (e.getClickCount() == 2) { // 더블클릭
                        openChatRoomWindow(roomData);
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

    // 채팅방 셀 커스텀 렌더러
    static class ChatRoomCellRenderer extends JPanel implements ListCellRenderer<String[]> {
        private JLabel iconLabel = new JLabel();
        private JLabel topLabel = new JLabel();
        private JLabel bottomLabel = new JLabel();

        public ChatRoomCellRenderer() {
            setLayout(new BorderLayout(10, 0));
            iconLabel.setPreferredSize(new Dimension(45, 45));
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            topLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
            bottomLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
            textPanel.add(topLabel);
            textPanel.add(bottomLabel);
            add(iconLabel, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }
        @Override
        public Component getListCellRendererComponent(JList<? extends String[]> list, String[] value, int index, boolean isSelected, boolean cellHasFocus) {
            topLabel.setText(value[0]);
            bottomLabel.setText(value[1]);
            ImageIcon icon = new ImageIcon(value[2]);
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(img));
            } else {
                iconLabel.setIcon(null);
            }
            setBackground(isSelected ? new Color(0xEDEDED) : Color.WHITE);
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OpenChatRoomSearchFrame().setVisible(true));
    }
}
