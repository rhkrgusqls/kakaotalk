
package view;

import javax.swing.*;
import java.awt.*;

public class FriendPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public JButton searchBtn;
	public JButton addFriendsBtn;
	public JList myPf;
	public JList friendList;

	public FriendPanel() {
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
		ImageIcon addFriendIcon = new ImageIcon("./image/addFriend.png");
		Image scaledAddFriendImg = addFriendIcon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
		addFriendsBtn = new JButton(new ImageIcon(scaledAddFriendImg));
		addFriendsBtn.setPreferredSize(new Dimension(30, 30));
		addFriendsBtn.setBackground(Color.WHITE);
		addFriendsBtn.setBorderPainted(false);
		addFriendsBtn.setContentAreaFilled(false);
		addFriendsBtn.setOpaque(true);
		addFriendsBtn.setFocusPainted(false);
		searchPanel.add(searchBtn);
		searchPanel.add(addFriendsBtn);
		this.add(searchPanel);

		// 내 프로필 영역
		JPanel myPfPanel = new JPanel();
		myPfPanel.setLayout(new BoxLayout(myPfPanel, BoxLayout.X_AXIS));
		myPfPanel.setBackground(new Color(0xFFFFFF));
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
		friendPanel.setBackground(new Color(0xFFFFFF));
		friendList = new JList();
		friendPanel.add(new JLabel("친구"), BorderLayout.NORTH);
		friendPanel.add(new JScrollPane(friendList), BorderLayout.CENTER);
		this.add(friendPanel);
	}
}

