package view;

import javax.swing.*;
import java.awt.*;
/*
 * 로그인에 실패했거나 자동로그인에 실패했을 경우 해당 프레임을 띄워 회원가입하는 프레임. 
 */
public class RegisterFrame extends JFrame {
	public JTextField idField;
	public JTextField pwField;
	public JTextField nameField;
	public JTextField phoneNumField;
	// TODO 프로필사진 설정??
	public JButton confirmBtn;
	public JButton cancelBtn;
	
	public RegisterFrame() {
		setTitle("회원가입");
		setUndecorated(true);
        setSize(350, 270);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(0xFEE500));
        setLayout(new BorderLayout());
        
        // --- 필드+라벨 패널 ---
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 8, 5));
        inputPanel.setBackground(new Color(0xFEE500));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 10, 30));

        JLabel nameLabel = new JLabel("이름 :");
        nameField = new JTextField();
        JLabel phoneNumLabel = new JLabel("전화번호 : ");
        phoneNumField = new JTextField();
        JLabel infoLabel = new JLabel("'-'없이 입력해주세요.");
        JLabel idLabel = new JLabel("아이디 :");
        idField = new JTextField();
        JLabel pwLabel = new JLabel("비밀번호 :");
        pwField = new JTextField();
        
        
        inputPanel.add(nameLabel);      inputPanel.add(nameField);
        inputPanel.add(phoneNumLabel);  inputPanel.add(phoneNumField);
        inputPanel.add(new JLabel("")); inputPanel.add(infoLabel); // 설명칸, 입력칸 비움
        inputPanel.add(idLabel);        inputPanel.add(idField);
        inputPanel.add(pwLabel);        inputPanel.add(pwField);
        
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
            String name = nameField.getText().trim();
            String pw = pwField.getText().trim();
            String phoneNum = phoneNumField.getText().trim();
            
            if (id.isEmpty() || pw.isEmpty() || name.isEmpty() || phoneNum.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 값을 입력하세요.", "입력오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // TODO: 실제 회원가입 로직 연동
            // 지금은 그냥 값을 다 채우면 "회원가입 완료" 출력 
            JOptionPane.showMessageDialog(this, name +"님 회원가입 완료되었습니다!");
            dispose();
        });

        // --- 추가 및 보이기 ---
        add(inputPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        
    }
}