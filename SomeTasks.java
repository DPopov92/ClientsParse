import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SomeTasks {
    private List<Client> clientList = new ArrayList<>();

    public static void main(String[] args) {
        SomeTasks someTasks = new SomeTasks();
//        someTasks.parseXML("clients_list.tld");

        someTasks.getByGender("male");
        System.out.println(someTasks.getTheReachestInDollars("clients_list.tld").getFirstName());

    }

    public void parseXML(String path) {

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(path);
            Node root = document.getDocumentElement();
            NodeList clients = root.getChildNodes();

            for (int i = 0; i < clients.getLength(); i++) {
                Node client = clients.item(i);

                if (client.getNodeType() != Node.TEXT_NODE) {
                    NodeList clientProps = client.getChildNodes();
                    String gender = client.getAttributes().getNamedItem("gender").getTextContent();
                    String lname = clientProps.item(1).getTextContent();
                    String fname = clientProps.item(3).getTextContent();
                    String mname = clientProps.item(5).getTextContent();
                    Date bdate = new SimpleDateFormat("dd/MM/yyyy").parse(clientProps.item(7).getTextContent());
                    String city = clientProps.item(9).getTextContent();
                    int amount = Integer.parseInt(clientProps.item(11).getTextContent());
                    String curr = clientProps.item(11).getAttributes().getNamedItem("curr").getTextContent();
                    clientList.add(new Client(gender, lname, fname, mname, city, amount, curr, bdate));

                }
            }
        } catch (ParserConfigurationException | SAXException | IOException | ParseException e) {
            e.printStackTrace();
        }
    }


    public List<Client> getByGender(String gender) {
        List<Client> genderList = new ArrayList<>();
        for (Client c : clientList) {
            if (c.getGender().equals(gender))
                genderList.add(c);
        }

        return genderList;
    }

    public List<Client> getSpecific(String gender, Date date, String currency) {
        List<Client> cList = new ArrayList<>();
        for (Client c : clientList) {
            if (c.getGender().equals(gender) && c.getCurrency().equals(currency) && c.getBirthday().getTime() < date.getTime()) {
                cList.add(c);
            }
        }
        return cList;
    }

    public Client getClientWithMaxAmount() {
        int max_amount = 0;
        Client clientWithMaxC = clientList.get(0);
        for (Client c : clientList) {
            int amount = c.getCurrency().equals("USD") ? c.getAmount() : c.getAmount() / 50;
            if (amount > max_amount) {
                max_amount = amount;
                clientWithMaxC = c;
            }
        }
        return clientWithMaxC;
    }

    public Client getTheReachestInDollars(String path) {
        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();
        DocumentBuilder documentBuilder;
        Document document;
        XPathExpression expr_cfn;
        NodeList clList = null;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = documentBuilder.parse(path);
            expr_cfn = xpath.compile("/Cl/Client");
            clList = (NodeList) expr_cfn.evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException | SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < clList.getLength(); i++) {
            Client client = new Client();
            client.setGender(clList.item(i).getAttributes().getNamedItem("gender").getTextContent());
            NodeList child_list = clList.item(i).getChildNodes();
            for (int j = 0; j < child_list.getLength(); j++) {
                switch (child_list.item(j).getNodeName()) {
                    case "FirstName":
                        client.setFirstName(child_list.item(j).getTextContent());
                        break;
                    case "LastName":
                        client.setLastName(child_list.item(j).getTextContent());
                        break;
                    case "MiddleName":
                        client.setMiddleName(child_list.item(j).getTextContent());
                        break;
                    case "PrimaryCity":
                        client.setCity(child_list.item(j).getTextContent());
                        break;
                    case "Amount":
                        client.setAmount(Integer.parseInt(child_list.item(j).getTextContent()));
                        client.setCurrency(child_list.item(j).getAttributes().getNamedItem("curr").getTextContent());
                        break;
                    case "BirthDay":
                        try {
                            client.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse(child_list.item(j).getTextContent()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
            clientList.add(client);
        }
        return getClientWithMaxAmount();
    }

}
