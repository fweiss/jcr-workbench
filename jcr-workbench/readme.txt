Problem running on new machine
java.lang.ClassNotFoundException
Fix: enable project specifi setting under 
Java Compiler + Building

Running
Run JCRWorkbench as an applet

rest the database by deleting the directories under repository


JCR libraries:

jcr-1.0.jar
jackrabbit-core
jackrabbit-api-1.5.0.jar
slf4j
slf4j-log4j12-1.5.3
log4j
jackrabbit-jcr-commons
jackrabbit-spi-commons
concurrent
commons-collections
commons-io
jackrabbit-spi
derby

java.lang.NoClassDefFoundError: org/apache/lucene/index/MultiReader

lucene-core
jackrabbit-text-extractors

Extractor dependency not found: org.apache.jackrabbit.extractor.MsWordTextExtractor
Actually, this is caused by a forced class load in MsWordTextExtractor.java:43 of the
class org.apache.poi.hwpf.extractor.WordExtractor. This class is in poi-scratchpad.
These are the dependencies:

poi-scratchpad-3.0.2
poi-3.0.2

Then there's also org/pdfbox/pdmodel/PDDocument

With those three jars added to the claspath, the errors when a session is opened
go away.

Swing
-----
http://www.softwarereality.com/soapbox/swing.jsp


Annotaions Idea
---------------
Annotations could be used to simplify programming:

For panels and dialogs, annotate fields with the control type, and have a framework
that creates the controls.

Panels and dialogs often need to communicate with other objects whose visibility is
in the main controller. Declare the references in the panels and dialogs, annotate
them and then let an framework set them via injection.


Jackrabbit
----------
http://wiki.apache.org/jackrabbit/ExamplesPage


Theory
------
1. A workspace is a tree of nodes.
2. A node, formally an Item, is a composite of formal Nodes and Properties.
3. Properties are leaf nodes.
4. A Property has a definite type.
5. A Property may have more than one value, even though it is a leaf.
6. (Corollary) The values of a Property are homogenous in type.
7. A Node has a definite type.
8. The type of a Node is a NodeType.
9. Inheritance of NodeType.
10. Mixins of NodeType.


Node type views
---------------
1. Node type Inheritance tree.
2. Mixin inheritance tree.
3. Search result.


UIManager Keys
--------------
http://www.duncanjauncey.com/java/ui/


UI Editors
----------
STRING inline
BINARY dialog/browse
DATE inline/+
BOOLEAN inline/dropdown
REFERENCE dialog/search/browse
PATH inline/browse/search
DOUBLE inline


Clipboard/Context Menu
----------------------
Copy
Paste
Paste Path
Paste Reference

Also consider Drag and Drop

Properties
----------

|| e              || wb ||
requiredType      | -
autoCreated       | autoCreated
multiple          | multiple
protected         | protected
name              | name
mandatory         | mandatory
primaryType       | primaryType
onParentVersion   | onParentVersion
-                 | mixinTypes
-                 | defaultValues
-                 | valueConstraints
supertype?        |




