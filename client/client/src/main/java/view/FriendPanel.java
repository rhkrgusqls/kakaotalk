package view;

import javax.swing.*;
import java.awt.*;
import model.User;
import model.DBManager;
import model.Friend;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.ChatRoom;
import java.util.List;
import controller.MainController;

public class FriendPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public JButton searchBtn;
    public JButton addFriendsBtn;
    public JTextField friendSearchBar;
    public JList friendList;

    public void refreshFriendList() {
        DBManager db = DBManager.getInstance();
        System.out.println("[DEBUG] FriendPanel DB: " + db.getCurrentDBName());
        java.util.List<String> friends = db.loadFriendList();
        DefaultListModel<Friend> friendModel = new DefaultListModel<>();
        for (String phone : friends) {
            // 이름/프로필은 UserData에서 phoneNum으로 조회
            String name = phone;
            String profileDir = null;
            try (java.sql.Connection conn = db.getConnection();
                 java.sql.PreparedStatement pstmt = conn.prepareStatement("SELECT name, profileDir FROM UserData WHERE phoneNum = ? LIMIT 1")) {
                pstmt.setString(1, phone);
                java.sql.ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    name = rs.getString("name");
                    profileDir = rs.getString("profileDir");
                }
            } catch (Exception ex) { ex.printStackTrace(); }
            byte[] profileBytes = null;
            if (profileDir != null) {
                try {
                    java.nio.file.Path path = java.nio.file.Paths.get(profileDir);
                    if (java.nio.file.Files.exists(path)) {
                        profileBytes = java.nio.file.Files.readAllBytes(path);
                    }
                } catch (Exception ex) { /* ignore */ }
            }
            if (name == null || name.isEmpty()) name = phone;
            friendModel.addElement(new Friend(0, phone, name, profileBytes));
        }
        friendList.setModel(friendModel);
        friendList.setCellRenderer(new FriendCellRenderer());
        friendList.revalidate();
        friendList.repaint();
    }

    public FriendPanel(User loggedInUser) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        this.setBackground(new Color(0xFFFFFF));

        // 검색 + 친구추가 버튼 영역
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchPanel.setBackground(Color.white);
        friendSearchBar = new JTextField();
        friendSearchBar.setPreferredSize(new Dimension(220, 20));
        friendSearchBar.setBackground(Color.WHITE);
        friendSearchBar.setOpaque(true);
        friendSearchBar.setHorizontalAlignment(SwingConstants.CENTER);
        searchPanel.add(friendSearchBar);
        ImageIcon searchIcon = new ImageIcon("./image/search.png");
        Image scaledSearchImg = searchIcon.getImage().getScaledInstance(23, 23, Image.SCALE_SMOOTH);
        searchBtn = new JButton(new ImageIcon(scaledSearchImg));
        searchBtn.setPreferredSize(new Dimension(30, 30));
        searchBtn.setBackground(Color.WHITE);
        searchBtn.setBorderPainted(false);
        searchBtn.setContentAreaFilled(false);
        searchBtn.setOpaque(true);
        searchBtn.setFocusPainted(false);    
        searchBtn.addActionListener(e -> {
            String input = friendSearchBar.getText().trim();
            if(input.isEmpty()) {
                JOptionPane.showMessageDialog(null, "이름을 입력해주세요.");
                return;
            }
            // 친구 목록에서 검색어와 일치하는 친구만 필터링
            filterFriendList(input);
        });
        
        // 실시간 검색을 위한 키 이벤트 리스너 추가
        friendSearchBar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String input = friendSearchBar.getText().trim();
                if (input.isEmpty()) {
                    refreshFriendList(); // 검색어가 없으면 전체 목록 표시
                } else {
                    filterFriendList(input);
                }
            }
        });

        ImageIcon addFriendIcon = new ImageIcon("./image/addFriend.png");
        Image scaledAddFriendImg = addFriendIcon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
        addFriendsBtn = new JButton(new ImageIcon(scaledAddFriendImg));
        addFriendsBtn.setPreferredSize(new Dimension(30, 30));
        addFriendsBtn.setBackground(Color.WHITE);
        addFriendsBtn.setBorderPainted(false);
        addFriendsBtn.setContentAreaFilled(false);
        addFriendsBtn.setOpaque(true);
        addFriendsBtn.setFocusPainted(false);
        addFriendsBtn.addActionListener(e -> {
            AddFriendFrame addFriend = new AddFriendFrame();
            addFriend.setVisible(true);
        });
        searchPanel.add(searchBtn);
        searchPanel.add(addFriendsBtn);
        this.add(searchPanel);

        // --- 내 프로필 영역 (왼쪽 정렬) ---
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.X_AXIS));
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 프로필 사진
        JLabel profileIcon = new JLabel();
        profileIcon.setPreferredSize(new Dimension(50, 50));
        profileIcon.setMaximumSize(new Dimension(50, 50));
        profileIcon.setMinimumSize(new Dimension(50, 50));
        profileIcon.setAlignmentY(Component.CENTER_ALIGNMENT);
        ImageIcon icon = new ImageIcon(loggedInUser.getProfileDir());
        Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        profileIcon.setIcon(new ImageIcon(scaled));

        // 이름 라벨
        JLabel nameLabel = new JLabel("  " + loggedInUser.getName());
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        nameLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // 패널 폭을 부모폭(가용폭)만큼 고정
        profilePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        profilePanel.add(profileIcon);
        profilePanel.add(nameLabel);
        // FlowLayout(LEFT)로 감싸서 진짜 왼쪽 정렬
        JPanel profileWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        profileWrapper.setOpaque(false);
        profileWrapper.add(profilePanel);
        add(profileWrapper);
        add(Box.createRigidArea(new Dimension(0, 12)));

        // 친구 리스트 영역
        JPanel friendPanel = new JPanel(new BorderLayout());
        friendPanel.setBackground(new Color(0xFFFFFF));
        friendList = new JList();
        friendPanel.add(new JLabel("친구"), BorderLayout.NORTH);
        friendPanel.add(new JScrollPane(friendList), BorderLayout.CENTER);
        this.add(friendPanel);

        // --- 친구 목록 불러와서 JList에 표시 (이름+아이콘) ---
        refreshFriendList();

        // 더블클릭 시 1:1 채팅방 진입
        friendList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = friendList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Friend friend = (Friend) friendList.getModel().getElementAt(index);
                        // 기존 openOneToOneChatRoom(friend) 대신 서버에 채팅방 생성 요청
                        controller.MainController.createChatRoomWithFriend(friend.id, friend.name);
                    }
                }
            }
        });
    }

    // 친구 목록 필터링 메서드
    private void filterFriendList(String searchText) {
        DBManager db = DBManager.getInstance();
        java.util.List<String> allFriends = db.loadFriendList();
        DefaultListModel<Friend> filteredModel = new DefaultListModel<>();
        
        for (String phone : allFriends) {
            String name = phone;
            String profileDir = null;
            try (java.sql.Connection conn = db.getConnection();
                 java.sql.PreparedStatement pstmt = conn.prepareStatement("SELECT name, profileDir FROM UserData WHERE phoneNum = ? LIMIT 1")) {
                pstmt.setString(1, phone);
                java.sql.ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    name = rs.getString("name");
                    profileDir = rs.getString("profileDir");
                }
            } catch (Exception ex) { ex.printStackTrace(); }
            
            if (name == null || name.isEmpty()) name = phone;
            
            // 검색어와 일치하는 친구만 추가 (대소문자 구분 없이)
            if (name.toLowerCase().contains(searchText.toLowerCase())) {
                byte[] profileBytes = null;
                if (profileDir != null) {
                    try {
                        java.nio.file.Path path = java.nio.file.Paths.get(profileDir);
                        if (java.nio.file.Files.exists(path)) {
                            profileBytes = java.nio.file.Files.readAllBytes(path);
                        }
                    } catch (Exception ex) { /* ignore */ }
                }
                filteredModel.addElement(new Friend(0, phone, name, profileBytes));
            }
        }
        
        friendList.setModel(filteredModel);
        friendList.setCellRenderer(new FriendCellRenderer());
        friendList.revalidate();
        friendList.repaint();
    }

    // 1:1 채팅방 진입 로직 (채팅방 존재 여부 확인 후 오픈)
    private void openOneToOneChatRoom(Friend friend) {
        // 1:1 채팅방 존재 여부 확인 (ChatRoomList에서 상대 id 포함된 1:1 채팅방 검색)
        DBManager db = DBManager.getInstance();
        List<ChatRoom> chatRooms = db.loadChatRooms();
        ChatRoom found = null;
        for (ChatRoom room : chatRooms) {
            if ("1:1".equals(room.getRoomType()) && room.getRoomName().contains(friend.id)) {
                found = room;
                break;
            }
        }
        if (found == null) {
            found = new ChatRoom();
            found.setRoomType("1:1");
            found.setRoomName(friend.name + "와의 1:1 채팅");
            // chatRoomNum 자동 할당
            int maxNum = 0;
            for (ChatRoom r : chatRooms) {
                if (r.getChatRoomNum() > maxNum) maxNum = r.getChatRoomNum();
            }
            found.setChatRoomNum(maxNum + 1);
            db.saveChatRoom(found);
        }
        // 메인 프레임의 ChatRoomPanel에서 채팅창 열기 (chatRoomNum 직접 전달)
        view.Base.getInstance().getChatRoomPanel().openChatRoomWindow(found.getChatRoomNum(), found.getRoomName(), found.getLastMessage() != null ? found.getLastMessage() : "최근 메시지", "./profile/chatRoom/test.jpg");
    }

    // 이름+아이콘으로 친구를 표시하는 셀 렌더러
    class FriendCellRenderer extends JPanel implements ListCellRenderer<Friend> {
        private JLabel iconLabel = new JLabel();
        private JLabel nameLabel = new JLabel();
        public FriendCellRenderer() {
            setLayout(new BorderLayout(5, 5));
            add(iconLabel, BorderLayout.WEST);
            add(nameLabel, BorderLayout.CENTER);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }
        @Override
        public Component getListCellRendererComponent(JList<? extends Friend> list, Friend friend, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            nameLabel.setText(friend.name);
            // 프로필 이미지가 있으면 표시, 없으면 기본
            if (friend.profileImageBytes != null) {
                ImageIcon icon = new ImageIcon(friend.profileImageBytes);
                Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(scaled));
            } else {
                iconLabel.setIcon(null);
            }
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }
    }
}
