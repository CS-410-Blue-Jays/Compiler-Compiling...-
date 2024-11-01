/*
 * Scanner.java
 * This program is a simple scanner that tokenizes input into keywords, operators, identifiers, literals, parenthesis, brackets and whitespace.
 * 
 * @authors: Brandon Hines, Tucker Amon, Steven Poripski
 * @reviewers: Connor White, Luke Pupilli, Creek Richmond
 * 
 */

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;


public class Scanner extends Token{

	public static void main(String args[]){
		System.out.println("Enter the name of the file you'd like to tokenize: ");
		String path = System.console().readLine();
		ArrayList<Token> tokens = new ArrayList<>(); // Its definitely used, quiet down compiler

		File newFile = new File(path);

		try (RandomAccessFile file = new RandomAccessFile(newFile.getAbsolutePath(), "r")) {
			System.out.println("Tokenizing file: '" + newFile.getName() + '\'');

			// Append each line to the total input
			String input = "";

			String line;
			while((line = file.readLine()) != null)
				input += line + '\n';
			tokens.addAll(scan(input));
			file.close();
		} catch(IOException e){
			String error = e.getLocalizedMessage();
			System.out.println("Error reading file '"+ newFile.getName() + "': " + error.substring(error.indexOf('(') + 1, error.length()-1) + ", check the file path and try again.");
			System.exit(-1);
		}

		// Once all tokens are found, print them
		if(tokens.isEmpty())
			System.out.println("No tokens found! Try pointing to a different file.");
		else
			for(Token token : tokens)
				System.out.println(token);
	}

