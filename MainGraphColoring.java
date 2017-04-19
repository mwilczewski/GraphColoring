import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class MainGraphColoring {
	
	/**
	* @author mWilczewski
	*/ 
	
	static int n = 2;
	static int allColors=0;
	static int[][] adj;
	static int[] nOfNeighbors;
	static Node[][] graph;
	static int solutions = 0;
	static int movements = 0;
	static ArrayList<Integer> colorsList2 = new ArrayList<Integer>();
	static int which;
	static int[] tabH3W = new int[n*n];
	private static HashMap <Integer, Integer> tabH3K = new HashMap<Integer, Integer>();
	
	static PrintWriter writer;
	
	/** Shows colors in graph */
	public static void show(Node[][] graph){
		for(int x = 0; x< n; x++){
			for(int y = 0; y< n; y++){
				System.out.print(graph[x][y].color + "  ");
			    writer.print(graph[x][y].color+1 + ";");
			}
			System.out.println("");
		}
		System.out.println("");
	}
	/** Shows an adjacency matrix */
	public static void showAdj(int[][] adj){
		for(int x = 0; x< n*n; x++){
			for(int y = 0; y< n*n; y++){
				System.out.print(adj[x][y]);
			}
			System.out.println("");
		}
	}
	public static void showNON(int[] nON){
		for(int x = 0; x< n*n; x++){
			System.out.print(nON[x]);
		}
	}
	/**
	* Copies a graph
	* @param Graph
	* @return Graph copy
	*/
	public static Node[][] copy(Node[][] graph){
		Node[][] graph2 = new Node[n][n];
		int indx = 0;
		for(int x = 0; x< n; x++){
			for(int y = 0; y< n; y++){		
				graph2[x][y] = new Node(indx);
				indx+=1;
				graph2[x][y].color = new Integer(graph[x][y].color);
			}
		}
		return graph2;
	}
	/** Creates an adjacency matrix for grid type graph */
	public static int[][] createAdj(Node[][] graph){
		int[][] adj = new int[n*n][n*n];
		for(int nd = 0; nd< n*n ; nd++){
			
			for(int x = 0; x< n ; x++){
				for(int y = 0; y< n ; y++){
					if(nd == graph[x][y].indx)
						adj[nd][graph[x][y].indx]=0;
					if(nd == (graph[x][y].indx)+n)
						adj[nd][graph[x][y].indx]=1;	
					if(nd == (graph[x][y].indx)-n)
						adj[nd][graph[x][y].indx]=1;	
					
					if(y!=0){
						if(nd == (graph[x][y].indx)-1)
							adj[nd][graph[x][y].indx]=1;	
					}
					if(y!=n-1){
						if(nd == (graph[x][y].indx)+1)
							adj[nd][graph[x][y].indx]=1;	
					}
				}					
			}

		}
		
		return adj;
	}
	/** Creates an adjacency matrix for grid type graph */
	public static int[] createNOfNeighbors(Node[][] graph){
		int[] nOfNeighbors = new int[n*n];
		for(int x= 0; x<n*n;x++){
			int number=0;
			for(int y= 0; y<n*n;y++){
				if(adj[x][y]==1){
					number++;
				}
			}
			nOfNeighbors[x]=number;
		}
		return nOfNeighbors;
	}
	public static Node get(int i){
		int xx = 0;
		int yy = 0;
		for(int x = 0; x< n; x++){
			for(int y = 0; y< n; y++){
				if(graph[x][y].indx == i){	
					xx = x;
					yy = y;
				}
			}
		}
		return graph[xx][yy];
	}
	/** Resets everything to run another algorithms */
	public static void reset(){
		for(int x = 0; x< n; x++){
			for(int y = 0; y< n; y++){
				graph[x][y].color = -1;
			}
		}
		solutions = 0;
		movements = 0;
		colorsList2.clear();
		for(int i = 0 ; i< allColors ; i++){
			colorsList2.add(i);
		}
	}
	/**
	* This method checks if it's safe to color the node (no neighbors with the same color)
	* @param Node to color
	* @return Whether it's safe or not
	*/
	public static boolean isSafe(int k){
		for (int i = 0 ; i< n*n;i++){
			if(adj[k][i]==1 && get(i).color==get(k).color){
				return false;
			}
		}
		return true;
	}
	
	public static int heuristic1(){
		int bestIndex = 0;
		int bestNumber = 0;
		for(int nod=0; nod<n*n; nod++){
			if(get(nod).color == -1){
				if(nOfNeighbors[nod]>bestNumber){
					bestNumber = nOfNeighbors[nod];
					bestIndex = nod;
				}
			}
		}
		return bestIndex;
	}
	public static ArrayList<Integer> heuristic2(int k){
		ArrayList<Integer> colorsH2= new ArrayList<Integer>();
		tabH3K.clear();
		
		for (int c =0 ; c < allColors ; c++) {
			for (int x = 0 ; x < tabH3W.length ; ++x) {
				tabH3W[x] = 0;
			}	
			get(k).color = c;
			for (int nod=0; nod<n*n ; nod++) {
				if (get(nod).color == 0) {
					for (int cc=0; cc < allColors; cc++) {
						get(nod).color=cc;
						if (isSafe (nod)) {
							tabH3W [nod]++;
						}
						get(nod).color = 0;
					}
				}
			}
			int suma = 0;
			for (int x = 0 ; x < tabH3W.length ; ++x) {
				suma += tabH3W[x];
			}
			tabH3K.put (c, suma);
		}

		while (!tabH3K.isEmpty()) {
			int bestKolor = 0, bestDziedzina = 0;
			for (int kolor : tabH3K.keySet()) {
				if (tabH3K.get(kolor) >= bestDziedzina) {
					bestDziedzina = tabH3K.get(kolor);
					bestKolor = kolor;
				}
			}
			colorsH2.add (bestKolor);
			tabH3K.remove (bestKolor);
		}
		get(k).color =0;

		return colorsH2;
	}
	/**
	* This method implements recursive backtracking algorithm and shows all solutions
	* @param Graph
	* @param Node (Should start from 0)
	* @return Last solution with colored graph
	*/
	public static void backtracking(int k){		
		for(int c = 0; c< allColors ; c++){
			get(k).color=c;	
			movements++;
			if(isSafe(k)){				
				if(k+1<n*n){
					backtracking(k+1);
					get(k+1).color = -1;
				}
				else{
					//show(graph);
					solutions+=1;
					//writer.println("");
				}
			}
		}
	}
	/** backtracking with heuristic 1 */
	public static void backtrackingH1(int k){
		int v = heuristic1();
		for(int c = 0; c< allColors ; c++){
			get(v).color=c;	
			movements++;
			if(isSafe(v)){				
				if(k+1<n*n){
					backtrackingH1(k+1);
					get(which).color = -1;
				}
				else{
					//show(graph);
					solutions+=1;
					//writer.println("");
				}
			}
		}
		 which =v;
	}
	/** Backtracking with heuristic 2 */
	public static void backtrackingH2(int k){	
		ArrayList<Integer> colorsH2 = heuristic2(k);
		for (int c : colorsH2) {
			get(k).color=c;	
			movements++;
			if(isSafe(k)){				
				if(k+1<n*n){
					backtrackingH2(k+1);
					get(k+1).color = -1;
				}
				else{
					//show(graph);
					solutions+=1;
					//writer.println("");
				}
			}
		}
	}
	/**
	* This method implements recursive forward checking algorithm and shows all solutions
	* @param Graph
	* @param Node (Should start from 0)
	* @return Last solution with colored graph
	*/
	public static ArrayList<Integer> predict(int k){
		colorsList2 = new ArrayList<Integer>();
		for (int c =0 ; c < allColors; c++) {
			get(k).color=c;	
			if(isSafe(k)){
				colorsList2.add(c);
			}
		}
		get(k).color = -1;
		return colorsList2;
	}
	public static void forward(int k, ArrayList<Integer> colorsList2){
		for(int c : colorsList2){
			get(k).color=c;	
			movements++;		
				if(k+1 == n*n){
					solutions+=1;
					//show(graph);
					//writer.println("");	
				}
				else{
					forward(k+1, predict(k+1));
					get(k+1).color = -1;
				}
		}
	}
	public static void forwardH1(int k, ArrayList<Integer> colorsList2){
		int v = heuristic1();
		for(int c : colorsList2){
			get(v).color=c;	
			movements++;		
				if(k+1 == n*n){
					solutions+=1;
					//show(graph);
					//writer.println("");	
				}
				else{
					forwardH1(k+1, predict(heuristic1()));
					get(which).color = -1;
				}
		}
		 which =v;
	}
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		writer = new PrintWriter("C:\\Users\\W\\Desktop\\siTest2.txt", "UTF-8");
		// Number of colors 10803674 8008063
		if(n%2==0)
			allColors=n*2;
		else
			allColors=n*2+1;
		
		for(int i = 0 ; i< allColors ; i++){
			colorsList2.add(i);
		}
		// Creates a graph
		graph = new Node[n][n];
		int indx = 0;
		for(int x = 0; x< n; x++){
			for(int y = 0; y< n; y++){
				graph[x][y] = new Node(indx);
				indx++;
			}
		}
		// creates an adjacency tab
		adj = createAdj(graph);
		show(graph);
		showAdj(adj);
		nOfNeighbors=createNOfNeighbors(graph);
		//long start = System.nanoTime();
		backtracking(0);
		System.out.println("\nBacktracking");
		System.out.println("Number of solutions: " +solutions);
		System.out.println("Number of movements: " +movements);
		reset();
		backtrackingH1(0);
		System.out.println("\nBacktracking heuristic 1");
		System.out.println("Number of solutions: " +solutions);
		System.out.println("Number of movements: " +movements);
		reset();
		backtrackingH2(0);
		System.out.println("\nBacktracking heuristic 2");
		System.out.println("Number of solutions: " +solutions);
		System.out.println("Number of movements: " +movements);
		reset();
		forward(0, colorsList2);
		System.out.println("\nForward checking");
		System.out.println("Number of solutions: " +solutions);
		System.out.println("Number of movements: " +movements);
		reset();
		forwardH1(0, colorsList2);
		System.out.println("\nForward checking heuristic 1");
		System.out.println("Number of solutions: " +solutions);
		System.out.println("Number of movements: " +movements);
		reset();
		//long stop = System.nanoTime();
		//System.out.println("Czas: " + (double)(stop-start)/1000000000);
		
		
		writer.close();
	}

}