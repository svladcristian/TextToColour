import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class TextToColour {
	
    public static void main(String[] args) {
    	
    	String FILENAME = "data.txt";
    	String text = new String();
    	try {
    		Scanner scanner = new Scanner(new File(FILENAME));
    		StringBuilder stringBuilder = new StringBuilder();
    		stringBuilder.append(scanner.nextLine());
    		String newLine = System.getProperty("line.separator");
	    	while (scanner.hasNextLine()) {
	    		stringBuilder.append(newLine + scanner.nextLine());
	    	}
	    	text = stringBuilder.toString();
    	}catch(FileNotFoundException ex) {
    		ex.printStackTrace();
    	}
    	System.out.println(text);

		int m = text.length();
		int n;
		if (m%3 == 0) n = m/3;
		else n = m/3 + 1;
		int roundm = n;
		System.out.println("m=" + m);
		System.out.println("round m/3=" + roundm);

		int THRESHOLD = 5;
		int sizeThreshold = THRESHOLD % n;
		int ni = 1, nj = 1, minDiff = n;
		for (int i = 0; i < sizeThreshold; i++) {
			int tempi = 1, tempj = 0, k = 1;
			do {
				k++;
				if (n%k == 0) {
					tempi = n/k;
					tempj = k;
				}
			}while (tempi > tempj);
			int tempDiff = tempj - tempi; 
			if (tempDiff < minDiff) {
				minDiff = tempDiff;
				ni = tempi;
				nj = tempj;
			}
			n++;
		}
		n = ni*nj;
		System.out.println("n=" + n + "=" + ni + "*" + nj);
	
		/*
		 * Determining pixel values, obtained strictly from text characters,
		 * with at most 2 rgb value fillers, to complete the last pixel.
		 * - roundm reached
		 *
		 * Creating image
		 */
		BufferedImage img = new BufferedImage(ni, nj, BufferedImage.TYPE_INT_RGB);
		int np = 0;
		int i;
		for (i = 0; i+3 < m; i += 3) {
			int red = (int) text.charAt(i);
			int green = (int) text.charAt(i+1);
			int blue = (int) text.charAt(i+2);
			img.setRGB(np/nj, np%nj, ((red << 16) | (green << 8) | blue));
			np++;
		}
		if (m - i > 0) {
			int red = (int) text.charAt(i);
			int green;
			if (m - i > 1) {
				green = (int) text.charAt(i+1);
			}
			else {
				green = 0;
			}
			int blue = 0;
			img.setRGB(np/nj, np%nj, ((red << 16) | (green << 8) | blue));
			np++;
		}
		System.out.println("Number of pixels obtained: " + np + " - " + "Number of pixels expected: " + roundm);
		
		/*
		 * Filling remaining pixels to completion.
		 * - n reached
		 */
		for (i = roundm; i < n; i++) {
			int red = 0;
			int green = 0;
			int blue = 0;
			img.setRGB(np/nj, np%nj, ((red << 16) | (green << 8) | blue));
			np++;
		}
		System.out.println("Number of pixels obtained: " + np + " - " + "Number of pixels expected: " + n);
		
		File f = new File("Image.png");
		try {
			ImageIO.write(img, "PNG", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }
}
