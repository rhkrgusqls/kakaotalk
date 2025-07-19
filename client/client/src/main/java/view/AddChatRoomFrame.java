package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

public class AddChatRoomFrame extends JFrame {
	Point initalClick;
	
    public AddChatRoomFrame() {
    	setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 현재 창만 닫기
        setSize(450, 350);
        setLocationRelativeTo(null); // 화면 중앙에 배치
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        
        tabbedPane.addMouseListener(new MouseAdapter() { // 화면 드래그 기능
			public void mousePressed(MouseEvent e) {
				initalClick = e.getPoint();
			}
		});
		
        tabbedPane.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				Point currentLocation = getLocation();
				int deltaX = e.getX() - initalClick.x;
				int deltaY = e.getY() - initalClick.y;
				
				setLocation(currentLocation.x + deltaX, currentLocation.y + deltaY);
				
			}
		});
        
        // 각 탭에 들어갈 패널 생성
        JPanel oneToOnePanel = createCreationPanel("1:1");
        JPanel teamPanel = createCreationPanel("팀");
        JPanel openPanel = createCreationPanel("오픈");

        // 탭 추가
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 친구 목록 ---
        DefaultListModel<String> friendListModel = new DefaultListModel<>();
        // 더미 데이터 추가
        for (int i = 1; i <= 10; i++) {
            friendListModel.addElement("친구 " + i);
        }
        JList<String> friendList = new JList<>(friendListModel);

        // 타입에 따라 선택 모드 설정
        if ("1:1".equals(type)) {
            friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } else {
            friendList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }

        JScrollPane friendScrollPane = new JScrollPane(friendList);
        friendScrollPane.setBorder(new TitledBorder("친구 목록")); // 테두리와 제목

        // --- 참여자 목록 ---
        DefaultListModel<String> participantListModel = new DefaultListModel<>();
        JList<String> participantList = new JList<>(participantListModel);
        JScrollPane participantScrollPane = new JScrollPane(participantList);
        participantScrollPane.setBorder(new TitledBorder("참여자")); // 테두리와 제목

        // --- 친구 추가/제거 ---
        JPanel buttonPanel = new JPanel(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0); 
        JButton addButton = new JButton(">>");
        JButton removeButton = new JButton("<<");
        gbc.gridy = 0; // 첫 번째 행
        buttonPanel.add(addButton, gbc);
        gbc.gridy = 1; // 두 번째 행
        buttonPanel.add(removeButton, gbc);

        // 친구 추가 이벤트
        addButton.addActionListener((ActionEvent e) -> {
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

        // 친구 제거 이벤트
        removeButton.addActionListener(e -> {
            List<String> selectedParticipants = participantList.getSelectedValuesList();
            for (String participant : selectedParticipants) {
                friendListModel.addElement(participant);
                participantListModel.removeElement(participant);
            }
        });

        // 목록과 버튼을 담을 중앙 패널
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        centerPanel.add(friendScrollPane);
        centerPanel.add(buttonPanel);
        centerPanel.add(participantScrollPane);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- 하단 버튼 (방 생성, 취소) ---
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createButton = new JButton("방 생성");
        JButton cancelButton = new JButton("취소");
        bottomButtonPanel.add(createButton);
        bottomButtonPanel.add(cancelButton);
        mainPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        // 방 생성 이벤트 (TODO: 실제 로직 구현)
        createButton.addActionListener(e -> {
            if (participantListModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "참여자를 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // 실제 방 생성 로직을 여기에 구현합니다.
            String message = String.format("'%s' 타입의 채팅방을 생성합니다.\n참여자: %s", type, participantListModel.toString());
            JOptionPane.showMessageDialog(this, message);
            dispose(); // 생성 후 창 닫기
        });

        // 취소 버튼 이벤트
        cancelButton.addActionListener(e -> dispose()); // 현재 창 닫기

        return mainPanel;
    }
}
