package de.hs_mannheim.informatik.lambda.model;
import java.lang.String;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

import de.hs_mannheim.informatik.lambda.model.DocumentFrequency;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;


@Document(collection = "df")
public class DocumentFrequency{

    @Id
    private String word;
   
    public DocumentFrequency(String word) {
        super();
        this.word = word;
        this.quantity = 1;
    }
   

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

  
}
