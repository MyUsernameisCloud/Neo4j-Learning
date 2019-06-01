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

/**
 * Hello world!
 *
 */
public class App {
    private static void registerShutdownHook(final GraphDatabaseService graphDB){
        // Registers a shutdown hook for the Neo4j instance so that it shuts down nicely
        // when the VM exits (even if you "Ctrl-C" the running example before it's completed)
        /*为了确保neo4j数据库的正确关闭，我们可以添加一个关闭钩子方法 registerShutdownHook。
         *这个方法的意思就是在jvm中增加一个关闭的钩子，
         *当jvm关闭的时候，会执行系统中已经设置的所有通过方法addShutdownHook添加的钩子，
         *当系统执行完这些钩子后，jvm才会关闭。
         *所以这些钩子可以在jvm关闭的时候进行内存清理、对象销毁等操作。*/
        Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    public void run() {
                        //Shutdown the Database
                        System.out.println("Server is shutting down");
                        graphDB.shutdown();
                    }
                }
        );
    }

    public static void main(String[] args) {
          boolean f = false;
        //指定 Neo4j 存储路径
        File file = new File("C:\\MyApplication\\Neo4j\\neo4j-community-3.4.5\\data\\databases\\graph.db");
        //Create a new Object of Graph Database
        GraphDatabaseService graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(file);
        System.out.println("Server is up and Running");
        
        new Search().search(graphDB);
        //new Searchmovies().searchmovies(graphDB);

        if (f){
        try(Transaction tx = graphDB.beginTx()){
            /**
             * 新增User节点
             * 添加Lable以区分节点类型
             * 每个节点设置name属性
             */
           
            Node user1 = graphDB.createNode(MyLabels.USERS);
            user1.setProperty("name", "John Johnson");

            Node user2 = graphDB.createNode(MyLabels.USERS);
            user2.setProperty("name", "Kate Smith");

            Node user3 = graphDB.createNode(MyLabels.USERS);
            user3.setProperty("name", "Jack Jeffries");
            /**
             * 为user1添加Friend关系
             * 注：Neo4j的关系是有向的箭头，正常来讲Friend关系应该是双向的，
             * 此处为了简单起见，将关系定义成单向的，不会影响后期的查询
             */
            user1.createRelationshipTo(user2,MyRelationshipTypes.IS_FRIEND_OF);
            user1.createRelationshipTo(user3,MyRelationshipTypes.IS_FRIEND_OF);
            /**
             * 新增Movie节点
             * 添加Lable以区分节点类型
             * 每个节点设置name属性
             */
            Node movie1 = graphDB.createNode(MyLabels.MOVIES);
            movie1.setProperty("name", "Fargo");

            Node movie2 = graphDB.createNode(MyLabels.MOVIES);
            movie2.setProperty("name", "Alien");

            Node movie3 = graphDB.createNode(MyLabels.MOVIES);
            movie3.setProperty("name", "Heat");
            /**
             * 为User节点和Movie节点之间添加HAS_SEEN关系, HAS_SEEN关系设置stars属性
             */
            Relationship relationship1 = user1.createRelationshipTo(movie1, MyRelationshipTypes.HAS_SEEN);
            relationship1.setProperty("stars", 5);
            Relationship relationship2 = user2.createRelationshipTo(movie3, MyRelationshipTypes.HAS_SEEN);
            relationship2.setProperty("stars", 3);
            Relationship relationship6 = user2.createRelationshipTo(movie2, MyRelationshipTypes.HAS_SEEN);
            relationship6.setProperty("stars", 6);
            Relationship relationship3 = user3.createRelationshipTo(movie1, MyRelationshipTypes.HAS_SEEN);
            relationship3.setProperty("stars", 4);
            Relationship relationship4 = user3.createRelationshipTo(movie2, MyRelationshipTypes.HAS_SEEN);
            relationship4.setProperty("stars", 5);

            tx.success();
            System.out.println("Done successfully");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            graphDB.shutdown();    //关闭数据库
        }
     }
        //Register a Shutdown Hook
        registerShutdownHook(graphDB);
    }
}

/**
 * Label类型枚举类
 */
enum MyLabels implements Label {
    MOVIES, USERS
}

/**
 * 关系类型枚举类
 */
