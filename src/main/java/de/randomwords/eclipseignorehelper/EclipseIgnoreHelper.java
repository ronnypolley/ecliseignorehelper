package de.randomwords.eclipseignorehelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This goal will changed the .classpath file of eclipse to ignore compiler
 * warning on the given paths
 */
@Mojo(name = "ignorePaths", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class EclipseIgnoreHelper extends AbstractMojo {

    private static final String CLASSPATHENTRY_PATH = "//classpathentry[@path='";

    @Parameter(property = "ignorePaths", required = true)
    private List<String> ignorePaths;

    @Parameter(readonly = true, defaultValue = "${project.basedir}/.classpath")
    private File classpathFile;

    @Parameter(property = "ignoreNotEclipseProjects", defaultValue = "true")
    private boolean ignoreNotEclipseProjects;

    private XPathFactory xPathFactory = XPathFactory.newInstance();

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Processing: " + classpathFile.toString());

        if (classpathFile.exists()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(classpathFile);

                for (String path2Ignore : ignorePaths) {
                    addIgnoreAttribute(document, path2Ignore);
                }

                writeToFile(classpathFile, document);

            } catch (Exception e) {
                throw new MojoExecutionException("error parsing .classpath file (" + classpathFile + ")", e);
            }
        } else {
            if (!ignoreNotEclipseProjects) {
                throw new MojoExecutionException("The file .classpath does not exist. Is this a eclipse project?");
            } else {
                getLog().info(
                        "The file .classpath does not exist. Processing will be skipped as of ignoreNotEclipseProjects is set to true.");
            }
        }
    }

    private void addIgnoreAttribute(Document document, String path2Ignore)
            throws XPathExpressionException {

        // entry with matching path
        Node attributes = findAttributesNode(document, path2Ignore);
        // xpath checking already existing element
        Node attributeIgnore = evaluateXpath(document,
                CLASSPATHENTRY_PATH + path2Ignore + "']/attributes/attribute[@name='ignore_optional_problems']");

        // only add
        if (attributeIgnore != null) {
            getLog().info("Path " + path2Ignore + " is already set to ignore warnings");
        } else {
            if (attributes == null) {
                getLog().debug("No additional attributes are currently set for " + path2Ignore);
                Node node = evaluateXpath(document, CLASSPATHENTRY_PATH + path2Ignore + "']");
                if (node == null) {
                    getLog().info("Node does not exist in .classpath. Will be ignored for new.");
                    return;
                }
                node.appendChild(document.createElement("attributes"));
                attributes = findAttributesNode(document, path2Ignore);
            }
            attributes.appendChild(createIgnoreAttribute(document));
        }
    }

    private Node findAttributesNode(Document document, String string) throws XPathExpressionException {
        return evaluateXpath(document, CLASSPATHENTRY_PATH + string + "']/attributes");
    }

    private Node evaluateXpath(Document document, String xpath) throws XPathExpressionException {
        return (Node) xPathFactory.newXPath().compile(xpath).evaluate(document, XPathConstants.NODE);
    }

    private static Element createIgnoreAttribute(Document document) {
        Element element = document.createElement("attribute");
        element.setAttribute("name", "ignore_optional_problems");
        element.setAttribute("value", "true");
        return element;
    }

    private static void writeToFile(File file, Document document) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
}
