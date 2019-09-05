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

package net.arccotangent.cgf.data;

import java.util.ArrayList;
import java.util.Comparator;

public class CGFData {

	private final ArrayList<CGFPoint> points;
	private final ArrayList<CGFProportion> proportions;
	private final long iterations;
	private final int x;
	private final int y;

	public CGFData(ArrayList<CGFPoint> points, ArrayList<CGFProportion> proportions, long iterations, int x, int y) {
		this.points = points;
		this.proportions = proportions;
		this.iterations = iterations;
		this.x = x;
		this.y = y;
	}

	private void sortAllData() {
		points.sort(Comparator.comparing(CGFPoint::getName));
		proportions.sort(Comparator.comparing(CGFProportion::getTargetPoint));
	}

	public boolean verifyAllData() {
		sortAllData();
		if (points.size() != proportions.size())
			return false;

		for (int i = 0; i < points.size(); i++) {
			if (!points.get(i).getName().equals(proportions.get(i).getTargetPoint()))
				return false;
		}

		return iterations > 0 && x > 0 && y > 0;
	}

	public ArrayList<CGFPoint> getPoints() {
		return points;
	}

	public ArrayList<CGFProportion> getProportions() {
		return proportions;
	}

	public long getIterations() {
		return iterations;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
