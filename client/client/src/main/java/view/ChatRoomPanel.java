
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
		this.setBackground(new Color(0xFFFFFF));
		// 검색 + 친구추가 버튼 영역
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		searchPanel.setBackground(Color.white);
		ImageIcon searchIcon = new ImageIcon("./image/search.png");
		Image scaledSearchImg = searchIcon.getImage().getScaledInstance(23, 23, Image.SCALE_SMOOTH);
		searchBtn = new JButton(new ImageIcon(scaledSearchImg));
		searchBtn.setPreferredSize(new Dimension(30, 30));
		searchBtn.setBackground(Color.WHITE);
		searchBtn.setBorderPainted(false);
		searchBtn.setContentAreaFilled(false);
		searchBtn.setOpaque(true);
		searchBtn.setFocusPainted(false);   
		searchPanel.setBackground(new Color(0xFFFFFF));
		addFriendsBtn = new JButton("오픈채팅");
		searchPanel.add(searchBtn);
		searchPanel.add(addFriendsBtn);
		this.add(searchPanel);

		JPanel chatRoomPanel = new JPanel(new BorderLayout());
		friendList = new JList();
		chatRoomPanel.setBackground(new Color(0xFFFFFF));
		chatRoomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		chatRoomPanel.add(new JScrollPane(friendList), BorderLayout.CENTER);
		this.add(chatRoomPanel);
	}
}
