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
		if (StringUtils.isEmpty(html)) {
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
	
	public static void main(String arg[]) {
		String description = "Change your application!\n"
				+ " This Android application lets you change the wallpaper with a beautiful pattern.\n"
				+ "Whether you shake your phone for random inspiring results or you search by keywords / hex colour code or by a specific username you are guaranteed to find a pattern that fits you from more then 1 million lovely patterns. All patterns come from colourlovers.com.\n"
				+ "Add patterns to favorites by clicking the heart.\n"
				+ "Share on email your favorite patterns with friends or save them to your gallery.\n"
				+ "Enjoy auto-changing your wallpaper with one of your heart patterns.";
		StringBuffer mantra = new StringBuffer();
		String sentence = null;
		int index = 0;
		while (index >= 0) {
			index = StringUtils.indexOfAny(description, '.', '!', '?');
			if (index >= 0) {
				sentence = description.substring(0, index + 1);
				description = description.substring(index + 2 > description.length() ? description.length() : index + 2);
			} else {
				sentence = description;
			}
			log.info(sentence + "  len=" + sentence.length() + ", total=" + (sentence.length() + mantra.length()));
			if (mantra.length() + sentence.length() < 100) {
				mantra.append(sentence);
			} else if (mantra.length() < 15) {
				mantra.append(sentence.substring(0, sentence.length() < 100 ? sentence.length() : 99));
				break;
			} else {
				break;
			}
		}
		log.info("Mantra: " + mantra.toString());
	}
}
