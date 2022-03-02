package org.jabref.logic.layout.format;

import org.jabref.logic.layout.LayoutFormatter;
import org.jabref.logic.layout.StringInt;
import org.jabref.logic.util.strings.RtfCharMap;
import org.jabref.model.strings.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Transform a LaTeX-String to RTF.
 *
 * This method will:
 *
 *   1.) Remove LaTeX-Command sequences.
 *
 *   2.) Replace LaTeX-Special chars with RTF equivalents.
 *
 *   3.) Replace emph and textit and textbf with their RTF replacements.
 *
 *   4.) Take special care to save all unicode characters correctly.
 *
 *   5.) Replace --- by \emdash and -- by \endash.
 */
public class RTFChars implements LayoutFormatter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutFormatter.class);

    private static final RtfCharMap RTF_CHARS = new RtfCharMap();

    private static final Map<Long, String> baseCharDictionary = new HashMap<>();

    public RTFChars() {
        // Instantiate special char to base char lookup table for
        // use in the transformSpecialCharacter method
        loadBaseCharDictionary();
    }

    @Override
    public String format(String field) {
        StringBuilder sb = new StringBuilder();
        StringBuilder currentCommand = null;
        boolean escaped = false;
        boolean incommand = false;
        for (int i = 0; i < field.length(); i++) {

            char c = field.charAt(i);

            if (escaped && (c == '\\')) {
                sb.append('\\');
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
                incommand = true;
                currentCommand = new StringBuilder();
            } else if (!incommand && ((c == '{') || (c == '}'))) {
                // Swallow the brace.
            } else if (Character.isLetter(c)
                    || StringUtil.SPECIAL_COMMAND_CHARS.contains(String.valueOf(c))) {
                escaped = false;
                if (incommand) {
                    // Else we are in a command, and should not keep the letter.
                    currentCommand.append(c);
                    testCharCom:
                    if ((currentCommand.length() == 1)
                            && StringUtil.SPECIAL_COMMAND_CHARS.contains(currentCommand.toString())) {
                        // This indicates that we are in a command of the type
                        // \^o or \~{n}
                        if (i >= (field.length() - 1)) {
                            break testCharCom;
                        }

                        String command = currentCommand.toString();
                        i++;
                        c = field.charAt(i);
                        String combody;
                        if (c == '{') {
                            StringInt part = getPart(field, i, true);
                            i += part.i;
                            combody = part.s;
                        } else {
                            combody = field.substring(i, i + 1);
                        }

                        String result = RTF_CHARS.get(command + combody);

                        if (result != null) {
                            sb.append(result);
                        }

                        incommand = false;
                        escaped = false;
                    }
                } else {
                    sb.append(c);
                }
            } else {
                testContent:
                if (!incommand || (!Character.isWhitespace(c) && (c != '{') && (c != '}'))) {
                    sb.append(c);
                } else {
                    assert incommand;

                    // First test for braces that may be part of a LaTeX command:
                    if ((c == '{') && (currentCommand.length() == 0)) {
                        // We have seen something like \{, which is probably the start
                        // of a command like \{aa}. Swallow the brace.
                        continue;
                    } else if ((c == '}') && (currentCommand.length() > 0)) {
                        // Seems to be the end of a command like \{aa}. Look it up:
                        String command = currentCommand.toString();
                        String result = RTF_CHARS.get(command);
                        if (result != null) {
                            sb.append(result);
                        }
                        incommand = false;
                        escaped = false;
                        continue;
                    }

                    // Then look for italics etc.,
                    // but first check if we are already at the end of the string.
                    if (i >= (field.length() - 1)) {
                        break testContent;
                    }

                    if (((c == '{') || (c == ' ')) && (currentCommand.length() > 0)) {
                        String command = currentCommand.toString();
                        // Then test if we are dealing with a italics or bold
                        // command. If so, handle.
                        if ("em".equals(command) || "emph".equals(command) || "textit".equals(command)
                                || "it".equals(command)) {
                            StringInt part = getPart(field, i, c == '{');
                            i += part.i;
                            sb.append("{\\i ").append(part.s).append('}');
                        } else if ("textbf".equals(command) || "bf".equals(command)) {
                            StringInt part = getPart(field, i, c == '{');
                            i += part.i;
                            sb.append("{\\b ").append(part.s).append('}');
                        } else {
                            LOGGER.info("Unknown command " + command);
                        }
                        if (c == ' ') {
                            // command was separated with the content by ' '
                            // We have to add the space a
                        }
                    } else {
                        sb.append(c);
                    }
                }
                incommand = false;
                escaped = false;
            }
        }

        char[] chars = sb.toString().toCharArray();
        sb = new StringBuilder();

        for (char c : chars) {
            if (c < 128) {
                sb.append(c);
            } else {
                sb.append("\\u").append((long) c).append(transformSpecialCharacter(c));
            }
        }

        return sb.toString().replace("---", "{\\emdash}").replace("--", "{\\endash}").replace("``", "{\\ldblquote}")
                 .replace("''", "{\\rdblquote}");
    }

    /**
     * @param text                  the text to extract the part from
     * @param i                     the position to start
     * @param commandNestedInBraces true if the command is nested in braces (\emph{xy}), false if spaces are sued (\emph xy)
     * @return a tuple of number of added characters and the extracted part
     */
    private StringInt getPart(String text, int i, boolean commandNestedInBraces) {
        char c;
        int count = 0;
        int icount = i;
        StringBuilder part = new StringBuilder();
        loop:
        while ((count >= 0) && (icount < text.length())) {
            icount++;
            c = text.charAt(icount);
            switch (c) {
                case '}':
                    count--;
                    break;
                case '{':
                    count++;
                    break;
                case ' ':
                    if (!commandNestedInBraces) {
                        // in any case, a space terminates the loop
                        break loop;
                    }
                    break;
                default:
                    break;
            }
            part.append(c);
        }
        String res = part.toString();
        // the wrong "}" at the end is removed by "format(res)"
        return new StringInt(format(res), part.length());
    }

    /**
     * This method transforms the unicode of a special character into its base character: 233 (é) - > e
     *
     * @param c long
     * @return returns the basic character of the given unicode
     */
    private String transformSpecialCharacter(long c) {
        while (c >= 192) {
            if (baseCharDictionary.get(c) != null) {
                return baseCharDictionary.get(c);
            } else {
                c--;
            }
        }
        return "?";
    }

    private void loadBaseCharDictionary() {
        baseCharDictionary.put(192L, "A");
        baseCharDictionary.put(224L, "a");
        baseCharDictionary.put(199L, "C");
        baseCharDictionary.put(262L, "C");
        baseCharDictionary.put(264L, "C");
        baseCharDictionary.put(266L, "C");
        baseCharDictionary.put(268L, "C");
        baseCharDictionary.put(231L, "c");
        baseCharDictionary.put(263L, "c");
        baseCharDictionary.put(265L, "c");
        baseCharDictionary.put(267L, "c");
        baseCharDictionary.put(269L, "c");
        baseCharDictionary.put(208L, "D");
        baseCharDictionary.put(272L, "D");
        baseCharDictionary.put(240L, "d");
        baseCharDictionary.put(273L, "d");
        baseCharDictionary.put(200L, "E");
        baseCharDictionary.put(274L, "E");
        baseCharDictionary.put(276L, "E");
        baseCharDictionary.put(278L, "E");
        baseCharDictionary.put(280L, "E");
        baseCharDictionary.put(282L, "E");
        baseCharDictionary.put(232L, "e");
        baseCharDictionary.put(275L, "e");
        baseCharDictionary.put(277L, "e");
        baseCharDictionary.put(279L, "e");
        baseCharDictionary.put(281L, "e");
        baseCharDictionary.put(283L, "e");
        baseCharDictionary.put(284L, "G");
        baseCharDictionary.put(286L, "G");
        baseCharDictionary.put(288L, "G");
        baseCharDictionary.put(290L, "G");
        baseCharDictionary.put(330L, "G");
        baseCharDictionary.put(285L, "g");
        baseCharDictionary.put(287L, "g");
        baseCharDictionary.put(289L, "g");
        baseCharDictionary.put(291L, "g");
        baseCharDictionary.put(331L, "g");
        baseCharDictionary.put(292L, "H");
        baseCharDictionary.put(294L, "H");
        baseCharDictionary.put(293L, "h");
        baseCharDictionary.put(295L, "h");
        baseCharDictionary.put(204L, "I");
        baseCharDictionary.put(296L, "I");
        baseCharDictionary.put(298L, "I");
        baseCharDictionary.put(300L, "I");
        baseCharDictionary.put(302L, "I");
        baseCharDictionary.put(304L, "I");
        baseCharDictionary.put(236L, "i");
        baseCharDictionary.put(297L, "i");
        baseCharDictionary.put(299L, "i");
        baseCharDictionary.put(301L, "i");
        baseCharDictionary.put(303L, "i");
        baseCharDictionary.put(308L, "J");
        baseCharDictionary.put(309L, "j");
        baseCharDictionary.put(310L, "K");
        baseCharDictionary.put(311L, "k");
        baseCharDictionary.put(313L, "L");
        baseCharDictionary.put(315L, "L");
        baseCharDictionary.put(319L, "L");
        baseCharDictionary.put(314L, "l");
        baseCharDictionary.put(316L, "l");
        baseCharDictionary.put(320L, "l");
        baseCharDictionary.put(322L, "l");
        baseCharDictionary.put(209L, "N");
        baseCharDictionary.put(323L, "N");
        baseCharDictionary.put(325L, "N");
        baseCharDictionary.put(327L, "N");
        baseCharDictionary.put(241L, "n");
        baseCharDictionary.put(324L, "n");
        baseCharDictionary.put(326L, "n");
        baseCharDictionary.put(328L, "n");
        baseCharDictionary.put(210L, "O");
        baseCharDictionary.put(332L, "O");
        baseCharDictionary.put(334L, "O");
        baseCharDictionary.put(242L, "o");
        baseCharDictionary.put(333L, "o");
        baseCharDictionary.put(335L, "o");
        baseCharDictionary.put(340L, "R");
        baseCharDictionary.put(342L, "R");
        baseCharDictionary.put(344L, "R");
        baseCharDictionary.put(341L, "r");
        baseCharDictionary.put(343L, "r");
        baseCharDictionary.put(345L, "r");
        baseCharDictionary.put(346L, "S");
        baseCharDictionary.put(348L, "S");
        baseCharDictionary.put(350L, "S");
        baseCharDictionary.put(352L, "S");
        baseCharDictionary.put(347L, "s");
        baseCharDictionary.put(349L, "s");
        baseCharDictionary.put(351L, "s");
        baseCharDictionary.put(353L, "s");
        baseCharDictionary.put(354L, "T");
        baseCharDictionary.put(356L, "T");
        baseCharDictionary.put(358L, "T");
        baseCharDictionary.put(355L, "t");
        baseCharDictionary.put(359L, "t");
        baseCharDictionary.put(217L, "U");
        baseCharDictionary.put(360L, "U");
        baseCharDictionary.put(362L, "U");
        baseCharDictionary.put(364L, "U");
        baseCharDictionary.put(366L, "U");
        baseCharDictionary.put(370L, "U");
        baseCharDictionary.put(249L, "u");
        baseCharDictionary.put(361L, "u");
        baseCharDictionary.put(363L, "u");
        baseCharDictionary.put(365L, "u");
        baseCharDictionary.put(367L, "u");
        baseCharDictionary.put(371L, "u");
        baseCharDictionary.put(372L, "W");
        baseCharDictionary.put(373L, "w");
        baseCharDictionary.put(374L, "Y");
        baseCharDictionary.put(376L, "Y");
        baseCharDictionary.put(221L, "Y");
        baseCharDictionary.put(375L, "y");
        baseCharDictionary.put(255L, "y");
        baseCharDictionary.put(377L, "Z");
        baseCharDictionary.put(379L, "Z");
        baseCharDictionary.put(381L, "Z");
        baseCharDictionary.put(378L, "z");
        baseCharDictionary.put(380L, "z");
        baseCharDictionary.put(382L, "z");
        baseCharDictionary.put(198L, "AE");
        baseCharDictionary.put(230L, "ae");
        baseCharDictionary.put(338L, "OE");
        baseCharDictionary.put(339L, "oe");
        baseCharDictionary.put(222L, "TH");
        baseCharDictionary.put(223L, "ss");
    }
}
