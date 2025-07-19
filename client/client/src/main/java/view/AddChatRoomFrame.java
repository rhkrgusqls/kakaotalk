package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import javax.swing.border.TitledBorder;

public class AddChatRoomFrame extends JFrame {

    private Point initialClick; // 드래그 시작점 저장용

    public AddChatRoomFrame() {
        // 프레임 기본 설정
        setTitle("채팅방 생성");
        setUndecorated(true); // 타이틀 바 제거
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 현재 창만 닫기
        setSize(550, 450);
        setLocationRelativeTo(null); // 화면 중앙에 배치
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE); // 프레임 배경 흰색

        // 프레임 모서리를 둥글게 처리
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
            }
        });

        // 1. 상단 드래그 및 타이틀 영역 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("채팅방 생성");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // 드래그 기능 추가
        topPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        topPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currentLocation = getLocation();
                int deltaX = e.getX() - initialClick.x;
                int deltaY = e.getY() - initialClick.y;
                setLocation(currentLocation.x + deltaX, currentLocation.y + deltaY);
            }
        });
        add(topPanel, BorderLayout.NORTH);

        // 2. 탭 패널
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);

        // 각 탭에 들어갈 패널 생성
        JPanel oneToOnePanel = createCreationPanel("1:1");
        JPanel teamPanel = createCreationPanel("팀");
        JPanel openPanel = createCreationPanel("오픈");

        tabbedPane.addTab("1:1", oneToOnePanel);
        tabbedPane.addTab("팀", teamPanel);
        tabbedPane.addTab("오픈", openPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * 채팅방 타입에 따라 친구/참여자 목록 패널을 생성하는 메서드
     * @param type "1:1", "팀", "오픈" 중 하나
     * @return 완성된 패널
     */
    private JPanel createCreationPanel(String type) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 친구 목록 ---
        DefaultListModel<String> friendListModel = new DefaultListModel<>();
        for (int i = 1; i <= 10; i++) {
            friendListModel.addElement("친구 " + i);
        }
        JList<String> friendList = new JList<>(friendListModel);
        friendList.setBackground(Color.WHITE);

        if ("1:1".equals(type)) {
            friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } else {
            friendList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }

        JScrollPane friendScrollPane = new JScrollPane(friendList);
        friendScrollPane.setBorder(new TitledBorder("친구 목록"));
        friendScrollPane.getViewport().setBackground(Color.WHITE); // 스크롤패인 배경 흰색

        // --- 참여자 목록 ---
        DefaultListModel<String> participantListModel = new DefaultListModel<>();
        JList<String> participantList = new JList<>(participantListModel);
        participantList.setBackground(Color.WHITE);
        JScrollPane participantScrollPane = new JScrollPane(participantList);
        participantScrollPane.setBorder(new TitledBorder("참여자"));
        participantScrollPane.getViewport().setBackground(Color.WHITE);

        // --- 중앙 버튼 (친구 추가/제거) ---
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);

        JButton addButton = new JButton(">>");
        addButton.setContentAreaFilled(false);
        JButton removeButton = new JButton("<<");
        removeButton.setContentAreaFilled(false);
        
        gbc.gridy = 0;
        buttonPanel.add(addButton, gbc);
        gbc.gridy = 1;
        buttonPanel.add(removeButton, gbc);

        addButton.addActionListener(e -> {
            List<String> selectedFriends = friendList.getSelectedValuesList();
            if ("1:1".equals(type) && !selectedFriends.isEmpty()) {
                if (participantListModel.getSize() > 0) {
                    JOptionPane.showMessageDialog(this, "1:1 채팅방에는 한 명만 초대할 수 있습니다.", "알림", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                participantListModel.addElement(selectedFriends.get(0));
                friendListModel.removeElement(selectedFriends.get(0));
            } else {
                for (String friend : selectedFriends) {
                    participantListModel.addElement(friend);
                    friendListModel.removeElement(friend);
                }
            }
        });

        removeButton.addActionListener(e -> {
            List<String> selectedParticipants = participantList.getSelectedValuesList();
            for (String participant : selectedParticipants) {
                friendListModel.addElement(participant);
                participantListModel.removeElement(participant);
            }
        });

        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(friendScrollPane);
        centerPanel.add(buttonPanel);
        centerPanel.add(participantScrollPane);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- 하단 버튼 (방 생성, 취소) ---
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomButtonPanel.setBackground(Color.WHITE);
        JButton createButton = new JButton("방 생성");
        JButton cancelButton = new JButton("취소");
        cancelButton.setContentAreaFilled(false);

        // "방 생성" 버튼 스타일 적용
        createButton.setBackground(new Color(0xFEE500));
        createButton.setOpaque(true);
        createButton.setBorderPainted(false);
        createButton.setFocusPainted(false);
        createButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));

        bottomButtonPanel.add(createButton);
        bottomButtonPanel.add(cancelButton);
        mainPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        // TODO: 방 생성 이벤트 (실제 로직 구현 필요)
        createButton.addActionListener(e -> {
            if (participantListModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "참여자를 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String message = String.format("'%s' 타입의 채팅방을 생성합니다.\n참여자: %s", type, participantListModel.toString());
            JOptionPane.showMessageDialog(this, message);
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        return mainPanel;
    }
}
