package de.hs_mannheim.informatik.lambda.data;

import org.springframework.data.annotation.Id;
//import org.springframework.data.annotation.Document;
// import org.springframework.data.mongodb.core.*;

//@Document annotation specifies the name of the MongoDB collection that this class maps to
// @Document("documents")
public class Document {
    // @Id annotation on the id field tells Spring Data MongoDB that this field is
    // the primary key for the document
    @Id
    private String id;

    private String name;

    public Document(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
