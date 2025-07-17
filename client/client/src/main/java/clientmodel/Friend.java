package clientmodel;

public class Friend {
	public int index;
	public String id;
	public String name;
	public byte[] profileImageBytes;
	
    public Friend(int index, String id, String name, byte[] profileImageBytes) {
        this.index = index;
        this.id = id;
        this.name = name;
        this.profileImageBytes = profileImageBytes;
    }
    /*
     * JList는 toString()메서드의 반환값을 화면에 표시
     * 이미지를 추가하려면 CellRenderer가 필요 TODO
     * 일단은 이름만 보이도록 설정
     */
        @Override
        public String toString() {
        	return this.name;
        }
   
}