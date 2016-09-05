import javax.xml.xpath.*
import javax.xml.parsers.DocumentBuilderFactory

def classpath = new File(basedir, ".classpath").getText()

assert processXml(classpath, "//classpathentry[@path='src/test/java']/attributes/attribute[@name='ignore_optional_problems']") != null
assert processXml(classpath, "//classpathentry[@path='src/test/javaWithoutAttributes']/attributes/attribute[@name='ignore_optional_problems']") != null


def processXml( String xml, String xpathQuery ) {
	def xpath = XPathFactory.newInstance().newXPath()
	def builder     = DocumentBuilderFactory.newInstance().newDocumentBuilder()
	def inputStream = new ByteArrayInputStream( xml.bytes )
	def records     = builder.parse(inputStream).documentElement
	xpath.evaluate( xpathQuery, records )
}