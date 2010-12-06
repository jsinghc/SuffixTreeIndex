import java.util.ArrayList;

import com.cowinvest.lib.suffixTreeIndex.SuffixTreeIndex;

/**
 * 
 */

/**
 * @author jagjitchoudhary
 *
 */
public class SuffixTreeIndexTest {
	public static void main (String[] args) {
		SuffixTreeIndex suffixTreeIndex = new SuffixTreeIndex();
		suffixTreeIndex.addText("tabcdabeabmtestforbestpestca", 1);
		suffixTreeIndex.addText("cacao", 2);
		
		suffixTreeIndex.addText("ca", 40);
        suffixTreeIndex.addText("cacaca", 41);
		
		suffixTreeIndex.addText("A quick brown fox jumps over a lazy dog", 3);
		
		
		ArrayList<Integer> caca = suffixTreeIndex.search("caca");
		ArrayList<Integer> btest = suffixTreeIndex.search("btest");
		ArrayList<Integer> pest = suffixTreeIndex.search("pest");
		ArrayList<Integer> beabmtest = suffixTreeIndex.search("beabmtest");
		System.out.print("caca result:" + caca);
		System.out.print("\nbtest result:" + btest);
		System.out.print("\nPest result:" + pest);
		System.out.print("\nquick result:" + suffixTreeIndex.search("quick"));
		System.out.print("\nbeabmtest result:" + beabmtest + "\n");
	}
}