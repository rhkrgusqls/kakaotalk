package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
	    JFrame chatWindow = new JFrame(data[0]); 
	    chatWindow.setSize(450, 600);
	    chatWindow.setLocationRelativeTo(null);
	    chatWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 독립된 창 닫기
	    
	    Color backgroundColor = Color.WHITE;

	    // 메인 컨테이너 패널
	    JPanel mainContainerPanel = new JPanel(new BorderLayout());
	    mainContainerPanel.setBackground(backgroundColor);

	    // 채팅방 제목 레이블
	    JLabel title = new JLabel(data[0], SwingConstants.CENTER);
	    title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
	    title.setOpaque(true); 
	    title.setBackground(backgroundColor);
	    title.setBorder(new EmptyBorder(10, 0, 10, 0));

	    // JSplitPane으로 채팅을 나눔

	    // 왼쪽(상대방) 채팅 영역
	    JTextArea otherUserChatArea = new JTextArea();
	    otherUserChatArea.setEditable(false);
	    otherUserChatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
	    otherUserChatArea.setBackground(backgroundColor);
	    otherUserChatArea.setBorder(new EmptyBorder(5, 5, 5, 5)); // 텍스트 영역 내부 여백
	    // 예시 상대 내용 추가
	    otherUserChatArea.append("안녕하세요!\n");
	    otherUserChatArea.append("만나서 반갑습니다.\n");

	    // 오른쪽(나) 채팅 영역
	    JTextArea myChatArea = new JTextArea();
	    myChatArea.setEditable(false);
	    myChatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
	    myChatArea.setBackground(backgroundColor);
	    myChatArea.setBorder(new EmptyBorder(5, 5, 5, 5)); // 텍스트 영역 내부 여백
	    // 예시 내 채팅 추가
	    myChatArea.append("네, 안녕하세요!\n");

	    // JSplitPane 생성 및 설정
	    JSplitPane splitPane = new JSplitPane(
	        JSplitPane.HORIZONTAL_SPLIT, // 좌우로 분할
	        new JScrollPane(otherUserChatArea),
	        new JScrollPane(myChatArea)
	    );
	    splitPane.setResizeWeight(0.5); // 양쪽 패널이 5:5 비율
	    splitPane.setDividerSize(0);    // 구분선 두께
	    splitPane.setBorder(null); 

	    // JScrollPane의 배경색도 흰색으로 설정
	    for (Component c : splitPane.getComponents()) {
	        if (c instanceof JScrollPane) {
	            ((JScrollPane) c).getViewport().setBackground(backgroundColor);
	            ((JScrollPane) c).setBorder(null);
	        }
	    }

	    // 하단 메시지 입력 및 전송 패널
	    JPanel sendPanel = new JPanel(new BorderLayout(5, 0));
	    sendPanel.setBackground(backgroundColor);
	    sendPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

	    JTextField messageInput = new JTextField();
	    JButton send = new JButton("전송");
	    sendPanel.add(messageInput, BorderLayout.CENTER);
	    sendPanel.add(send, BorderLayout.EAST);

	    // 메인 컨테이너에 컴포넌트들 최종 조립
	    mainContainerPanel.add(title, BorderLayout.NORTH);
	    mainContainerPanel.add(splitPane, BorderLayout.CENTER); // GridLayout 패널 대신 JSplitPane을 추가
	    mainContainerPanel.add(sendPanel, BorderLayout.SOUTH);

	    chatWindow.add(mainContainerPanel);
	    chatWindow.setVisible(true);

	    // splitPane이 화면에 표시된 후 divider 위치를 정확히 중앙으로 설정
	    SwingUtilities.invokeLater(() -> {
	        splitPane.setDividerLocation(0.5);
	    });
}
	}
