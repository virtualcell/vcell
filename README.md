The Virtual Cell plugin for ImageJ.

### How to import this project

* In [Eclipse](http://eclipse.org), for example, it is as simple as
  _File&gt;Import...&gt;Existing Maven Project_.

* In [NetBeans](http://netbeans.org), it is even simpler:
  _File&gt;Open Project_.

* The same works in [IntelliJ](http://jetbrains.net).

* If [jEdit](http://jedit.org) is your preferred IDE, you will need the
  [Maven Plugin](http://plugins.jedit.org/plugins/?MavenPlugin).

### Eclipse: To ensure that Maven copies the plugin to your ImageJ folder

1. Go to _Run Configurations..._
2. Choose _Maven Build_
3. Add the following parameter:
    - name: `imagej.app.directory`
    - value: `/path/to/ImageJ.app/`

This ensures that the final `.jar` file will also be copied to your ImageJ
plugins folder everytime you run the Maven Build