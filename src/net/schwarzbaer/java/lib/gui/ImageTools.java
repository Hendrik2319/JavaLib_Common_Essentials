package net.schwarzbaer.java.lib.gui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.IntUnaryOperator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

public class ImageTools
{
	public static BufferedImage rotate90_clockwise(BufferedImage image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		AffineTransform transform = AffineTransform.getQuadrantRotateInstance(1);
		transform.translate(0, -height);
		return createTransformImage(image, height, width, transform);
	}
	
	public static BufferedImage rotate90_counterclockwise(BufferedImage image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		AffineTransform transform = AffineTransform.getQuadrantRotateInstance(-1);
		transform.translate(-width, 0);
		return createTransformImage(image, height, width, transform);
	}

	public static BufferedImage rotate180(BufferedImage image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		AffineTransform transform = AffineTransform.getQuadrantRotateInstance(2);
		transform.translate(-width, -height);
		return createTransformImage(image, width, height, transform);
	}

	private static BufferedImage createTransformImage(BufferedImage image, int width, int height, AffineTransform transform)
	{
		BufferedImage resultImage = new BufferedImage(width,height,image.getType());
		resultImage.createGraphics().drawImage(image, transform, null);
		return resultImage;
	}

	public static BufferedImage convert(BufferedImage image, IntUnaryOperator rgbConverter)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage resultImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++)
				resultImage.setRGB( x, y, rgbConverter.applyAsInt( image.getRGB( x, y ) ) );
		return resultImage;
	}
	
	public static BufferedImage scale(BufferedImage image, int width, int height)
	{
		BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.drawImage(image, 0, 0, width, height, null);
		return bufferedImage;
	}
	
	public static BufferedImage cutOut(BufferedImage image, int x, int y, int width, int height)
	{
		BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bufferedImage.createGraphics();
		//g2.drawImage(image, -x, -y, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
		g2.drawImage(image, -x, -y, null);
		return bufferedImage;
	}

	public static IntUnaryOperator keepAlpha(IntUnaryOperator rgbConverter)
	{
		return rgb -> {
			int alpha = rgb & 0xFF000000;
			rgb = rgb & 0xFFFFFF;
			rgb = rgbConverter.applyAsInt(rgb);
			rgb = rgb & 0xFFFFFF;
			return alpha | rgb;
		};
	}
	
	public static BufferedImage removeAlphaChannel(BufferedImage image)
	{
		if (image==null) return null;
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		result.getGraphics().drawImage(image, 0, 0, null);
		return result;
	}
	
	private static ImageWriter getImageWriterByFormatName(String formatName) {
		Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName(formatName);
		if (imageWriters.hasNext())
			return imageWriters.next();
		return null;
	}

	public static void saveJpgFileWithImageWriter(BufferedImage image, File file, float quality) throws FileIOException
	{
		ImageWriter imageWriter = getImageWriterByFormatName("jpg");
		if (imageWriter==null)
			throw new FileIOException("Can't find an ImageWriter for JPEG");
		
		System.out.printf("Write image to file: %s%n", file.getAbsolutePath());
		
		try (ImageOutputStream ios = ImageIO.createImageOutputStream(file))
		{
			imageWriter.setOutput(ios);
			ImageWriteParam param = imageWriter.getDefaultWriteParam();
			
			if (param.canWriteCompressed())
			{
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(quality);
				
				if (param instanceof JPEGImageWriteParam jpegParam)
					jpegParam.setOptimizeHuffmanTables(true);
			}
			
			if (param.canWriteProgressive())
			{
				param.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
			}
			
			image = removeAlphaChannel(image);
			imageWriter.write(null, new IIOImage(image, null, null), param);
		}
		catch (FileNotFoundException ex)
		{
			//ex.printStackTrace();
			throw new FileIOException(ex,
					"FileNotFoundException while writing image file",
					"   file: \"%s\"".formatted(file.getAbsolutePath()),
					"   message: %s".formatted(ex.getMessage())
			);
		}
		catch (IOException ex)
		{
			//ex.printStackTrace();
			throw new FileIOException(ex,
					"IOException while writing image file",
					"   file: \"%s\"".formatted(file.getAbsolutePath()),
					"   message: %s".formatted(ex.getMessage())
			);
		}
		finally
		{
			imageWriter.dispose();
		}
	}
	
	public static void saveJpgFileWithImageWriter(BufferedImage image, File file, float quality, String firstLineOfErrorMsg)
	{
		try
		{
			saveJpgFileWithImageWriter(image, file, quality);
		}
		catch (FileIOException ex)
		{
			ex.showInErrorConsole(firstLineOfErrorMsg);
			//ex.printStackTrace();
		}
	}

	public static class FileIOException extends Exception
	{
		private static final long serialVersionUID = 9159884647024013426L;
		private final String[] lines;
		
		private FileIOException(Throwable cause, String... lines)
		{
			super( String.join(" ", lines), cause );
			this.lines = lines;
		}
		private FileIOException(String... lines)
		{
			super( String.join(" ", lines) );
			this.lines = lines;
		}
		
		public void showInErrorConsole(String firstLine)
		{
			System.err.println(firstLine);
			for (String line : lines)
				System.err.printf("   %s%n", line);
			
			Throwable cause = getCause();
			if (cause!=null) 
				System.err.printf("   caused by %s: %s%n", cause.getClass().getCanonicalName(), cause.getMessage());
		}
	}
}
