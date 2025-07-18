
package view;

import javax.swing.*;
import java.awt.*;

public class ChatRoomPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public JButton searchBtn;
	public JButton addFriendsBtn;
	public JList myPf;
	public JList friendList;

	public ChatRoomPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

		// 검색 + 친구추가 버튼 영역
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		searchBtn = new JButton("검색");
		addFriendsBtn = new JButton("오픈채팅");
		searchPanel.add(searchBtn);
		searchPanel.add(addFriendsBtn);
		this.add(searchPanel);

		// 내 프로필 영역
		JPanel myPfPanel = new JPanel();
		myPfPanel.setLayout(new BoxLayout(myPfPanel, BoxLayout.X_AXIS));
		myPf = new JList();
		JScrollPane myPfScroll = new JScrollPane(myPf);
		myPfScroll.setPreferredSize(new Dimension(200, 60));
		myPfPanel.add(myPfScroll);
		myPfPanel.add(myPf);
		myPfPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		this.add(myPfPanel);
		this.add(Box.createRigidArea(new Dimension(0, 10)));

		// 친구 리스트 영역
		JPanel friendPanel = new JPanel(new BorderLayout());
		friendList = new JList();
		friendPanel.add(new JLabel("친구"), BorderLayout.NORTH);
		friendPanel.add(new JScrollPane(friendList), BorderLayout.CENTER);
		this.add(friendPanel);
	}
}
