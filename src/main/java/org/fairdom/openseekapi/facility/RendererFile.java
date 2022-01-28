package org.fairdom.openseekapi.facility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class RendererFile
{
	public static final int LENGTHOFLINE = 120;
	File currentFile;
	String fileName;
	FileWriter ourFileOutStream;
	int iCharInLine;
	int iFiles; // Number of files written
	String folderName ;

	public RendererFile()
	{
		fileName="Names";
		folderName = "Names";
		
		checkAndCreateFolder();
		
		currentFile = new File(folderName+"/"+fileName+".txt");
		
		if (currentFile.exists())
		{
			currentFile.delete();
		}
		try
		{
			currentFile.createNewFile();
		} 
		catch (IOException e1)
		{
			System.out.println("Unable to create to the file "+currentFile);
			e1.printStackTrace();
		}
		
		try
		{
			ourFileOutStream = new FileWriter(currentFile);
		} 
		catch (IOException e)
		{
			System.out.println("Unable to write to the file "+currentFile);
			e.printStackTrace();
		}
		iCharInLine = 0;
		iFiles=1;
	}
	
	public RendererFile(String fileName)
	{
		this.fileName=fileName;
		folderName = "Names";
		
		checkAndCreateFolder();
		
		currentFile = new File(folderName+"/"+fileName+".txt");
		if (currentFile.exists())
		{
			currentFile.delete();
		}
		try
		{
			currentFile.createNewFile();
		} 
		catch (IOException e1)
		{
			System.out.println("Unable to create to the file "+currentFile);
			e1.printStackTrace();
		}
		
		try
		{
			ourFileOutStream = new FileWriter(currentFile);
		} 
		catch (IOException e)
		{
			System.out.println("Unable to write to the file "+currentFile);
			e.printStackTrace();
		}
		iCharInLine = 0;
		iFiles=1;
	}
	
	public void closeFile()
	{
		try
		{
			ourFileOutStream.close();
		}
		catch (IOException e)
		{
			System.out.println("Problem while closing the file "+currentFile);
			e.printStackTrace();
		};
	}
	
	public String checkAndCreateFolder()
	{
		Date today = GregorianCalendar.getInstance().getTime();
		SimpleDateFormat myFormat = new SimpleDateFormat("_dd-MM-yyyy_HH-mm");
		File rootFolder = new File("generatedNames/");
		if (!rootFolder.exists())
		{
			rootFolder.mkdir();
		}
		folderName = "generatedNames/"+folderName+myFormat.format(today);
		File currentFolder = new File(folderName);
		currentFolder.mkdir();
		
		return folderName;
	}
	

	    
	public void renderOneString(String oneString)
	{
		// TODO Auto-generated method stub
		try
		{
			ourFileOutStream.write(oneString);
			iCharInLine+=(oneString+ " - ").length();
			if (iCharInLine > LENGTHOFLINE)
			{
				iCharInLine = 0;
				ourFileOutStream.write("\n");
			}
			else
			{
				ourFileOutStream.write(" - ");
			}
		} 
		catch (IOException e)
		{
			System.out.println("Unable to write "+oneString+" to the file "+currentFile);
			e.printStackTrace();
		}
	}

	public void next()
	{
		// TODO Auto-generated method stub
		closeFile();
		
		currentFile = new File(folderName+"/"+fileName+"-"+iFiles+".txt");
		
		if (currentFile.exists())
		{
			currentFile.delete();
		}
		try
		{
			currentFile.createNewFile();
		} 
		catch (IOException e1)
		{
			System.out.println("Unable to create to the file "+currentFile);
			e1.printStackTrace();
		}
		
		try
		{
			ourFileOutStream = new FileWriter(currentFile);
		} 
		catch (IOException e)
		{
			System.out.println("Unable to write to the file "+currentFile);
			e.printStackTrace();
		}
		iCharInLine = 0;
		iFiles++;
	}

	
}
