/*
 * $Id: HTMLParser.java,v 1.5 2003/03/04 17:46:47 gregor Exp $
 * <License>
 * The Apache Software License
 *
 * Copyright (c) 2002 wyona. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgment: "This product includes software developed
 *    by wyona (http://www.wyona.org)"
 *
 * 4. The name "wyona" must not be used to endorse or promote products derived from
 *    this software without prior written permission. For written permission, please
 *    contact contact@wyona.org
 *
 * 5. Products derived from this software may not be called "wyona" nor may "wyona"
 *    appear in their names without prior written permission of wyona.
 *
 * 6. Redistributions of any form whatsoever must retain the following acknowledgment:
 *    "This product includes software developed by wyona (http://www.wyona.org)"
 *
 * THIS SOFTWARE IS PROVIDED BY wyona "AS IS" WITHOUT ANY WARRANTY EXPRESS OR IMPLIED,
 * INCLUDING THE WARRANTY OF NON-INFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTI-
 * BILITY AND FITNESS FOR A PARTICULAR PURPOSE. wyona WILL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY YOU AS A RESULT OF USING THIS SOFTWARE. IN NO EVENT WILL wyona BE LIABLE
 * FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR LOST PROFITS EVEN IF wyona HAS
 * BEEN ADVISED OF THE POSSIBILITY OF THEIR OCCURRENCE. wyona WILL NOT BE LIABLE FOR ANY
 * THIRD PARTY CLAIMS AGAINST YOU.
 *
 * Wyona includes software developed by the Apache Software Foundation, W3C,
 * DOM4J Project, BitfluxEditor and Xopus.
 * </License>
 */
/* Generated By:JavaCC: Do not edit this line. HTMLParser.java */
package org.lenya.lucene.html;

