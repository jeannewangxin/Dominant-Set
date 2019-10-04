package algorithms;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import supportGUI.FramedGUI;

public class DefaultTeam {
	public ArrayList<Point> calculDominatingSet(ArrayList<Point> origin, int edgeThreshold) {
		ArrayList<Point> points = (ArrayList<Point>) origin.clone();
		ArrayList<Point> rest = (ArrayList<Point>) points.clone();
		ArrayList<Point> result = (ArrayList<Point>) points.clone();

		for (int i = 0; i < 3; i++) {
			ArrayList<Point> doSet = localSearch32(rest,
					localSearch1(deleteOne(points, greedy(points, edgeThreshold), edgeThreshold), points,
							edgeThreshold),
					edgeThreshold);
			System.out.println("MAIN. greedy and localsearch10 and localsearch21 and localsearch32 Current sol: "
					+ result.size() + ". Found next sol: " + doSet.size());
			if (doSet.size() < result.size())
				result = doSet;
		}
		System.out.println("MAIN result: " + result.size());
		return result;

	}

	private ArrayList<Point> localSearch1(ArrayList<Point> firstSolution, ArrayList<Point> origin, int edgeThreshold) {
		ArrayList<Point> points = removeDuplicates(origin);
		ArrayList<Point> current = removeDuplicates(firstSolution);
		ArrayList<Point> next = (ArrayList<Point>) current.clone();
		System.out.println("LS. First sol(Solution of greedy): " + current.size());
		do {
			current = next;
			next = remove2add1(current, points, edgeThreshold);
			// System.out.println("LS. Current sol: " + current.size() + ". Found next sol:
			// " + next.size());
		} while (score(current) > score(next));// 当current的size > next的size的时候，current = next;
		System.out.println("LS. 2 replace 1 Last sol: " + current.size());
		return next;
	}

	public ArrayList<Point> deleteOne(ArrayList<Point> pointsIn, ArrayList<Point> firstResult, int edgeThreshold) {
		ArrayList<Point> result = removeDuplicates(firstResult);
		ArrayList<Point> candidat = (ArrayList<Point>) result.clone();
		for (Point p : result) {
			candidat.remove(p);
			if (isValid(result, pointsIn, edgeThreshold)) {
				return result;
			}
			result.add(p);
		}
		return result;
	}

	private ArrayList<Point> remove2add1(ArrayList<Point> candidate, ArrayList<Point> origin, int edgeThreshold) {
		ArrayList<Point> points = removeDuplicates(origin);
		ArrayList<Point> test = removeDuplicates(candidate);
		long seed = System.nanoTime();
		Collections.shuffle(test, new Random(seed));
		ArrayList<Point> rest = removeDuplicates(points);
		rest.removeAll(test);
		for (int i = 0; i < test.size(); i++) {
			for (int j = i + 1; j < test.size(); j++) {
				Point q = test.remove(j);
				Point p = test.remove(i);
				for (Point r : rest) {
					if (r.distance(q) < edgeThreshold && r.distance(p) < edgeThreshold) {
						test.add(r);
						if (isValid(test, points, edgeThreshold))
							return test;
						test.remove(r);
					}
				}
				test.add(i, p);
				test.add(j, q);
			}
		}

		return candidate;
	}

	public boolean isNeighbour(Point p, Point q, int thr) {
		return p.distanceSq(q) < thr * thr;
	}

	private static <T> T swapRemove(List<T> lst, int i) {
		int end = lst.size() - 1;
		if (i == end) {
			return lst.remove(i);
		}
		T swapped = lst.remove(end);
		T removed = lst.get(i);
		lst.set(i, swapped);
		return removed;
	}

	private static <T> void swapBack(List<T> lst, int i, T val) {
		if (i >= lst.size()) {
			lst.add(val);
		} else {
			T swapped = lst.get(i);
			lst.set(i, val);
			lst.add(swapped);
		}
	}

