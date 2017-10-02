package com.ukubuka.core.parser;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.ukubuka.core.exception.ParserException;
import com.ukubuka.core.exception.ReaderException;
import com.ukubuka.core.model.ExtractFlags;
import com.ukubuka.core.reader.UkubukaReader;
import com.ukubuka.core.utilities.Constants;

/**
 * Ukubuka Base Parser
 * 
 * @author agrawroh
 * @version v1.0
 */
public class UkubukaBaseParser {

    @Autowired
    private UkubukaReader reader;

    /**
     * Read With Options
     * 
     * @param completeFileName
     * @return File Content
     * @throws ParserException
     */
    public String readWithOptions(final String completeFileName,
            Map<String, String> flags) throws ParserException {
        boolean withHeader = Boolean.parseBoolean(flags
                .get(ExtractFlags.FILE_CONTAINS_HEADER.getFlag()));
        String fileContents = readWithOptions(completeFileName,
                flags.get(ExtractFlags.FILE_ENCODING.getFlag()),
                flags.get(ExtractFlags.FILE_DELIMITER.getFlag()));
        return withHeader ? fileContents : appendHeader(fileContents);
    }

    /**
     * Append Header
     * 
     * @param fileContents
     * @return File Contents With Glued Header
     */
    private String appendHeader(final String fileContents) {
        /* Get Column Size */
        String singleLine = fileContents
                .split(Constants.DEFAULT_FILE_END_LINE_DELIMITER)[0];
        int columnSize = singleLine.length()
                - singleLine.replace(
                        Constants.DELIMITER_REPLACE_REGEX_START
                                + Constants.DEFAULT_FILE_DELIMITER
                                + Constants.DELIMITER_REPLACE_REGEX_END, "")
                        .length();
        return new StringBuilder().append(stitchHeader(columnSize))
                .append(fileContents).toString();
    }

    /**
     * Stitch Header
     * 
     * @param fileContents
     * @param columnSize
     * @return Glued Header
     */
    private String stitchHeader(final int columnSize) {
        StringBuilder builder = new StringBuilder();

        /* Stitch Header*/
        for (int i = 0; i < columnSize; i++) {
            builder.append(Constants.DEFAULT_COLUMN_NAME_PREFIX).append(i)
                    .append(Constants.DEFAULT_FILE_DELIMITER);
        }

        return builder.append(Constants.DEFAULT_FILE_END_LINE_DELIMITER)
                .toString();
    }

    /**
     * Read With Options
     * 
     * @param completeFileName
     * @param fileEncoding
     * @param fileDelimiter
     * @return File Content
     * @throws ParserException
     */
    private String readWithOptions(final String completeFileName,
            final String fileEncoding, final String fileDelimiter)
            throws ParserException {
        try {
            return StringUtils.isEmpty(fileDelimiter) ? reader
                    .readFileAsString(completeFileName, fileEncoding) : reader
                    .readFileAsString(completeFileName, fileEncoding).replace(
                            Constants.DELIMITER_REPLACE_REGEX_START
                                    + fileDelimiter
                                    + Constants.DELIMITER_REPLACE_REGEX_END,
                            Constants.DEFAULT_FILE_DELIMITER);
        } catch (ReaderException ex) {
            throw new ParserException(ex);
        }
    }
}
