import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class TextToColourWrite {

    public static void main(String[] args) {
    	
    	BufferedImage img = null;
		try {
    		img = ImageIO.read(new File("image.png"));
		} catch (IOException e) {
				e.printStackTrace();
		}
		
		int ni, nj;
		ni = img.getHeight();
		nj = img.getWidth();
		System.out.println(ni + "*" + nj);
		
		StringBuilder stringBuilder = new StringBuilder();
		// permutations: rgb rbg grb gbr brg bgr
		int perm = 0;
		for (int i = 0; i < nj; i++) {
			for (int j = 0; j < ni; j++) {
				int rgb = img.getRGB(i, j);
				int red = (rgb >> 16) & 0xFF;
				int green = (rgb >>8 ) & 0xFF;
				int blue = (rgb) & 0xFF;
				System.out.print("[ " + red + " " + green + " " + blue + " ] ");
				System.out.println(Character.toString ((char) red) + Character.toString ((char) green) + Character.toString ((char) blue));
				switch (perm) {
					case 0: {
						stringBuilder.append(Character.toString ((char) red) + Character.toString ((char) green) + Character.toString ((char) blue));
						perm++;
					}break;
					case 1: {
						stringBuilder.append(Character.toString ((char) red) + Character.toString ((char) blue) + Character.toString ((char) green));
						perm++;
					}break;
					case 2: {
						stringBuilder.append(Character.toString ((char) green) + Character.toString ((char) red) + Character.toString ((char) blue));
						perm++;
					}break;
					case 3: {
						stringBuilder.append(Character.toString ((char) blue) + Character.toString ((char) red) + Character.toString ((char) green));
						perm++;
					}break;
					case 4: {
						stringBuilder.append(Character.toString ((char) green) + Character.toString ((char) blue) + Character.toString ((char) red));
						perm++;
					}break;
					case 5: {
						stringBuilder.append(Character.toString ((char) blue) + Character.toString ((char) green) + Character.toString ((char) red));
						perm = 0;
					}break;
					default: {
						stringBuilder.append(Character.toString ((char) red) + Character.toString ((char) green) + Character.toString ((char) blue));
					}break;
				}
			}
		}
		String text;
		text = stringBuilder.toString();
		text = text.trim();
		System.out.println(text);
		
		try {
			FileWriter fw = new FileWriter(new File("data.out"));
            fw.write(text);
            fw.close();
      	}catch(IOException e) {
      		e.printStackTrace();
      	}
		
    }
}