	private ArrayList<Point> localSearch32(ArrayList<Point> rest, ArrayList<Point> sol, int thr) {
		int thrS = 9 * thr * thr;
		for (int i = 0; i < sol.size(); i++) {
			Point p = swapRemove(sol, i);
			for (int j = i; j < sol.size(); j++) {
				Point q = swapRemove(sol, j);
				for (int k = j; k < sol.size(); k++) {
					Point r = swapRemove(sol, k);

					if (p.distanceSq(q) < thrS && p.distanceSq(r) < thrS && q.distanceSq(r) < thrS) {

						ArrayList<Point> toCheck = new ArrayList<>();
						toCheck.add(p);
						toCheck.add(q);
						toCheck.add(r);
						for (Point pk : rest) {
							if (isNeighbour(pk, p, thr) || isNeighbour(pk, q, thr) || isNeighbour(pk, r, thr)) {
								toCheck.add(pk);
							}
						}

						for (int l = 0; l < rest.size(); l++) {
							Point n1 = swapRemove(rest, l);
							for (int m = l; m < rest.size(); m++) {
								Point n2 = swapRemove(rest, m);

								if (n1.distanceSq(p) < thrS && n1.distanceSq(q) < thrS && n1.distanceSq(r) < thrS
										&& n2.distanceSq(p) < thrS && n2.distanceSq(q) < thrS
										&& n2.distanceSq(r) < thrS) {
									rest.add(p);
									rest.add(q);
									rest.add(r);

									sol.add(n1);
									sol.add(n2);

									if (isValid(sol, toCheck, thr)) {
										System.out.println("Iterate localSearch32 result: " + sol.size());
										return localSearch32(rest, sol, thr);
									}

									sol.remove(sol.size() - 1);
									sol.remove(sol.size() - 1);

									rest.remove(rest.size() - 1);
									rest.remove(rest.size() - 1);
									rest.remove(rest.size() - 1);
								}
								swapBack(rest, m, n2);
							}
							swapBack(rest, l, n1);
						}
					}
					swapBack(sol, k, r);
				}
				swapBack(sol, j, q);
			}
			swapBack(sol, i, p);
		}

		return sol;
	}

	public ArrayList<Point> localSearch21(ArrayList<Point> rest, ArrayList<Point> sol, int thr) {
		int thrS = 9 * thr * thr;
		for (int i = 0; i < sol.size(); i++) {
			Point p = swapRemove(sol, i);
			for (int j = i; j < sol.size(); j++) {
				Point q = swapRemove(sol, j);

				ArrayList<Point> toCheck = new ArrayList<>();
				toCheck.add(p);
				toCheck.add(q);
				for (int k = 0; k < rest.size(); k++) {
					Point pk = rest.get(k);
					if (isNeighbour(pk, p, thr) || isNeighbour(pk, q, thr)) {
						toCheck.add(pk);
					}
				}

				if (p.distanceSq(q) < thrS) {
					for (int k = 0; k < rest.size(); k++) {
						Point n = swapRemove(rest, k);

						if (n.distanceSq(p) < thrS && n.distanceSq(q) < thrS) {
							rest.add(p);
							rest.add(q);
							sol.add(n);

							if (isValid(sol, toCheck, thr)) {
								System.out.println("Local serach 21 result" + sol.size());
								return localSearch21(rest, sol, thr);
							}

							sol.remove(sol.size() - 1);
							rest.remove(rest.size() - 1);
							rest.remove(rest.size() - 1);
						}
						swapBack(rest, k, n);
					}
				}
				swapBack(sol, j, q);
			}
			swapBack(sol, i, p);
		}

		return sol;
	}

	// FILE PRINTER
	private void saveToFile(String filename, ArrayList<Point> result) {
		int index = 0;
		try {
			while (true) {
				BufferedReader input = new BufferedReader(
						new InputStreamReader(new FileInputStream(filename + Integer.toString(index) + ".points")));
				try {
					input.close();
				} catch (IOException e) {
					System.err.println(
							"I/O exception: unable to close " + filename + Integer.toString(index) + ".points");
				}
				index++;
			}
		} catch (FileNotFoundException e) {
			printToFile(filename + Integer.toString(index) + ".points", result);
		}
	}

	private void printToFile(String filename, ArrayList<Point> points) {
		try {
			PrintStream output = new PrintStream(new FileOutputStream(filename));
			int x, y;
			for (Point p : points)
				output.println(Integer.toString((int) p.getX()) + " " + Integer.toString((int) p.getY()));
			output.close();
		} catch (FileNotFoundException e) {
			System.err.println("I/O exception: unable to create " + filename);
		}
	}

