package util;

import java.util.Map;
import java.util.HashMap;

public class ReservedWords {
    public static final Map<String, TokenType> TABLE = new HashMap<>();

    static {
        TABLE.put("int", TokenType.INT);
        TABLE.put("float", TokenType.FLOAT);
        TABLE.put("print", TokenType.PRINT);
        TABLE.put("if", TokenType.IF);
        TABLE.put("else", TokenType.ELSE);
    }
}
