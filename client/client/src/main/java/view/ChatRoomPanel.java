package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import controller.MainController;
import model.Chat;
import model.ChatRoom;
import model.DBManager;
import model.LocalDBManager;
import model.ChatFileManager;
import observer.Observer;
import observer.ServerCallEventHandle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomPanel extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;

	public JButton searchBtn;
	public JButton searchOpenChatBtn;
	public JButton addChatRoomBtn;
	public JList<String[]> chatList;
	public JTextField chatRoomSearchBar;
	
	// 채팅방 목록을 저장할 리스트
	private List<ChatRoom> chatRooms = new ArrayList<>();

	public ChatRoomPanel() {
		System.out.println("[DEBUG] ChatRoomPanel 생성자 호출");
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
//		
//		// 10개의 더미 데이터 생성
//        String[][] dummyData = new String[10][3];
//        for (int i = 0; i < 10; i++) {
//            dummyData[i][0] = "채팅방 " + (i + 1);               // 상단 라벨 텍스트
//            dummyData[i][1] = "최근 메시지 미리보기 " + (i + 1);  // 하단 라벨 텍스트
//            dummyData[i][2] = "./profile/chatRoom/test.jpg";                 // 아이콘 이미지 경로
//        }
		
		chatList = new JList<>(new String[0][3]); // 빈 데이터로 JList 초기화
        chatList.setCellRenderer(new ChatRoomCellRenderer());
        chatList.setFixedCellHeight(70);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        chatList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = chatList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        String[] data = chatList.getModel().getElementAt(index);
                        openChatRoomWindow(data);
                    }
                }
            }
        });
        JPanel chatRoomPanel = new JPanel(new BorderLayout());
        chatRoomPanel.setBackground(new Color(0xFFFFFF));
        chatRoomPanel.add(new JScrollPane(chatList), BorderLayout.CENTER);
        this.add(chatRoomPanel);
        ServerCallEventHandle.registerObserver(this);
        System.out.println("[DEBUG] ChatRoomPanel이 옵저버로 등록됨");
//        JPanel chatRoomPanel = new JPanel(new BorderLayout());
        
//        chatRoomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//        chatRoomPanel.add(new JScrollPane(chatList), BorderLayout.CENTER);
//        this.add(chatRoomPanel);
	}
	@Override
    public void onMessageReceived(String message) {}
	@Override
    public void onChatRoomListUpdated(List<ChatRoom> rooms) {
        System.out.println("[LOG] onChatRoomListUpdated 호출됨! 방 개수: " + rooms.size());

        // 항상 로컬 DB에서 최신 목록을 읽어와 표시
        LocalDBManager db = new LocalDBManager();
        this.chatRooms = new ArrayList<>(db.loadChatRooms());
        List<ChatRoom> displayRooms = this.chatRooms;

        String[][] chatData = new String[displayRooms.size()][3];
        for (int i = 0; i < displayRooms.size(); i++) {
            ChatRoom room = displayRooms.get(i);
            chatData[i][0] = room.getRoomName();
            chatData[i][1] = room.getLastMessage() != null ? room.getLastMessage() : "메시지 없음";
            chatData[i][2] = "./profile/chatRoom/test.jpg";
        }
        chatList.setListData(chatData); // JList의 데이터를 새 데이터로 교체
        this.revalidate();
        this.repaint();
    }
    
    @Override
    public void onChatDataUpdated(int chatRoomNum, List<Chat> chats) {
        System.out.println("[LOG] onChatDataUpdated 호출됨! 채팅방 번호: " + chatRoomNum + ", 채팅 개수: " + chats.size());
        // 채팅 데이터를 저장하고 필요시 UI 업데이트
        // 현재는 로그만 출력
    }
    
    // 채팅방 이름으로 채팅방 번호를 찾는 메서드
    private int findChatRoomNumber(String roomName) {
        for (ChatRoom room : chatRooms) {
            if (room.getRoomName().equals(roomName)) {
                return room.getChatRoomNum();
            }
        }
        return -1; // 찾지 못한 경우
    }
	
	// 채팅방 창을 열어서 채팅을 표시하는 메서드
	public void openChatRoomWindow(String[] data) {
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

	    // 오른쪽(나) 채팅 영역
	    JTextArea myChatArea = new JTextArea();
	    myChatArea.setEditable(false);
	    myChatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
	    myChatArea.setBackground(backgroundColor);
	    myChatArea.setBorder(new EmptyBorder(5, 5, 5, 5)); // 텍스트 영역 내부 여백
	    
	    // 채팅방 번호를 찾아서 서버에서 채팅 데이터 요청
	    // data[0]은 채팅방 이름이므로, 채팅방 목록에서 해당 이름의 방 번호를 찾아야 함
	    int chatRoomNum = findChatRoomNumber(data[0]);
	    if (chatRoomNum != -1) {
	        System.out.println("[LOG] 채팅방 번호 " + chatRoomNum + "의 채팅 데이터 요청");
	        MainController.requestChatDataFromServer(chatRoomNum);
	        
	        // 채팅 데이터를 받아서 표시하는 옵저버 등록
	        ChatWindowObserver chatObserver = new ChatWindowObserver(chatRoomNum, otherUserChatArea, myChatArea);
	        ServerCallEventHandle.registerObserver(chatObserver);
	        // .txt 파일에서 메시지 읽어와 바로 표시
	        ChatFileManager fileManager = new ChatFileManager();
	        String allChats = fileManager.loadChatsFromFile(chatRoomNum);
	        otherUserChatArea.setText(allChats);
	    }

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

	    send.addActionListener(e -> {
	        String message = messageInput.getText().trim();
	        if (!message.isEmpty()) {
	            // 서버에 메시지 전송 (메인 컨트롤러에 전송 함수 있어야 함)
	            MainController.sendChatMessage(chatRoomNum, message);

	            // 전송 후 입력 필드 초기화
	            messageInput.setText("");
	        }
	    });
	    
	    // splitPane이 화면에 표시된 후 divider 위치를 정확히 중앙으로 설정
	    SwingUtilities.invokeLater(() -> {
	        splitPane.setDividerLocation(0.5);
	    });
	}
	}
