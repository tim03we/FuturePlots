package tim03we.futureplots.provider.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoAPI {

  private static MongoClient mongoClient;
  
  public static MongoDatabase mongoDatabase;
  
  public static void load(String uri, String database) {
    MongoClientURI mongoClientURI = new MongoClientURI(uri);
    mongoClient = new MongoClient(mongoClientURI);
    mongoDatabase = mongoClient.getDatabase(database);
  }
  
  public static void changeValue(String collection, Document searchDocument, String searchValue, Object newValue) {
    Document found = mongoDatabase.getCollection(collection).find(searchDocument).first();
    Document document1 = new Document(searchValue, newValue);
    Document document2 = new Document("$set", document1);
    mongoDatabase.getCollection(collection).updateOne(found, document2);
  }
}
