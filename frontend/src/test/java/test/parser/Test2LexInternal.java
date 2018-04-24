package test.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import parser.JCCMinijavaParser;
import parser.JCCMinijavaParserConstants;
import parser.Token;
import util.SampleCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;

import static parser.JCCMinijavaParserConstants.EOF;
import static parser.JCCMinijavaParserConstants.IDENTIFIER;


/**
 * The tests in this class correspond more or less to the work in Chapter 2.
 * <p>
 * It tries to use the Tokenizer of the generated parser in isolation to see
 * if it properly splits input into tokens.
 * <p>
 * These tests call directly into the generated parser and therefore assume
 * that a particular parser implementation is used. In this case, we assume
 * that the parser is generated by JavaCC and the generated parser class is
 * called JCCExpressionParser.
 * <p>
 * There are also some dependencies on the names that are chosen for different
 * types of tokens such as IDENTIFIER and INTEGER_LITERAL.
 * <p>
 * It is not really the intention of these tests to make you completely conform
 * to these choices. I.e. you may change these tests if you so wish. The tests
 * that really matter are the ones that test the parser.
 * <p>
 * These tests here are provided so that you could work and test in stages, starting
 * with just implementing and testing the lexer, before proceeding
 * to the parser which depends on it.
 * <p>
 * The tests in the class are also rather sparse (e.g. there's only an explicit test for
 * comments and identifiers, but not any other types of tokens).
 *
 * @author kdvolder
 */
public class Test2LexInternal {

    private JCCMinijavaParser parserOn(String inputString) {
        return new JCCMinijavaParser(new StringReader(inputString));
    }

    private JCCMinijavaParser parserOn(File file) throws FileNotFoundException {
        return new JCCMinijavaParser(new FileReader(file));
    }


    void test(String input, int[] tokenKinds) {
        JCCMinijavaParser parser = parserOn(input);
        for (int i = 0; i < tokenKinds.length; i++) {
            Assertions.assertEquals(tokenKinds[i], parser.getNextToken().kind);
        }
        Assertions.assertEquals(EOF, parser.getNextToken().kind);
    }

    @Test
    public void identifiers() {
        test("abc de f A_bs\n a_ x12 booho__",
                new int[]{
                        IDENTIFIER, IDENTIFIER, IDENTIFIER, IDENTIFIER,
                        IDENTIFIER, IDENTIFIER, IDENTIFIER
                });
    }

    @Test
    public void comments() {
        test("// A single line comment\n" +
                        "/* A multi line comment\n" +
                        "   with more than one\n" +
                        "   line of comments in it\n" +
                        "*/\n" +
                        "  \t // should allow them to preceeded by spaces or tabs\n",
                new int[0]);
    }

    @Test
    public void starComments() {
        test("/**/", new int[0]);
        test("/***/", new int[0]);
        test("/****/", new int[0]);
    }

    @Test
    public void commentsWithSomethingInBetween() {
        test("/* A comment */" +
                        "anIdentifier" +
                        "/* Another comment */",
                new int[]{IDENTIFIER});
    }

    @Test
    public void commentAtEOF() {
        test("// A single line comment",
                new int[0]);
    }

    @Test
    public void sampleCode() throws FileNotFoundException {
        //Read all the sample code. It should contain only valid tokens.
        File[] files = SampleCode.sampleFiles("java");
        for (int i = 0; i < files.length; i++) {
            JCCMinijavaParser parser = parserOn(files[i]);
            System.out.println("Lexing file " + files[i]);
            Token tok;
            while ((tok = parser.getNextToken()).kind != EOF) {
                System.out.println(JCCMinijavaParserConstants.tokenImage[tok.kind] + " = " + tok.image);
            }
        }
    }

}
