package net.schwarzbaer.java.lib.system;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class UniqueStringID
{
	private final Set<String> knownIDs;
	private final Random rnd;
	private final String[] charArr; 
	
	public UniqueStringID(int length)
	{
		knownIDs = new HashSet<>();
		rnd = new Random();
		charArr = new String[length];
	}
	
	public String createNew()
	{
		String newID = null;
		while (newID==null || knownIDs.contains(newID))
		{
			for (int i=0; i<charArr.length; i++)
				charArr[i] =  Character.toString( nextChar() );
			newID = String.join("", charArr);
		}
		knownIDs.add(newID);
		return newID;
	}

	private char nextChar()
	{
		return (char)rnd.nextInt('A', ((int)'Z')+1);
	}

	public void addKnownID(String id) throws UniqueIDException
	{
		if (knownIDs.contains(id))
			throw new UniqueIDException("ID \"%s\" is already in use.", id);
		knownIDs.add(id);
	}
	
	class UniqueIDException extends Exception
	{
		private static final long serialVersionUID = 5645187579709944735L;

		public UniqueIDException(String format, Object... values)
		{
			super(format.formatted(values));
		}
	}
}
