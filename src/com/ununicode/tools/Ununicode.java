package com.ununicode.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Ununicode {
	// it's ugly, but here we can decode all decodable 1-byte unicode escaped sequences
	// and skip unescaped and non-decodable \ uXXXX values
	private static String unescape(String input) {
		StringBuilder result = new StringBuilder(input.length());
		int length = input.length();
		int i = 0;

		while (i < length) {
			if (i + 6 <= length && input.charAt(i) == '\\' && input.charAt(i + 1) == 'u') {
				int code = 0;
				boolean valid = true;

				for (int j = 0; j < 4; j++) {
					char c = input.charAt(i + 2 + j);
					int digit = Character.digit(c, 16);
					if (digit == -1) {
						valid = false;
						break;
					}
					code = (code << 4) | digit;
				}

				if (valid) {
					result.append((char) code);
					i += 6;
					continue;
				}
			}

			result.append(input.charAt(i++));
		}

		return result.toString();
	}

	private static String prettifyJson(String json) {
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		try{
			Object jsonObject = mapper.readValue(json, Object.class);
			return mapper.writer(new BurpPrettyPrinter()).writeValueAsString(jsonObject);
 		}catch (Exception e) {
			return json;
		}
	}

    public static byte[] getUnescapedRawContent(byte[] content) throws IOException {
        String prettified = prettifyJson(new String(content));

        String unescaped = unescape(prettified);
		
        return unescaped.getBytes();
    }

	// we decode HTTP-headers and body separetely because we want prettify body if can
	// without relying on content-type
    public static byte[] getUnescapedHttpContent(byte[] content, int bodyOffset) throws IOException {
		byte[] headers = Arrays.copyOfRange(content, 0, bodyOffset);
		byte[] body = Arrays.copyOfRange(content, bodyOffset, content.length);

		String unescapedHeaders = unescape(new String(headers));
        String unescapedBody = unescape(prettifyJson(new String(body)));

        byte[] out = (unescapedHeaders + unescapedBody).getBytes();

        return out;
    }
}
