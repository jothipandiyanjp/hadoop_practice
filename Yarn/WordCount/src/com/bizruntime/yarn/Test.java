package com.bizruntime.yarn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;

public class Test {

	public static void main(String[] args) throws IOException {
		StringReader reader=new StringReader("Test ");
		BufferedReader br=new BufferedReader(reader);
		
		int count = 0;
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            count++;
		StringTokenizer itr = new StringTokenizer(line.toString());
		while (itr.hasMoreTokens()) {
			word.set(itr.nextToken());
			context.write(word, one);
		}
        }
		
	}
}
