package dev.jok.verse;

import dev.jok.verse.ast.types.AstStmt;
import dev.jok.verse.ast.types.decl.AstFunctionDecl;
import dev.jok.verse.interpreter.FunctionSearchVisitor;
import dev.jok.verse.interpreter.VerseInterpreter;
import dev.jok.verse.lexer.Token;
import dev.jok.verse.lexer.VerseScanner;
import dev.jok.verse.parser.VerseParser;
import dev.jok.verse.util.AstPrinter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class VerseLang {

    public static final AstPrinter PRINTER = new AstPrinter();
    private static Logger LOGGER;
    private static boolean hadSyntaxError = false;

    public static void main(String[] args) throws IOException {
        InputStream stream = VerseLang.class.getClassLoader().getResourceAsStream("logging.properties");
        LogManager.getLogManager().readConfiguration(stream);
        LOGGER = Logger.getLogger("Verse");

        if (args.length != 2) {
            LOGGER.log(Level.SEVERE, "Usage: verse [script] [debug]");
            System.exit(64);
        }

        String fileName = args[0];
        File file = new File(fileName);
        if (!file.exists()) {
            LOGGER.log(Level.SEVERE, "File does not exist: " + fileName);
            System.exit(64);
        }

        if (!file.isFile()) {
            LOGGER.log(Level.SEVERE, "Input is not a file: " + fileName);
            System.exit(64);
        }

        String fileContent = Files.readString(file.toPath());

        boolean debug = Boolean.parseBoolean(args[1]);

        List<Token> tokens = new VerseScanner(fileContent).scanTokens();
        VerseParser parser = new VerseParser(debug, tokens);

        List<AstStmt> statements;
        long start = System.currentTimeMillis();
        try {
            statements = parser.parse();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while parsing", e);
            // @Todo(Jok): exit codes
            return;
        }

        if (hadSyntaxError) {
            // @Todo(Jok): exit codes
            return;
        }

        LOGGER.log(Level.INFO, "Parsed " + statements.size() + " statements in " + (System.currentTimeMillis() - start) + "ms");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt"))) {
            for (AstStmt stmt : statements) {
                writer.write(PRINTER.print(stmt));
                writer.newLine();
            }
        }

        System.out.println();
        LOGGER.log(Level.INFO, "Running interpreter...");

        VerseInterpreter interp = new VerseInterpreter(statements);
        AstFunctionDecl mainFunction = interp.lookupFunctionDecl("Main");

        if (mainFunction == null) {
            LOGGER.log(Level.SEVERE, "No main function found");
            return;
        }

        interp.interpret(mainFunction.body);
    }

    public static void syntaxError(@Nullable Token previous, @NotNull Token current, @Nullable Token next, String message) {
        message = message.replace("{peek}", current.errorString());
        message = message.replace("{peekNext}", next != null ? next.errorString() : "null");
        message = message.replace("{peekPrev}", previous != null ? previous.errorString() : "null");
        syntaxError(current.line, current.col, message);
    }

    public static void syntaxError(int line, int col, String message) {
        hadSyntaxError = true;
        LOGGER.log(Level.SEVERE, "Syntax Error: " + message + " [ Ln " + line + ", Col " + col + " ]");
    }

}
