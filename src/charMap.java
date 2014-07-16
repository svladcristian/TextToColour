import java.util.ArrayList;
import java.io.Serializable;

class charMap implements Serializable {
	ArrayList<Character> chars = new ArrayList<Character>();
	charMap(ArrayList<Character> charArray) {
		chars = charArray;
	}	
}