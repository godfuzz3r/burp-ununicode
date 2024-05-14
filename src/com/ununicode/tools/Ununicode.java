package com.ununicode.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.text.translate.UnicodeUnescaper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Ununicode {
	private static byte[] concatHttp(byte[] headers, byte[] content) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(headers);
		outputStream.write(content);
		return outputStream.toByteArray();
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

        String unescaped = new UnicodeUnescaper().translate(prettified);
		
        return unescaped.getBytes();
    }

    public static byte[] getUnescapedHttpContent(byte[] content, int bodyOffset) throws IOException {
		byte[] headers = Arrays.copyOfRange(content, 0, bodyOffset);
		byte[] body = Arrays.copyOfRange(content, bodyOffset, content.length);

        String prettified = prettifyJson(new String(body));

        String unescaped = new UnicodeUnescaper().translate(prettified);

        byte[] out = concatHttp(headers, unescaped.getBytes());

        return out;
    }
}
