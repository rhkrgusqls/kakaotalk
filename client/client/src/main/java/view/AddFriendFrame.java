package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;

public class AddFriendFrame extends JFrame {

    private Point initialClick; 
    private JTextField inputField;
    private JButton addButton;
    private JButton cancelButton;

    public AddFriendFrame() {
        // 프레임 기본 설정
        setUndecorated(true);
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));

        // 프레임 모서리를 둥글게 처리
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
            }
        });

        // 1. 상단 안내 문구 및 드래그 영역 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel instructionLabel = new JLabel("상대방의 id 또는 전화번호를 입력해주세요.");
        instructionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        topPanel.add(instructionLabel, BorderLayout.CENTER);

        // --- 여기에 드래그 기능 추가 ---
        topPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        topPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currentLocation = getLocation();
                int deltaX = e.getX() - initialClick.x;
                int deltaY = e.getY() - initialClick.y;
                setLocation(currentLocation.x + deltaX, currentLocation.y + deltaY);
            }
        });
        // --- 드래그 기능 추가 완료 ---

        add(topPanel, BorderLayout.NORTH);

        // 2. 중앙 입력 필드
        JPanel centerPanel = new JPanel(new FlowLayout());
        centerPanel.setBackground(Color.WHITE);
        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(200, 35));
        centerPanel.add(inputField);
        add(centerPanel, BorderLayout.CENTER);

        // 3. 하단 버튼 영역
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        // "추가" 버튼
        addButton = new JButton("추가");
        addButton.setPreferredSize(new Dimension(100, 40));
        addButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        addButton.setBackground(new Color(0xFEE500));
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);

        // "취소" 버튼
        cancelButton = new JButton("취소");
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        cancelButton.setBackground(new Color(0xEFEFEF));
        cancelButton.setOpaque(true);
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 버튼 이벤트 리스너 추가
        // TODO : 추가를 추가하는 실제 로직이 필요
        addButton.addActionListener(e -> {
            String input = inputField.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID 또는 전화번호를 입력하세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "'" + input + "'님을 추가합니다. (로직 구현 필요)");
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());
    }
}
