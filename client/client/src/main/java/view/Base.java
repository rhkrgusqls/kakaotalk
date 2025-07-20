package view;
import java.awt.*;
import javax.swing.*;

import controller.MainController;

import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import model.User;

public class Base extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	 // 상단
	private JPanel top;
	private JButton exit;
	private JButton hide;
	private Point initalClick;
	
	// 베이스 패널
	JPanel basePanel; 
	
	// REVIEW : 친구창을 다른 클래스에서 정의 검토 필요
	//JButton searchBtn;
	//JButton addFriendsBtn;
	//JLabel myPf;
	//private ImageIcon imageIcon; // 이미지  
	//private JLabel myPfImage; // 프로필 이미지
	
//	JList  birthdayList;
//	JLabel birthdayPf; // 생일자 라벨
	
	//JList friendList; // 친구리스트
//	JLabel friendPf; // 친구들 프로필은 각각 생성이 필요
	
	
	// 채팅창 패널
	///JPanel chatPanel; 
	//JList chatList; // 채팅방 리스트
	
	//JButton openChatSearchBtn; // 검색과 오픈채팅 버튼의 기능에대해 생각할것 
	//JButton makeChatBtn;
	
	// 왼쪽 패널
	JPanel leftPanel;
	JButton friendBtn;
	JButton chatBtn;
	JButton alarm; // boolean 값으로 설정 @@@@@@@@@@@@@@@@@@@@@@@@
	boolean alarmON = true;
	JButton setting;
	
	FriendPanel friendPanel;
	ChatRoomPanel chatRoomPanel;
	
	public Base() {
		this.setUndecorated(true);
		this.setLayout(new BorderLayout());
		this.setLocation(1000, 200);
		this.setLayout(new BorderLayout());
		// 최상단 hide, exit 
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
		top.addMouseListener(new MouseAdapter() { // 화면 드래그 기능
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
		})
		;
		ImageIcon exListIcon = new ImageIcon("./image/exit.png");
		Image scaledexListImg = exListIcon.getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH);
		exit = new JButton(new ImageIcon(scaledexListImg));
		exit.setFont(new Font("Arial", Font.BOLD, 10));
		exit.setOpaque(false);
		exit.setContentAreaFilled(false);
		exit.setBorderPainted(false);
		exit.setPreferredSize(new Dimension(20, 20));
		exit.setMargin(new Insets(0, 0, 0, 0));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
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
		hide.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setState(JFrame.ICONIFIED);
			}
		});
		ImageIcon mxListIcon = new ImageIcon("./image/maximize.png");
		Image scaledmxListImg = mxListIcon.getImage().getScaledInstance(23, 23, Image.SCALE_SMOOTH);
		
		top.add(hide);
		top.add(exit);
		
		this.add(top, BorderLayout.NORTH);
		
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
		friendBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switchingPanel(0);
			}
		});
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
		chatBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switchingPanel(1);
			}
		});
		
//		alarmOFF = new JButton("알람off");
		alarm = new JButton("알람");
		alarm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				alarmON = !alarmON;
				
				if(alarmON) {
					JOptionPane.showMessageDialog(null, "알람이 활성화 되었습니다.");
				} else {
					JOptionPane.showMessageDialog(null, "알람이 비활성화 되었습니다.");
				}
				
			}
			
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

		
		// 베이스 패널
		basePanel = new JPanel();
		basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));
		basePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		basePanel.setBackground(new Color(0xFFFFFF));
		/*
		// 검색 친구 추가 최상단 패널
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		searchBtn = new JButton("검색");
		addFriendsBtn = new JButton("친구추가");
		searchPanel.add(searchBtn);
		searchPanel.add(addFriendsBtn);
		basePanel.add(searchPanel);
		
		// 내 프로필 구역
		JPanel myPfPanel = new JPanel();
		myPfPanel.setLayout(new BoxLayout(myPfPanel, BoxLayout.X_AXIS));
//		imageIcon = new ImageIcon("내프로필사진.png");
//		myPfImage = new JLabel(imageIcon);
//		myPfPanel.add(myPfImage);
		JList myPf = new JList();
		JScrollPane myPfScroll = new JScrollPane(myPf);
		myPfScroll.setPreferredSize(new Dimension(200, 60));
		myPfPanel.add(myPfScroll);
		myPfPanel.add(myPf);
		myPfPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		basePanel.add(myPfPanel);
		basePanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		//친구 리스트 구역
		JPanel friendPanel = new JPanel(new BorderLayout());
		friendList = new JList();
		friendPanel.add(new JLabel("친구"), BorderLayout.NORTH);
		friendPanel.add(new JScrollPane(friendList), BorderLayout.CENTER);
		basePanel.add(friendPanel);
		*/

		switchingPanel(0); //화면전환 메서드 디폴트값 출력
		
		this.add(basePanel, BorderLayout.CENTER);
		
		
		this.setSize(400, 650);
		this.setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 13, 13));
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		new Base();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		
	}
	
	/**
	 * 화면전환용 메서드
	 */
	private void switchingPanel(int type) {
		basePanel.removeAll();
		switch(type) {
		case 0:
            User loggedInUser = MainController.getLoggedInUser();
            if (loggedInUser == null) {
            	JOptionPane.showMessageDialog(this, "로그인 정보가 없습니다. 앱을 다시 시작해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                return; 
            }
            friendPanel = new FriendPanel(loggedInUser);
            basePanel.add(friendPanel);
            basePanel.revalidate();
			basePanel.repaint();
			break;
		case 1:
			chatRoomPanel = new ChatRoomPanel();
			basePanel.add(chatRoomPanel); 
			basePanel.revalidate();
			basePanel.repaint();
			break;
		}
	}

}
