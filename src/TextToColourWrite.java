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
		for (int i = 0; i < nj; i++) {
			for (int j = 0; j < ni; j++) {
				int rgb = img.getRGB(i, j);
				int red = (rgb >> 16) & 0xFF;
				int green = (rgb >>8 ) & 0xFF;
				int blue = (rgb) & 0xFF;
				stringBuilder.append(Character.toString ((char) red) + Character.toString ((char) green) + Character.toString ((char) blue));
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