/*
$Id: Token.java,v 1.8 2003/07/23 13:21:18 gregor Exp $
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
 * Describes the input token stream.
 */
public class Token {
    /**
     * An integer that describes the kind of this token.  This numbering system is determined by
     * JavaCCParser, and a table of these numbers is stored in the file ...Constants.java.
     */
    public int kind;

    /**
     * beginLine and beginColumn describe the position of the first character of this token;
     * endLine and endColumn describe the position of the last character of this token.
     */
    public int beginLine;

    /**
     * beginLine and beginColumn describe the position of the first character of this token;
     * endLine and endColumn describe the position of the last character of this token.
     */
    public int beginColumn;

    /**
     * beginLine and beginColumn describe the position of the first character of this token;
     * endLine and endColumn describe the position of the last character of this token.
     */
    public int endLine;

    /**
     * beginLine and beginColumn describe the position of the first character of this token;
     * endLine and endColumn describe the position of the last character of this token.
     */
    public int endColumn;

    /** The string image of the token. */
    public String image;

    /**
     * A reference to the next regular (non-special) token from the input stream.  If this is the
     * last token from the input stream, or if the token manager has not read tokens beyond this
     * one, this field is set to null.  This is true only if this token is also a regular token.
     * Otherwise, see below for a description of the contents of this field.
     */
    public Token next;

    /**
     * This field is used to access special tokens that occur prior to this token, but after the
     * immediately preceding regular (non-special) token. If there are no such special tokens,
     * this field is set to null. When there are more than one such special token, this field
     * refers to the last of these special tokens, which in turn refers to the next previous
     * special token through its specialToken field, and so on until the first special token
     * (whose specialToken field is null). The next fields of special tokens refer to other
     * special tokens that immediately follow it (without an intervening regular token).  If there
     * is no such token, this field is null.
     */
    public Token specialToken;

    /**
     * Returns the image.
     *
     * @return DOCUMENT ME!
     */
    public final String toString() {
        return image;
    }

    /**
     * Returns a new Token object, by default. However, if you want, you can create and return
     * subclass objects based on the value of ofKind. Simply add the cases to the switch for all
     * those special cases. For example, if you have a subclass of Token called IDToken that you
     * want to create if ofKind is ID, simlpy add something like : case MyParserConstants.ID :
     * return new IDToken(); to the following switch statement. Then you can cast matchedToken
     * variable to the appropriate type and use it in your lexical actions.
     *
     * @param ofKind DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static final Token newToken(int ofKind) {
        switch (ofKind) {
        default:
            return new Token();
        }
    }
}
