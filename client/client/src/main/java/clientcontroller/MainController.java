package clientcontroller;

import model.TCPManager;

public class MainController {
	
	public static void main(String[] args) {
		MainController controller = new MainController();
		controller.runTest();
	}
	
	public void runTest() {
		boolean isConnected = this.connectTCP();
		if(!isConnected) {
			System.out.println("서버 연결 실패");
			return;
		}
		String response1 = TCPManager.getInstance().sendSyncMessage("%Login%&id$ahnys2024@daum.net&password$pwtest12%");
		System.out.println("C1에 대한 서버 응답 :" + response1);
		/*
		String response2 = TCPManager.getInstance().sendSyncMessage("C2:사용자정보요청");
		System.out.println("C2에 대한 서버 응답 :" + response2);*/
		
		TCPManager.getInstance().disconnect();
	}
	
	public boolean connectTCP() {
		TCPManager.getInstance().connect();
		return TCPManager.getInstance().isConnected();
	}
	//샌더랑 리시버에서 반환된 문자열을 받고, 모듈(모듈매니저)을 통해서 DB을 저장 후 DB를 다시 불러오고 불러온 내용을 다시 싸준다.
}
