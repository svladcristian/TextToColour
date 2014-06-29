import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

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
		System.out.println("n=" + n);

		int THRESHOLD = 5;
		int sizeThreshold = THRESHOLD % n;
		int ni = 1, nj = 1, minDiff = n;
		for (int i = 0; i < sizeThreshold; i++) {
			int tempi = 0, tempj = 1, k = 1;
			do {
				k++;
				if (n%k == 0) {
					tempj = n/k;
					tempi = k;
				}
			}while (tempi < tempj);
			int tempDiff = tempi - tempj; 
			if (tempDiff < minDiff) {
				minDiff = tempDiff;
				ni = tempi;
				nj = tempj;
			}
			n++;
		}
		n = ni*nj;
		System.out.println(n + "=" + ni + "*" + nj);
    }
}
