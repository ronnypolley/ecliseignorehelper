package de.randomwords.eclipseignorehelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This goal will changed the .classpath file of eclipse to ignore compiler
 * warning on the given paths
 */
@Mojo(name = "ignorePaths", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class EclipseIgnoreHelper extends AbstractMojo {

	@Parameter(property = "ingorePaths", required = true)
	private List<String> ingorePaths;

	public void execute() throws MojoExecutionException {
		File file = new File(".classpath");

		if (file != null && file.exists()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(file);

				XPathFactory xPathFactory = XPathFactory.newInstance();
				for (String string : ingorePaths) {
					addIgnoreAttribute(document, xPathFactory, string);
				}

				writeToFile(file, document);

			} catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException
					| TransformerException e) {
				throw new MojoExecutionException("error parsing .classpath file", e);
			}
		} else {
			throw new MojoExecutionException("The file .classpath does not exist. Is this a eclipse project?");
		}
	}

	private void addIgnoreAttribute(Document document, XPathFactory xPathFactory, String string)
			throws XPathExpressionException {
		XPathExpression xPathExpression = xPathFactory.newXPath()
				.compile("//classpathentry[@path='" + string + "']/attributes");
		NodeList nodeList = (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);
		getLog().error(nodeList.toString());
		Element element = document.createElement("attribute");
		element.setAttribute("name", "ignore_optional_problems");
		element.setAttribute("value", "true");
		getLog().error(nodeList.item(0).getNodeName());
		nodeList.item(0).appendChild(element);
	}

	private void writeToFile(File file, Document document)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(file);
		transformer.transform(source, result);
	}
}
