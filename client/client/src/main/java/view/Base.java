package view;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class Base extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	 // 상단
	private JPanel top;
	private JButton exit;
	private JButton hide;
	
	// 베이스 패널
	JPanel basePanel; 
	JButton searchBtn;
	JButton addFriendsBtn;
	JLabel myPf;
	private ImageIcon imageIcon; // 이미지  
	private JLabel myPfImage; // 프로필 이미지
	
//	JList  birthdayList;
//	JLabel birthdayPf; // 생일자 라벨
	
	JList friendList; // 친구리스트
//	JLabel friendPf; // 친구들 프로필은 각각 생성이 필요
	
	
	// 채팅창 패널
	JPanel chatPanel; 
	JList chatList; // 채팅방 리스트
	
	JButton openChatSearchBtn; // 검색과 오픈채팅 버튼의 기능에대해 생각할것 
	JButton makeChatBtn;
	
	// 왼쪽 패널
	JPanel leftPanel;
	JButton friendBtn;
	JButton chatBtn;
	JButton alarm; // boolean 값으로 설정 @@@@@@@@@@@@@@@@@@@@@@@@
	boolean alarmON = true;
//	JButton alarmOFF;
	JButton setting;
	
	public Base() {
		this.setUndecorated(true);
		this.setLayout(new BorderLayout());
		this.setLocation(1000, 200);
		this.setLayout(new BorderLayout());
		// 최상단 hide, exit 
		top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		top.setOpaque(false);
		exit = new JButton("X");
		exit.setFont(new Font("Arial", Font.BOLD, 20));
		exit.setOpaque(false);
		exit.setContentAreaFilled(false);
		exit.setBorderPainted(false);
		exit.setPreferredSize(new Dimension(50, 50));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		hide = new JButton("_");
		hide.setFont(new Font("Arial", Font.BOLD, 20));
		hide.setOpaque(false);
		hide.setContentAreaFilled(false);
		hide.setBorderPainted(false);
		hide.setPreferredSize(new Dimension(50, 50));
		hide.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setState(JFrame.ICONIFIED);
			}
		});
		top.add(hide);
		top.add(exit);
		
		this.add(top, BorderLayout.NORTH);
		
		//왼쪽 패널
		leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(5, 1));
		friendBtn = new JButton("친구목록");
		chatBtn = new JButton("채팅목록");
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
//		alarmOFF = new JButton("알람off");
		setting = new JButton("설정");
		
		leftPanel.add(friendBtn);
		leftPanel.add(chatBtn);
		leftPanel.add(alarm);
//		leftPanel.add(alarmOFF);
		leftPanel.add(setting);
		
		this.add(leftPanel, BorderLayout.WEST);

		
		// 베이스 패널
		basePanel = new JPanel();
		basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));
		basePanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
		
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
		
		this.add(basePanel, BorderLayout.CENTER);
		
		
		this.setSize(400, 650);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		new Base();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		
	}

}
