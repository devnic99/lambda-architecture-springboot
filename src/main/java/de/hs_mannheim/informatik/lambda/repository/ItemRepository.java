package de.hs_mannheim.informatik.lambda.repository;

import java.util.List;

import de.hs_mannheim.informatik.lambda.model.DocumentFrequency;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

@Component
public interface ItemRepository extends MongoRepository<DocumentFrequency, String> {

    @Query("{word:'?0'}")
    DocumentFrequency findItemByName(String name);

    @Query(value="{}", fields="{'name' : 1, 'quantity' : 1}")
    List<DocumentFrequency> findAll(String category);
}