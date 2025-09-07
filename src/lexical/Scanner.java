package lexical;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import util.TokenType;

public class Scanner {
	private int state;
	private char[] sourceCode;
	private int pos;

	public Scanner(String filename) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			sourceCode = content.toCharArray();
			pos = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Token nextToken() {
		char currentChar;
		String content = "";
		state = 0;

		while (true) {
			if (isEoF()) {
				return null;
			}
			currentChar = nextChar();

			switch (state) {
				case 0:
					if (isLetter(currentChar)) {
						content += currentChar;
						state = 1;
					} else if (isMathOperator(currentChar)) {
						content += currentChar;
						state = 3;
					} else if (isAssignOperator(currentChar)) {
						content += currentChar;
						state = 4;
					}
					break;
				case 1:
					if (isLetter(currentChar) || isDigit(currentChar)) {
						content += currentChar;
						state = 1;
					} else {
						state = 2;
					}
					break;
				case 2:
					back();
					return new Token(TokenType.IDENTIFIER, content);
				case 3:
					back();
					return new Token(TokenType.MATH_OPERATOR, content);
				case 4:
					if (isAssignOperator(currentChar)) {
						content += currentChar;
						state = 5;
					} else {
						state = 6;
					}
					break;
				case 5:
					back();
					return new Token(TokenType.REL_OPERATOR, content);
				case 6:
					back();
					return new Token(TokenType.ASSIGNMENT, content);
			}
		}
	}

	private boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isMathOperator(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/';
	}

	private boolean isRelOperator(char c) {
		return c == '>' || c == '<' || c == '=' || c == '!';
	}

	private char nextChar() {
		return sourceCode[pos++];
	}

	private void back() {
		pos--;
	}

	private boolean isEoF() {
		return pos >= sourceCode.length;
	}

	private boolean isPoint(char c) {
		return c == '.';
	}

	private boolean isSpace(char c) {
		return c == ' ';
	}

	private boolean isAssignOperator(char c) {
		return c == '=';
	}
}
