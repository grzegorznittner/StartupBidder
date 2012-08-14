/**
 * StartupBidder.com
 * Copyright 2012
 */
package com.startupbidder.util;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrMatcher;
import org.apache.commons.lang3.text.StrTokenizer;

import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.EndTag;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.Tag;

/**
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class HtmlConverter {
	private static final Logger log = Logger.getLogger(HtmlConverter.class.getName());
	
	public static String convertHtmlToText(String html) {
		if (StringUtils.isBlank(html)) {
			return "";
		}
		if (html.trim().equalsIgnoreCase("null")) {
			return "";
		}

		try {
			StringBuffer out = new StringBuffer();
			boolean lastSpace = false;
			Stack<Tag> tagStack = new Stack<Tag>(); 
			
			Source src = new Source(html);
			for (Segment segment : src) {
				if (segment instanceof StartTag) {
					Tag tag = (Tag) segment;
					if (tag.getName().equalsIgnoreCase("br")) {
						out.append("\n");
						lastSpace = true;
					} if (tag.getName().equalsIgnoreCase("p")) {
						out.append("\n");
						lastSpace = true;
						tagStack.push((Tag)segment);
					} else {
						if (!lastSpace) {
							out.append(" ");
							lastSpace = true;
						}
						tagStack.push((Tag)segment);
					}
				} else if (segment instanceof EndTag) {
					if (((EndTag) segment).getName().equalsIgnoreCase("p")) {
						out.append("\n");
					} else {
						if (!lastSpace) {
							out.append(" ");
							lastSpace = true;
						}
					}
					if (!tagStack.isEmpty()) {
						tagStack.pop();
					}
				} else if (segment instanceof CharacterReference) {
					// do nothing
				} else {
					Tag tag = null;
					if (!tagStack.isEmpty()) {
						tag = tagStack.pop();
					}
					String text = segment.toString().trim();
					if (tag != null && tag.getName().equalsIgnoreCase("li") && text.length() > 0) {
						out.append("\n * ");
					}
					if (text.length() > 0) {
						out.append(text);
						lastSpace = false;
					}
					if (tag != null) {
						tagStack.push(tag);
					}
				}
			}
			
			return out.toString();
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception while parsing html: " + html, e);
			return html;
		}
	}
}
