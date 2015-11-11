package main.java.avii.util;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.imageio.metadata.IIOMetadataNode;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLHelper {

	public static String nodeToString(Node node) {
		 StringWriter sw = new StringWriter();
		 try {
		   Transformer t = TransformerFactory.newInstance().newTransformer();
		   t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		   
		   t.setOutputProperty(OutputKeys.INDENT, "yes");
		   t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		   
		   t.transform(new DOMSource(node), new StreamResult(sw));
		 } catch (TransformerException te) {
		   System.out.println("nodeToString Transformer Exception");
		 }
		 return sw.toString();
		}
	
	
	public static ArrayList<IIOMetadataNode> nodeListToIIOMetadataNodeArrayList(NodeList nodeList)
	{
		ArrayList<IIOMetadataNode> arrayList = new ArrayList<IIOMetadataNode>();
		for(int i = 0; i < nodeList.getLength(); i++)
		{
			IIOMetadataNode node = (IIOMetadataNode) nodeList.item(i);
			arrayList.add(node);
		}
		return arrayList;
	}
}
