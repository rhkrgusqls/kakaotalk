from PIL import Image

def soften_edges(image_path, output_path):
    img = Image.open(image_path).convert("RGB")
    pixels = img.load()
    width, height = img.size

    for y in range(height):
        for x in range(width):
            r, g, b = pixels[x, y]
            if (r, g, b) != (255, 255, 255):
                # 곡선 인식에 필요한 최소한의 대비 유지하며 연하게 처리
                new_r = min(255, int(r + (255 - r) * 0.6))  # 밝게
                new_g = min(255, int(g + (255 - g) * 0.6))
                new_b = min(255, int(b + (255 - b) * 0.6))
                pixels[x, y] = (new_r, new_g, new_b)

    img.save(output_path)
    print(f"선이 얇게 보이도록 조정 완료: {output_path}")

# 예시 사용
soften_edges("C:/workspace/KDT/kakaotalk/client/client/image/addFriend.png",
             "C:/workspace/KDT/kakaotalk/client/client/image/addFriend_soft.png")
