import org.sbml.libcombine.*;

class printExample
{
  
  static void printMetaDataFor(CombineArchive archive, String location)
  {
    OmexDescription desc = archive.getMetadataForLocation(location);
    if (desc.isEmpty())
    {
      System.out.println(String.format("  no metadata for '%s'",location));
      return;
    }
    System.out.println(String.format("  metadata for '%s':", location));
    System.out.println(String.format("     Created : %s", desc.getCreated().getDateAsString()));
    for (int i = 0; i < desc.getNumModified(); ++i)
    {
      System.out.println(String.format("     Modified : %s", desc.getModified(i).getDateAsString()));
    }
  
    System.out.println(String.format("     # Creators: %d", desc.getNumCreators()));
    for (int i = 0; i < desc.getNumCreators(); ++i)
    {
      VCard creator = desc.getCreator(i);
      System.out.println(String.format("       %s %s", creator.getGivenName(), creator.getFamilyName()));
    }
  
  }
  
  public static void main(String[] args)
  {
    if (args.length < 1)
    {
      System.out.println("usage: printExample archive-file");
      return;
    }
    
    System.loadLibrary("combinej");
    
    CombineArchive archive = new CombineArchive();
    if (!archive.initializeFromArchive(args[0]))
    {
      System.out.println("Invalid Combine Archive");
      return;
    }
    
    printMetaDataFor(archive, ".");
    
    System.out.println(String.format("Num Entries: %d", archive.getNumEntries()));
    
    for (int i = 0; i < archive.getNumEntries(); ++i)
    {
      CaContent entry = archive.getEntry(i);
      
      System.out.println(String.format(" %d: location: %s format: %s", i, entry.getLocation(), entry.getFormat()));
      printMetaDataFor(archive, entry.getLocation());
    
      // the entry could now be extracted via 
      // archive.extractEntry(entry.getLocation(), <filename or folder>)
    
      // or used as string
      // string content = archive.extractEntryToString(entry.getLocation());
    
    }
    
    archive.cleanUp();
  }
}
