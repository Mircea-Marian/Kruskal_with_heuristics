import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;


public class Main {

	//Este un nod din Disjoint Data Set.
	static class Node {
        public int rank;
        public int parent;
        public Node( int n ){
            this.rank = 0;
            this.parent = n;
        }
    }

    //Este o legatura si greutatea ei.
    static class EdgeAndWeight {
        public int toVertex;
        public long weight;
        public EdgeAndWeight(int n, long m){
            this.toVertex = n;
            this.weight = m;
        }
        boolean isEqualTo( EdgeAndWeight e ){
        	if( this.toVertex == e.toVertex && this.weight == e.weight )
        		return true;
        	return false;
        }
    }

    //Simple linked list node
    static class SLLnode {
    	public EdgeAndWeight info;
    	SLLnode next;
    	public SLLnode(EdgeAndWeight e){
    		this.next = null;
    		this.info = e;
    	}
    }


    static class Vertex{
        public int level;
        public long dP;//distance to father
        public int parent;//father
        public int ancestor;//superior section ancestor
        public long maxDistToAncestor;//max distance to ancestor
        public SLLnode children;
        public int numberOfChildren;
        char ch;
        public Vertex(){
            this.level = 0;
            this.numberOfChildren = 0;
            this.children = null;
            this.ch = 'a';
            this.dP = -1;
        }
        public void addChild( EdgeAndWeight e){
            if( this.children == null )
            	this.children = new SLLnode(e);
            else{
            	SLLnode n = new SLLnode(e);
            	n.next = this.children;
            	this.children = n;
            }
            this.numberOfChildren ++;
        }
    }

    //o simpla muchie
    static class Edge{
		public int u, v;
		long w;
		public Edge(int u, int v, long w){
				this.u=u;
				this.v=v;
			this.w=w;
		}
	}

    //path compression cu gasirea setului pentru vertex-ul n
    public static int findSetOf(int u, Node[] V){
        if( u != V[u].parent )
            V[u].parent = findSetOf(V[u].parent, V);
        return V[u].parent;
    }

