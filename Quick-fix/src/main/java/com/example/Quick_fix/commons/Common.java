package com.example.Quick_fix.commons;

import java.util.concurrent.ThreadLocalRandom;

public class Common {

	public String generateUniqueId() {
	    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	    StringBuilder uniqueId = new StringBuilder(6);
	    for (int i = 0; i < 6; i++) {
	        int index = ThreadLocalRandom.current().nextInt(characters.length());
	        uniqueId.append(characters.charAt(index));
	    }
	    return uniqueId.toString();
	}

}
