import java.util.Scanner;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TextToColourRead {
	
    public static void main(String[] args) {
    	
		// Reading text from file.
		String text = new String();
    	try {
    		String INPUTFILENAME = "data.in";
    		Scanner scanner = new Scanner(new File(INPUTFILENAME));
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
    	
		// Creating character mapping.
		ArrayList<Character> allChars = new ArrayList<Character>();
		for (int i = 0; i < text.length(); i++) {
			if (allChars.contains(text.charAt(i)) == false) {
				allChars.add(text.charAt(i));
			}
		}
		allChars.add('\0');
		Collections.shuffle(allChars);
		System.out.println("Charmap size=" + allChars.size());
		int intervalSpan = 256 / allChars.size(); 
		
		try {
			charMap cm = new charMap(allChars);
			String CHARMAPFILENAME = "charmap.ser";
			FileOutputStream fos = new FileOutputStream("charmap.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(cm);
			oos.close();
			fos.close();
		}catch(IOException e) {
			e.printStackTrace();
		}

		 // Determining the size the resulting image.
		int m = text.length();
		int n;
		if (m%3 == 0) n = m/3;
		else n = m/3 + 1;
		int roundm = n;
		System.out.println("m=" + m);
		System.out.println("initial n=round m/3=" + roundm);
		
		// Determining the optimal dimensions the resulting image.
		double THRESHOLD_VALUE = 5;
		double thresholdSize = (((THRESHOLD_VALUE / 100) * n ) < 1) ? 1 : (THRESHOLD_VALUE / 100) * n;
		int ni = 1, nj = 1, minDiff = n;
		for (int i = 0; i < thresholdSize; i++) {
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
		
		// Determining pixel values, obtained strictly from text characters,
		// with at most 2 rgb value fillers, to complete the last pixel.
		// - roundm reached
		// Creating image
		BufferedImage img = new BufferedImage(nj, ni, BufferedImage.TYPE_INT_RGB);
		int np = 0;
		int i;
		// permutations: rgb rbg grb gbr brg bgr
		int perm = 0;
		Random rand = new Random();
		int range, randNum;
		for (i = 0; i+3 <= m; i += 3) {
			// int red = (int) text.charAt(i);
			// int green = (int) text.charAt(i+1);
			// int blue = (int) text.charAt(i+2);
			
			// character mapping
			int red = allChars.indexOf(text.charAt(i));
			range = ((((red + 1) *intervalSpan) - 1) - (red *intervalSpan))  + 1;
			randNum =  rand.nextInt(range) + (red *intervalSpan);
			red = randNum;
			
			int green = allChars.indexOf(text.charAt(i+1));
			range = (((green + 1) *intervalSpan) - 1) - (green *intervalSpan)  + 1;
			randNum =  rand.nextInt(range) + (green *intervalSpan);
			green = randNum;
			
			int blue = allChars.indexOf(text.charAt(i+2));
			range = (((blue + 1) *intervalSpan) - 1) - (blue *intervalSpan)  + 1;
			randNum =  rand.nextInt(range) + (blue *intervalSpan);
			blue = randNum;
			
			System.out.print("[ " + red + " " + green + " " + blue + " ] ");
			switch (perm) {
				case 0: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (green << 8) | blue));
					perm++;
				}break;
				case 1: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (blue << 8) | green));
					perm++;
				}break;
				case 2: {
					img.setRGB(np/ni, np%ni, ((green << 16) | (red << 8) | blue));
					perm++;
				}break;
				case 3: {
					img.setRGB(np/ni, np%ni, ((green << 16) | (blue << 8) | red));
					perm++;
				}break;
				case 4: {
					img.setRGB(np/ni, np%ni, ((blue << 16) | (red << 8) | green));
					perm++;
				}break;
				case 5: {
					img.setRGB(np/ni, np%ni, ((blue << 16) | (green << 8) | red));
					perm = 0;
				}break;
				default: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (green << 8) | blue));	
				}break;
			}
			np++;
		}
		if (m - i > 0) {
			//int red = (int) text.charAt(i);
			int red = allChars.indexOf(text.charAt(i));
			range = (((red + 1) *intervalSpan) - 1) - (red *intervalSpan)  + 1;
			randNum =  rand.nextInt(range) + (red *intervalSpan);
			red = randNum;
			
			int green;
			if (m - i > 1) {
				green = allChars.indexOf(text.charAt(i+1));
				range = (((green + 1) *intervalSpan) - 1) - (green *intervalSpan)  + 1;
				randNum =  rand.nextInt(range) + (green *intervalSpan);
				green = randNum;
			}
			else {
				green = allChars.indexOf('\0');
				range = ((((green + 1) *intervalSpan) - 1) - (green *intervalSpan))  + 1;
				randNum =  rand.nextInt(range) + (green *intervalSpan);
				green = randNum;
			}
			int blue = allChars.indexOf('\0');
			range = ((((blue + 1) *intervalSpan) - 1) - (blue *intervalSpan))  + 1;
			randNum =  rand.nextInt(range) + (blue *intervalSpan);
			blue = randNum;
			System.out.println("*[ " + red + " " + green + " " + blue + " ]* ");
			switch (perm) {
				case 0: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (green << 8) | blue));
					perm++;
				}break;
				case 1: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (blue << 8) | green));
					perm++;
				}break;
				case 2: {
					img.setRGB(np/ni, np%ni, ((green << 16) | (red << 8) | blue));
					perm++;
				}break;
				case 3: {
					img.setRGB(np/ni, np%ni, ((green << 16) | (blue << 8) | red));
					perm++;
				}break;
				case 4: {
					img.setRGB(np/ni, np%ni, ((blue << 16) | (red << 8) | green));
					perm++;
				}break;
				case 5: {
					img.setRGB(np/ni, np%ni, ((blue << 16) | (green << 8) | red));
					perm = 0;
				}break;
				default: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (green << 8) | blue));	
				}break;
			}
			np++;
		}
		System.out.println();
		System.out.println("Number of pixels obtained: " + np + " - " + "Number of pixels expected: " + roundm);
			
		// Filling remaining pixels to completion.
		// - n reached
		for (i = roundm; i < n; i++) {
			int red = allChars.indexOf('\0');
			range = ((((red + 1) *intervalSpan) - 1) - (red *intervalSpan))  + 1;
			randNum =  rand.nextInt(range) + (red *intervalSpan);
			red = randNum;
			
			int green = allChars.indexOf('\0');
			range = ((((green + 1) *intervalSpan) - 1) - (green *intervalSpan))  + 1;
			randNum =  rand.nextInt(range) + (green *intervalSpan);
			green = randNum;
			
			int blue = allChars.indexOf('\0');
			range = ((((blue + 1) *intervalSpan) - 1) - (blue *intervalSpan))  + 1;
			randNum =  rand.nextInt(range) + (blue *intervalSpan);
			blue = randNum;
			
			System.out.print("**[ " + red + " " + green + " " + blue + " ]** ");
			switch (perm) {
				case 0: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (green << 8) | blue));
					perm++;
				}break;
				case 1: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (blue << 8) | green));
					perm++;
				}break;
				case 2: {
					img.setRGB(np/ni, np%ni, ((green << 16) | (red << 8) | blue));
					perm++;
				}break;
				case 3: {
					img.setRGB(np/ni, np%ni, ((green << 16) | (blue << 8) | red));
					perm++;
				}break;
				case 4: {
					img.setRGB(np/ni, np%ni, ((blue << 16) | (red << 8) | green));
					perm++;
				}break;
				case 5: {
					img.setRGB(np/ni, np%ni, ((blue << 16) | (green << 8) | red));
					perm = 0;
				}break;
				default: {
					img.setRGB(np/ni, np%ni, ((red << 16) | (green << 8) | blue));	
				}break;
			}
			np++;
		}
		System.out.println();
		System.out.println("Number of pixels obtained: " + np + " - " + "Number of pixels expected: " + n);
		
		// Writing resulting image.
		try {
			ImageIO.write(img, "PNG", new File("image.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	
    }
}
