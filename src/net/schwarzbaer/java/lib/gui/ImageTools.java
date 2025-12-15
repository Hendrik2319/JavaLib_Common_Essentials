package net.schwarzbaer.java.lib.gui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.function.IntUnaryOperator;

public class ImageTools
{
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
	
	public static BufferedImage scale(BufferedImage image, int width, int height) {
		BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2.drawImage(image, 0, 0, width, height, null);
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
}
