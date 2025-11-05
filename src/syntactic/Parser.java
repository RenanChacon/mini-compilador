package syntactic;

import exceptions.SyntacticException;
import lexical.Scanner;
import lexical.Token;
import util.TokenType;

public class Parser {
    
    private Scanner scanner;
	private Token token;
	
	public Parser(Scanner scanner) throws Exception {
		this.scanner = scanner;
		token = this.scanner.nextToken();
	}
	
	private void confereToken(TokenType expected) throws Exception {
        if (token != null && token.getType() == expected) {
            token = scanner.nextToken();
        } else {
            throw new SyntacticException(
                "Esperado token " + expected + " mas encontrado " + (token != null ? token.getType()+ "("+token.getText()+")" : "fim do arquivo")
            );
        }
    }

    // ===== REGRAS DA GRAMÁTICA =====

    public void programa() throws Exception {
        confereToken(TokenType.INICIO);
        confereToken(TokenType.DECLS);
        blocoDeclaracoes();
        confereToken(TokenType.FIMDECLS);
        confereToken(TokenType.CODIGO);
        blocoComandos();
        confereToken(TokenType.FIMPROG);
    }

    private void blocoDeclaracoes() throws Exception {
        declaracao();
        if (token != null && token.getType() == TokenType.IDENTIFIER) {
            blocoDeclaracoes();
        }
    }

    private void declaracao() throws Exception {
        confereToken(TokenType.IDENTIFIER);
        confereToken(TokenType.COLON);
        tipo();
    }
 
    private void tipo() throws Exception {
        if (token == null) {
            throw new SyntacticException("Esperado tipo (INT ou FLOAT) mas encontrado final do arquivo");
        }
        
        if (token.getType() == TokenType.INT) confereToken(TokenType.INT);
        else if (token.getType() == TokenType.FLOAT) confereToken(TokenType.FLOAT);
        else throw new SyntacticException("Tipo esperado (INT ou FLOAT)");
    }

    // --- EXPRESSÕES ARITMÉTICAS ---

    private void expressaoAritmetica() throws Exception {
        termo();
        while (token != null && token.getType() == TokenType.MATH_OPERATOR &&
              (token.getText().equals("+") || token.getText().equals("-"))) {
            confereToken(TokenType.MATH_OPERATOR);
            termo();
        }
    }

    private void termo() throws Exception {
        fator();
        while (token != null && token.getType() == TokenType.MATH_OPERATOR &&
              (token.getText().equals("*") || token.getText().equals("/"))) {
            confereToken(TokenType.MATH_OPERATOR);
            fator();
        }
    }

    private void fator() throws Exception {
        if (token == null) {
            throw new SyntacticException("Esperado ID, número ou '(', mas encontrado fim do arquivo");
        }
        
        if (token.getType() == TokenType.NUMBER || token.getType() == TokenType.IDENTIFIER) {
            confereToken(token.getType());
        } else if (token.getType() == TokenType.L_PAREN) {
            confereToken(TokenType.L_PAREN);
            expressaoAritmetica();
            confereToken(TokenType.R_PAREN);
        } else {
            throw new SyntacticException("Esperado ID, número ou '(', encontrado " + token.getText());
        }
    }

    // ===== EXPRESSÃO RELACIONAL =====

    private void expressaoRelacional() throws Exception {
        termoRelacional();
        while (token != null && token.getType() == TokenType.E || token.getType() == TokenType.OU) {
            confereToken(token.getType());
            termoRelacional();
        }
    }

    private void termoRelacional() throws Exception {
        if (token == null) {
            throw new SyntacticException("Esperado expressão relacional mas encontrado final do arquivo");
        }
        
        if (token.getType() == TokenType.L_PAREN) {
            confereToken(TokenType.L_PAREN);
            expressaoRelacional();
            confereToken(TokenType.R_PAREN);
        } else {
            expressaoAritmetica();
            confereToken(TokenType.REL_OPERATOR);
            expressaoAritmetica();
        }
    }

    // ===== COMANDOS =====

    private void blocoComandos() throws Exception {
        comando();
        if (isInicioComando()) blocoComandos();
    }

    private boolean isInicioComando() {
        if (token == null) return false; 
            
        return token.getType() == TokenType.IDENTIFIER ||
               token.getType() == TokenType.LEIA ||
               token.getType() == TokenType.ESCREVA ||
               token.getType() == TokenType.SE ||
               token.getType() == TokenType.REPITA ||
               token.getType() == TokenType.BLOCO;
    }

    private void comando() throws Exception {
        if (token == null) {
            throw new SyntacticException("Esperado um comando, mas encontrado fim de arquivo");
        }
        
        switch (token.getType()) {
            case IDENTIFIER: atribuicao(); break;
            case LEIA: entrada(); break;
            case ESCREVA: saida(); break;
            case SE: condicional(); break;
            case REPITA: repeticao(); break;
            case BLOCO: subrotina(); break;
            default: throw new SyntacticException("Comando inválido em: " + token.getText());
        }
    }

    private void atribuicao() throws Exception {
        confereToken(TokenType.IDENTIFIER);
        confereToken(TokenType.ASSIGNMENT);
        expressaoAritmetica();
    }

    private void entrada() throws Exception {
        confereToken(TokenType.LEIA);
        confereToken(TokenType.IDENTIFIER);
    }

    private void saida() throws Exception {
        confereToken(TokenType.ESCREVA);
        confereToken(TokenType.L_PAREN);

        if (token == null) {
            throw new SyntacticException("Esperado IDENTIFIER ou CADEIA, mas encontrado fim do arquivo");
        }

        if (token.getType() == TokenType.IDENTIFIER)
            confereToken(token.getType());
        else throw new SyntacticException("Esperado ID ou CADEIA");
        confereToken(TokenType.R_PAREN);
    }

    private void condicional() throws Exception {
        confereToken(TokenType.SE);
        expressaoRelacional();
        confereToken(TokenType.ENTAO);
        comando();
        if (token != null && token.getType() == TokenType.SENAO) {
            confereToken(TokenType.SENAO);
            comando();
        }
    }

    private void repeticao() throws Exception {
        confereToken(TokenType.REPITA);
        expressaoRelacional();
        comando();
    }

    private void subrotina() throws Exception {
        confereToken(TokenType.BLOCO);
        blocoComandos();
        confereToken(TokenType.FIMBLOCO);
    }
}
