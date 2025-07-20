package view;

import javax.swing.*;
import java.awt.*;

public class ResetPwFrame extends JFrame {
    private JTextField idField;
    private JTextField pwField;
    private JTextField newPwField;
    private JButton confirmBtn;
    private JButton cancelBtn;

    public ResetPwFrame() {
        setTitle("비밀번호 재설정");
        setUndecorated(true);
        setSize(350, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(0xFEE500));
        setLayout(new BorderLayout());

        // --- 필드+라벨 패널 ---
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        inputPanel.setBackground(new Color(0xFEE500));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 10, 30));

        JLabel idLabel = new JLabel("아이디:");
        idField = new JTextField();
        JLabel pwLabel = new JLabel("현재 비밀번호:");
        pwField = new JTextField();
        JLabel newPwLabel = new JLabel("새 비밀번호:");
        newPwField = new JTextField();

        inputPanel.add(idLabel);    inputPanel.add(idField);
        inputPanel.add(pwLabel);    inputPanel.add(pwField);
        inputPanel.add(newPwLabel); inputPanel.add(newPwField);

        // --- 버튼 패널 ---
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(0xFEE500));
        confirmBtn = new JButton("확인");
        cancelBtn = new JButton("취소");

        btnPanel.add(confirmBtn);
        btnPanel.add(cancelBtn);

        // --- 버튼 이벤트 ---
        cancelBtn.addActionListener(e -> dispose());

        confirmBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String pw = pwField.getText().trim();
            String newPw = newPwField.getText().trim();
            if (id.isEmpty() || pw.isEmpty() || newPw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 값을 입력하세요.", "입력오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // TODO: 실제 비밀번호 변경 로직 구현
            // 지금은 그냥 값을 다 채우면 "변경완료" 출력 
            JOptionPane.showMessageDialog(this, "비밀번호가 재설정되었습니다!");
            dispose();
        });

        // --- 추가 및 보이기 ---
        add(inputPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
}
