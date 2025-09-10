package lexical;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import util.ReservedWords;
import util.TokenType;

public class Scanner {
	private int state;
	private char[] sourceCode;
	private int pos, lin, col;

	public Scanner(String filename) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			sourceCode = content.toCharArray();
			pos = 0;
			col = 0;
			lin = 1;
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
					} else if (isSlash(currentChar)) {
						content += currentChar;
						state = 7;
					} else if (isMathOperator(currentChar)) {
						content += currentChar;
						return new Token(TokenType.MATH_OPERATOR, content);
					} else if (isAssignOperator(currentChar)) {
						content += currentChar;
						state = 2;
					} else if (isRelOperator(currentChar)) {
						content += currentChar;
						state = 3;
					} else if (isLParen(currentChar)) {
						content += currentChar;
						return new Token(TokenType.L_PAREN, content);
					} else if (isRParen(currentChar)) {
						content += currentChar;
						return new Token(TokenType.R_PAREN, content);
					} else if (isPoint(currentChar)) {
						content += currentChar;
						state = 4;
					} else if (isDigit(currentChar)) {
						content += currentChar;
						state = 6;
					} else if (isHash(currentChar)) {
						while (!isEoF() && currentChar != '\n') {
							currentChar = nextChar();
						}
					} else if (!Character.isWhitespace(currentChar)) {
						throw new RuntimeException(
								"Erro léxico na linha " + lin + ", coluna " + col + ": símbolo '" + currentChar + "' não reconhecido."
							);				
					}
					break;
				case 1:
					if (isLetter(currentChar) || isDigit(currentChar)) {
						content += currentChar;
					} else {
						back();
						TokenType type = ReservedWords.TABLE.getOrDefault(content, TokenType.IDENTIFIER);
						return new Token(type, content);
					}
					break;
				case 2:
					if (isAssignOperator(currentChar)) {
						content += currentChar;
						return new Token(TokenType.REL_OPERATOR, content);
					} else {
						back();
						return new Token(TokenType.ASSIGNMENT, content);
					}
				case 3:
					if (isAssignOperator(currentChar)) {
						content += currentChar;
						return new Token(TokenType.REL_OPERATOR, content);
					} else if (!content.equals("!")) {
						back();
						return new Token(TokenType.REL_OPERATOR, content);
					} else {
						content = "";
						state = 0;
					}
					break;
				case 4:
					if (isDigit(currentChar)) {
						content += currentChar;
						state = 5;
					} else {
						content = "";
						state = 0;
					}
					break;
				case 5:
					if (isDigit(currentChar)) {
						content += currentChar;
					} else {
						back();
						return new Token(TokenType.NUMBER, content);
					}
					break;
				case 6:
					if (isDigit(currentChar)) {
						content += currentChar;
					} else if (isPoint(currentChar)) {
						content += currentChar;
						state = 4;
					} else {
						back();
						return new Token(TokenType.NUMBER, content);
					}
					break;
				case 7:
					if (isAsterisk(currentChar)) {
						content = "";
						state = 8;
					} else {
						back();
						return new Token(TokenType.MATH_OPERATOR, content);
					}
					break;
				case 8:
					while (!isEoF()) {
						currentChar = nextChar();

						if (isAsterisk(currentChar)) {
							if (!isEoF()) {
								char next = nextChar();

								if (isSlash(next)) {
									state = 0;
									break;
								}
							}
						}
					}
					state = 0;
					break;
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
		if (sourceCode[pos] == '\n') {
			lin++;
			col = 0;
		} else {
			col++;
		}
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

	private boolean isAssignOperator(char c) {
		return c == '=';
	}

	private boolean isLParen(char c) {
		return c == '(';
	}

	private boolean isRParen(char c) {
		return c == ')';
	}

	private boolean isSlash(char c) {
		return c == '/';
	}

	private boolean isHash(char c) {
		return c == '#';
	}

	private boolean isAsterisk(char c) {
		return c == '*';
	}
}
