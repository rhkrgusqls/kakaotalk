import model.DBManagerModule;

public class Server {
    public static void main(String[] args) {
        DBManagerModule db = new DBManagerModule();

        // 테스트용 데이터
        String testId = "test@gmail.com";
        String testPw = "test1234";
        String testName = "김철수";
        String testProfileDir = "./01012345678";
        String testPhone = "01012345678";

        // 1. 이미 등록된 유저인지 확인
        boolean exists = db.isRegisteredUser(testPhone);
        System.out.println("등록 여부: " + exists);

        // 2. 등록 시도
        if (!exists) {
            boolean result = db.registerUser(testId, testPw, testName, testProfileDir, testPhone);
            System.out.println("등록 결과: " + result);
        }
    }
}
