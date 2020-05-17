import org.sbml.libcombine.*;

  class createArchiveExample
  {
    public static void main(String[] args)
    {
      if (args.length < 1)
      {
        System.out.println("usage: createArchiveExample sbml-file");
        return;
      }
      
      System.loadLibrary("combinej");
      
      CombineArchive archive = new CombineArchive();
      archive.addFile(
            args[0], // filename
            "./models/model.xml", // target file name
            KnownFormats.lookupFormat("sbml"), // look up identifier for SBML models
            true // mark file as master
            );

      OmexDescription description = new OmexDescription();
      description.setAbout("."); // about the archive itself
      description.setDescription("Simple test archive including one SBML model");
      description.setCreated(OmexDescription.getCurrentDateAndTime());

      VCard creator = new VCard();
      creator.setFamilyName("Bergmann");
      creator.setGivenName("Frank");
      creator.setEmail("fbergman@caltech.edu");
      creator.setOrganization("Caltech");

      description.addCreator(creator);

      archive.addMetadata(".", description);

      archive.writeToFile("out.omex");
    }
  }
