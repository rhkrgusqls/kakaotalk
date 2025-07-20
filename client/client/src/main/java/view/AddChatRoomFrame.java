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
        JPanel openPanel = createCreationPanel("오픈");

        tabbedPane.addTab("1:1", oneToOnePanel);
        tabbedPane.addTab("팀", teamPanel);
        tabbedPane.addTab("오픈", openPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCreationPanel(String type) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 체크박스 기능이 있는 친구 목록
        DefaultListModel<FriendCheckboxItem> friendListModel = new DefaultListModel<>();

        // =========================================================================
        // TODO 1: 친구 목록 로딩 (DB 연동)
        // 현재는 더미 데이터(친구 1~10)를 사용하고 있습니다.
        // 추후 이 부분을 DB에서 실제 친구 목록을 불러오는 로직으로 변경해야 합니다.
        // 예를 들어, `List<UserDTO> friends = userDao.getFriendList(currentUserId);` 와 같은 형태가 될 것입니다.
        // 가져온 친구 목록을 순회하며 `friendListModel.addElement()`를 호출해야 합니다.
        for (int i = 1; i <= 10; i++) {
            // UserDTO에서 친구의 이름이나 닉네임을 가져와 FriendCheckboxItem을 생성합니다.
            // new FriendCheckboxItem("친구 " + i) -> new FriendCheckboxItem(friend.getNickname(), friend.getId())
            friendListModel.addElement(new FriendCheckboxItem("친구 " + i));
        }
        // =========================================================================
        
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
                    // "1:1" 탭이고, 새로운 항목을 "선택"하려는 경우에만
                    if ("1:1".equals(type) && isGoingToBeSelected) {
                        // 다른 모든 항목을 우선 선택 해제한다.
                        for (int i = 0; i < friendListModel.getSize(); i++) {
                            friendListModel.getElementAt(i).setSelected(false);
                        }
                    }
                    // 최종적으로 현재 클릭한 아이템의 상태를 토글한다.
                    clickedItem.setSelected(isGoingToBeSelected);
                    // JList 전체를 다시 그려서 모든 변경사항(해제된 항목 포함)을 반영한다.
                    friendList.repaint();
                }
            }
        });

        JScrollPane friendScrollPane = new JScrollPane(friendList);
        friendScrollPane.setBorder(new TitledBorder("친구 목록 (참여할 친구 선택)"));
        friendScrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(friendScrollPane, BorderLayout.CENTER);
        
        // 하단 버튼 (방 생성, 취소)
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
            List<String> selectedFriendNames = new ArrayList<>();
            // List<Integer> selectedFriendIds = new ArrayList<>(); // 이름 대신 ID를 수집
            for (int i = 0; i < friendListModel.getSize(); i++) {
                FriendCheckboxItem item = friendListModel.getElementAt(i);
                if (item.isSelected()) {
                    selectedFriendNames.add(item.getName());
                    // selectedFriendIds.add(item.getId());
                }
            }

            if (selectedFriendNames.isEmpty()) {
                JOptionPane.showMessageDialog(this, "참여자를 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // =========================================================================
            // TODO 2: 채팅방 생성 로직 (서버/DB 연동)
            // 현재는 선택된 친구 목록을 단순히 메시지 박스로 보여주고 창을 닫습니다.
            // 추후 이 부분을 실제 채팅방 생성 요청을 보내는 로직으로 변경
            // 1. 선택된 친구들의 정보(ID 리스트 등)와 채팅방 타입(type)을 DTO에 담습니다.
            // 2. 컨트롤러(또는 서비스)에 해당 DTO를 전달하여 채팅방 생성을 요청
            // 3. 생성되면 이 창을 닫고(dispose()), 채팅방 목록을 갱신
            String message = String.format("'%s' 타입의 채팅방을 생성합니다.\n참여자: %s", type, selectedFriendNames.toString());
            JOptionPane.showMessageDialog(this, message);
            dispose();
            // =========================================================================
        });

        cancelButton.addActionListener(e -> dispose());
        return mainPanel;
    }

    // JList 아이템 데이터 모델 클래스
    class FriendCheckboxItem {
        private final String name;
        // private final int userId; // 예: DB의 사용자 ID
        private boolean selected;

        public FriendCheckboxItem(String name) {
            this.name = name;
            // this.userId = userId;
            this.selected = false;
        }

        public boolean isSelected() { return selected; }
        public void setSelected(boolean selected) { this.selected = selected; }
        public String getName() { return name; }
        // public int getId() { return userId; }
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
