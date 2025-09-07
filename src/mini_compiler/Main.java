/*
Alunos:
Etienne Pautet    / 31334369
Fabiano Quirino   / 31719511
João Pedro Araújo / 30914132
Renan Chacon      / 31761607
 */

package mini_compiler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import lexical.Scanner;
import lexical.Token;

public class Main {

	public static void main(String[] args) {
		Scanner sc = new Scanner("programa.mc");
		Token tk;
		do {
			tk = sc.nextToken();
			System.out.println(tk);
		} while (tk != null);
	}

}
