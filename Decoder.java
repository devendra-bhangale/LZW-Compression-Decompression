/*
 * Class:			Decoder.java
 * 
 * Author:			Devendra Bhangale
 * 
 * Description:		This class decodes the input '.lzw' file and outputs a decompressed file with '.txt' extension.
 */

// package decoder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Decoder {
	public static ArrayList<String> Table = null; /* Data structure for dictionary to store the codes */

	public static int bit_length = 0; /* bit length of the codes */
	public static int max_table_size = 0; /* max number of codes that can be saved in the dictionary */

	public static void main(String... args) {
		bit_length = Integer.parseInt(args[1]);
		max_table_size = (int) Math.pow((double) 2, (double) bit_length);
		Table = new ArrayList<String>(max_table_size);

		BufferedReader input = null; /* input buffer */
		File file = null; /* to open a file */
		FileWriter fw = null; /* to write a file */
		BufferedWriter output = null; /* output buffer */

		String currentDir = System.getProperty("user.dir") + "/"; /* path to current working directory */
		String inputFile = args[0]; /* name of the input file */
		String inputPath = currentDir + inputFile; /* system path of the input file */
		String outputFile = (inputFile.split("\\."))[0] + "_decoded.txt"; /* system path of the output file with extension '.txt' */

		String line = ""; /* current line being checked */
		String newLine = ""; /* new line being read */
		String code = ""; /* current code being checked */
		String str = ""; /* current string being checked */
		String newStr = ""; /* new string being checked */
		
		char temp[] = {}; /* temporary array of codes from input file */
		
		try {
			/* open an output file and create a file writer to write in it */
			file = new File(outputFile);
			fw = new FileWriter(file.getAbsoluteFile());
			
			/* open the input and output files as buffers */
			input = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), Charset.forName("UTF-16BE")));
			output = new BufferedWriter(fw);
			
			initTable(); /* inititalize the dictionary with existing 256 ASCII character codes */

			boolean firstTime = true;
			System.out.println("\nDecompression initialized...\tTable size: " + Table.size());
			line = input.readLine(); /* read first input 'line' */
			while (line != null) { 
				if((newLine = input.readLine()) != null){ /* read next input line */
					line += "\n"; /* add a 'line feed' character at the end of 'line' only if this is not the last line  */
				}
				
				temp = line.toCharArray(); /* line to array of 'codes' */
				
				for (int i = 0; i < temp.length; i++) {
					code = String.valueOf(temp[i]); /* get new 'code' for testing */
					
					if (firstTime) { /* evaluate this block only first time and only once */
						firstTime = false;
						
						str = Table.get((int) code.charAt(0)); /* get the 'string' from table at index 'code' */
						
						output.write(str); /* write the first 'string' of the first 'code' to output buffer */
						System.out.print("Decoded data:\t" + Table.indexOf(str) + "\t" + str);
						System.out.println("\t\t\tcodes:\t" + (int) code.charAt(0) + "\t" + code);
						continue;
					}

					if (Table.size() <= (int) code.charAt(0)) {
						newStr = str + str.toCharArray()[0]; /* if the 'code' is NOT DEFINED in the table then generate the 'new string' from 'string' */
					} else {
						newStr = Table.get((int) code.charAt(0)); /* else get the 'new string' from the table at index 'code' */
					}
					
					output.write(newStr); /* write the 'new string' for the existing 'code' to output buffer */
					System.out.print("Decoded data:\t" + Table.indexOf(newStr) + "\t" + newStr);

					if (Table.size() <= max_table_size) {
						Table.add(str + newStr.toCharArray()[0]); /* add new 'code' for the 'new string' if max capacity of dictionary not reached */
						System.out.print("\t\t\tcodes:\t" + (int) code.charAt(0) + "\t" + code);
						System.out.println("\t\t\tTable updates:\t" + Table.indexOf(str + newStr.toCharArray()[0]) + "\t" + str + newStr.toCharArray()[0]);
					}
					str = newStr; /* start again with the 'new string' */
				}
				line = newLine; /* assign the 'new line' as current 'line' to continue */
			}
			System.out.println("Decompression completed...\tTable size: " + Table.size());
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

