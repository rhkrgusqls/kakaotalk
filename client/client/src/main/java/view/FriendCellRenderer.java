package view;

import clientmodel.Friend;
import javax.swing.*;
import java.awt.*;
import clientmodel.ImageDecodingModule;

public class FriendCellRenderer extends JPanel implements ListCellRenderer<Friend> {
    private JLabel iconLabel = new JLabel();
    private JLabel nameLabel = new JLabel();
    private ImageDecodingModule decoder = new ImageDecodingModule();

    public FriendCellRenderer() {
        setLayout(new BorderLayout(5, 5));
        add(iconLabel, BorderLayout.WEST);
        add(nameLabel, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Friend> list, Friend friend, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        // 친구 이름 설정
        nameLabel.setText(friend.name);

        // 친구의 프로필 이미지 바이트를 ImageIcon으로 디코딩
        ImageIcon icon = decoder.decode(friend.profileImageBytes);
        if (icon != null) {
            // 이미지 크기를 40x40으로 조절하여 아이콘 설정
            Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            // 이미지가 없으면 기본 아이콘 또는 빈 아이콘 설정
            iconLabel.setIcon(null); // 또는 기본 이미지 아이콘
        }

        // 선택된 항목의 배경색/글자색 변경
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
