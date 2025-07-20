package view;

import javax.swing.*;
import java.awt.*;
import model.User;

public class FriendPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public JButton searchBtn;
    public JButton addFriendsBtn;
    public JTextField friendSearchBar;
    public JList friendList;

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
            }
            //TODO 친구이름입력에 따른 검색버튼입니다, 이름검색에 따른 친구 리스트 표기 구현필요
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

        // 이 패널의 크기만큼 간격 유지
        add(profilePanel);
        add(Box.createRigidArea(new Dimension(0, 12)));

        // 친구 리스트 영역
        JPanel friendPanel = new JPanel(new BorderLayout());
        friendPanel.setBackground(new Color(0xFFFFFF));
        friendList = new JList();
        friendPanel.add(new JLabel("친구"), BorderLayout.NORTH);
        friendPanel.add(new JScrollPane(friendList), BorderLayout.CENTER);
        this.add(friendPanel);
    }
}
