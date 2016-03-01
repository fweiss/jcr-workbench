# JCR Workbench

This Java application lets you explore and author a Java Content Repository (JCR). It is implemented with
Apache Jackrabbit and Java JFC Swing.

It is still a work in progress.

## Capabilities

The current capabilities are:

* Open an existing repository
* Initialize a new repository
* Navigate through the JCR nodes with a tree view
* View the content of nodes
* Add nodes
* Add node properties
* Save (commit) nodes to the repository
* Export a node to a file
* Import a node from a file

Future work is intended to: version nodes, manage users, use backing stores other than Derby, add richer
UI components, such as a date picker, and add users, groups and ACLs, upgrade to JCR 2.0.

## Runtime requirements

This is a pure Java application, however, it currently requires a Java IDE to run.

* JRE 1.6 or later
* A Java IDE

It has been developed and tested with the following IDEs:

* Eclipse Mars 4.5.0
* IntelliJ 14.0.3

## Running

In the IDE, run the class JCRWorkbench as a Java application. That class has a main method. Running as an Applet
is deprecated (mainly because IntelliJ borks user.dir to its JRE directory).

The initial UI selects the location of the JCR repository. It will initialize a new one if there is none.

## Testing

Unfortunately, this application was not developed with TDD or BDD. Is is currently manually tested.

There were several challenges: learning JCR and Swing, not having a suitable Swing UI testing framework at the
beginning, and - admittedly - wanting something quickly that worked.

## Background information

I started this project mid-2009, after I evaluated some CMSes, such as Alfresco and Magnolia. Mainly, I wanted to
understand the JCR data model better by being able to visually explore it. At the time, there were a few UIs,
but I didn't feel they were what I needed. (list them).

I'd already built some Applets in AWT, so it was natural to use Swing as a UI. The original version of JCR Workbench
was an Applet - from the comments in the code, this was because it was easy to launch in Eclipse. Since then,
it's been modified to run as a Java application that launches a Swing UI.

If I were to start now (early 2016), I'd use an HTML front end and a REST service. The Apache Sling project is a
possible candidate, but I think it has a weird REST API, kind of crappy UI, and seems to lack support for
some JCR features such as versioning and ACLs.

## Links and references

[Apache Jackrabbit home page](http://jackrabbit.apache.org/jcr/index.html)

[Wikipedia JCR](https://en.wikipedia.org/wiki/Content_repository_API_for_Java)

[https://www.jahia.com/get-started/for-developers/developers-techwiki/content-manipulation/restful-jcr-access]
