/*
 * Class:			Encoder.java
 * 
 * Author:			Devendra Bhangale
 * 
 * Description:		This class encodes the input file and outputs a compressed file with '.lzw' extension.
 */

// package encoder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Encoder {
	public static ArrayList<String> Table = null; /* Data structure for dictionary to store the codes */

	public static int bit_length = 0; /* bit length of the codes */
	public static int max_table_size = 0; /* max number of codes that can be saved in the dictionary */

	public static void main(String... args) {
		bit_length = Integer.parseInt(args[1]);
		max_table_size = (int) Math.pow((double) 2, (double) bit_length);
		Table = new ArrayList<String>(max_table_size);

		BufferedReader input = null; /* input buffer */
		BufferedWriter output = null; /* output buffer */

		String currentDir = System.getProperty("user.dir") + "/"; /* path to current working directory */
		String inputFile = args[0]; /* name of the input file */
		String inputPath = currentDir + inputFile; /* system path of the input file */
		String outputFile = (inputFile.split("\\."))[0] + ".lzw"; /* system path of the output file with extension '.lzw' */

		String line = ""; /* current line being read */
		String symbol = ""; /* current symbol being checked */
		String str = ""; /* current string being checked */

		char temp[] = {}; /* temporary array of symbols from input file */

		try {
			/* open the input and output files as buffers */
			input = new BufferedReader(new FileReader(inputPath));
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-16BE")); /* 16-bit Unicode Transformation Format with Big Endianness */

			initTable(); /* inititalize the dictionary with existing 256 ASCII character codes */

			System.out.println("\nCompression initialized...\tTable size: " + Table.size());
			while ((line = input.readLine()) != null) { /* read input line by line */
				line += "\n"; /* add a 'line feed' character at the end of 'line' */
				temp = line.toCharArray(); /* line to array of 'symbols' */

				for (int i = 0; i < temp.length; i++) {
					symbol = String.valueOf(temp[i]); /* get new 'symbol' for testing */

					if (Table.contains(str + symbol)) {
						str += symbol; /* concatenate if 'code' for 'STRING + SYMBOL' exists in dictionary (greedy algorithm) */
					} else {
						output.write(Table.indexOf(str)); /* else write the 'code' for the existing 'string' to output buffer */
						System.out.print("Encoded data:\t" + Table.indexOf(str) + "\t" + str);
						if (Table.size() <= max_table_size) {
							Table.add(str + symbol); /* add new 'code' for the new string ('STRING + SYMBOL') if max capacity of dictionary not reached */
							System.out.println("\t\t\tTable updates:\t" + Table.indexOf(str+symbol) + "\t" + str+symbol);
						}
						str = symbol; /* start again with the new 'symbol' */
					}
				}
			}
			if(str.length() > 1){
				str = str.substring(0, str.length()-1); /* remove the 'line feed' character from the last line (since an extra '\n' is added earlier), if length of 'string' is greater than 1 */
				output.write(Table.indexOf(str)); /* write the 'code' for the last 'string' to output buffer */
				System.out.println("Last Encoded data:\t" + Table.indexOf(str) + "\t" + str);
			}
			
			System.out.println("Compression completed...\tTable size: " + Table.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally { // 'finally' block is always executed; cleanup code is included in most cases.
			if (input != null) {
				try {
					input.close(); /* close the input buffer */
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (output != null) {
				try {
					output.close(); /* close the output buffer */
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/* inititalize the dictionary with existing 256 ASCII character codes */
	public static void initTable() {
		System.out.println("Initializing Table...\tTable size: " + Table.size());

		for (int i = 0; i < 256; i++) {
			Table.add(String.valueOf((char) i));
			// System.out.println(i + "\t" + Table.get(i));
		}

		System.out.println("Initialization completed...\tTable size: " + Table.size());
	}

}

