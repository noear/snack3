package org.noear.snack.from;

import org.noear.snack.ONode;
import org.noear.snack.core.Context;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

public class XmlFromer implements Fromer{
    @Override
    public void handle(Context ctx) throws Exception {
        ctx.target = do_handle(ctx, (String) ctx.source);
    }

    private ONode do_handle(Context ctx, String text) throws Exception {
        Document doc = parseDoc(text);

        ONode node_o = new ONode();
        Node node_x = doc.getDocumentElement();

        load0(node_x, node_o);

        return node_o;
    }

    private void load0(Node node_x, ONode node_o) {
        int type = node_x.getNodeType();

        if (type ==3 || type == 4) {//text:3 or CDATA:4
            node_o.val(node_x.getTextContent().trim());
            return;
        }

        if (type == 1) { //elem
            node_o.attrSet("@name", node_x.getNodeName());

            if (node_x.hasAttributes()) {
                NamedNodeMap atts = node_x.getAttributes();
                for (int i = 0, len = atts.getLength(); i < len; i++) {
                    Node x1 = atts.item(i);
                    node_o.attrSet(x1.getNodeName(), x1.getNodeValue());
                }
            }

            if (node_x.hasChildNodes()) {
                NodeList xl1 = node_x.getChildNodes();
                for (int i = 0, len = xl1.getLength(); i < len; i++) {
                    Node x1 = xl1.item(i);
                    ONode n1 = node_o.addNew();

                    load0(x1, n1);
                }
                return;
            }
        }
    }

    private static DocumentBuilderFactory dbf = null;
    private static DocumentBuilder db = null;
    //xml:解析文档
    private static Document parseDoc(String xml) throws Exception{
        if(dbf ==null) {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
        }

        return db.parse(new ByteArrayInputStream(xml.getBytes()));
    }
}
