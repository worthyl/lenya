/*
$Id: TokenMgrError.java,v 1.8 2003/07/23 13:21:18 gregor Exp $
<License>

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Lenya" and  "Apache Software Foundation"  must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Michael Wechner <michi@apache.org>. For more information on the Apache Soft-
 ware Foundation, please see <http://www.apache.org/>.

 Lenya includes software developed by the Apache Software Foundation, W3C,
 DOM4J Project, BitfluxEditor, Xopus, and WebSHPINX.
</License>
*/
package org.apache.lenya.lucene.html;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.8 $
 */
public class TokenMgrError extends Error {
    /*
     * Ordinals for various reasons why an Error of this type can be thrown.
     */

    /** Lexical error occured. */
    static final int LEXICAL_ERROR = 0;

    /** An attempt wass made to create a second instance of a static token manager. */
    static final int STATIC_LEXER_ERROR = 1;

    /** Tried to change to an invalid lexical state. */
    static final int INVALID_LEXICAL_STATE = 2;

    /** Detected (and bailed out of) an infinite loop in the token manager. */
    static final int LOOP_DETECTED = 3;

    /** Indicates the reason why the exception is thrown. It will have one of the above 4 values. */
    int errorCode;

    /*
     * Constructors of various flavors follow.
     */
    public TokenMgrError() {
    }

    /**
     * Creates a new TokenMgrError object.
     *
     * @param message DOCUMENT ME!
     * @param reason DOCUMENT ME!
     */
    public TokenMgrError(String message, int reason) {
        super(message);
        errorCode = reason;
    }

    /**
     * Creates a new TokenMgrError object.
     *
     * @param EOFSeen DOCUMENT ME!
     * @param lexState DOCUMENT ME!
     * @param errorLine DOCUMENT ME!
     * @param errorColumn DOCUMENT ME!
     * @param errorAfter DOCUMENT ME!
     * @param curChar DOCUMENT ME!
     * @param reason DOCUMENT ME!
     */
    public TokenMgrError(boolean EOFSeen, int lexState, int errorLine, int errorColumn,
        String errorAfter, char curChar, int reason) {
        this(LexicalError(EOFSeen, lexState, errorLine, errorColumn, errorAfter, curChar), reason);
    }

    /**
     * Replaces unprintable characters by their espaced (or unicode escaped) equivalents in the
     * given string
     *
     * @param str DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected static final String addEscapes(String str) {
        StringBuffer retval = new StringBuffer();
        char ch;

        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
            case 0:

                continue;

            case '\b':
                retval.append("\\b");

                continue;

            case '\t':
                retval.append("\\t");

                continue;

            case '\n':
                retval.append("\\n");

                continue;

            case '\f':
                retval.append("\\f");

                continue;

            case '\r':
                retval.append("\\r");

                continue;

            case '\"':
                retval.append("\\\"");

                continue;

            case '\'':
                retval.append("\\\'");

                continue;

            case '\\':
                retval.append("\\\\");

                continue;

            default:

                if (((ch = str.charAt(i)) < 0x20) || (ch > 0x7e)) {
                    String s = "0000" + Integer.toString(ch, 16);
                    retval.append("\\u" + s.substring(s.length() - 4, s.length()));
                } else {
                    retval.append(ch);
                }

                continue;
            }
        }

        return retval.toString();
    }

    /**
     * Returns a detailed message for the Error when it is thrown by the token manager to indicate
     * a lexical error. Parameters :  EOFSeen     : indicates if EOF caused the lexicl error
     * curLexState : lexical state in which this error occured errorLine   : line number when the
     * error occured errorColumn : column number when the error occured errorAfter  : prefix that
     * was seen before this error occured curchar     : the offending character Note: You can
     * customize the lexical error message by modifying this method.
     *
     * @param EOFSeen DOCUMENT ME!
     * @param lexState DOCUMENT ME!
     * @param errorLine DOCUMENT ME!
     * @param errorColumn DOCUMENT ME!
     * @param errorAfter DOCUMENT ME!
     * @param curChar DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static final String LexicalError(boolean EOFSeen, int lexState, int errorLine,
        int errorColumn, String errorAfter, char curChar) {
        return ("Lexical error at line " + errorLine + ", column " + errorColumn +
        ".  Encountered: " +
        (EOFSeen ? "<EOF> "
                 : (("\"" + addEscapes(String.valueOf(curChar)) + "\"") + " (" + (int) curChar +
        "), ")) + "after : \"" + addEscapes(errorAfter) + "\"");
    }

    /**
     * You can also modify the body of this method to customize your error messages. For example,
     * cases like LOOP_DETECTED and INVALID_LEXICAL_STATE are not of end-users concern, so you can
     * return something like :  "Internal Error : Please file a bug report .... " from this method
     * for such cases in the release version of your parser.
     *
     * @return DOCUMENT ME!
     */
    public String getMessage() {
        return super.getMessage();
    }
}
