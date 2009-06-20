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


Jackrabbit
----------
http://wiki.apache.org/jackrabbit/ExamplesPage

