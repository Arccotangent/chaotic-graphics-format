/*
Chaotic Graphics Format - Scalable and variable graphics based on the chaos game
Copyright (C) 2019 Arccotangent

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package net.arccotangent.cgf;

import net.arccotangent.cgf.data.CGFData;
import net.arccotangent.cgf.file.CGFFile;
import net.arccotangent.cgf.log.Logger;
import net.arccotangent.cgf.proc.BitmapGenerator;

import java.io.File;

public class Main {

	private static final String VERSION = "0.0.1";
	private static final Logger log = new Logger("Main");

	public static void main(String[] args) {
		System.out.println("This is CGF version " + VERSION);

		if (args.length < 2) {
			System.out.println("Usage: cgf <file> <target file> - Generate a bitmap from a CGF file");
			System.out.println();
			System.out.println("FILE - A CGF file");
			System.out.println("TARGET FILE - The target PNG file path (*.png)");

			return;
		}

		String cgfFilePath = args[0];
		String targetFile = args[1];
		//String targetFormat = args[2];

/*		if (!(targetFormat.equals("PNG") || targetFormat.equals("JPG"))) {
			log.e("Invalid target bitmap format: " + targetFormat);
			log.e("Target format must be either 'PNG' or 'JPG'");

			return;
		}*/

		CGFFile cgfFile = new CGFFile(new File(cgfFilePath));

		cgfFile.open();

		if (cgfFile.isReady()) {
			try {
				cgfFile.parse();
			} catch (Exception e) {
				log.e("Error while parsing CGF file!");
				e.printStackTrace();
			}
		} else {
			log.e("Unable to prepare CGF file for parsing.");
			return;
		}

		CGFData cgfData = cgfFile.getCGFData();

		if (cgfData == null) {
			log.e("Parsing appears to have failed. Bailing out.");
			return;
		}

		File targetFileObj = new File(targetFile);
		BitmapGenerator generator = new BitmapGenerator(targetFileObj);
		boolean success = false;

		try {
			success = generator.generateBitmap(cgfData);
		} catch (Exception e) {
			log.e("Error during bitmap generation.");
			e.printStackTrace();
		}

		if (success)
			log.i("Bitmap generation complete. Your bitmap is located at: " + targetFileObj.getAbsolutePath());
		else
			log.e("Bitmap generation failed. There is likely an error above.");
	}

}
