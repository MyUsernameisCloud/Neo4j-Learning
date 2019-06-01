package com.nfr;

import java.io.File;
import java.util.ArrayList;
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
import org.semanticweb.owlapi.apibinding.OWLManager;
// import org.semanticweb.HermiT.Reasoner;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Search{
    Result tmp;
    public void search(GraphDatabaseService graphDB){
        try(Transaction tx = graphDB.beginTx()){
            StringBuilder sb = new StringBuilder();
            sb.append("match (a)-[r:nfr__conflictWith]->(b)");
            sb.append("return a");
            Result result = graphDB.execute(sb.toString());
            tmp = result;
            System.out.println(result.toString());
           // System.out.println(result);
            //  遍历结果
            // while(result.hasNext()){

                // get("a")和查询语句的return a 相匹配
              //  Node a = (Node)result.next().get("a");
                //Relationship r = (Relationship)result.next().get("r");
                //Node b = (Node)result.next().get("b");
             //   System.out.println(a.getId()+" : "+ a.getProperty("rdfs__label"));
               /*
                System.out.println(a.getId()+" : "+
                a.getProperty("rdfs__label")+"--"+r.getProperty("rdfs__label")+"-->" + b.getId()+" : "+ b.getProperty("rdfs__label")
                );*/
           // }
            Node n = (Node)tmp.next().get("a");
            Iterable<Relationship> r = n.getRelationships();
                for (Relationship relationship : r){
                    System.out.println("start: "+ relationship.getStartNode().getProperty("rdfs__label")+
                       "---"+ relationship.getType() + "-->" + 
                        "end: "+relationship.getEndNode().getProperty("rdfs__label"));
                } 
            tx.success();
            System.out.println("Done successfully");
        }
        catch(Exception e){ e.printStackTrace(); }
        finally{ graphDB.shutdown(); }
    }
}