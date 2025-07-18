package view;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.awt.Color;
import java.awt.geom.RoundRectangle2D;
// 123123 
public class LoginFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	//DB 객체 필드선언
	private JPanel top; // 상단
	private JButton exit;
	private JButton hide;
	
	private JPanel middle; // 중간
	private ImageIcon imageIcon; // 이미지  
	private JLabel imageLabel; // 이미지 라벨
	private JTextField inputId;
	private JTextField inputPw;
	private JButton loginVerify;
	private JCheckBox autoLogin;
	
	private JPanel bottom; // 하단
	private JButton resetPw;
	
	public LoginFrame() { // 생성자
//		System.out.println(new File(".").getAbsolutePath()); // 경로 확인
		
		this.setUndecorated(true);
		this.setLayout(new BorderLayout());
		this.getContentPane().setBackground(new Color(0xFEE500)); // 현재 프레임 색상 YELLOW
		// 최상단 부분 ( 삭제, 트레이 최소화 )
		top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		top.setOpaque(false);
		exit = new JButton("X");
		exit.setFont(new Font("Arial", Font.BOLD, 20));
		exit.setOpaque(false);
		exit.setContentAreaFilled(false);
		exit.setBorderPainted(false);
		exit.setPreferredSize(new Dimension(50, 50));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		hide = new JButton("_");
		hide.setFont(new Font("Arial", Font.BOLD, 20));
		hide.setOpaque(false);
		hide.setContentAreaFilled(false);
		hide.setBorderPainted(false);
		hide.setPreferredSize(new Dimension(50, 50));
		hide.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setState(JFrame.ICONIFIED);
			}
		});
		top.add(hide);
		top.add(exit);
		
		this.add(top, BorderLayout.NORTH);
		/*------------------------------------------------------------------------------------*/
		middle = new JPanel();
		middle.setBackground(new Color(0xFEE500));
		middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
		// 카카오톡 로고 
		imageIcon = new ImageIcon("image/KakaoTalk_logo151.png");
		imageLabel = new JLabel(imageIcon);
		imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		middle.add(imageLabel);
		
		middle.add(Box.createRigidArea(new Dimension(0, 10))); // 10px 세로 간격
		middle.setBorder(BorderFactory.createEmptyBorder(20 , 80, 20, 80));
		// id, pw 텍스트 필드 및 로그인 버튼
		inputId = new JTextField("아이디");
		inputId.setMaximumSize(new Dimension(240, 35));
//		inputId.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
		inputPw = new JTextField("비밀번호");
		inputPw.setMaximumSize(new Dimension(240, 35));
		loginVerify = new JButton("로그인");
		//Color brownColor = new Color(108, 60, 12);new Color(0xFEE500)
		//loginVerify.setBackground(brownColor);
		loginVerify.setBackground(new Color(0x423630));
		loginVerify.setForeground(Color. WHITE);
		loginVerify.setOpaque(true);
		loginVerify.setContentAreaFilled(true);
		loginVerify.setBorderPainted(true); // 테두리 감추기 
		loginVerify.setMaximumSize(new Dimension(240, 35));
		loginVerify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO : DB을 통한 아이디, 비밀번호를 가져와야함 지금은 임시
				String id = inputId.getText();
				String pw = inputPw.getText();
//				boolean isUserId = db.isRegisteredUser(id);@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
				if(id.equals("test") && pw.equals("test")) {
					new Base();
				} else {
					JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호가 올바르지 않습니다.");
//					new SignUpFrame(); // 로그인실패시 회원가입 프레임 창으로 넘어감  
				}
			}
		});
		autoLogin = new JCheckBox("자동 로그인");
		autoLogin.setOpaque(false);
		autoLogin.setContentAreaFilled(false);
		autoLogin.setBorderPainted(false);
		autoLogin.setMaximumSize(new Dimension(240, 25));
		
		inputId.setAlignmentX(Component.CENTER_ALIGNMENT);
		middle.add(Box.createRigidArea(new Dimension(0, 10)));
		middle.add(inputId);
		inputPw.setAlignmentX(Component.CENTER_ALIGNMENT);
		middle.add(Box.createRigidArea(new Dimension(0, 10)));
		middle.add(inputPw);
		loginVerify.setAlignmentX(Component.CENTER_ALIGNMENT);
		middle.add(Box.createRigidArea(new Dimension(0, 10)));
		middle.add(loginVerify);
		autoLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
		middle.add(Box.createRigidArea(new Dimension(0, 10)));
		middle.add(autoLogin);

		this.add(middle, BorderLayout.CENTER);
		
		/*------------------------------------------------------------------------------------*/
		bottom = new JPanel();
		bottom.setBackground(new Color(0xFEE500));
		resetPw = new JButton("비밀번호 재설정");
		resetPw.setOpaque(false);
		resetPw.setContentAreaFilled(false);
		resetPw.setBorderPainted(false);
		bottom.add(resetPw);
		this.add(bottom, BorderLayout.SOUTH);

		this.pack();
		this.setLocation(1000, 200);
		this.setSize(400, 650);
		this.setVisible(true);
		this.setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 13, 13));
		/*------------------------------------------------------------------------------------*/

	}
	
	public static void main(String[] args) {
		new LoginFrame();
	}
}