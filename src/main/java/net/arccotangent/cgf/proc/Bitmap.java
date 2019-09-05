package net.arccotangent.cgf.proc;

import net.arccotangent.cgf.log.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

class Bitmap {

	private File targetFile;
	private BitmapPixelMatrix pixelMatrix;
	private final Logger log;

	Bitmap(File targetFile, BitmapPixelMatrix pixelMatrix) {
		this.targetFile = targetFile;
		this.pixelMatrix = pixelMatrix;
		this.log = new Logger("Bitmap/" + pixelMatrix.hashCode());
	}

	private BufferedImage toBufferedImage() {

		BufferedImage image = new BufferedImage(pixelMatrix.getX(), pixelMatrix.getY(), BufferedImage.TYPE_INT_ARGB);
		//image.getRaster().setPixels(0, 0, pixelMatrix.getX(), pixelMatrix.getY(), pixelMatrix.getPixels());
		for (int y = 0; y < pixelMatrix.getY(); y++) {
			for (int x = 0; x < pixelMatrix.getX(); x++) {
				image.setRGB(x, y, pixelMatrix.getPixel(x, y));
			}
		}

		return image;
	}

	boolean writeToTargetFile() throws Exception {
		return ImageIO.write(toBufferedImage(), "png", targetFile);
	}

}
