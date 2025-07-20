package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
	    chatWindow.setSize(450, 600);
	    chatWindow.setLocationRelativeTo(null);
	    chatWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 독립된 창 닫기
	    
	    Color backgroundColor = Color.WHITE;

	    // 메인 컨테이너 패널
	    JPanel mainContainerPanel = new JPanel(new BorderLayout());
	    mainContainerPanel.setBackground(backgroundColor);

	    // 채팅방 제목 레이블
	    JLabel title = new JLabel(data[0], SwingConstants.CENTER);
	    title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
	    title.setOpaque(true); 
	    title.setBackground(backgroundColor);
	    title.setBorder(new EmptyBorder(10, 0, 10, 0));

	    // JSplitPane으로 채팅을 나눔

	    // 왼쪽(상대방) 채팅 영역
	    JTextArea otherUserChatArea = new JTextArea();
	    otherUserChatArea.setEditable(false);
	    otherUserChatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
	    otherUserChatArea.setBackground(backgroundColor);
	    otherUserChatArea.setBorder(new EmptyBorder(5, 5, 5, 5)); // 텍스트 영역 내부 여백
	    // 예시 상대 내용 추가
	    otherUserChatArea.append("안녕하세요!\n");
	    otherUserChatArea.append("만나서 반갑습니다.\n");

	    // 오른쪽(나) 채팅 영역
	    JTextArea myChatArea = new JTextArea();
	    myChatArea.setEditable(false);
	    myChatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
	    myChatArea.setBackground(backgroundColor);
	    myChatArea.setBorder(new EmptyBorder(5, 5, 5, 5)); // 텍스트 영역 내부 여백
	    // 예시 내 채팅 추가
	    myChatArea.append("네, 안녕하세요!\n");

	    // JSplitPane 생성 및 설정
	    JSplitPane splitPane = new JSplitPane(
	        JSplitPane.HORIZONTAL_SPLIT, // 좌우로 분할
	        new JScrollPane(otherUserChatArea),
	        new JScrollPane(myChatArea)
	    );
	    splitPane.setResizeWeight(0.5); // 양쪽 패널이 5:5 비율
	    splitPane.setDividerSize(0);    // 구분선 두께
	    splitPane.setBorder(null); 

	    // JScrollPane의 배경색도 흰색으로 설정
	    for (Component c : splitPane.getComponents()) {
	        if (c instanceof JScrollPane) {
	            ((JScrollPane) c).getViewport().setBackground(backgroundColor);
	            ((JScrollPane) c).setBorder(null);
	        }
	    }

	    // 하단 메시지 입력 및 전송 패널
	    JPanel sendPanel = new JPanel(new BorderLayout(5, 0));
	    sendPanel.setBackground(backgroundColor);
	    sendPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

	    JTextField messageInput = new JTextField();
	    JButton send = new JButton("전송");
	    sendPanel.add(messageInput, BorderLayout.CENTER);
	    sendPanel.add(send, BorderLayout.EAST);

	    // 메인 컨테이너에 컴포넌트들 최종 조립
	    mainContainerPanel.add(title, BorderLayout.NORTH);
	    mainContainerPanel.add(splitPane, BorderLayout.CENTER); // GridLayout 패널 대신 JSplitPane을 추가
	    mainContainerPanel.add(sendPanel, BorderLayout.SOUTH);

	    chatWindow.add(mainContainerPanel);
	    chatWindow.setVisible(true);

	    // splitPane이 화면에 표시된 후 divider 위치를 정확히 중앙으로 설정
	    SwingUtilities.invokeLater(() -> {
	        splitPane.setDividerLocation(0.5);
	    });
}
	}
