import observer.ChatGroup;
import console.ObserverConsole;
import console.ServerTCP;
import controller.ParsingController;
public class Server {
    public static void main(String[] args) {
        ServerTCP.run();
        //TCP통신 대기...
        //접속이될시! 접속이 왔을 때 매니저로 가고 그매니저 안에서 루프상태가 된다.
        //거기서 이벤트를 두개를 만들어야함 하나는 클라이언트가 주면 다시 보내는것(문자열) ex'1번데이터 수신'이라고 갔을 떄 여기에다 STring을 + 해서 '수신받음' << 클라이언트는 1번데이터에대해 수신받음을 받음!
         //매니저가 자기혼자 실행을 해서 일방적으로 데이터를 주는것... //클라이언트 입장에서 아무것도 안했는데 데이터가 하나 표현이 되어야한다.(문자열)
        //TEST()메서드는 호출될때마다 클라이언트에게 아무 메시지나 주면된다 5초마다 실행시키기할것 테스트용도 , 이어서 두세명씩 접속을 계속 해서
        //INTEGER로 접속자를 구분 1, 2 ,3 처럼 구분하고 1번한테는 1번한테만 들어감메시지를 2번한테는 2번한테만 들어감메시지를 3번한테는 3번한테만 들어감메시지를 줘야함 
    }
}
