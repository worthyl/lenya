package org.apache.lenya.cms.content.flat;
import java.io.File;
import org.apache.lenya.cms.content.Content;
import org.apache.lenya.cms.content.Resource;
/**
 * 
 * @author solprovider
 * @since 1.3
 */
public class FlatContent implements Content {
   File directory;
   FlatIndexer indexer;
   String[] languages = {"en"};
   public FlatContent(File pdirectory, String[] planguages) {
      directory = pdirectory;
      languages = planguages;
      indexer = new FlatIndexer(new File(directory, "index"), this);
      // The next line starts indexing everything in the background when Lenya starts.
      updateIndexes();
   }
   /* Content API */
   public String getIndexFilename(String indexName, String language) {
      return indexer.getIndexFilename(indexName, language);
   }
   /* FlatContent API */
   public String[] getLanguages() {
      return languages;
   }
   public String getMetaURI(String unid, String language, String revision) {
      Resource resource = getResource(unid, language, revision);
      return resource.getMetaURI();
   }
   public String getNewURI(String unid, String language) {
      Resource resource = getResource(unid, language, "edit");
      return resource.getNewURI();
   }
   public FlatRelations getRelations(String structure) {
      return new FlatRelations(new File(directory, "relation" + File.separator + structure + ".xml"));
   }
   public Resource getResource(String unid) {
      return (Resource) new FlatResource(directory, unid);
   }
   public Resource getResource(String unid, String language, String revision) {
      return (Resource) new FlatResource(directory, unid, language, revision);
   }
   public String[] getResources() {
      return (new File(directory, "resource")).list();
   }
   public String getUNID(String structure, String id) {
      FlatRelations relations = getRelations(structure);
      return relations.getUNID(id);
   }
   public String getURI(String unid, String language, String revision) {
      Resource resource = getResource(unid, language, revision);
      return resource.getURI();
   }
   /**
    * Updates Indexes in background.
    */
   void updateIndexes() {
         new Thread((Runnable) indexer).start();
   }
}
