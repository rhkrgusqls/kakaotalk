package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class ChatRoomPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public JButton searchBtn;
	public JButton searchOpenChatBtn;
	public JButton addChatRoomBtn;
	public JList myPf;
	public JList chatList;
	public JTextField chatRoomSearchBar;

	public ChatRoomPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
		this.setBackground(new Color(0xFFFFFF));
		// 검색 + 친구추가 버튼 영역
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		searchPanel.setBackground(Color.white);
		chatRoomSearchBar = new JTextField();
		chatRoomSearchBar.setPreferredSize(new Dimension(100, 20));
		chatRoomSearchBar.setBackground(Color.WHITE);
		chatRoomSearchBar.setOpaque(true);
		chatRoomSearchBar.setHorizontalAlignment(SwingConstants.CENTER);
		searchPanel.add(chatRoomSearchBar);
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
			String input = chatRoomSearchBar.getText().trim();
			if(input.isEmpty()) {
				JOptionPane.showMessageDialog(null, "채팅방 이름을 입력해주세요.");
			}
			//TODO 채팅방이름 입력에 따른 검색버튼입니다, 채티방이름 검색에 따른 채팅방 표기 구현필요
		});
		searchPanel.setBackground(new Color(0xFFFFFF));
		searchOpenChatBtn = new JButton("오픈채팅");
		searchOpenChatBtn.addActionListener(e -> {
		    OpenChatRoomSearchFrame openFrame = new OpenChatRoomSearchFrame();
		    openFrame.setVisible(true);
		});
		searchPanel.add(searchBtn);
		searchPanel.add(searchOpenChatBtn);
		addChatRoomBtn = new JButton("방 생성"); // 방생성 버튼
		addChatRoomBtn.addActionListener(e -> {
			AddChatRoomFrame addChatRoom = new AddChatRoomFrame();
			addChatRoom.setVisible(true);
		});
		searchPanel.add(addChatRoomBtn);
		this.add(searchPanel);
		
		// 10개의 더미 데이터 생성
        String[][] dummyData = new String[10][3];
        for (int i = 0; i < 10; i++) {
            dummyData[i][0] = "채팅방 " + (i + 1);               // 상단 라벨 텍스트
            dummyData[i][1] = "최근 메시지 미리보기 " + (i + 1);  // 하단 라벨 텍스트
            dummyData[i][2] = "./profile/chatRoom/test.jpg";                 // 아이콘 이미지 경로
        }

        chatList = new JList<>(dummyData);
        chatList.setCellRenderer(new ChatRoomCellRenderer());
        chatList.setFixedCellHeight(70);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        chatList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // 혹은 2로 바꾸면 더블클릭
                	int index = chatList.locationToIndex(e.getPoint());
                	if (index >= 0) {
                	    String[] data = (String[]) chatList.getModel().getElementAt(index);
                	    openChatRoomWindow(data);
                	}
                }
            }
        });
        
        JPanel chatRoomPanel = new JPanel(new BorderLayout());
        chatRoomPanel.setBackground(new Color(0xFFFFFF));
        chatRoomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chatRoomPanel.add(new JScrollPane(chatList), BorderLayout.CENTER);
        this.add(chatRoomPanel);
	}
	
	private void openChatRoomWindow(String[] data) {
	    JFrame chatWindow = new JFrame(data[0]); // 채팅방 이름
	    chatWindow.setSize(400, 500);
	    chatWindow.setLocationRelativeTo(null);
	    chatWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 독립된 창 닫기

	    JPanel chatingPanel = new JPanel();
	    chatingPanel.setLayout(new BorderLayout());

	    JLabel title = new JLabel(data[0], SwingConstants.CENTER);
	    title.setFont(new Font("맑은 고딕", Font.BOLD, 18));

	    JTextArea messageArea = new JTextArea("여기에 메시지가 표시됩니다...");
	    messageArea.setEditable(false);
	    
	    JPanel sendPanel = new JPanel(new BorderLayout(5, 0)); // 채팅창 아래 메시지입력 후 "전송"하는 패널
	    JTextField messageInput = new JTextField();
	    JButton send = new JButton("전송");
	    
	    sendPanel.add(messageInput, BorderLayout.CENTER);
	    sendPanel.add(send, BorderLayout.EAST);
	    
	    chatingPanel.add(sendPanel, BorderLayout.SOUTH);
	    chatingPanel.add(title, BorderLayout.NORTH);
	    chatingPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

	    chatWindow.add(chatingPanel);
	    chatWindow.setVisible(true);
	}

}
