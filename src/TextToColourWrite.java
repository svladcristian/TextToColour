import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class TextToColourWrite {

    public static void main(String[] args) {
    	
    	// Open image file
    	BufferedImage img = null;
		try {
    		img = ImageIO.read(new File("image.png"));
		} catch (IOException e) {
				e.printStackTrace();
		}
		
		// Determine image dimensions
		int ni, nj;
		ni = img.getHeight();
		nj = img.getWidth();
		System.out.println(ni + "*" + nj);
		
		// Open & read charmap file
		charMap cm = null;
		try {
			String CHARMAPFILENAME = "charmap.ser";
			FileInputStream fis = new FileInputStream(CHARMAPFILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			cm = (charMap) ois.readObject();
			ois.close();
			fis.close();
		}catch(IOException e) {
			e.printStackTrace();
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		ArrayList<Character> allChars = new ArrayList<Character>(cm.chars);
		System.out.println("Charmap size=" + allChars.size());
		int numberOfIntervals = 256 / allChars.size(); 
		
		// Read & decode image data
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
				
				red = red / numberOfIntervals;
				green = green / numberOfIntervals;
				blue = blue / numberOfIntervals;
				
				System.out.print("[ " + red + " " + green + " " + blue + " ] ");
				System.out.print(allChars.get(red));
				System.out.print(allChars.get(green));
				System.out.print(allChars.get(blue));
				System.out.println();
				switch (perm) {
					case 0: {
						stringBuilder.append(allChars.get(red));
						stringBuilder.append(allChars.get(green));
						stringBuilder.append(allChars.get(blue));
						perm++;
					}break;
					case 1: {
						stringBuilder.append(allChars.get(red));
						stringBuilder.append(allChars.get(blue));
						stringBuilder.append(allChars.get(green));
						perm++;
					}break;
					case 2: {
						stringBuilder.append(allChars.get(green));
						stringBuilder.append(allChars.get(red));
						stringBuilder.append(allChars.get(blue));
						perm++;
					}break;
					case 3: {
						stringBuilder.append(allChars.get(blue));
						stringBuilder.append(allChars.get(red));
						stringBuilder.append(allChars.get(green));
						perm++;
					}break;
					case 4: {
						stringBuilder.append(allChars.get(green));
						stringBuilder.append(allChars.get(blue));
						stringBuilder.append(allChars.get(red));
						perm++;
					}break;
					case 5: {
						stringBuilder.append(allChars.get(blue));
						stringBuilder.append(allChars.get(green));
						stringBuilder.append(allChars.get(red));
						perm = 0;
					}break;
					default: {
						stringBuilder.append(allChars.get(red));
						stringBuilder.append(allChars.get(green));
						stringBuilder.append(allChars.get(blue));
					}break;
				}
			}
		}
		String text;
		text = stringBuilder.toString();
		text = text.trim();
		System.out.println(text);
		System.out.println("size of text = " + text.length());
		
		// Write to file.
		try {
			FileWriter fw = new FileWriter(new File("data.out"));
            fw.write(text);
            fw.close();
      	}catch(IOException e) {
      		e.printStackTrace();
      	}
    }
}