/*
Alunos:
Etienne Pautet    / 31334369
Fabiano Quirino   / 31719511
João Pedro Araújo / 30914132
Renan Chacon      / 31761607
 */

package mini_compiler;

import exceptions.SyntacticException;
import lexical.Scanner;
import syntactic.Parser;

public class Main {

	public static void main(String[] args) {
		Scanner sc = new Scanner("programa.mc");
		try {
			Parser parser = new Parser(sc);
			parser.programa();
			System.out.println("Compilation successful");
		} catch (SyntacticException e) {
			System.out.println("Syntactic error: " + e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
