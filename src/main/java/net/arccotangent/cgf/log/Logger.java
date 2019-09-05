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

package net.arccotangent.cgf.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Logger {

	private static final boolean debug = true;
	private String tag;

	public Logger(String tag) {
		this.tag = tag;
	}

	public static String getTime() {
		Calendar c = Calendar.getInstance();
		Date d = c.getTime();
		DateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss.SSS aa z");
		return df.format(d);
	}

	public void d(String msg) {
		if (!debug)
			return;

		System.out.println(getTime() + " [" + tag + "] [DEBUG] " + msg);
	}

	public void i(String msg) {
		System.out.println(getTime() + " [" + tag + "] [INFO] " + msg);
	}

	public void w(String msg) {
		System.out.println(getTime() + " [" + tag + "] [WARN] " + msg);
	}

	public void e(String msg) {
		System.out.println(getTime() + " [" + tag + "] [ERROR] " + msg);
	}

}
