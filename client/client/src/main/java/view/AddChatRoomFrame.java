package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddChatRoomFrame extends JFrame {

    private Point initialClick;

    public AddChatRoomFrame() {
        setTitle("채팅방 생성");
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
            }
        });

        // 상단 드래그 및 타이틀 영역 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("채팅방 생성");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));
        topPanel.add(titleLabel, BorderLayout.WEST);

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

        // 탭 패널
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);

        JPanel oneToOnePanel = createCreationPanel("1:1");
        JPanel teamPanel = createCreationPanel("팀");
        // 오픈채팅 패널 제거
        // JPanel openPanel = createCreationPanel("오픈");

        tabbedPane.addTab("1:1", oneToOnePanel);
        tabbedPane.addTab("팀", teamPanel);
        // 오픈채팅 탭 제거
        // tabbedPane.addTab("오픈", openPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCreationPanel(String type) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 방제목 입력 필드와 라벨 추가
        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("방 제목 :");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        JTextField titleField = new JTextField();
        titleField.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(titleField, BorderLayout.CENTER);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 실제 친구목록 연동
        DefaultListModel<FriendCheckboxItem> friendListModel = new DefaultListModel<>();
        java.util.List<String> friendPhones = model.DBManager.getInstance().loadFriendList();
        for (String phone : friendPhones) {
            String name = phone;
            try (java.sql.Connection conn = model.DBManager.getInstance().getConnection();
                 java.sql.PreparedStatement pstmt = conn.prepareStatement("SELECT name FROM UserData WHERE phoneNum = ? LIMIT 1")) {
                pstmt.setString(1, phone);
                java.sql.ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    name = rs.getString("name");
                }
            } catch (Exception ex) { ex.printStackTrace(); }
            friendListModel.addElement(new FriendCheckboxItem(name + " (" + phone + ")", phone));
        }

        JList<FriendCheckboxItem> friendList = new JList<>(friendListModel);
        friendList.setCellRenderer(new CheckboxListRenderer());
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setBackground(Color.WHITE);

        friendList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = friendList.locationToIndex(e.getPoint());
                if (index != -1) {
                    FriendCheckboxItem clickedItem = friendListModel.getElementAt(index);
                    boolean isGoingToBeSelected = !clickedItem.isSelected();
                    if ("1:1".equals(type) && isGoingToBeSelected) {
                        for (int i = 0; i < friendListModel.getSize(); i++) {
                            friendListModel.getElementAt(i).setSelected(false);
                        }
                    }
                    clickedItem.setSelected(isGoingToBeSelected);
                    friendList.repaint();
                }
            }
        });

        JScrollPane friendScrollPane = new JScrollPane(friendList);
        friendScrollPane.setBorder(new TitledBorder("친구 목록 (참여할 친구 선택)"));
        friendScrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(friendScrollPane, BorderLayout.CENTER);

        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomButtonPanel.setBackground(Color.WHITE);
        JButton createButton = new JButton("방 생성");
        JButton cancelButton = new JButton("취소");
        cancelButton.setContentAreaFilled(false);

        createButton.setBackground(new Color(0xFEE500));
        createButton.setOpaque(true);
        createButton.setBorderPainted(false);
        createButton.setFocusPainted(false);
        createButton.setFont(new Font("맑은 고딕", Font.BOLD, 12));

        bottomButtonPanel.add(createButton);
        bottomButtonPanel.add(cancelButton);
        mainPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        createButton.addActionListener(e -> {
            String roomTitle = titleField.getText().trim();
            if (roomTitle.isEmpty()) {
                JOptionPane.showMessageDialog(this, "방 제목을 입력해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<String> selectedFriendPhones = new ArrayList<>();
            for (int i = 0; i < friendListModel.getSize(); i++) {
                FriendCheckboxItem item = friendListModel.getElementAt(i);
                if (item.isSelected()) {
                    selectedFriendPhones.add(item.getPhoneNum());
                }
            }
            if (selectedFriendPhones.isEmpty()) {
                JOptionPane.showMessageDialog(this, "참여자를 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // 실제 방 생성(DB 저장)
            List<model.ChatRoom> existingRooms = model.DBManager.getInstance().loadChatRooms();
            int maxNum = 0;
            for (model.ChatRoom r : existingRooms) {
                if (r.getChatRoomNum() > maxNum) maxNum = r.getChatRoomNum();
            }
            model.ChatRoom newRoom = new model.ChatRoom();
            newRoom.setChatRoomNum(maxNum + 1);
            newRoom.setRoomType(type);
            newRoom.setRoomName(roomTitle);

            // 1. 내 로컬 DB에 저장 (새로운 구조 사용)
            String myId = model.DBManager.getInstance().getLoggedInUser().getId();
            model.LocalDBManager localDB = new model.LocalDBManager();
            localDB.saveOrUpdateChatRoom(newRoom);
            localDB.saveChatRoomMember(newRoom.getChatRoomNum(), myId);

            // 2. 친구들도 로컬 DB에 멤버로 추가
            for (String friendPhone : selectedFriendPhones) {
                String friendId = model.DBManager.getInstance().getIdByPhoneNum(friendPhone);
                if (friendId != null) {
                    localDB.saveChatRoomMember(newRoom.getChatRoomNum(), friendId);
                }
            }
            // [추가] 서버에 참여자 정보 동기화 요청
            java.util.List<String> memberIds = new java.util.ArrayList<>();
            memberIds.add(myId);
            for (String friendPhone : selectedFriendPhones) {
                String friendId = model.DBManager.getInstance().getIdByPhoneNum(friendPhone);
                if (friendId != null) memberIds.add(friendId);
            }
            StringBuilder msg = new StringBuilder("%CreateChatRoom%&chatRoomNum$").append(newRoom.getChatRoomNum());
            for (String id : memberIds) {
                msg.append("&id$").append(id);
            }
            msg.append("%");
            System.out.println("[DEBUG] 그룹 채팅방 생성 요청: " + msg.toString());
            String response = model.TCPManager.getInstance().sendSyncMessage(msg.toString());
            System.out.println("[DEBUG] 서버 응답: " + response);
            
            // 채팅방 생성 후 로컬 DB 동기화
            if (response != null && response.contains("success$true")) {
                String currentUserId = model.DBManager.getInstance().getLoggedInUser().getId();
                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                        // 서버에서 전체 채팅방 목록을 다시 요청하여 로컬 DB 동기화
                        String syncMsg = "%chatListLoad%&id$" + currentUserId + "%";
                        String syncResponse = model.TCPManager.getInstance().sendSyncMessage(syncMsg);
                        if (syncResponse != null && syncResponse.startsWith("%chatListLoad%")) {
                            controller.MainController.getInstance().onMessageReceived(syncResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
            
            JOptionPane.showMessageDialog(this, "방이 생성되었습니다!\n방 제목: " + roomTitle);
            dispose();
        });
        cancelButton.addActionListener(e -> dispose());
        return mainPanel;
    }

    // JList 아이템 데이터 모델 클래스
    class FriendCheckboxItem {
        private final String name;
        private final String phoneNum;
        private boolean selected;
        public FriendCheckboxItem(String name, String phoneNum) {
            this.name = name;
            this.phoneNum = phoneNum;
            this.selected = false;
        }
        public boolean isSelected() { return selected; }
        public void setSelected(boolean selected) { this.selected = selected; }
        public String getName() { return name; }
        public String getPhoneNum() { return phoneNum; }
    }

    // JList를 체크박스 형태로 보여주는 렌더러 클래스
    class CheckboxListRenderer extends JCheckBox implements ListCellRenderer<FriendCheckboxItem> {
        @Override
        public Component getListCellRendererComponent(JList<? extends FriendCheckboxItem> list, FriendCheckboxItem value, int index, boolean isSelected, boolean cellHasFocus) {
            setEnabled(list.isEnabled());
            setSelected(value.isSelected());
            setFont(list.getFont());
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            setText(value.getName());
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            return this;
        }
    }
}