	// FILE LOADER
	private ArrayList<Point> readFromFile(String filename) {
		String line;
		String[] coordinates;
		ArrayList<Point> points = new ArrayList<Point>();
		int count = 0;
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			try {
				while ((line = input.readLine()) != null && count < 1000) {
					coordinates = line.split("\\s+");
					points.add(new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
					count++;
				}

			} catch (IOException e) {
				System.err.println("Exception: interrupted I/O.");
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					System.err.println("I/O exception: unable to close " + filename);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Input file not found.");
		}
		return points;
	}

	private ArrayList<Point> removeDuplicates(ArrayList<Point> points) {
		ArrayList<Point> result = (ArrayList<Point>) points.clone();
		for (int i = 0; i < result.size(); i++) {
			for (int j = i + 1; j < result.size(); j++)
				if (result.get(i).equals(result.get(j))) {
					result.remove(j);
					j--;
				}
		}
		return result;
	}

	private ArrayList<Point> getPointSolide(ArrayList<Point> pointsIn, int edgeThreshold) {
		ArrayList<Point> result = new ArrayList<Point>();
		for (Point p : pointsIn) {
			if (degree(p, pointsIn, edgeThreshold) == 0) {
				result.add((Point) p.clone());
			}
		}
		return result;
	}

	private ArrayList<Point> greedy(ArrayList<Point> pointsIn, int edgeThreshold) {
		ArrayList<Point> domSet = new ArrayList<Point>();
		ArrayList<Point> reste = (ArrayList<Point>) pointsIn.clone();
		ArrayList<Point> pSolid = getPointSolide(pointsIn, edgeThreshold);
		if (pSolid.size() > 0) {
			domSet.addAll(pSolid);
			reste.removeAll(pSolid);
		}
		Collections.shuffle(reste, new Random(System.nanoTime()));
		ArrayList<Point> rest = removeDuplicates(reste);
		while (!isValid(domSet, pointsIn, edgeThreshold)) {

			int max = 0;
			int maxP = 0;
			for (int i = 0; i < rest.size(); i++) {
				int population = degree(rest.get(i), rest, edgeThreshold);
				if (population > max) {
					max = population;
					maxP = i;
				}
			}
			if (rest.size() > 0) {
				domSet.add((Point) rest.get(maxP).clone());
				Point p = rest.get(maxP);
				rest.remove(maxP);
				rest.removeAll(getNeighbors(p, rest, edgeThreshold));
			}
		}

		return domSet;
	}

	private boolean isValid(ArrayList<Point> candidates, ArrayList<Point> pointsIn, int edgeThreshold) {
		ArrayList<Point> points = (ArrayList<Point>) pointsIn.clone();
		points.removeAll(candidates);
		for (Point p : candidates) {
			ArrayList<Point> neighbors = getNeighbors(p, points, edgeThreshold);
			points.removeAll(neighbors);
		}
		if (points.size() == 0)
			return true;
		return false;
	}

	private ArrayList<Point> getNeighbors(Point p, ArrayList<Point> vertices, int edgeThreshold) {
		ArrayList<Point> result = new ArrayList<Point>();
		for (Point point : vertices)
			if (point.distance(p) < edgeThreshold && !point.equals(p))
				result.add((Point) point.clone());
		return result;
	}

	private boolean isEdge(Point p, Point q, int edgeThreshold) {
		return p.distance(q) < edgeThreshold;
	}

	private int degree(Point p, ArrayList<Point> points, int edgeThreshold) {
		int degree = -1;
		for (Point q : points)
			if (isEdge(p, q, edgeThreshold))
				degree++;
		return degree;
	}

	private int score(ArrayList<Point> candidate) {
		return candidate.size();
	}

	public static void main(String arg[]) throws FileNotFoundException {

		ArrayList<Point> origine = new ArrayList<Point>();
		DefaultTeam defaultTeam = new DefaultTeam();
		origine = defaultTeam.readFromFile("./input.points");
		supportGUI.FramedGUI fram = new FramedGUI(1200, 800, null, 0, 100, false);
		fram.drawPoints(origine, origine);

	}
}
