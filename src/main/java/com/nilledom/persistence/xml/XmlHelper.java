package com.nilledom.persistence.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XmlHelper {
    protected static XPathFactory xpath = XPathFactory.newInstance();

    /**
     * Executes an XQuery and returns the result as a list of elements.
     *
     * @param element
     * @param query
     * @return
     */
    public static List<Element> query(Element element, String query) {
        try {
            XPathExpression expr = xpath.newXPath().compile(query);
            NodeList list = (NodeList) expr.evaluate(element, XPathConstants.NODESET);

            return XmlHelper.toElementList(list);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return new ArrayList<Element>();
    }

    public static Element querySingle(Element element, String query) {
        try {
            XPathExpression expr = xpath.newXPath().compile(query);
            NodeList list = (NodeList) expr.evaluate(element, XPathConstants.NODESET);

            if (list.getLength() > 0)
                return (Element) list.item(0);

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<Element> toElementList(NodeList list) {
        ArrayList<Element> nodes = new ArrayList<Element>();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element)
                nodes.add((Element) node);
        }
        return nodes;
    }


    public static List<String> getAttributeNames(Element element) {
        List<String> names = new ArrayList<String>();
        NamedNodeMap list = element.getAttributes();
        for (int i = 0; i < list.getLength(); i++) {
            names.add(list.item(i).getNodeName());
        }
        return names;
    }


    public static Element getNewElement(Element element, String name) {
        String safeName = name.replaceAll("\\$", "::");
        return element.getOwnerDocument().createElement(safeName);
    }

    public static Element addChildElement(Element root, Element element, String name) {
        String safeName = name.replaceAll("\\$", "::");
        Element child = getNewElement(root, safeName);
        element.appendChild(child);
        return child;
    }


    public static Attr getNewAttribute(Element element, String name) {
        String safeName = name.replaceAll("\\$", "::");
        return element.getOwnerDocument().createAttribute(safeName);
    }

    public static String getAttribute(Element element, String name) {

        String value = element.getAttribute(name);
        String safeValue = value.replace((CharSequence) "::", "$");
        return safeValue;
    }

    public static Attr addAtribute(Element root, Element element, String name, String value) {
        String safeName = name.replaceAll("\\$", "::");
        String safeValue = value.replaceAll("\\$", "::");
        Attr attribute = getNewAttribute(root, safeName);
        attribute.setNodeValue(safeValue);
        element.setAttributeNode(attribute);
        return attribute;
    }

    public static Element getChild(Element element, String name) {
        String safeName = name.replaceAll("\\$", "::");
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            Node node = element.getChildNodes().item(i);
            if (node instanceof Element) {
                Element el = (Element) node;
                if (el.getNodeName().equals(safeName))
                    return el;
            }
        }
        return null;
    }

    public static Element getFirstChild(Element element) {

        NodeList nodelist = element.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = nodelist.item(i);
            if (node instanceof Element)
                return (Element) node;
        }
        return null;
    }
}
