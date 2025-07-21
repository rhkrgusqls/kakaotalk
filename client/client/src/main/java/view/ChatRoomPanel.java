package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import controller.MainController;
import model.Chat;
import model.ChatRoom;
import model.DBManager;
import model.LocalDBManager;
import model.ChatFileManager;
import model.User;
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
				return;
			}
			// 채팅방 목록에서 검색어와 일치하는 채팅방만 필터링
			filterChatRoomList(input);
		});
		
		// 실시간 검색을 위한 키 이벤트 리스너 추가
		chatRoomSearchBar.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyReleased(java.awt.event.KeyEvent e) {
				String input = chatRoomSearchBar.getText().trim();
				if (input.isEmpty()) {
					// 검색어가 없으면 전체 목록 표시
					onChatRoomListUpdated(chatRooms);
				} else {
					filterChatRoomList(input);
				}
			}
		});
		searchPanel.setBackground(new Color(0xFFFFFF));
		//searchOpenChatBtn = new JButton("오픈채팅");
        //searchOpenChatBtn.addActionListener(e -> {
        //    OpenChatRoomSearchFrame openFrame = new OpenChatRoomSearchFrame();
        //    openFrame.setVisible(true);
        //});
		searchPanel.add(searchBtn);
        //searchPanel.add(searchOpenChatBtn); // 오픈채팅 버튼 제거
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
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { // 우클릭
                    int index = chatList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        showChatRoomContextMenu(e.getPoint(), index);
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

        this.chatRooms = new ArrayList<>(rooms); // 서버에서 받은 목록을 직접 사용
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
    
    // 채팅방 목록 필터링 메서드
    private void filterChatRoomList(String searchText) {
        List<ChatRoom> filteredRooms = new ArrayList<>();
        
        for (ChatRoom room : chatRooms) {
            // 채팅방 이름에서 검색어와 일치하는 채팅방만 추가 (대소문자 구분 없이)
            if (room.getRoomName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredRooms.add(room);
            }
        }
        
        String[][] chatData = new String[filteredRooms.size()][3];
        for (int i = 0; i < filteredRooms.size(); i++) {
            ChatRoom room = filteredRooms.get(i);
            chatData[i][0] = room.getRoomName();
            chatData[i][1] = room.getLastMessage() != null ? room.getLastMessage() : "메시지 없음";
            chatData[i][2] = "./profile/chatRoom/test.jpg";
        }
        chatList.setListData(chatData);
        this.revalidate();
        this.repaint();
    }
    
    // 채팅방 우클릭 컨텍스트 메뉴 표시
    private void showChatRoomContextMenu(Point point, int index) {
        JPopupMenu contextMenu = new JPopupMenu();
        
        String[] data = chatList.getModel().getElementAt(index);
        String roomName = data[0];
        int chatRoomNum = findChatRoomNumber(roomName);
        
        JMenuItem deleteItem = new JMenuItem("채팅방 나가기");
        deleteItem.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, 
                "정말로 이 채팅방을 나가시겠습니까?", 
                "채팅방 나가기", 
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                deleteChatRoom(chatRoomNum);
            }
        });
        
        contextMenu.add(deleteItem);
        contextMenu.show(chatList, point.x, point.y);
    }
    
    // 채팅방 삭제 요청
    private void deleteChatRoom(int chatRoomNum) {
        String myId = controller.MainController.getLoggedInUser().getId();
        String deleteMsg = String.format("%%DeleteChatRoom%%&chatRoomNum$%d&userId$%s%%", chatRoomNum, myId);
        String response = model.TCPManager.getInstance().sendSyncMessage(deleteMsg);
        
        if (response != null && response.contains("success$true")) {
            JOptionPane.showMessageDialog(this, "채팅방을 나갔습니다.");
            // 채팅방 목록 새로고침
            controller.MainController.requestChatRoomsFromServer(myId);
        } else {
            String errorMsg = "채팅방 나가기에 실패했습니다.";
            if (response != null && response.contains("error$")) {
                errorMsg = response.split("error\\$")[1].split("&")[0];
            }
            JOptionPane.showMessageDialog(this, errorMsg, "오류", JOptionPane.ERROR_MESSAGE);
        }
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
	
	// 채팅방 창을 열어서 채팅을 표시하는 메서드 (chatRoomNum 직접 전달)
    public void openChatRoomWindow(int chatRoomNum, String roomName, String lastMessage, String profilePath) {
        JFrame chatWindow = new JFrame(roomName); 
        chatWindow.setSize(450, 600);
        chatWindow.setLocationRelativeTo(null);
        chatWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 독립된 창 닫기
        
        Color backgroundColor = Color.WHITE;

        // 메인 컨테이너 패널
        JPanel mainContainerPanel = new JPanel(new BorderLayout());
        mainContainerPanel.setBackground(backgroundColor);

        // 채팅방 제목 레이블
        JLabel title = new JLabel(roomName, SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        title.setOpaque(true); 
        title.setBackground(backgroundColor);
        title.setBorder(new EmptyBorder(10, 0, 10, 0));

        // 통합 채팅 영역 (카카오톡 스타일)
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        chatArea.setBackground(backgroundColor);
        chatArea.setBorder(new EmptyBorder(5, 5, 5, 5));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        
        // 서버에서 채팅 데이터 요청
        if (chatRoomNum != -1) {
            System.out.println("[LOG] 채팅방 번호 " + chatRoomNum + "의 채팅 데이터 요청");
            MainController.requestChatDataFromServer(chatRoomNum);
            ChatWindowObserver chatObserver = new ChatWindowObserver(chatRoomNum, chatArea, chatArea);
            ServerCallEventHandle.registerObserver(chatObserver);
            // 채팅창 닫힐 때 옵저버 해제
            chatWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    chatObserver.unregister();
                    System.out.println("[DEBUG] ChatWindowObserver 해제 완료");
                }
            });
        }

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBorder(null);
        chatScrollPane.getViewport().setBackground(backgroundColor);
        JPanel sendPanel = new JPanel(new BorderLayout(5, 0));
        sendPanel.setBackground(backgroundColor);
        sendPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTextField messageInput = new JTextField();
        JButton send = new JButton("전송");
        sendPanel.add(messageInput, BorderLayout.CENTER);
        sendPanel.add(send, BorderLayout.EAST);
        send.addActionListener(e -> {
            String text = messageInput.getText().trim();
            if (text.isEmpty()) return;
            String myId = MainController.getLoggedInUser().getId();
            String msg = String.format("%%Chat%%&chatRoomNum$%d&userId$%s&text$%s%%", chatRoomNum, myId, text);
            model.TCPManager.getInstance().sendSyncMessage(msg);
            // 내 메시지를 바로 append (optimistic UI)
            chatArea.append(myId + ": " + text + "\n");
            messageInput.setText("");
        });
        mainContainerPanel.add(title, BorderLayout.NORTH);
        mainContainerPanel.add(chatScrollPane, BorderLayout.CENTER);
        mainContainerPanel.add(sendPanel, BorderLayout.SOUTH);
        chatWindow.add(mainContainerPanel);
        chatWindow.setVisible(true);
    }

    // 기존 openChatRoomWindow(String[] data)에서 chatRoomNum을 찾아서 위 메서드로 위임
    public void openChatRoomWindow(String[] data) {
        int chatRoomNum = findChatRoomNumber(data[0]);
        openChatRoomWindow(chatRoomNum, data[0], data[1], data[2]);
    }
    
    // 채팅방 목록 새로고침
    public void refreshChatRoomList() {
        User currentUser = MainController.getLoggedInUser();
        if (currentUser != null) {
            LocalDBManager localDB = new LocalDBManager();
            List<ChatRoom> userRooms = localDB.loadChatRoomsForUser(currentUser.getId());
            onChatRoomListUpdated(userRooms);
        }
    }
    
    // 채팅창 업데이트 (새 메시지 수신 시)
    public void updateChatWindow(int chatRoomNum, String userId, String text) {
        // 열려있는 채팅창이 있다면 업데이트
        // ChatWindowObserver에서 처리하므로 여기서는 로그만 출력
        System.out.println("[DEBUG] 채팅창 업데이트 요청: " + chatRoomNum + " | " + userId + " | " + text);
    }
	}
