# Kruskal_with_heuristics

The program is presented with a graph and a number of querrys. Each querry presents an edge that MUST be in the "minimum" spanning tree forcing the program to reevaluate the MST.

Input format example:

The first three numbers represent N(the number of nodes), M(the number of edges) and Q(the number of querrys). They are followed by pairs like nodeI nodeJ costIJ representing the nodes IDs and the weight of the edge between them. Nonetheless, the last Q lines represent the querrys with the required edge ID.

6 8 8

1 2 4

4 1 6

3 1 2

2 3 3

5 2 4

3 4 3

4 5 5

5 6 1

1

2

3

4

5

6

7

8

Output format example:
On the first line there is the total cost of the MST. The last Q lines represent the cost of the "MST" with the required edges in it.

13

14

16

13

13

13

13

14

13
