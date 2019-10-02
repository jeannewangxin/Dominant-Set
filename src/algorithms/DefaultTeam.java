package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import supportGUI.FramedGUI;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DefaultTeam {
	public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold) {
		//ArrayList<Point> ds = new ArrayList<Point>();
		//ArrayList<Point> res = new ArrayList<Point>();
		//ds  = greedy(points,edgeThreshold);//只用这个95.64
		//ds = deleteOne(points,ds,edgeThreshold);//+ 这个95.34
		//ds = remove2add1(ds,points,edgeThreshold);
		//return ds;
		ArrayList<Point> result = (ArrayList<Point>) points.clone();

		for (int i = 0; i < 10; i++) {
			ArrayList<Point> doSet = localSearch1(deleteOne(points,greedy(points, edgeThreshold),edgeThreshold), points, edgeThreshold);

			System.out.println("MAIN. Current sol: " + result.size() + ". Found next sol: " + doSet.size());

			if (doSet.size() < result.size())
				result = doSet;
		}
		System.out.println("MAIN. result: " + result.size());

		return result;
		/*
//		
//		ArrayList<Point> separateur = getMiniSparateur(points, edgeThreshold);
//		ArrayList<Point> result = (ArrayList<Point>) separateur.clone();
//		result.addAll(greedy(points,edgeThreshold));//只用这个95.64
//		
//		return result;
 */
	}


	private ArrayList<Point> localSearch1(ArrayList<Point> firstSolution, ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> current = removeDuplicates(firstSolution);
		ArrayList<Point> next = (ArrayList<Point>) current.clone();
		System.out.println("LS. First sol(Solution of greedy): " + current.size());
		do {
			current = next;
			next = remove2add1(current, points, edgeThreshold);
			System.out.println("LS. Current sol: " + current.size() + ". Found next sol: " + next.size());
		} while (score(current) > score(next));// 当current的size > next的size的时候，current = next;
		System.out.println("LS. Last sol: " + current.size());
		
		/*
		System.out.println("LS. First 3 replace 2 sol(Solution of 2 replace 1): " + current.size());
		do {
			current = next;
			next = remove3add2(current, points, edgeThreshold);
			System.out.println("LS. Current 3 replace 2  sol: " + current.size() + ". Found next sol: " + next.size());
		} while (score(current) > score(next));// 当current的size > next的size的时候，current = next;
		System.out.println("LS. 3 replace 2 Last sol: " + current.size());
		*/
		return next;
	}
	
	private ArrayList<Point> localSearch(ArrayList<Point> firstSolution, ArrayList<Point> points, int edgeThreshold) {
		/*
		 * 1.找到separateur，加入结果 2.分为左右两边，分别greedy之后加入 3.最后的结果进行删减（先一个个减，二代1）
		 * 
		 */

		ArrayList<Point> current = removeDuplicates(firstSolution);
		ArrayList<Point> separateur = getMiniSparateur(points, edgeThreshold);
		ArrayList<Point> result = removeDuplicates(separateur);
		while (!isValid(result, points, edgeThreshold)) {
			ArrayList<Point> gauch = getGaucheX(points, separateur, edgeThreshold);
			ArrayList<Point> droit = getDroitX(points, separateur, edgeThreshold);
			ArrayList<Point> gauchcan = greedy(gauch, edgeThreshold);
			gauchcan = deleteOne(gauch, gauchcan, edgeThreshold);
			do {
				current = gauchcan;
				gauchcan = remove2add1(gauchcan, gauch, edgeThreshold);
			} while (score(current) > score(gauchcan));// 当current的size > ne
			result.addAll(current);

			ArrayList<Point> droitcan = greedy(droit, edgeThreshold);
			droitcan = deleteOne(droit, droitcan, edgeThreshold);
			do {
				current = droitcan;
				droitcan = remove2add1(droitcan, droit, edgeThreshold);
			} while (score(current) > score(droitcan));// 当current的size > ne

			result.addAll(droitcan);
		}
		ArrayList<Point> can = (ArrayList<Point>) result.clone();
		return can;

		/*
		 * // firstSolution = solution of greedy ArrayList<Point> current =
		 * removeDuplicates(firstSolution); // next = 去掉重复之后的 firstSolution
		 * ArrayList<Point> next = (ArrayList<Point>) current.clone();
		 * System.out.println("LS. First sol(Solution of greedy): " + current.size());
		 * 
		 * do { current = next; next = remove2add1(current, points, edgeThreshold);
		 * System.out.println("LS. Current sol: " + current.size() +
		 * ". Found next sol: " + next.size()); } while (score(current) >
		 * score(next));// 当current的size > next的size的时候，current = next;
		 * 
		 * System.out.println("LS. Last sol: " + current.size()); return next;
		 */
	}

	public ArrayList<Point> calculPart(ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> separateur = getMiniSparateur(points, edgeThreshold);
		ArrayList<Point> result = (ArrayList<Point>) separateur.clone();
		while (!isValid(result, points, edgeThreshold)) {
			ArrayList<Point> gauch = getGaucheX(points, separateur, edgeThreshold);
			ArrayList<Point> droit = getDroitX(points, separateur, edgeThreshold);
			result.addAll(greedy(gauch, edgeThreshold));
			result.addAll(greedy(droit, edgeThreshold));
		}

		return result;
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

	private ArrayList<Point> remove2add1(ArrayList<Point> candidate, ArrayList<Point> points, int edgeThreshold) {
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

	

	private ArrayList<Point> remove3add2(ArrayList<Point> candidate, ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> test = removeDuplicates(candidate);
		long seed = System.nanoTime();
		Collections.shuffle(test, new Random(seed));
		ArrayList<Point> rest = removeDuplicates(points);
		//ArrayList<Point> rest = (ArrayList<Point>) points.clone();
		rest.removeAll(test); 
		for (int i = 0; i < test.size(); i++) {
			for (int j = i + 1; j < test.size(); j++) {
				for (int m = j + 1; m < test.size(); m++) {
					Point z = test.remove(m);
					Point q = test.remove(j);
					Point p = test.remove(i);
					if(z.distance(q)>5*edgeThreshold && z.distance(p)>5*edgeThreshold && p.distance(q)>5*edgeThreshold) {break;}
						for (int r = 0; r < rest.size(); r++) {
							for (int k = r + 1; k < rest.size(); k++) {	
								if((z.distance(rest.get(r))<edgeThreshold||z.distance(rest.get(k))<edgeThreshold) && (q.distance(rest.get(r))<edgeThreshold||q.distance(rest.get(k))<edgeThreshold) && (p.distance(rest.get(r))<edgeThreshold||p.distance(rest.get(k))<edgeThreshold))
								{
									test.add(rest.get(r));
									test.add(rest.get(k));
									if (isValid(test, points, edgeThreshold))
										return test;
									test.remove(rest.get(r));
									test.remove(rest.get(k));	
								}
														
							}
						}
					
					test.add(i, p);
					test.add(j, q);
					test.add(m, z);

				}
			}

		}
		return candidate;
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
				while ((line = input.readLine()) != null && count < 100) {
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
		//for (int k = 0; k < 1; k++) {
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
					//maxP++;
				}
			}
		//}

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

	private int getMaxX(ArrayList<Point> pointsIn) {
		int Max_x = 0;
		for (Point p : pointsIn) {
			if (p.x > Max_x)
				Max_x = p.x;
		}
		return Max_x;
	}

	private int getMinX(ArrayList<Point> pointsIn) {
		int Min_x = 10000;
		for (Point p : pointsIn) {
			if (p.x < Min_x)
				Min_x = p.x;
		}
		return Min_x;
	}

	private int getMaxY(ArrayList<Point> pointsIn) {
		int Max_y = 0;
		for (Point p : pointsIn) {
			if (p.x > Max_y)
				Max_y = p.y;
		}
		return Max_y;
	}

	private int getMinY(ArrayList<Point> pointsIn) {
		int Min_y = 100000;
		for (Point p : pointsIn) {
			if (p.y < Min_y)
				Min_y = p.y;
		}
		return Min_y;
	}

	private ArrayList<Point> getGaucheX(ArrayList<Point> pointsIn, ArrayList<Point> separteur, int edgeThreshold) {
		ArrayList<Point> reste = (ArrayList<Point>) pointsIn.clone();
		for (Point p : separteur) {
			reste.removeAll(getNeighbors(p, reste, edgeThreshold));
		}
		reste.removeAll(separteur);
		int min = getMinX(separteur);
		ArrayList<Point> candidat = (ArrayList<Point>) reste.clone();
		for (Point point : reste) {
			if (point.x > min) {
				candidat.remove(point);
			}
		}
		System.out.println("Cherche des points a gauches,nombre = " + candidat.size());
		return candidat;
	}

	private ArrayList<Point> getDroitX(ArrayList<Point> pointsIn, ArrayList<Point> separteur, int edgeThreshold) {
		ArrayList<Point> reste = (ArrayList<Point>) pointsIn.clone();
		for (Point p : separteur) {
			reste.removeAll(getNeighbors(p, reste, edgeThreshold));
		}
		reste.removeAll(separteur);
		int max = getMaxX(separteur);
		ArrayList<Point> candidat = (ArrayList<Point>) reste.clone();
		for (Point p : reste) {
			if (p.x < max) {
				candidat.remove(p);
			}
		}
		System.out.println("Cherche des points a droit,nombre = " + candidat.size());
		return candidat;
	}

	private ArrayList<Point> getSeparateurParX(ArrayList<Point> pointsIn, int edgeThreshold, int Center) {
		ArrayList<Point> separteur = new ArrayList<Point>();
		ArrayList<Point> candidat = new ArrayList<Point>();
		ArrayList<Point> reste = (ArrayList<Point>) pointsIn.clone();

		for (Point p : pointsIn) {
			if (p.x >= Center - edgeThreshold / 2 && p.x <= Center + edgeThreshold / 2) {
				separteur.add(p);
				reste.remove(p);
				reste.removeAll(getNeighbors(p, pointsIn, edgeThreshold));
			}
		}
		return separteur;
	}

	private ArrayList<Point> getSeparateurParY(ArrayList<Point> pointsIn, int Max_y, int Min_y, int edgeThreshold,
			int i) {
		ArrayList<Point> separteur = new ArrayList<Point>();
		ArrayList<Point> candidat = new ArrayList<Point>();
		ArrayList<Point> reste = (ArrayList<Point>) pointsIn.clone();
		int Pilot_y = Min_y + edgeThreshold * (i + 1);
		for (Point p : pointsIn) {
			if (p.x >= Pilot_y - edgeThreshold / 2 && p.x <= Pilot_y + edgeThreshold / 2) {
				separteur.add(p);
				reste.remove(p);
			}
		}
		return separteur;
	}

	private ArrayList<Point> getMiniSparateur(ArrayList<Point> pointsIn, int edgeThreshold) {
		ArrayList<Point> separateur = (ArrayList<Point>) pointsIn.clone();
		ArrayList<Point> candidat = new ArrayList<Point>();
		int count = 0;
		int Max_x, Min_x, Max_y, Min_y, Pilot_x;
		Max_x = getMaxX(pointsIn);
		Min_x = getMinX(pointsIn);
//		Max_y = getMaxY(pointsIn);
//		Min_y = getMinY(pointsIn);
		Pilot_x = (Max_x + Min_x) / 2;

		// System.out.println("Max_x = "+Max_x + "; Min_x = " +Min_x + "; Pilot_x = " +
		// Pilot_x);
		// 向右走
		while (Pilot_x + edgeThreshold / 2 < Max_x) {
			candidat = getSeparateurParX(pointsIn, edgeThreshold, Pilot_x);
			count++;
			if (score(separateur) > score(candidat)) {
				separateur = (ArrayList<Point>) candidat.clone();
			}
			Pilot_x += count * edgeThreshold;
		}

		// 向左走
		count = 0;
		Pilot_x = (Max_x + Min_x) / 2;
		while (Pilot_x - edgeThreshold / 2 > Min_x) {
			candidat = getSeparateurParX(pointsIn, edgeThreshold, Pilot_x);
			count++;
			if (score(separateur) > score(candidat)) {
				separateur = (ArrayList<Point>) candidat.clone();
			}
			Pilot_x -= count * edgeThreshold;
		}

//		while (Min_y + count * edgeThreshold < Max_y) {
//			candidat = getSeparateurParY(pointsIn, edgeThreshold, Max_y, Min_y, count);
//			count++;
//			if (score(separateur) > score(candidat)) {
//				separateur = (ArrayList<Point>) candidat.clone();
//			}
//		}

		return separateur;
	}

	public static void main(String arg[]) throws FileNotFoundException {

		ArrayList<Point> origine = new ArrayList<Point>();
		ArrayList<Point> separateur = new ArrayList<Point>();
		DefaultTeam defaultTeam = new DefaultTeam();
		origine = defaultTeam.readFromFile("input.points");

//		int count = 1;
//		for(Point p:origine) {
//			System.out.println(count + "eme: (" + p.x + "," + p.y + ")");
//			count++;
//		}
/*
		separateur = defaultTeam.getMiniSparateur(origine, 100);
		int count = 1;
		for (Point p : separateur) {
			System.out.println(count + "eme: (" + p.x + "," + p.y + ")");
			count++;
		}
		ArrayList<Point> gauch = new ArrayList<Point>();
		gauch = defaultTeam.getGaucheX(origine, separateur, 100);
		ArrayList<Point> droit = new ArrayList<Point>();
		droit = defaultTeam.getDroitX(origine, separateur, 100);

		System.out.println("gauch:");
		for (Point p : gauch) {
			System.out.print("(" + p.x + "," + p.y + ")");
			count++;
		}
		System.out.println("");
		System.out.println("droit:");
		for (Point p : droit) {
			System.out.print("(" + p.x + "," + p.y + ")");
			count++;
		}*/
		supportGUI.FramedGUI fram = new FramedGUI(1200, 800, null, 0, 100, false);
		fram.drawPoints(origine, origine);

	}
}
