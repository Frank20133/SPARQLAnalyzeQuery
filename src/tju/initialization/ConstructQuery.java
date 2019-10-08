package tju.initialization;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;

import java.io.InputStream;
import java.util.Iterator;

public class ConstructQuery {

    String execCONDefaultGraphViaTriples() {
        Dataset dataset = generateCompany();

        String queryString = "CONSTRUCT {?s ?p ?o.} WHERE{?s ?p ?o.}";
        Query query2 = QueryFactory.create(queryString, Syntax.syntaxARQ);
        try (QueryExecution qexec = QueryExecutionFactory
                .create(query2, dataset)) {
            Iterator<Triple> quads = qexec.execConstructTriples();
            PrintUtil.printOut(quads);
        }
        return queryString;
    }

    private Dataset generateCompany() {
        Model company = ModelFactory.createDefaultModel();
        InputStream inY = FileManager.get().open("data/data_final_with_dot.txt");
        if(inY == null)
            throw new IllegalArgumentException("File: data_final.txt not found.");

        company.read(inY,null,"turtle");

        Dataset dataset = DatasetFactory.create();
        dataset.addNamedModel("http://Company", company);
        return dataset;
    }
}
