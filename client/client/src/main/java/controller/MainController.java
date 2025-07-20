package controller;

import model.DBManager;
import model.DataParsingModule;
import model.TCPManager;
import model.User;

public class MainController {
	static TCPManager tcp;
	static MainController instance = new MainController();
	private MainController() {
		 tcp = TCPManager.getInstance();
		 tcp.connect();
		 System.out.println("[LOG][TCP]:TCP 연결시도");
		 if (tcp.isConnected()) {
		        System.out.println("TCP 연결 성공");
		 }
	}; 
	//ToDo:명령어 추가 및 서버핸들 파싱 컨트롤러(서버에서 일방적으로 주는 데이터를 처리) 생성
	public static boolean login(String id, String password) {
		DataParsingModule data = new DataParsingModule();
	    if (tcp.isConnected()) {
	        System.out.println("TCP 연결 성공! 로그인 메시지를 보냅니다.");
	        data.parseData(tcp.sendSyncMessage("%Login%&id$" + id + "&password$" + password + "%"));
	    }else {
	        System.out.println("TCP 연결 실패!");
	    }
	    if(data.getUserName()==null) {
			return false;
		}
	    User myData = new User(id, password, data.getUserName(), "./profile/self/coldplay.jpg");
	    //ToDo:프로필을 파일저장하는 코드 생성
		DBManager db = new DBManager(); 
		db.saveUser(myData);
	    return true;
	}
}