enum MyRelationshipTypes implements RelationshipType {
    IS_FRIEND_OF, HAS_SEEN
}



        /*
        File file1 = new File("C:\\MyApplication\\Neo4j\\neo4j-community-3.4.5\\data\\databases\\graph.db");
        // Create a new Object of Graph Database
        GraphDatabaseService graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(file1);
        System.out.println("Server is up and Running");

        try {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            // we can load a local ontology file
            File file = new File(
                    "C:\\Users\\lenovo\\Desktop\\Thing\\NFR ontologies\\NFR_demo\\Non_functional_requirements.owl");
            // Now load the local copy
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file);
            // We can always obtain the location where an ontology was loaded from
            IRI documentIRI = manager.getOntologyDocumentIRI(ontology);
            System.out.println(" from:" + documentIRI);
            // System.out.println("loaded ontology: "+ ontology);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("error");
        }

        OWLSignature ontology;
        OWLReasoner reasoner = new Reasoner(ontology);
         if (!reasoner.isConsistent()) {
              //logger.error("Ontology is inconsistent");
              //throw your exception of choice here
              throw new Exception("Ontology is inconsistent");
         }

        Transaction tx = graphDB.beginTx();
         try {
             Node thingNode = getOrCreateNodeWithUniqueFactory("owl:Thing", graphDB);
             for (OWLClass c :ontology.getClassesInSignature(true)) {
                 String classString = c.toString();
                 if (classString.contains("#")) {
                      classString = classString.substring(
                      classString.indexOf("#")+1,classString.lastIndexOf(">"));
                 }
                      Node classNode = getOrCreateNodeWithUniqueFactory(classString, graphDB);
                      NodeSet<OWLClass> superclasses = reasoner.getSuperClasses(c, true);
                      if (superclasses.isEmpty()) {
                           classNode.createRelationshipTo(thingNode,
                           DynamicRelationshipType.withName("isA"));
                           } else {
                           for (org.semanticweb.owlapi.reasoner.Node<OWLClass>
                           parentOWLNode: superclasses) {
                                OWLClassExpression parent =
                                parentOWLNode.getRepresentativeElement();
                                String parentString = parent.toString();
                                if (parentString.contains("#")) {
                                     parentString = parentString.substring(
                                     parentString.indexOf("#")+1,
                                     parentString.lastIndexOf(">"));
                                }
                                Node parentNode =
                                getOrCreateNodeWithUniqueFactory(parentString, graphDB);
                                classNode.createRelationshipTo(parentNode,
                                    DynamicRelationshipType.withName("isA"));
                           }
                      }
                      for (org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual> in
                              : reasoner.getInstances(c, true)) {
                              OWLNamedIndividual i = in.getRepresentativeElement();
                              String indString = i.toString();
                              if (indString.contains("#")) {
                                   indString = indString.substring(
                                        indString.indexOf("#")+1,indString.lastIndexOf(">"));
                              }
                              Node individualNode =
                              getOrCreateNodeWithUniqueFactory(indString, graphDB);
                              individualNode.createRelationshipTo(classNode,
                              DynamicRelationshipType.withName("isA"));
                              for (OWLObjectPropertyExpression objectProperty:
                                  ontology.getObjectPropertiesInSignature()) {
                                       for
                                       (org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual>
                                       object: reasoner.getObjectPropertyValues(i,
                                       objectProperty)) {
                                            String reltype = objectProperty.toString();
                                            reltype = reltype.substring(reltype.indexOf("#")+1,
                                            reltype.lastIndexOf(">"));
                                            String s =
                                            object.getRepresentativeElement().toString();
                                            s = s.substring(s.indexOf("#")+1,
                                            s.lastIndexOf(">"));
                                            Node objectNode =
                                            getOrCreateNodeWithUniqueFactory(s, graphDB);
                                            individualNode.createRelationshipTo(objectNode,
                                            DynamicRelationshipType.withName(reltype));
                                       }
                                 }
                                 for (OWLDataPropertyExpression dataProperty:
                                 ontology.getDataPropertiesInSignature()) {
                                       for (OWLLiteral object: reasoner.getDataPropertyValues(
                                       i, dataProperty.asOWLDataProperty())) {
                                            String reltype =
                                            dataProperty.asOWLDataProperty().toString();
                                            reltype = reltype.substring(reltype.indexOf("#")+1, 
                                            reltype.lastIndexOf(">"));
                                            String s = object.toString();
                                            individualNode.setProperty(reltype, s);
                                       }
                                  }
                             }
                        }
                        tx.success();
                   } finally {
                        tx.finish();
                   }

        System.out.println( "Hello World!" );

        Node thingNode = getOrCreateNodeWithUniqueFactory("owl:Thing", graphDB);
        
    }


    private static Node getOrCreateNodeWithUniqueFactory(String nodeName,
            GraphDatabaseService graphDb) {
        UniqueFactory<Node> factory = new UniqueFactory.UniqueNodeFactory(
                graphDb, "index") {
            @Override
            protected void initialize(Node created,
                    Map<String, Object> properties) {
                created.setProperty("name", properties.get("name"));
            }
        };

        return factory.getOrCreate("name", nodeName);
    }

private void importOntology(OWLOntology ontology) throws Exception {
    OWLReasoner reasoner = new Reasoner(ontology);
         if (!reasoner.isConsistent()) {
              logger.error("Ontology is inconsistent");
              //throw your exception of choice here
              throw new Exception("Ontology is inconsistent");
         }
         Transaction tx = db.beginTx();
         try {
             Node thingNode = getOrCreateNodeWithUniqueFactory("owl:Thing");
             for (OWLClass c :ontology.getClassesInSignature(true)) {
                 String classString = c.toString();
                 if (classString.contains("#")) {
                      classString = classString.substring(
                      classString.indexOf("#")+1,classString.lastIndexOf(">"));
                 }
                      Node classNode = getOrCreateNodeWithUniqueFactory(classString);
                      NodeSet<OWLClass> superclasses = reasoner.getSuperClasses(c, true);
                      if (superclasses.isEmpty()) {
                           classNode.createRelationshipTo(thingNode,
                           DynamicRelationshipType.withName("isA"));
                           } else {
                           for (org.semanticweb.owlapi.reasoner.Node<OWLClass>
                           parentOWLNode: superclasses) {
                                OWLClassExpression parent =
                                parentOWLNode.getRepresentativeElement();
                                String parentString = parent.toString();
                                if (parentString.contains("#")) {
                                     parentString = parentString.substring(
                                     parentString.indexOf("#")+1,
                                     parentString.lastIndexOf(">"));
                                }
                                Node parentNode =
                                getOrCreateNodeWithUniqueFactory(parentString);
                                classNode.createRelationshipTo(parentNode,
                                    DynamicRelationshipType.withName("isA"));
                           }
                      }
                      for (org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual> in
                              : reasoner.getInstances(c, true)) {
                              OWLNamedIndividual i = in.getRepresentativeElement();
                              String indString = i.toString();
                              if (indString.contains("#")) {
                                   indString = indString.substring(
                                        indString.indexOf("#")+1,indString.lastIndexOf(">"));
                              }
                              Node individualNode =
                              getOrCreateNodeWithUniqueFactory(indString);
                              individualNode.createRelationshipTo(classNode,
                              DynamicRelationshipType.withName("isA"));
                              for (OWLObjectPropertyExpression objectProperty:
                                  ontology.getObjectPropertiesInSignature()) {
                                       for
                                       (org.semanticweb.owlapi.reasoner.Node<OWLNamedIndividual>
                                       object: reasoner.getObjectPropertyValues(i,
                                       objectProperty)) {
                                            String reltype = objectProperty.toString();
                                            reltype = reltype.substring(reltype.indexOf("#")+1,
                                            reltype.lastIndexOf(">"));
                                            String s =
                                            object.getRepresentativeElement().toString();
                                            s = s.substring(s.indexOf("#")+1,
                                            s.lastIndexOf(">"));
                                            Node objectNode =
                                            getOrCreateNodeWithUniqueFactory(s, null);
                                            individualNode.createRelationshipTo(objectNode,
                                            DynamicRelationshipType.withName(reltype));
                                       }
                                 }
                                 for (OWLDataPropertyExpression dataProperty:
                                 ontology.getDataPropertiesInSignature()) {
                                       for (OWLLiteral object: reasoner.getDataPropertyValues(
                                       i, dataProperty.asOWLDataProperty())) {
                                            String reltype =
                                            dataProperty.asOWLDataProperty().toString();
                                            reltype = reltype.substring(reltype.indexOf("#")+1, 
                                            reltype.lastIndexOf(">"));
                                            String s = object.toString();
                                            individualNode.setProperty(reltype, s);
                                       }
                                  }
                             }
                        }
                        tx.success();
                   } finally {
                        tx.finish();
                   }
              }*/

