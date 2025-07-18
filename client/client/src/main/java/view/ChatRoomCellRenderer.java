package view;

import javax.swing.*;
import java.awt.*;

//ToDo:규격 수정(꾸미기)
//	   ID인계
//	   인계받은 ID로 ChatData불러오기
//	   최초실행시 chatData불러오기

public class ChatRoomCellRenderer extends JPanel implements ListCellRenderer<String[]> {
	
    private JLabel iconLabel;
    private JLabel topLabel;
    private JLabel bottomLabel;
    private int chatRoomID;
    
    public ChatRoomCellRenderer() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(50, 50));
        add(iconLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        topLabel = new JLabel();
        topLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topLabel.setForeground(Color.BLACK);

        bottomLabel = new JLabel();
        bottomLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        bottomLabel.setForeground(Color.GRAY);

        textPanel.add(topLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(bottomLabel);

        add(textPanel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String[]> list, String[] value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        // value[0]: top text, value[1]: bottom text, value[2]: image path
        ImageIcon icon = new ImageIcon(value[2]);
        Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
       
        iconLabel.setIcon(new ImageIcon(scaled));
        
        topLabel.setText(value[0]);
        topLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        
        bottomLabel.setText(value[1]);
        bottomLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        
        if (isSelected) {
            setBackground(new Color(0xE6F0FF));
        } else {
            setBackground(Color.WHITE);
        }

        return this;
    }
}
