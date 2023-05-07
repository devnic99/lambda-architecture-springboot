package de.hs_mannheim.informatik.lambda.repository;

import java.util.List;

import de.hs_mannheim.informatik.lambda.model.WordCount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Component;

@Component
public interface ItemRepository extends MongoRepository<WordCount, String> {

    @Query("{name:'?0'}")
    WordCount findItemByName(String name);

    @Query(value="{category:'?0'}", fields="{'name' : 1, 'quantity' : 1}")
    List<WordCount> findAll(String category);

    public long count();

/*    List<WordCount> findByTitleContaining(String title);
    List<WordCount> findByPublished(boolean published);*/


}