	//creates an immutable map
	static Map<Character, Integer> stateMap = Map.ofEntries(
		new AbstractMap.SimpleEntry<>('(', 0),
		new AbstractMap.SimpleEntry<>(')', 1),
		new AbstractMap.SimpleEntry<>('{', 2),
		new AbstractMap.SimpleEntry<>('}', 3),
		new AbstractMap.SimpleEntry<>('&', 4),
		new AbstractMap.SimpleEntry<>('|', 5),
		new AbstractMap.SimpleEntry<>('!', 6),
		new AbstractMap.SimpleEntry<>('=', 7),
		new AbstractMap.SimpleEntry<>('.', 8),
		new AbstractMap.SimpleEntry<>('+', 9),
		new AbstractMap.SimpleEntry<>('-', 10),
		new AbstractMap.SimpleEntry<>('*', 11),
		new AbstractMap.SimpleEntry<>('/', 12),
		new AbstractMap.SimpleEntry<>('%', 13),
		new AbstractMap.SimpleEntry<>('<', 14),
		new AbstractMap.SimpleEntry<>('>', 15),

		new AbstractMap.SimpleEntry<>('0', 16),
		new AbstractMap.SimpleEntry<>('1', 16),
		new AbstractMap.SimpleEntry<>('2', 16),
		new AbstractMap.SimpleEntry<>('3', 16),
		new AbstractMap.SimpleEntry<>('4', 16),
		new AbstractMap.SimpleEntry<>('5', 16),
		new AbstractMap.SimpleEntry<>('6', 16),
		new AbstractMap.SimpleEntry<>('7', 16),
		new AbstractMap.SimpleEntry<>('8', 16),
		new AbstractMap.SimpleEntry<>('9', 16),

		new AbstractMap.SimpleEntry<>('w', 17),
		new AbstractMap.SimpleEntry<>('h', 18),
		new AbstractMap.SimpleEntry<>('i', 19),
		new AbstractMap.SimpleEntry<>('l', 20),
		new AbstractMap.SimpleEntry<>('e', 21),
		new AbstractMap.SimpleEntry<>('f', 22),
		new AbstractMap.SimpleEntry<>('o', 23),
		new AbstractMap.SimpleEntry<>('r', 24),
		new AbstractMap.SimpleEntry<>('a', 25),
		new AbstractMap.SimpleEntry<>('t', 26),
		new AbstractMap.SimpleEntry<>('n', 27),
		new AbstractMap.SimpleEntry<>('c', 28),
		
		new AbstractMap.SimpleEntry<>('b', 29),
		new AbstractMap.SimpleEntry<>('d', 29),
		new AbstractMap.SimpleEntry<>('g', 29),
		new AbstractMap.SimpleEntry<>('j', 29),
		new AbstractMap.SimpleEntry<>('k', 29),
		new AbstractMap.SimpleEntry<>('m', 29),
		new AbstractMap.SimpleEntry<>('p', 29),
		new AbstractMap.SimpleEntry<>('q', 29),
		new AbstractMap.SimpleEntry<>('s', 29),
		new AbstractMap.SimpleEntry<>('u', 29),
		new AbstractMap.SimpleEntry<>('v', 29),
		new AbstractMap.SimpleEntry<>('x', 29),
		new AbstractMap.SimpleEntry<>('y', 29),
		new AbstractMap.SimpleEntry<>('z', 29),

		new AbstractMap.SimpleEntry<>('A', 29),
		new AbstractMap.SimpleEntry<>('B', 29),
		new AbstractMap.SimpleEntry<>('C', 29),
		new AbstractMap.SimpleEntry<>('D', 29),
		new AbstractMap.SimpleEntry<>('E', 29),
		new AbstractMap.SimpleEntry<>('F', 29),
		new AbstractMap.SimpleEntry<>('G', 29),
		new AbstractMap.SimpleEntry<>('H', 29),
		new AbstractMap.SimpleEntry<>('I', 29),
		new AbstractMap.SimpleEntry<>('J', 29),
		new AbstractMap.SimpleEntry<>('K', 29),
		new AbstractMap.SimpleEntry<>('L', 29),
		new AbstractMap.SimpleEntry<>('M', 29),
		new AbstractMap.SimpleEntry<>('N', 29),
		new AbstractMap.SimpleEntry<>('O', 29),
		new AbstractMap.SimpleEntry<>('P', 29),
		new AbstractMap.SimpleEntry<>('Q', 29),
		new AbstractMap.SimpleEntry<>('R', 29),
		new AbstractMap.SimpleEntry<>('S', 29),
		new AbstractMap.SimpleEntry<>('T', 29),
		new AbstractMap.SimpleEntry<>('U', 29),
		new AbstractMap.SimpleEntry<>('V', 29),
		new AbstractMap.SimpleEntry<>('W', 29),
		new AbstractMap.SimpleEntry<>('X', 29),
		new AbstractMap.SimpleEntry<>('Y', 29),
		new AbstractMap.SimpleEntry<>('Z', 29),

		new AbstractMap.SimpleEntry<>(';', 30),
		new AbstractMap.SimpleEntry<>('\'', 31),
		new AbstractMap.SimpleEntry<>('"', 32),
		new AbstractMap.SimpleEntry<>(',', 33),
		new AbstractMap.SimpleEntry<>('[', 34),
		new AbstractMap.SimpleEntry<>(']', 35),
		new AbstractMap.SimpleEntry<>('\\', 36),
		new AbstractMap.SimpleEntry<>(':', 37),
		new AbstractMap.SimpleEntry<>('#', 38),
		new AbstractMap.SimpleEntry<>(' ', 39),
		new AbstractMap.SimpleEntry<>('\n', 39)
		);

