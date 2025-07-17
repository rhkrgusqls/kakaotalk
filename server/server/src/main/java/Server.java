import observer.ChatGroup;
import console.ObserverConsole;
import controller.ParsingController;
public class Server {
    public static void main(String[] args) {
        String testInput1 = "%Login%&id$user1&password$pass1%user1";
        String testInput2 = "%AddFriend%&phoneNum$01012345678&phoneNum$01099993333%user1";
        String testInput3 = "%SendMsg%&name$Hello&name$World%user1";
        String testInput4 = "%Unknown%&id$abc%user2";

        ParsingController.controllerHandle(testInput1);
        ParsingController.controllerHandle(testInput2);
        ParsingController.controllerHandle(testInput3);
        ParsingController.controllerHandle(testInput4);
    }
}
