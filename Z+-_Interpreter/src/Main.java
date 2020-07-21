/* Bryan Hayes 
 * 2/4/2020
 * Z+- Interpreter 
 * Interprets a language similar to Java, can perform basic math, variable declaration, and for loops 
 * 
 * 
 * Every test I have run, as well as the tests provided on the homework document, all work.  
 * None of the tests I have run have failed.  
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	// Stores names of all current variables to make searching easier
	public static ArrayList<String> variableNames = new ArrayList<String>();
	// Contains all info about current variables, including name, data type, and
	// value
	public static ArrayList<zObject> variables = new ArrayList<zObject>();
	// Keeps track of current line number
	public static int line = 0;

	public static void main(String[] args) { 
		try {
			Scanner in = new Scanner(new File(args[0]));

			while (in.hasNextLine()) {
				line++;
				String command = in.nextLine();
				if (!command.equals("")) {
					command = command.toLowerCase();
					String[] commandArgs = command.split("\\s+");

					// Kicks each line off to see what operation needs to be done
					decideOperation(commandArgs);
				}
			}
		}

		catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
	}

	public static void decideOperation(String[] commandArgs) {
		// Creating variables
		if (commandArgs[0].equalsIgnoreCase("int") || commandArgs[0].equalsIgnoreCase("string")
				|| commandArgs[0].equalsIgnoreCase("bool")) {
			variables.add(createVariable(commandArgs));
		}

		// For loops
		else if (commandArgs[0].equalsIgnoreCase("for")) {
			String temp = "";
			for (int i = 0; i < commandArgs.length; i++) {
				temp += " " + commandArgs[i];
			}
			forLoops(temp);
		}

		// Print statement
		else if (commandArgs[0].equalsIgnoreCase("print")) {
			printOut(commandArgs);
		}

		// Changing value of declared variable
		else if (variableNames.contains(commandArgs[0]) && commandArgs[1].equals("=")) {
			int pos = -1;
			int count = 0;
			for (String s : variableNames) {
				if (s.equalsIgnoreCase(commandArgs[0]))
					pos = count;
				count++;
			}
			String type = variables.get(pos).getDataType();
			if (type.equalsIgnoreCase("string")) {
				String data = commandArgs[2];
				if (commandArgs.length != 4) {
					for (int i = 3; i < commandArgs.length - 1; i++) {
						data += " " + commandArgs[i];
					}
				}
				variables.set(pos, changeValue(commandArgs[0], type, data));
			} else
				variables.set(pos, changeValue(commandArgs[0], type, commandArgs[2]));
		}

		// Math statement
		else if (commandArgs[1].equals("+=") || commandArgs[1].equals("*=") || commandArgs[1].equals("-=")) {
			int pos = -1;
			int count = 0;
			for (String s : variableNames) {
				if (s.equalsIgnoreCase(commandArgs[0])) {
					pos = count;
					break;
				}
				count++;
			}

			if (pos != -1)
				variables.set(pos, doMath(commandArgs[0], commandArgs[1], commandArgs[2], variables.get(pos).getValue(),
						variables.get(pos).getDataType()));
		}
	}

	// Method for creating variables
	public static zObject createVariable(String[] commands) {
		zObject newZ = new zObject(commands[0], commands[1], "");
		variableNames.add(commands[1]);
		return newZ;
	}

	// Method for changing the value of a variable
	public static zObject changeValue(String name, String dataType, String newValue) {
		zObject replace;

		if (dataType.equalsIgnoreCase("string")) {
			newValue = newValue.substring(1, newValue.length() - 1);
			replace = new zObject(dataType, name, newValue);
		} else {
			replace = new zObject(dataType, name, newValue);
		}

		return replace;
	}

	// Method for printing
	public static void printOut(String[] output) {
		if (output[1].charAt(0) == '"') {
			if (output.length == 3)
				System.out.println(output[1].substring(1, output[1].length() - 1));
			else {
				for (int i = 2; i < output.length - 2; i++) {
					System.out.print(" " + output[i]);
				}
				System.out
						.println(" " + output[output.length - 2].substring(0, output[output.length - 2].length() - 1));
			}
		} else if (output.length == 3) {
			int pos = variableNames.indexOf(output[1]);
			if (variables.get(pos).getValue() != null)
				System.out.println(variables.get(pos).getValue());
			else {
				System.out.println("RUNTIME ERROR: line " + line);
				System.exit(1);
			}
		}
	}

	// Handles +=, -=, and *= operations
	public static zObject doMath(String name, String operation, String toBeAdded, String currentValue,
			String dataType) {
		try {
			
			// Handles adding two strings 
			if (dataType.equalsIgnoreCase("string")) {
				toBeAdded = toBeAdded.substring(1, toBeAdded.length() - 1);
				return new zObject("string", name, currentValue + toBeAdded);
			} else {

				int added, currValue;
				
				// Handles adding a boolean to an int  
				if (toBeAdded.equalsIgnoreCase("true"))
					added = 1;
				else if (toBeAdded.equalsIgnoreCase("false"))
					added = 0;
				else if (variableNames.contains(toBeAdded)) {
					int pos = variableNames.indexOf(toBeAdded);
					if (variables.get(pos).getValue().equalsIgnoreCase("true"))
						added = 1;
					else if (variables.get(pos).getValue().equalsIgnoreCase("false"))
						added = 0;
					else
						added = Integer.parseInt(variables.get(pos).getValue());
				} else
					added = Integer.parseInt(toBeAdded);

				if (currentValue.equalsIgnoreCase("true"))
					currValue = 1;
				else if (currentValue.equalsIgnoreCase("false"))
					currValue = 0;
				else if (variableNames.contains(currentValue)) {
					int pos = variableNames.indexOf(currentValue);
					if (variables.get(pos).getValue().equalsIgnoreCase("true"))
						currValue = 1;
					else if (variables.get(pos).getValue().equalsIgnoreCase("false"))
						currValue = 0;
					else
						currValue = Integer.parseInt(variables.get(pos).getValue());
				} else
					currValue = Integer.parseInt(currentValue);

				// Increment statements 
				if (operation.equals("+=")) {
					currValue += added;
				} else if (operation.equals("-=")) {
					currValue -= added;
				} else if (operation.equals("*="))
					currValue *= added;

				currentValue = "" + currValue;

				zObject ret = new zObject("int", name, currentValue);

				return ret;
			}
		} catch (Exception e) {
			System.out.println("RUNTIME ERROR: line " + line);
			System.exit(1);
			return null;
		}
	}

	// Handles single and nested for-loops
	public static void forLoops(String fullLoop) {
		
		String[] temp = fullLoop.trim().split("\\s+");

		int numLoops = Integer.parseInt(temp[1]);
		String placeHolder = "";
		for (int i = 2; i < temp.length - 1; i++) {
			placeHolder += " " + temp[i];
		}
		
		String[] commands = placeHolder.split(";");
		for (int i = 0; i < numLoops; i++) {
			for (int j = 0; j < commands.length; j++) {
				String temp1 = commands[j] + " ;";
				if (temp1.contains("endfor ;"))
					continue;
				if (temp1.contains("endfor"))
					temp1 = temp1.substring(7);
				String[] command = temp1.trim().split("\\s+");
				decideOperation(command);
			}
		}
	}

}
