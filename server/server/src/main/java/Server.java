import java.util.*;
import java.util.regex.*;
import java.util.Base64;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;

import java.util.List;

import javax.swing.*;
import java.awt.*;

import controller.mainController;

public class Server {

	static class Friend {
	    int index;
	    String id;
	    String name;
	    byte[] profileImageBytes;

	    Friend(int index, String id, String name, byte[] profileImageBytes) {
	        this.index = index;
	        this.id = id;
	        this.name = name;
	        this.profileImageBytes = profileImageBytes;
	    }
	}


    public static List<Friend> parseFriendData(String data) {
        List<Friend> friends = new ArrayList<>();

        // friendIndex, friendId, friendName, friendProfile 모두 파싱
        Pattern pattern = Pattern.compile(
            "&friendIndex\\$(\\d+)" +
            "&friendId\\$([^&]+)" +
            "&friendName\\$([^&]+)" +
            "&friendProfile\\$([^&]*)"
        );
        Matcher matcher = pattern.matcher(data);

        while (matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            String id = matcher.group(2);
            String name = matcher.group(3);
            String base64Image = matcher.group(4);

            byte[] imageBytes = null;
            if (!base64Image.isEmpty()) {
                try {
                    imageBytes = Base64.getDecoder().decode(base64Image);
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                    BufferedImage img = ImageIO.read(bais);
                    if (img == null) {
                        System.out.println("Image decode failed for friend " + name);
                        imageBytes = null;
                    }
                } catch (Exception e) {
                    System.out.println("Exception during image decode: " + e.getMessage());
                    imageBytes = null;
                }
            }
            // id 필드도 Friend 클래스에 추가 필요 (아래 참고)
            friends.add(new Friend(index, id, name, imageBytes));
        }

        return friends;
    }


    // 이미지 윈도우 띄우는 함수
    public static void showImageWindow(String title, BufferedImage img) {
        if (img == null) {
            System.out.println("No image to display.");
            return;
        }

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ImageIcon icon = new ImageIcon(img);
        JLabel label = new JLabel(icon);
        frame.getContentPane().add(label, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        String rawData = mainController.getFriendListAsString("01091827364");

        System.out.println("Raw data from controller:");
        System.out.println(rawData);

        List<Friend> friends = parseFriendData(rawData);

        System.out.println("\nParsed Friend List:");
        for (Friend f : friends) {
        	System.out.printf("Index: %d, ID: %s, Name: %s\n", f.index, f.id, f.name);

            if (f.profileImageBytes != null) {
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(f.profileImageBytes);
                    BufferedImage img = ImageIO.read(bais);
                    showImageWindow("Friend " + f.index + ": " + f.name, img);
                } catch (Exception e) {
                    System.out.println("Failed to show image: " + e.getMessage());
                }
            } else {
                System.out.println("No image data.");
            }
        }
    }
}

