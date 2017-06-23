This is an example Maven project implementing an ImageJ command.

It is intended as an ideal starting point to develop new ImageJ commands
in an IDE of your choice. You can even collaborate with developers using a
different IDE than you.

* In [Eclipse](http://eclipse.org), for example, it is as simple as
  _File&gt;Import...&gt;Existing Maven Project_.

* In [NetBeans](http://netbeans.org), it is even simpler:
  _File&gt;Open Project_.

* The same works in [IntelliJ](http://jetbrains.net).

* If [jEdit](http://jedit.org) is your preferred IDE, you will need the
  [Maven Plugin](http://plugins.jedit.org/plugins/?MavenPlugin).

Die-hard command-line developers can use Maven directly by calling `mvn`
in the project root.

However you build the project, in the end you will have the `.jar` file
(called *artifact* in Maven speak) in the `target/` subdirectory.

To copy the artifact into the correct place, you can call
`mvn -Dimagej.app.directory=/path/to/ImageJ.app/`.
This will not only copy your artifact, but also all the dependencies. Restart
your ImageJ or call *Help>Refresh Menus* to see your plugin in the menus.

Developing plugins in an IDE is convenient, especially for debugging. To
that end, the plugin contains a `main` method which sets the `plugins.dir`
system property (so that the plugin is added to the Plugins menu), starts
ImageJ, loads an image and runs the plugin. See also
[this page](https://imagej.net/Debugging#Debugging_plugins_in_an_IDE_.28Netbeans.2C_IntelliJ.2C_Eclipse.2C_etc.29)
for information how ImageJ makes it easier to debug in IDEs.

Since this project is intended as a starting point for your own
developments, it is in the public domain.

How to use this project as a starting point
===========================================

Either

* `git clone git://github.com/imagej/example-imagej-command`, or
* unpack https://github.com/imagej/example-imagej-command/archive/master.zip

Then:

1. Edit the `pom.xml` file. Every entry should be pretty self-explanatory.
   In particular, change
    1. the *artifactId* (**NOTE**: should contain a '_' character)
    2. the *groupId*, ideally to a reverse domain name your organization owns
    3. the *version* (note that you typically want to use a version number
       ending in *-SNAPSHOT* to mark it as a work in progress rather than a
       final version)
    4. the *dependencies* (read how to specify the correct
       *groupId/artifactId/version* triplet
       [here](https://imagej.net/Maven#How_to_find_a_dependency.27s_groupId.2FartifactId.2Fversion_.28GAV.29.3F))
    5. the *developer* information
    6. the *scm* information
2. Remove the `GaussFiltering.java` file and add your own `.java` files
   to `src/main/java/<package>/` (if you need supporting files -- like icons
   -- in the resulting `.jar` file, put them into `src/main/resources/`)
4. Replace the contents of `README.md` with information about your project.

If you cloned the `example-imagej-command` repository, you probably want to
publish the result in your own repository:

1. Call `git status` to verify .gitignore lists all the files (or file
   patterns) that should be ignored
2. Call `git add .` and `git add -u` to stage the current files for
   commit
3. Call `git commit` or `git gui` to commit the changes
4. [Create a new GitHub repository](https://github.com/new)
5. `git remote set-url origin git@github.com:<username>/<projectname>`
6. `git push origin HEAD`

### Eclipse: To ensure that Maven copies the plugin to your ImageJ folder

1. Go to _Run Configurations..._
2. Choose _Maven Build_
3. Add the following parameter:
    - name: `imagej.app.directory`
    - value: `/path/to/ImageJ.app/`

This ensures that the final `.jar` file will also be copied to your ImageJ
plugins folder everytime you run the Maven Build
