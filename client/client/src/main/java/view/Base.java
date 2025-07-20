package view;

import java.awt.*;
import javax.swing.*;
import controller.MainController;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import model.User;

public class Base extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    // --- 상단 ---
    private JPanel top;
    private JButton exit;
    private JButton hide;
    private Point initalClick;

    // --- 중앙 ---
    private JPanel basePanel; 

    // --- 왼쪽 ---
    private JPanel leftPanel;
    private JButton friendBtn;
    private JButton chatBtn;
    private JButton alarm;
    private boolean alarmON = true;
    private JButton setting;

    // [수정 1] FriendPanel과 ChatRoomPanel을 멤버 변수로 선언합니다.
    private FriendPanel friendPanel;
    private ChatRoomPanel chatRoomPanel;

    public Base() {
        this.setUndecorated(true);
        this.setLayout(new BorderLayout());
        this.setLocation(1000, 200);
        this.setLayout(new BorderLayout());
        
        // --- 최상단 hide, exit ---
        top = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int width = getWidth();
                int height = getHeight();
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0xECECED));
                g2.fillRect(0, 0, 65, height);
                g2.setColor(Color.white);
                g2.fillRect(65, 0, width - 10, height);
            }
        };
        top.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        top.setOpaque(false);
        top.setPreferredSize(new Dimension(0, 23));
        top.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initalClick = e.getPoint();
            }
        });
        top.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currentLocation = getLocation();
                int deltaX = e.getX() - initalClick.x;
                int deltaY = e.getY() - initalClick.y;
                setLocation(currentLocation.x + deltaX, currentLocation.y + deltaY);
            }
        });
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

        ImageIcon mnListIcon = new ImageIcon("./image/minimize.png");
        Image scaledmnListImg = mnListIcon.getImage().getScaledInstance(12, 1, Image.SCALE_SMOOTH);
        hide = new JButton(new ImageIcon(scaledmnListImg));
        hide.setFont(new Font("Arial", Font.BOLD, 20));
        hide.setFocusPainted(false);
        hide.setOpaque(false);
        hide.setContentAreaFilled(false);
        hide.setBorderPainted(false);
        hide.setPreferredSize(new Dimension(20, 20));
        hide.setMargin(new Insets(0, 0, 0, 0));
        hide.addActionListener(e -> setState(JFrame.ICONIFIED));

        top.add(hide);
        top.add(exit);
        this.add(top, BorderLayout.NORTH);

        // --- 왼쪽 패널 ---
        int buttonHeight = 10;
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(0xECECED));
        leftPanel.setPreferredSize(new Dimension(65, getHeight()));
        int topButtonHeight = 3500;
        int bottomButtonHeight = 30;

        ImageIcon freiendListIcon = new ImageIcon("./image/friendList_icon.png");
        Image scaledfreiendListImg = freiendListIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        friendBtn = new JButton(new ImageIcon(scaledfreiendListImg));
        friendBtn.setMaximumSize(new Dimension(65, topButtonHeight));
        friendBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        friendBtn.setPreferredSize(new Dimension(friendBtn.getPreferredSize().width, buttonHeight));
        friendBtn.setBackground(new Color(0xECECED));
        friendBtn.setBorderPainted(false);
        friendBtn.setContentAreaFilled(false);
        friendBtn.setOpaque(true);
        friendBtn.setFocusPainted(false);   
        friendBtn.setMargin(new Insets(0, 0, 0, 0));
        friendBtn.addActionListener(e -> switchingPanel(0));

        ImageIcon chatListIcon = new ImageIcon("./image/chatLogo.png");
        Image scaledchatListImg = chatListIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        chatBtn = new JButton(new ImageIcon(scaledchatListImg));
        chatBtn.setMaximumSize(new Dimension(65, topButtonHeight));
        chatBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        chatBtn.setPreferredSize(new Dimension(chatBtn.getPreferredSize().width, buttonHeight));
        chatBtn.setBackground(new Color(0xECECED));
        chatBtn.setBorderPainted(false);
        chatBtn.setContentAreaFilled(false);
        chatBtn.setOpaque(true);
        chatBtn.setFocusPainted(false);
        chatBtn.setMargin(new Insets(0, 0, 0, 0));
        chatBtn.addActionListener(e -> switchingPanel(1));

        alarm = new JButton("알람");
        alarm.addActionListener(e -> {
            alarmON = !alarmON;
            if(alarmON) JOptionPane.showMessageDialog(null, "알람이 활성화 되었습니다.");
            else JOptionPane.showMessageDialog(null, "알람이 비활성화 되었습니다.");
        });
        alarm.setMaximumSize(new Dimension(65, bottomButtonHeight));
        alarm.setAlignmentX(Component.CENTER_ALIGNMENT);

        setting = new JButton("설정");
        setting.setMaximumSize(new Dimension(65, bottomButtonHeight));
        setting.setAlignmentX(Component.CENTER_ALIGNMENT);

        Component verticalGlue = Box.createVerticalGlue();
        leftPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        leftPanel.add(friendBtn);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(chatBtn);
        leftPanel.add(verticalGlue);
        leftPanel.add(alarm);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(setting);
        this.add(leftPanel, BorderLayout.WEST);

        // --- 베이스 패널 ---
        basePanel = new JPanel();
        basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));
        basePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        basePanel.setBackground(new Color(0xFFFFFF));
        this.add(basePanel, BorderLayout.CENTER);

        // [수정 2] 생성자에서 패널들을 미리 한 번만 생성합니다.
        // 이렇게 하면 로그인 직후 컨트롤러가 보내는 데이터 수신 이벤트를 놓치지 않습니다.
        User loggedInUser = MainController.getLoggedInUser();
        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(this, "로그인 정보가 없어 앱을 종료합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            System.exit(0); // 로그인 정보가 없으면 진행 불가
            return; 
        }
        friendPanel = new FriendPanel(loggedInUser);
        chatRoomPanel = new ChatRoomPanel();

        // 처음에는 친구 패널을 기본으로 보여줍니다.
        switchingPanel(0);

        this.setSize(400, 650);
        this.setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 13, 13));
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

    /**
     * 화면전환용 메서드
     */
    private void switchingPanel(int type) {
        basePanel.removeAll(); // 패널의 모든 컴포넌트를 지웁니다.
        
        // [수정 3] 패널을 새로 생성하지 않고, 미리 만들어 둔 멤버 변수 패널을 붙여줍니다.
        switch(type) {
            case 0:
                basePanel.add(friendPanel);
                break;
            case 1:
                basePanel.add(chatRoomPanel); 
                break;
        }
        basePanel.revalidate(); // 레이아웃을 다시 계산
        basePanel.repaint();    // 화면을 다시 그림
    }
    
    // main 메서드는 LoginFrame에서 실행하므로 여기서는 없어도 됩니다.
    // public static void main(String[] args) { new Base(); }
}
