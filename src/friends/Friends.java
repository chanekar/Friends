package friends;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null or empty array list if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		
		ArrayList<String> bfs = new ArrayList<String>();
		ArrayList<String> chain = new ArrayList<String>();
		Queue<Integer> q = new Queue<Integer>();
		int[] visited = new int[g.members.length];
		Arrays.fill(visited, -1);
		boolean[] marked = new boolean[g.members.length];
		Arrays.fill(marked, false);
		
		// System.out.println(g.map);
 		
		int s1 = -1;
		for (int i = 0; i < g.members.length; i++) {
			String name = g.members[i].name;
			if (name.equals(p1)) s1 = i;
		}
		
		int s2 = -1;
		for (int i = 0; i < g.members.length; i++) {
			String name = g.members[i].name;
			if (name.equals(p2)) s2 = i;
		}
		
		if (s1 == -1 || s2 == -1) return null;
	
		q.enqueue(s1);
		while (!q.isEmpty()) {
			int c = q.dequeue();
			marked[c] = true;
			bfs.add(g.members[c].name);
			
			if (g.members[s2].name.equals(g.members[c].name)) {
				int cur = visited[c];
				chain.add(0, g.members[c].name);
				while (s1 != cur) {
					chain.add(0, g.members[cur].name);
					cur = visited[cur];
				}
				chain.add(0, g.members[s1].name);
				return chain;
			}
			
			if (g.members[c].first == null) return null;
			
			if (marked[g.members[c].first.fnum] == false) {
				q.enqueue(g.members[c].first.fnum);
				marked[g.members[c].first.fnum] = true;
				visited[g.members[c].first.fnum] = c;
			}
			
			Friend ptr = g.members[c].first.next;
			while (ptr != null) {
				if (marked[ptr.fnum] == false) {
					q.enqueue(ptr.fnum);
					visited[ptr.fnum] = c;
				}
				marked[ptr.fnum] = true;
				
				ptr = ptr.next;
			}	
			
		}
		
		return bfs;
	}
	
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null or empty array list if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		ArrayList<Integer> visited = new ArrayList<Integer>();
		while (visited.size() != g.members.length) visited.add(0, -1);
		int[] match = new int[g.members.length];
		Arrays.fill(match, -1);
		int index = 0; 
		Queue<Integer> q = new Queue<Integer>();
		
		ArrayList<String> bfs = new ArrayList<String>();
		
		int s = -1;
		for (int i = 0; i < g.members.length; i++) {
			if (g.members[i].student == true) {
				String name = g.members[i].school; 
				if (name.equals(school)) {
					s = i;
					break;
				}
			}
		}
		
		if (s == -1) return null;
		
		while (visited.contains(-1)) {
			boolean change = false;
			q.enqueue(visited.indexOf(-1));
			while (!q.isEmpty()) {
				int c = q.dequeue();
				bfs.add(g.members[c].name);
				
				if (g.members[c].student == true) {
					if (g.members[c].school.equals(school)) {
						visited.set(c, index);
						change = true;
					} else {
						visited.set(c, -2);
					}
				} else {
					visited.set(c, -2);
				}
				
				if (visited.get(g.members[c].first.fnum) == -1) {
					if (g.members[g.members[c].first.fnum].student && g.members[g.members[c].first.fnum].school.equals(school)) {
						q.enqueue(g.members[c].first.fnum);
						change = true;
					}
				}
						
				Friend ptr = g.members[c].first.next;
				while (ptr != null) {
					if (visited.get(ptr.fnum) == -1) {
						if (g.members[ptr.fnum].student && g.members[ptr.fnum].school.equals(school)) {
							q.enqueue(ptr.fnum);
							change = true;
						}
					}
					
					ptr = ptr.next;
				}	
				
			}
			
			if (change == true) index++;
			
		}

		int i = 0; 
		while (i <= index-1) {
			ArrayList<String> chain = new ArrayList<String>();
			while (visited.contains(i)) {
				chain.add(g.members[visited.indexOf(i)].name);
				visited.set(visited.indexOf(i), -1);
			}
			list.add(i, chain);
			i++;
		}
		
		return list;
		
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null or empty array list if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		
		if (g == null) return null;
		
		ArrayList<String> dfs = new ArrayList<String>();
		ArrayList<String> chain = new ArrayList<String>();
		Stack<Integer> s = new Stack<Integer>();
		ArrayList<Integer> visited = new ArrayList<Integer>();
		while (visited.size() != g.members.length) visited.add(0, -1);
		boolean[] marked = new boolean[g.members.length];
		Arrays.fill(marked, false);
		
		if (g.members.length <= 2) return null;
		
		s.push(0);
		while (visited.contains(-1)) {
			
			//System.out.println(visited.toString());
			//System.out.println();
			if (s.isEmpty()) {
				s.push(visited.indexOf(-1));
				visited.set(visited.indexOf(-1), 0);
			}
			
			while (!s.isEmpty()) {
				int c = s.pop();
				marked[c] = true;
				dfs.add(g.members[c].name);
				
				if (g.members[c].first == null) return null;
				
				if (g.members[c].first.next == null && !chain.contains(g.members[g.members[c].first.fnum].name)) {
					chain.add(g.members[g.members[c].first.fnum].name);
					visited.set(c, 0);
				}
				
				if (marked[g.members[c].first.fnum] == false) {
					s.push(g.members[c].first.fnum);
					marked[g.members[c].first.fnum] = true;
					visited.set(g.members[c].first.fnum, 0);
				}
				
			}
		}
		
		return chain;
		
	}
}

