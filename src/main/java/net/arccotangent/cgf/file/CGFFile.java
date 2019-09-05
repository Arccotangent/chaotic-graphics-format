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

package net.arccotangent.cgf.file;

import net.arccotangent.cgf.data.*;
import net.arccotangent.cgf.log.Logger;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CGFFile {

	private final File file;
	private final Logger log;
	private int fileVersion = 0;
	private CGFData cgfData = null;
	private boolean ready = false;

	private static final HashMap<String, CGFOption> validOptions = new HashMap<>();
	private static final HashMap<String, CGFOptionCommand> validOptionCommands = new HashMap<>();

	private static final ArrayList<String> invalidPointNames = new ArrayList<>();

	static {
		validOptionCommands.put("CGF", CGFOptionCommand.CGF);
		validOptionCommands.put("POINTS", CGFOptionCommand.POINTS);
		validOptionCommands.put("PROPORTIONS", CGFOptionCommand.PROPORTIONS);
		validOptionCommands.put("END-POINTS", CGFOptionCommand.END_POINTS);
		validOptionCommands.put("END-PROPORTIONS", CGFOptionCommand.END_PROPORTIONS);
		validOptionCommands.put("END-CGF", CGFOptionCommand.END_CGF);

		validOptions.put("ITERATIONS", CGFOption.ITERATIONS);
		validOptions.put("SIZE", CGFOption.SIZE);

		invalidPointNames.add("CGFSEEDPOINT");
	}

	public CGFFile(File file) {
		this.file = file;
		this.log = new Logger("CGFFile/" + file.getName());
	}

	public void open() {
		log.i("Opening file: " + file.getName());

		if (!file.exists()) {
			log.e("This file does not exist. Cannot open.");
			return;
		}

		if (!file.canRead()) {
			log.e("CGF does not have permission to access this file.");
			return;
		}

		ready = true;
		log.i("This file is ready for parsing.");
	}

	public boolean isReady() {
		return ready;
	}

	public int getFileVersion() {
		return fileVersion;
	}

	public CGFData getCGFData() {
		return cgfData;
	}

	public void parse() throws Exception {
		if (!ready) {
			log.e("This file isn't ready for parsing. Either open() was not called or it failed.");
			return;
		}

		String rawFileData = new String(Files.readAllBytes(file.toPath()));
		String fileDataNoWS = rawFileData.replaceAll("[\\s]", "");

		Pattern optionPattern = Pattern.compile("(?<=\\[).+?(?=])");
		Matcher matcher = optionPattern.matcher(fileDataNoWS);

		ArrayList<String> options = new ArrayList<>();

		while (matcher.find()) {
			options.add(matcher.group().replaceAll("[\\[|\\]]", ""));
		}

		options.removeIf(String::isEmpty);

		log.d("Parsing found " + options.size() + " config options.");

		ArrayList<CGFPoint> points = new ArrayList<>();
		ArrayList<CGFProportion> proportions = new ArrayList<>();
		long iterations = -1;
		int x = -1, y = -1;

		CGFOptionCommand optionCommand = CGFOptionCommand.ROOT;

		for (int i = 0; i < options.size(); i++) {
			String option = options.get(i);

			log.d("Option " + i + ": " + option);

			if (i == 0) {
				String[] parts = option.split(":");
				String key = parts[0];
				String value = parts[1];

				if (key.equals("CGF")) {
					fileVersion = Integer.parseInt(value);
					log.i("Using CGF file version: " + fileVersion);
					optionCommand = CGFOptionCommand.CGF;
					continue;
				} else {
					log.e("Invalid or missing CGF file magic: " + option);
				}
			} else {
				if (optionCommand == CGFOptionCommand.ROOT) {
					log.e("Invalid or missing CGF file magic!");
					return;
				}
			}

			if (!option.contains(":")) {
				optionCommand = validOptionCommands.get(option);

				if (optionCommand == CGFOptionCommand.END_POINTS || optionCommand == CGFOptionCommand.END_PROPORTIONS)
					optionCommand = CGFOptionCommand.CGF;

				log.d("Context switched to: " + optionCommand);

				if (optionCommand == CGFOptionCommand.END_CGF) {
					if (i < options.size() - 1)
						log.w("Premature CGF data ending! Obeying anyway.");
					else
						log.i("CGF file processing complete.");
					break;
				}
				continue;
			}

			String[] parts = option.split(":");
			String key = parts[0];
			String value = parts[1];

			String[] values = value.contains(",") ? value.split(",") : new String[] {value};

			switch (optionCommand) {
				case POINTS: {
					if (values.length != 2) {
						log.e("PARSER ERROR: Point " + key + " is invalid - contains " + values.length + " points, expected 2.");
						return;
					}

					if (invalidPointNames.contains(key)) {
						log.e("PARSER ERROR: Point " + key + " is invalid - is a keyword.");
						return;
					}

					int ptX = Integer.parseInt(values[0]);
					int ptY = Integer.parseInt(values[1]);

					points.add(new CGFPoint(key, ptX, ptY));
					log.d("Processed point " + key + " with position (" + ptX + ", " + ptY + ")");
					break;
				}
				case PROPORTIONS: {
					if (values.length != 1) {
						log.e("PARSER ERROR: Proportion targeting point " + key + " is invalid - contains " + values.length + " values, expected 1.");
						return;
					}

					proportions.add(new CGFProportion(key, Double.parseDouble(values[0])));
					break;
				}
				case CGF: {
					CGFOption rootOption = validOptions.get(key);

					switch (rootOption) {
						case ITERATIONS: {
							iterations = Long.parseLong(value);
							break;
						}
						case SIZE: {
							if (values.length != 2) {
								log.e("PARSER ERROR: Bitmap size is invalid - contains " + values.length + " values, expected 2.");
								return;
							}

							x = Integer.parseInt(values[0]);
							y = Integer.parseInt(values[1]);
							break;
						}
						default: {
							log.e("Invalid root CGF option encountered: " + key + " - CGF file is malformed");
							break;
						}
					}
					break;
				}
				default: {
					log.w("Strange context encountered: " + optionCommand + " - please report error if it persists");
					break;
				}
			}
		}

		cgfData = new CGFData(points, proportions, iterations, x, y);

		boolean verified = cgfData.verifyAllData();
		if (verified) {
			log.i("CGF file parsed and verified. Data is valid.");
		} else {
			cgfData = null;
			log.e("CGF file parsed, but was not verified. Data is potentially invalid and was cleared.");
		}
	}

}
