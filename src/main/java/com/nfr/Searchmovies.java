package com.nfr;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import scala.util.control.Exception.Catch;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.UniqueFactory;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.semanticweb.owlapi.apibinding.OWLManager;
// import org.semanticweb.HermiT.Reasoner;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Searchmovies{
    public void searchmovies(GraphDatabaseService graphDB){
        try(Transaction tx = graphDB.beginTx()){
            StringBuilder sb = new StringBuilder();
            sb.append("match (a)-[r:nfr__conflictWith]->(b)");
            sb.append("return a");
            Result result = graphDB.execute(sb.toString());
            Node userJohn = (Node)result.next().get("a");
            TraversalDescription td = graphDB.traversalDescription().depthFirst()
            .relationships(MyRelationshipTypes.IS_FRIEND_OF).relationships(MyRelationshipTypes.HAS_SEEN,Direction.OUTGOING).evaluator(Evaluators.atDepth(2));
            
            Traverser traverser = td.traverse(userJohn);
            Iterable<Node> movies = traverser.nodes();
            for (Node m:movies){
                System.out.println("found movies: "+m.getProperty("name"));
            }
            tx.success();
            System.out.println("done");


        }catch(Exception e){ e.printStackTrace(); }
        finally{ graphDB.shutdown(); }
    }

}
enum MyRelationshipTypes implements RelationshipType {
    IS_FRIEND_OF, HAS_SEEN
}