	static Integer[][] stateTransition = {
		{1, 2, 3, 4, 5, 7, 44, 9, 12, 29, 31, 33, 34, 35, 36, 38, 11, 13, 46, 25, 46, 46, 18, 46, 46, 46, 46, 46, 40, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, 6, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, 8, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, 10, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, 12, null, null, null, null, null, null, null, 11, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 12, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 14, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 15, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 16, 46, 46, 46, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 17, 46, 46, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 21, 46, 46, 19, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 46, 46, 20, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 46, 22, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 46, 46, 46, 23, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 24, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 26, 46, 46, 46, 46, 27, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 28, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, 30, null, 30, 30, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, 32, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, 60, 56, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, 37, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, 39, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 41, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 46, 46, 46, 42, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 46, 46, 43, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, 45, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 57, 48, 48, 48, 48, 48, 48, 48, 48},
		{49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 53, 49, 58, 49, 49, 49, 49, 49, 49, 49}, 
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, 59, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},	
		{60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 61, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60},
		{60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 62, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60},
		{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
	};

	static TokenType[] acceptingStates = {null, TokenType.OPEN_PARENTHESIS, TokenType.CLOSE_PARENTHESIS, TokenType.OPEN_BRACKET, TokenType.CLOSE_BRACKET, null, TokenType.OPERATOR, null, TokenType.OPERATOR, 
		TokenType.OPERATOR, TokenType.OPERATOR, TokenType.LITERAL, TokenType.LITERAL, TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.KEYWORD, 
		TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.KEYWORD, TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.KEYWORD, TokenType.IDENTIFIER, TokenType.KEYWORD, 
		TokenType.IDENTIFIER, TokenType.KEYWORD, TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR, 
		TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR, TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.KEYWORD, TokenType.OPERATOR, 
		TokenType.OPERATOR, TokenType.IDENTIFIER, TokenType.SEMICOLON, null, null, TokenType.COMMA, TokenType.OPEN_SQ_BRACKET, TokenType.CLOSE_SQ_BRACKET, 
		null, TokenType.COLON, TokenType.PROCESSOR, TokenType.COMMENT, TokenType.CHAR, TokenType.STRING, TokenType.ESCAPE_SEQUENCE, null, null, TokenType.MULTILINE_COMMENT,};

	public static List<Token> scan(String input){
		input += "\n"; // Add newline to end of input to finalize last token in a sequence
		List<Token> tokens = new ArrayList<>();
		int state = 0; // Start
		int bookmark = 0; // Start of token

		for(int i = 0; i < input.length(); i++){
				char c = input.charAt(i);

				if(Character.isWhitespace(c) && state != 56 && state != 49 && state != 60 && state != 61){
					// Finalizes a token if it is in a state and resets the state
					if(state != 0){
						tokens.add(new Token(acceptingStates[state], input.substring(bookmark, i)));
						state = 0;
					}
					// Skips whitespace between tokens
					bookmark = i+1;
					continue;
				} else if (state == 56) {
					// Finalizes a single-line comment token when newline is found
					if(c == '\n'){
						tokens.add(new Token(acceptingStates[state], input.substring(bookmark, i)));
						state = 0;
						bookmark = i+1;
					}
				}

				if(stateMap.get(c) != null && stateTransition[state][stateMap.get(c)] != null){
					//	check to see if accepting state to finalize token
					if((acceptingStates[state] != null && i < input.length() - 1 && i < 30 && stateTransition[state][i+1] != null && acceptingStates[stateTransition[state][i+1]] != acceptingStates[state])){
						tokens.add(new Token(acceptingStates[state], input.substring(bookmark, i)));		
						state = 0;
						bookmark = i;
					} else if (input.length() == i){
						tokens.add(new Token(acceptingStates[state], input.substring(bookmark, i)));		
						state = 0;
						bookmark = i;
					}
				} else {
					//	check to see if accepting state to finalize token
					if(acceptingStates[state] != null){
						tokens.add(new Token(acceptingStates[state], input.substring(bookmark, i)));
						state = 0;
						bookmark = i;
					}
				}
				if(stateMap.get(c) == null) {
					System.out.println("Character not recognized: " + c);
					break;
				} else if(stateTransition[state][stateMap.get(c)] != null)
					state = stateTransition[state][stateMap.get(c)];
		}
		return tokens;
	}
}
