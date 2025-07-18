package controller;

import model.TCPManager;

public class MainController {
	
	public static void main(String[] args) {
		MainController controller = new MainController();
//		controller.runTest();
		controller.runReceiverTest();
	}
	
	/*TCPReceiver 테스트를 위한 메서드*/
	public void runReceiverTest() {
		System.out.println("[리시버 테스트]");
		//서버연결
		TCPManager.getInstance().connect(this);
		if(!TCPManager.getInstance().isConnected()) {
			System.out.println("서버 연결 실패, 테스트 종료");
			return;
		}
		
		//서버가 메시지를 보낼때까지 기다리기
		System.out.println("4초 여유 대기 시간...");
		try {
			Thread.sleep(4000);
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		//테스트 종료
		TCPManager.getInstance().disconnect();
		System.out.println("[리시버 테스트 종료]");
	}
	//파싱하기전 원본 메시지를 받음
	public void processIncomingMessage(String message) {
		System.out.println("------ 수신 데이터 ------");
		System.out.println(message);
		
	}
	//TODO 여기에 parsingController를 호출하는 로직을 추가
	// 데이터를 파싱해서 UI를 업데이트하거나 DB에 저장하거나...
	
	
//	public void runTest() {`
//		TCPManager.getInstance().disconnect();
//	}
//	
//	public boolean connectTCP() {
//		TCPManager.getInstance().connect();
//		return TCPManager.getInstance().isConnected();
//	}
	//샌더랑 리시버에서 반환된 문자열을 받고, 모듈(모듈매니저)을 통해서 DB을 저장 후 DB를 다시 불러오고 불러온 내용을 다시 싸준다.
}
