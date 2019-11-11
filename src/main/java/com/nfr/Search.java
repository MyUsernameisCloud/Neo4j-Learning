package com.nfr;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSignature;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import scala.util.control.Exception.Catch;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.UniqueFactory;
import org.neo4j.kernel.impl.core.NodeProxy;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.neo4j.cypher.internal.compatibility.StringInfoLogger;
import org.neo4j.cypher.internal.javacompat.ExecutionEngine;
import org.neo4j.cypher.internal.javacompat.ExecutionResult;
// import org.semanticweb.HermiT.Reasoner;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Search{
    Result tmp;
    public void search(GraphDatabaseService graphDB){
        try(Transaction tx = graphDB.beginTx()){
            StringBuilder sb = new StringBuilder();
            sb.append("match (a)-[r:nfr__conflictWith]->(b)");
            sb.append("return a,r,b");
           // ExecutionEngine engine = new ExecutionEngine(graphDB);
            //ExecutionResult result = engine.execute(sb);
            //System.out.println( result );


           Result result = graphDB.execute(sb.toString());
            tmp = result;
            //String str = result.resultAsString();

            System.out.println(result.toString());
            System.out.println("output 1");


           System.out.println(result);
           System.out.println("output 2");

           //System.out.println(str);
           //System.out.println("output 3");





            // 遍历结果
            while(result.hasNext()){

                // get("a")和查询语句的return a 相匹配
                // Node a = (Node)result.next().get("a");
                // Relationship r = (Relationship)result.next().get("r");
                // Node b = (Node)result.next().get("b");
                // System.out.println(a.getId()+" : "+ a.getProperty("rdfs__label"));
               
                // System.out.println(a.getId()+" : "+
                // a.getProperty("rdfs__label")+"--"+r.getProperty("rdfs__label")+"-->" + b.getId()+" : "+ b.getProperty("rdfs__label")
                // );

                 Map<String, Object> row = result.next();
                 //for ( String key : result.columns() )
                // {
                   //  System.out.printf( "%s = %s%n", key, row.get( key ) );
                 //}
                 
                Iterator<String> iter = row.keySet().iterator();
                Node value;
                while (iter.hasNext()) {
                    String r="" ;
 
                    String key = iter.next();
                    //System.out.println("key:"+ key);

                    if (row.get(key) instanceof Relationship){
                        Relationship relationship = (Relationship)row.get(key);
                        r = relationship.getType().name();
                        System.out.print(" <-- "+ r + " -- ");
                        //System.out.println(relationship.getType().name().getClass());
                        Map<String,Object> it = relationship.getProperties();
                        Iterator<String> i = it.keySet().iterator();
                        while (i.hasNext()) {
                            String k  = i.next();
                           // String value = (String) it.get(k);
                           //System.out.println(relationship.getClass());
                           System.out.println(i); 
                        } 

                       // for (String ts:it.keySet()){
                        //    System.out.println(ts);   
                        //}
                        //System.out.println(row.get(key) instanceof Relationship);  //  output is true
                        continue;
                    }

                    value = (Node)row.get(key);
                    //System.out.println(value.getClass());
                    //Node node = (Node)value;
                    //Iterable<Label> lab = value.getLabels();
                   // for (Label l: lab){
                   //     System.out.println(l.name());
                   // }
                   
                   System.out.print(value.getProperty("rdfs__label"));   // ### !!! getproperty()  的使用。
                   //System.out.println("<-- "+ r + " --");
                   //System.out.println(value.getProperty("rdfs__label")); 
                }
                //break;
                //System.out.println(value.getProperty("rdfs__label"));
                System.out.println(" ");
                //System.out.println("one row over");

            }
          
        //    Node n = (Node)tmp.next().get("a");
        //     Iterable<Relationship> r = n.getRelationships();
        //         for (Relationship relationship : r){
        //             System.out.println("start: "+ relationship.getStartNode().getProperty("rdfs__label")+
        //                "---"+ relationship.getType() + "-->" + 
        //                 "end: "+relationship.getEndNode().getProperty("rdfs__label"));
        //         } 
            tx.success();
            System.out.println("Done successfully");
        }
        catch(Exception e){ e.printStackTrace(); }
        finally{ graphDB.shutdown(); }
    }
}