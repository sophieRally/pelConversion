import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


public class MyUtils {

    public static final String workingFolder = "/Users/sophie.likit/temp/DARE-66/";
    public static final File xslFile = new File(workingFolder + "ipmxml/ipm.xsl");
    public static final File inputFolder = new File(workingFolder + "ipmxml");
    public static final File outputFolder = new File(workingFolder + "output");

    public static FilenameFilter xmlFileFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            String lowercaseName = name.toLowerCase();
            if (lowercaseName.endsWith(".xml")) {
                return true;
            } else {
                return false;
            }
        }
    };

    public static void ListFilesForFolder() {
        if (!inputFolder.exists()) {
            System.out.print("Error --> Folder does not exist");
            return;
        }

        try {
            if (outputFolder.exists()) {
                FileUtils.cleanDirectory(outputFolder);
            } else {
                outputFolder.mkdir();
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (!xslFile.exists()) {
            System.out.print("Error --> xsl does not exist");
            return;
        }

        // listAllFilesForFolder(folder);
        listXmlFilesForFolder(inputFolder);
    }

    protected static void listAllFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listAllFilesForFolder(fileEntry);
            } else {
                System.out.println(fileEntry.getName());
            }
        }
    }

    /**
     *
     * @param folder
     */
    protected static void listXmlFilesForFolder(final File folder) {

        Source xslSource = new StreamSource(xslFile);

        File[] files = folder.listFiles(xmlFileFilter);

        for (File file : files) {
            if (file.isDirectory()) {
                System.out.print("directory:");
            } else {
                System.out.print("     file:");
            }
            System.out.println(file.getName());

            convertXMLToHTML(file, xslSource);
        }
    }


    /**
     *
     * @param xmlFile
     * @param xslt
     */
    public static void convertXMLToHTML(File xmlFile, Source xslt) {
        StringWriter sw = new StringWriter();

        try {
            Source xmlSource = new StreamSource(xmlFile);

            String outputFile = "/Users/sophie.likit/temp/DARE-66/output/" + FilenameUtils.removeExtension(xmlFile.getName()) + ".html";
            FileWriter fw = new FileWriter(outputFile);

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(xslt);
            // Add the XSL parameters
            transformer.setParameter("pGender", "Female");
            transformer.setParameter("pFontSize", "12");
            transformer.setParameter("pLanguage", "eng");
            transformer.setParameter("pProductDispensed", "Dispensed Medication Name");

            transformer.transform(xmlSource, new StreamResult(sw));
            fw.write(sw.toString());
            fw.close();

            System.out.println(outputFile + " created");
            // System.out.println(String.format("--> %s generated successfully", outputFile));

        } catch (IOException | TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }


    }

}