import java.io.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class HTMLParser implements HTMLParserConstants {
    public static int SUMMARY_LENGTH = 200;
    StringBuffer title = new StringBuffer(SUMMARY_LENGTH);
    StringBuffer summary = new StringBuffer(SUMMARY_LENGTH * 2);
    int length = 0;
    boolean titleComplete = false;
    boolean inTitle = false;
    boolean inScript = false;
    boolean afterTag = false;
    boolean afterSpace = false;
    String eol = System.getProperty("line.separator");
    PipedReader pipeIn = null;
    PipedWriter pipeOut;
    int MAX_WAIT = 1000;
    public HTMLParserTokenManager token_source;
    SimpleCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    public boolean lookingAhead = false;
    private boolean jj_semLA;
    private int jj_gen;
    final private int[] jj_la1 = new int[13];
    final private int[] jj_la1_0 = {
        0xb3e, 0xb3e, 0x1000, 0x38000, 0x2000, 0x8000, 0x10000, 0x20000, 0x3b000, 0x3b000, 0x800000,
        0x2000000, 0x18,
    };
    final private JJCalls[] jj_2_rtns = new JJCalls[2];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private java.util.Vector jj_expentries = new java.util.Vector();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    /**
     * Creates a new HTMLParser object.
     *
     * @param file DOCUMENT ME!
     *
     * @throws FileNotFoundException DOCUMENT ME!
     */
    public HTMLParser(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    /**
     * Creates a new HTMLParser object.
     *
     * @param stream DOCUMENT ME!
     */
    public HTMLParser(java.io.InputStream stream) {
        jj_input_stream = new SimpleCharStream(stream, 1, 1);
        token_source = new HTMLParserTokenManager(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;

        for (int i = 0; i < 13; i++)
            jj_la1[i] = -1;

        for (int i = 0; i < jj_2_rtns.length; i++)
            jj_2_rtns[i] = new JJCalls();
    }

    /**
     * Creates a new HTMLParser object.
     *
     * @param stream DOCUMENT ME!
     */
    public HTMLParser(java.io.Reader stream) {
        jj_input_stream = new SimpleCharStream(stream, 1, 1);
        token_source = new HTMLParserTokenManager(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;

        for (int i = 0; i < 13; i++)
            jj_la1[i] = -1;

        for (int i = 0; i < jj_2_rtns.length; i++)
            jj_2_rtns[i] = new JJCalls();
    }

    /**
     * Creates a new HTMLParser object.
     *
     * @param tm DOCUMENT ME!
     */
    public HTMLParser(HTMLParserTokenManager tm) {
        token_source = tm;
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;

        for (int i = 0; i < 13; i++)
            jj_la1[i] = -1;

        for (int i = 0; i < jj_2_rtns.length; i++)
            jj_2_rtns[i] = new JJCalls();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws InterruptedException DOCUMENT ME!
     */
    public String getTitle() throws IOException, InterruptedException {
        if (pipeIn == null) {
            getReader(); // spawn parsing thread
        }

        int elapsedMillis = 0;

        while (true) {
            synchronized (this) {
                if (titleComplete || (length > SUMMARY_LENGTH)) {
                    break;
                }

                wait(10);

                elapsedMillis = elapsedMillis + 10;

                if (elapsedMillis > MAX_WAIT) {
                    break;
                }
            }
        }

        return title.toString().trim();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws InterruptedException DOCUMENT ME!
     */
    public String getSummary() throws IOException, InterruptedException {
        System.out.println("HTMLParser().getSummary()");

        if (pipeIn == null) {
            getReader(); // spawn parsing thread
        }

        int elapsedMillis = 0;

        while (true) {
            synchronized (this) {
                if (summary.length() >= SUMMARY_LENGTH) {
                    break;
                }

                //System.out.println("HTMLParser().getSummary(): Current length: "+summary.length());
                //System.out.println("HTMLParser().getSummary(): wait(10)");
                wait(10);

                elapsedMillis = elapsedMillis + 10;

                if (elapsedMillis > MAX_WAIT) {
                    break;
                }
            }
        }

        if (summary.length() > SUMMARY_LENGTH) {
            summary.setLength(SUMMARY_LENGTH);
        }

        String sum = summary.toString().trim();
        String tit = getTitle();

        if (sum.startsWith(tit)) {
            //return sum.substring(tit.length());
            return sum;
        } else {
            return sum;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public Reader getReader() throws IOException {
        if (pipeIn == null) {
            pipeIn = new PipedReader();
            pipeOut = new PipedWriter(pipeIn);

            Thread thread = new ParserThread(this);
            thread.start(); // start parsing
        }

        return pipeIn;
    }

    void addToSummary(String text) {
        //System.out.println("HTMLParser.addToSummary(): Current length: "+summary.length()+" ("+text+")");
        if (summary.length() < SUMMARY_LENGTH) {
            summary.append(text);

            if (summary.length() >= SUMMARY_LENGTH) {
                synchronized (this) {
                    notifyAll();
                }
            }
        }
    }

    void addToTitle(String text) {
        //System.out.println("HTMLParser.addToTitle(): Current length: "+title.length()+" ("+text+")");
        title.append(text);
    }

    void addText(String text) throws IOException {
        if (inScript) {
            return;
        }

        if (inTitle) {
            //title.append(text);
            addToTitle(text);
        } else {
            addToSummary(text);

            if (!titleComplete && !title.equals("")) { // finished title

                synchronized (this) {
                    titleComplete = true; // tell waiting threads
                    notifyAll();
                }
            }
        }

        length += text.length();
        pipeOut.write(text);

        afterSpace = false;
    }

    void addSpace() throws IOException {
        if (inScript) {
            return;
        }

        if (!afterSpace) {
            if (inTitle) {
                //title.append(" ");
                addToTitle(" ");
            } else {
                addToSummary(" ");
            }

            String space = afterTag ? eol : " ";
            length += space.length();
            pipeOut.write(space);
            afterSpace = true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws ParseException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    final public void HTMLDocument() throws ParseException, IOException {
        Token t;
label_1: 
        while (true) {
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case TagName:
            case DeclName:
            case Comment1:
            case Comment2:
            case Word:
            case Entity:
            case Space:
            case Punct:
                ;

                break;

            default:
                jj_la1[0] = jj_gen;

                break label_1;
            }

            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case TagName:
                Tag();
                afterTag = true;

                break;

            case DeclName:
                t = Decl();
                afterTag = true;

                break;

            case Comment1:
            case Comment2:
                CommentTag();
                afterTag = true;

                break;

            case Word:
                t = jj_consume_token(Word);
                addText(t.image);
                afterTag = false;

                break;

            case Entity:
                t = jj_consume_token(Entity);
                addText(Entities.decode(t.image));
                afterTag = false;

                break;

            case Punct:
                t = jj_consume_token(Punct);
                addText(t.image);
                afterTag = false;

                break;

            case Space:
                jj_consume_token(Space);
                addSpace();
                afterTag = false;

                break;

            default:
                jj_la1[1] = jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            }
        }

        jj_consume_token(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws ParseException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    final public void Tag() throws ParseException, IOException {
        Token t1;
        Token t2;
        boolean inImg = false;
        t1 = jj_consume_token(TagName);
        inTitle = t1.image.equalsIgnoreCase("<title"); // keep track if in <TITLE>
        inImg = t1.image.equalsIgnoreCase("<img"); // keep track if in <IMG>

        if (inScript) { // keep track if in <SCRIPT>
            inScript = !t1.image.equalsIgnoreCase("</script");
        } else {
            inScript = t1.image.equalsIgnoreCase("<script");
        }

label_2: 
        while (true) {
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case ArgName:
                ;

                break;

            default:
                jj_la1[2] = jj_gen;

                break label_2;
            }

            t1 = jj_consume_token(ArgName);

            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case ArgEquals:
                jj_consume_token(ArgEquals);

                switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                case ArgValue:
                case ArgQuote1:
                case ArgQuote2:
                    t2 = ArgValue();

                    if (inImg && t1.image.equalsIgnoreCase("alt") && (t2 != null)) {
                        addText("[" + t2.image + "]");
                    }

                    break;

                default:
                    jj_la1[3] = jj_gen;
                    ;
                }

                break;

            default:
                jj_la1[4] = jj_gen;
                ;
            }
        }

        jj_consume_token(TagEnd);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ParseException DOCUMENT ME!
     * @throws Error DOCUMENT ME!
     */
    final public Token ArgValue() throws ParseException {
        Token t = null;

        switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case ArgValue:
            t = jj_consume_token(ArgValue);
             {
                if (true) {
                    return t;
                }
            }

            break;

        default:
            jj_la1[5] = jj_gen;

            if (jj_2_1(2)) {
                jj_consume_token(ArgQuote1);
                jj_consume_token(CloseQuote1);

                if (true) {
                    return t;
                }
            } else {
                switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                case ArgQuote1:
                    jj_consume_token(ArgQuote1);
                    t = jj_consume_token(Quote1Text);
                    jj_consume_token(CloseQuote1);
                     {
                        if (true) {
                            return t;
                        }
                    }

                    break;

                default:
                    jj_la1[6] = jj_gen;

                    if (jj_2_2(2)) {
                        jj_consume_token(ArgQuote2);
                        jj_consume_token(CloseQuote2);

                        if (true) {
                            return t;
                        }
                    } else {
                        switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                        case ArgQuote2:
                            jj_consume_token(ArgQuote2);
                            t = jj_consume_token(Quote2Text);
                            jj_consume_token(CloseQuote2);
                             {
                                if (true) {
                                    return t;
                                }
                            }

                            break;

                        default:
                            jj_la1[7] = jj_gen;
                            jj_consume_token(-1);
                            throw new ParseException();
                        }
                    }
                }
            }
        }

        throw new Error("Missing return statement in function");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws ParseException DOCUMENT ME!
     * @throws Error DOCUMENT ME!
     */
    final public Token Decl() throws ParseException {
        Token t;
        t = jj_consume_token(DeclName);
label_3: 
        while (true) {
            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case ArgName:
            case ArgEquals:
            case ArgValue:
            case ArgQuote1:
            case ArgQuote2:
                ;

                break;

            default:
                jj_la1[8] = jj_gen;

                break label_3;
            }

            switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
            case ArgName:
                jj_consume_token(ArgName);

                break;

            case ArgValue:
            case ArgQuote1:
            case ArgQuote2:
                ArgValue();

                break;

            case ArgEquals:
                jj_consume_token(ArgEquals);

                break;

            default:
                jj_la1[9] = jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            }
        }

        jj_consume_token(TagEnd);

        if (true) {
            return t;
        }

        throw new Error("Missing return statement in function");
    }

    /**
     * DOCUMENT ME!
     *
     * @throws ParseException DOCUMENT ME!
     */
    final public void CommentTag() throws ParseException {
        switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case Comment1:
            jj_consume_token(Comment1);
label_4: 
            while (true) {
                switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                case CommentText1:
                    ;

                    break;

                default:
                    jj_la1[10] = jj_gen;

                    break label_4;
                }

                jj_consume_token(CommentText1);
            }

            jj_consume_token(CommentEnd1);

            break;

        case Comment2:
            jj_consume_token(Comment2);
label_5: 
            while (true) {
                switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
                case CommentText2:
                    ;

                    break;

                default:
                    jj_la1[11] = jj_gen;

                    break label_5;
                }

                jj_consume_token(CommentText2);
            }

            jj_consume_token(CommentEnd2);

            break;

        default:
            jj_la1[12] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
        }
    }

    final private boolean jj_2_1(int xla) {
        jj_la = xla;
        jj_lastpos = jj_scanpos = token;

        boolean retval = !jj_3_1();
        jj_save(0, xla);

        return retval;
    }

    final private boolean jj_2_2(int xla) {
        jj_la = xla;
        jj_lastpos = jj_scanpos = token;

        boolean retval = !jj_3_2();
        jj_save(1, xla);

        return retval;
    }

    final private boolean jj_3_1() {
        if (jj_scan_token(ArgQuote1)) {
            return true;
        }

        if ((jj_la == 0) && (jj_scanpos == jj_lastpos)) {
            return false;
        }

        if (jj_scan_token(CloseQuote1)) {
            return true;
        }

        if ((jj_la == 0) && (jj_scanpos == jj_lastpos)) {
            return false;
        }

        return false;
    }

    final private boolean jj_3_2() {
        if (jj_scan_token(ArgQuote2)) {
            return true;
        }

        if ((jj_la == 0) && (jj_scanpos == jj_lastpos)) {
            return false;
        }

        if (jj_scan_token(CloseQuote2)) {
            return true;
        }

        if ((jj_la == 0) && (jj_scanpos == jj_lastpos)) {
            return false;
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param stream DOCUMENT ME!
     */
    public void ReInit(java.io.InputStream stream) {
        jj_input_stream.ReInit(stream, 1, 1);
        token_source.ReInit(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;

        for (int i = 0; i < 13; i++)
            jj_la1[i] = -1;

        for (int i = 0; i < jj_2_rtns.length; i++)
            jj_2_rtns[i] = new JJCalls();
    }

    /**
     * DOCUMENT ME!
     *
     * @param stream DOCUMENT ME!
     */
    public void ReInit(java.io.Reader stream) {
        jj_input_stream.ReInit(stream, 1, 1);
        token_source.ReInit(jj_input_stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;

        for (int i = 0; i < 13; i++)
            jj_la1[i] = -1;

        for (int i = 0; i < jj_2_rtns.length; i++)
            jj_2_rtns[i] = new JJCalls();
    }

    /**
     * DOCUMENT ME!
     *
     * @param tm DOCUMENT ME!
     */
    public void ReInit(HTMLParserTokenManager tm) {
        token_source = tm;
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;

        for (int i = 0; i < 13; i++)
            jj_la1[i] = -1;

        for (int i = 0; i < jj_2_rtns.length; i++)
            jj_2_rtns[i] = new JJCalls();
    }

    final private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken;

        if ((oldToken = token).next != null) {
            token = token.next;
        } else {
            token = token.next = token_source.getNextToken();
        }

        jj_ntk = -1;

        if (token.kind == kind) {
            jj_gen++;

            if (++jj_gc > 100) {
                jj_gc = 0;

                for (int i = 0; i < jj_2_rtns.length; i++) {
                    JJCalls c = jj_2_rtns[i];

                    while (c != null) {
                        if (c.gen < jj_gen) {
                            c.first = null;
                        }

                        c = c.next;
                    }
                }
            }

            return token;
        }

        token = oldToken;
        jj_kind = kind;
        throw generateParseException();
    }

    final private boolean jj_scan_token(int kind) {
        if (jj_scanpos == jj_lastpos) {
            jj_la--;

            if (jj_scanpos.next == null) {
                jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
            } else {
                jj_lastpos = jj_scanpos = jj_scanpos.next;
            }
        } else {
            jj_scanpos = jj_scanpos.next;
        }

        if (jj_rescan) {
            int i = 0;
            Token tok = token;

            while ((tok != null) && (tok != jj_scanpos)) {
                i++;
                tok = tok.next;
            }

            if (tok != null) {
                jj_add_error_token(kind, i);
            }
        }

        return (jj_scanpos.kind != kind);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    final public Token getNextToken() {
        if (token.next != null) {
            token = token.next;
        } else {
            token = token.next = token_source.getNextToken();
        }

        jj_ntk = -1;
        jj_gen++;

        return token;
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    final public Token getToken(int index) {
        Token t = lookingAhead ? jj_scanpos : token;

        for (int i = 0; i < index; i++) {
            if (t.next != null) {
                t = t.next;
            } else {
                t = t.next = token_source.getNextToken();
            }
        }

        return t;
    }

    final private int jj_ntk() {
        if ((jj_nt = token.next) == null) {
            return (jj_ntk = (token.next = token_source.getNextToken()).kind);
        } else {
            return (jj_ntk = jj_nt.kind);
        }
    }

    private void jj_add_error_token(int kind, int pos) {
        if (pos >= 100) {
            return;
        }

        if (pos == (jj_endpos + 1)) {
            jj_lasttokens[jj_endpos++] = kind;
        } else if (jj_endpos != 0) {
            jj_expentry = new int[jj_endpos];

            for (int i = 0; i < jj_endpos; i++) {
                jj_expentry[i] = jj_lasttokens[i];
            }

            boolean exists = false;

            for (java.util.Enumeration enum = jj_expentries.elements(); enum.hasMoreElements();) {
                int[] oldentry = (int[]) (enum.nextElement());

                if (oldentry.length == jj_expentry.length) {
                    exists = true;

                    for (int i = 0; i < jj_expentry.length; i++) {
                        if (oldentry[i] != jj_expentry[i]) {
                            exists = false;

                            break;
                        }
                    }

                    if (exists) {
                        break;
                    }
                }
            }

            if (!exists) {
                jj_expentries.addElement(jj_expentry);
            }

            if (pos != 0) {
                jj_lasttokens[(jj_endpos = pos) - 1] = kind;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    final public ParseException generateParseException() {
        jj_expentries.removeAllElements();

        boolean[] la1tokens = new boolean[27];

        for (int i = 0; i < 27; i++) {
            la1tokens[i] = false;
        }

        if (jj_kind >= 0) {
            la1tokens[jj_kind] = true;
            jj_kind = -1;
        }

        for (int i = 0; i < 13; i++) {
            if (jj_la1[i] == jj_gen) {
                for (int j = 0; j < 32; j++) {
                    if ((jj_la1_0[i] & (1 << j)) != 0) {
                        la1tokens[j] = true;
                    }
                }
            }
        }

        for (int i = 0; i < 27; i++) {
            if (la1tokens[i]) {
                jj_expentry = new int[1];
                jj_expentry[0] = i;
                jj_expentries.addElement(jj_expentry);
            }
        }

        jj_endpos = 0;
        jj_rescan_token();
        jj_add_error_token(0, 0);

        int[][] exptokseq = new int[jj_expentries.size()][];

        for (int i = 0; i < jj_expentries.size(); i++) {
            exptokseq[i] = (int[]) jj_expentries.elementAt(i);
        }

        return new ParseException(token, exptokseq, tokenImage);
    }

    /**
     * DOCUMENT ME!
     */
    final public void enable_tracing() {
    }

    /**
     * DOCUMENT ME!
     */
    final public void disable_tracing() {
    }

    final private void jj_rescan_token() {
        jj_rescan = true;

        for (int i = 0; i < 2; i++) {
            JJCalls p = jj_2_rtns[i];

            do {
                if (p.gen > jj_gen) {
                    jj_la = p.arg;
                    jj_lastpos = jj_scanpos = p.first;

                    switch (i) {
                    case 0:
                        jj_3_1();

                        break;

                    case 1:
                        jj_3_2();

                        break;
                    }
                }

                p = p.next;
            } while (p != null);
        }

        jj_rescan = false;
    }

    final private void jj_save(int index, int xla) {
        JJCalls p = jj_2_rtns[index];

        while (p.gen > jj_gen) {
            if (p.next == null) {
                p = p.next = new JJCalls();

                break;
            }

            p = p.next;
        }

        p.gen = (jj_gen + xla) - jj_la;
        p.first = token;
        p.arg = xla;
    }

    static final class JJCalls {
        int gen;
        Token first;
        int arg;
        JJCalls next;
    }

    //    void handleException(Exception e) {
    //      System.out.println(e.toString());  // print the error message
    //      System.out.println("Skipping...");
    //      Token t;
    //      do {
    //        t = getNextToken();
    //      } while (t.kind != TagEnd);
    //    }
}
