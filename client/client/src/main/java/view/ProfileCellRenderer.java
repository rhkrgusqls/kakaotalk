package view;

import javax.swing.*;
import java.awt.*;

public class ProfileCellRenderer extends JPanel implements ListCellRenderer<String[]> {
    private JLabel nameLabel;
    private JLabel iconLabel;

    public ProfileCellRenderer() {
        setLayout(new BorderLayout(10, 0)); // 아이콘과 이름 사이 간격
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 내부 여백
        setBackground(Color.WHITE);

        iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(50, 50));
        add(iconLabel, BorderLayout.WEST);

        nameLabel = new JLabel();
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        add(nameLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String[]> list, String[] value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        // value[0]: 이름, value[1]: 이미지 경로
        nameLabel.setText(value[0]);

        ImageIcon icon = new ImageIcon(value[1]);
        // 아이콘 로드 실패 시 예외 처리
        if (icon.getIconWidth() == -1) {
            // 대체 이미지 또는 텍스트 설정
            iconLabel.setText("?");
            iconLabel.setIcon(null);
        } else {
            Image scaled = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaled));
            iconLabel.setText("");
        }

        if (isSelected) {
            setBackground(new Color(0xF0F0F0)); // 선택 시 배경색 변경
        } else {
            setBackground(Color.WHITE);
        }
        return this;
    }
}

