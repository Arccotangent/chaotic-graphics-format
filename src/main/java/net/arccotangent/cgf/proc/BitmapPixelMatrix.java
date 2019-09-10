package net.arccotangent.cgf.proc;

import net.arccotangent.cgf.log.Logger;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BitmapPixelMatrix {

	private ArrayList<ArrayList<Integer>> matrix;
	private final Logger log;

	public BitmapPixelMatrix(ArrayList<ArrayList<Integer>> matrix) {
		this.matrix = matrix;
		this.log = new Logger("BitmapPixelMatrix/" + hashCode());

		if (!verifyMatrix()) {
			throw new IllegalArgumentException("Empty or non-rectangular pixel matrix");
		}
	}

	BitmapPixelMatrix(int x, int y) {
		this.matrix = new ArrayList<>();

		for (int i = 0; i < y; i++) {
			ArrayList<Integer> row = new ArrayList<>();
			for (int j = 0; j < x; j++) {
				row.add(0xffffffff);
			}
			this.matrix.add(row);
		}

		this.log = new Logger("BitmapPixelMatrix/" + hashCode());

		if (!verifyMatrix()) {
			throw new IllegalArgumentException("Empty or non-rectangular pixel matrix");
		}
	}

	int getX() {
		return matrix.get(0).size();
	}

	int getY() {
		return matrix.size();
	}

	private boolean verifyMatrix() {
		int y = matrix.size();
		if (y == 0) {
			log.e("Verification failure: Matrix is empty.");
			return false;
		}

		log.d("This bitmap has " + y + " rows.");

		int x = matrix.get(0).size();

		for (int i = 1; i < matrix.size(); i++) {
			if (matrix.get(i).size() != x) {
				log.e("Verification failure: Matrix row " + i + " is nonstandard. Matrix is not rectangular.");
				return false;
			}
		}

		return true;
	}

	int getPixel(int x, int y) {
		return matrix.get(y).get(x);
	}

	void setPixel(int x, int y, int pixel) {
		if (x < 0 || y < 0 || x > getX() || y > getY()) {
			log.w("Invalid pixel coordinate (" + x + ", " + y + "). Ignoring.");
			return;
		}

		if (getPixel(x, y) != pixel)
			matrix.get(y).set(x, pixel);
	}

	public int[] getPixels() {
		ArrayList<Integer> flattened = (ArrayList<Integer>) matrix.stream().flatMap(ArrayList::stream).collect(Collectors.toList());
		return flattened.stream().mapToInt(Integer::intValue).toArray();
	}

}