    static class Pair{
    	int u, v;
    	public Pair(int i, int j){
    		if(i < j){
    			this.u = i;
    			this.v = j;
    		} else {
    			this.u = j;
    			this.v = i;
    		}
    	}
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try{
            int i;
            File f = new File("kim.in");
            BufferedReader reader =  new BufferedReader(new FileReader(f));
            int nV, nE, Q;
            try{
                StringTokenizer st = new StringTokenizer(reader.readLine()," ");
                nV = Integer.parseInt(st.nextToken());
                nE = Integer.parseInt(st.nextToken());
                Q = Integer.parseInt(st.nextToken());
                long cost;
                int ur,vr;
                ArrayList<Edge> positionsOfEdges = new ArrayList<>();
                TreeMap<Pair, Long> setOfEdges = new TreeMap<>(new Comparator<Pair>() {
                	public int compare( Pair p1, Pair p2){
                		int rez = p1.u - p2.u;
                		if(rez != 0)
                			return rez;
                		return p1.v - p2.v;
                	}
				});
                Pair p;
                for(i = 0 ; i <  nE ; i++ ){
                    st = new StringTokenizer(reader.readLine()," ");
                    ur = Integer.parseInt(st.nextToken())-1;
                    vr =  Integer.parseInt(st.nextToken()) - 1;
                    cost = Long.parseLong(st.nextToken());
                    p = new Pair(ur, vr);
                    if( !setOfEdges.containsKey(p) )
                    	setOfEdges.put(p, cost);
                    positionsOfEdges.add(new Edge(ur, vr, cost));
                }
                Map.Entry<Pair, Long> en;
                //Se creeaza un set in care se vor adauga muchiile.
                //Acestea vor fii sortate automat in interiorul lui pe
                //masura ce vot fii scoase din "setOfEdges".
                TreeSet<Edge> wSorted = new TreeSet<>(new Comparator<Edge>() {
                	public int compare( Edge e1, Edge e2 ){
                        long rez = e1.w - e2.w;
                        if( rez == 0 )
                            return -1;
                        return (0L<rez)?1:(-1);
                    }
				});
                while( !setOfEdges.isEmpty() ){
                	en = setOfEdges.pollFirstEntry();
                	wSorted.add(new Edge(en.getKey().u, en.getKey().v, en.getValue()));
                }
                cost = 0;
                Node[] V = new Node[nV];
                for(i = 0 ; i < nV ; i++ )
                    V[i] = new Node(i);
                Edge e;
                ArrayList<Vertex> vertexGraph = new ArrayList<>(nV);
                for( i = 0 ; i < nV ; i++ )
                    vertexGraph.add(new Vertex());
                //Implementare Kruskal cu Disjoint Set
                while( !wSorted.isEmpty() ){
                	e = wSorted.pollFirst();
                	setOfEdges.put(new Pair(e.u, e.v), e.w);
                    ur = findSetOf(e.u, V);
                    vr = findSetOf(e.v, V);
                    if( ur != vr ){
                        cost = cost + e.w;
                        vertexGraph.get(e.u).addChild(new EdgeAndWeight(e.v, e.w));
                        vertexGraph.get(e.v).addChild(new EdgeAndWeight( e.u, e.w));
                        //Implementare euristica Union by Rank
                        if( V[ur].rank > V[vr].rank )
                            V[vr].parent = ur;
                        else{
                            V[ur].parent = vr;
                            if( V[ur].rank == V[vr].rank )
                                V[vr].rank++;
                        }
                    }
                }
                //Sortare topologica
                Queue<Integer> q =  new LinkedList<>();
                Vertex u, v;
                u = vertexGraph.get(0);
                u.parent = -1;
                u.dP = -1;
                int j;
                SLLnode iterator;
                long minusCost;
                int depth = 0;
                q.add(0);
                while( !q.isEmpty() ){
                    j = q.poll();
                    u = vertexGraph.get(j);
                    if( depth < u.level )
                    	depth = u.level;
                    iterator = u.children;
                    while( iterator != null ){
                    	v = vertexGraph.get(iterator.info.toVertex);
                    	if( v.ch == 'a' ){
                    		v.parent = j;
                    		v.dP = iterator.info.weight;
                    		v.level = u.level + 1;
                            v.ch = 'g';
                            q.offer(iterator.info.toVertex);
                    	}
                    	iterator = iterator.next;
                    }
                    u.ch = 'n';
                }
                //Preprocesare RMQ
                Stack<Integer> s = new Stack<>();
                s.add(0);
                ur = (int)Math.floor(Math.sqrt(depth));
                while( !s.isEmpty() ){
                	j = s.pop();
                	u = vertexGraph.get(j);
                	if( u.ch == 'a' )
                		continue;
                	u.ch = 'a';
                	if( u.level < ur ){
                		u.ancestor = 0;
                		u.maxDistToAncestor = 0;
                	} else if( u.level % ur == 0 ) {
                		u.ancestor = u.parent;
                		u.maxDistToAncestor = u.dP;
                	} else {
                		v = vertexGraph.get(u.parent);
                		u.ancestor = v.ancestor;
                		u.maxDistToAncestor = ( v.maxDistToAncestor > u.dP )?
                				v.maxDistToAncestor:u.dP;
                	}
                	SLLnode nIterator = u.children;
                	while( nIterator != null ){
                		s.add(nIterator.info.toVertex);
                		nIterator = nIterator.next;
                	}
                }
                File of = new File("kim.out");
                if (!of.exists()) {
                    of.createNewFile();
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter(of));
                writer.write(cost+"\n");
                Vertex uv, vv;
                long mstCost;
                for( i = 0 ; i < Q ; i++ ){
                	e = positionsOfEdges.get(Integer.parseInt(reader.readLine())-1);
                	uv = vertexGraph.get(e.u);
                	vv = vertexGraph.get(e.v);
                	if( e.u != vv.parent && e.v != uv.parent){
                		ur = e.u;
	               		vr = e.v;
	               		minusCost = -1;
	                	while( uv.ancestor != vv.ancestor )
	                		//LCA cu RMQ
	                		if( uv.level > vv.level ){
	                			if( minusCost < uv.maxDistToAncestor )
	                				minusCost = uv.maxDistToAncestor;
	                			ur = uv.ancestor;
	                			uv = vertexGraph.get(ur);
	                		}
	                		else {
	                			if( minusCost < vv.maxDistToAncestor )
	                				minusCost = vv.maxDistToAncestor;
	                			vr = vv.ancestor;
	                			vv = vertexGraph.get(vr);
	                		}
	                	while( ur != vr )
	                		//LCA in interiorul unei sectiuni
	                		if( uv.level > vv.level ){
	                			if( minusCost < uv.dP )
	                				minusCost = uv.dP;
	                			ur = uv.parent;
	                			uv = vertexGraph.get(ur);
	                		} else {
	                			if( minusCost < vv.dP )
	                				minusCost = vv.dP;
	                			vr = vv.parent;
	                			vv = vertexGraph.get(vr);
	                		}
	                	writer.write((cost + e.w - minusCost)+"\n");
                	} else{
                    	mstCost = setOfEdges.get(new Pair(e.u, e.v));
                		writer.write((cost - mstCost + e.w)+"\n");
                	}
                }
                writer.close();
            } catch (IOException e){
                System.err.println("Nu s-a putut citi linia !");
            }
            try{
                reader.close();
            } catch (IOException e){
                System.err.println("Nu s-a putut inchide reader-ul!");
            }
        } catch (FileNotFoundException e){
            System.err.println("Nu s-a gasit fisierul!");
        }
    }
}
