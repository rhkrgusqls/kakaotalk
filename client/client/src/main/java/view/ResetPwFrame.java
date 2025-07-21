package view;

import javax.swing.*;
import java.awt.*;
import controller.MainController;
import model.TCPManager;

public class ResetPwFrame extends JFrame {
    private JTextField idField;
    private JPasswordField pwField;
    private JPasswordField newPwField;
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
        pwField = new JPasswordField();
        JLabel newPwLabel = new JLabel("새 비밀번호:");
        newPwField = new JPasswordField();

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
            String pw = new String(pwField.getPassword());
            String newPw = new String(newPwField.getPassword());
            
            if (id.isEmpty() || pw.isEmpty() || newPw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 값을 입력하세요.", "입력오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 서버에 비밀번호 변경 요청 (메시지 형식 수정)
            String resetMsg = String.format("%%ResetPassword%%&id$%s&currentPassword$%s&newPassword$%s%%", id, pw, newPw);
            System.out.println("[DEBUG] 비밀번호 재설정 요청: " + resetMsg);
            String response = TCPManager.getInstance().sendSyncMessage(resetMsg);
            System.out.println("[DEBUG] 서버 응답: " + response);
            
            if (response != null && response.contains("success$true")) {
                JOptionPane.showMessageDialog(this, "비밀번호가 성공적으로 변경되었습니다!");
                dispose();
            } else {
                String errorMsg = "비밀번호 변경에 실패했습니다.";
                if (response != null && response.contains("error$")) {
                    try {
                        errorMsg = response.split("error\\$")[1].split("&")[0];
                    } catch (Exception ex) {
                        errorMsg = "비밀번호 변경 중 오류가 발생했습니다.";
                    }
                }
                JOptionPane.showMessageDialog(this, errorMsg, "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- 추가 및 보이기 ---
        add(inputPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
}
