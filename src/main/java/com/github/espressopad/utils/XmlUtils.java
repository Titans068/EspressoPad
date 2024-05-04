package com.github.espressopad.utils;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.github.espressopad.models.SettingsModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class XmlUtils {
    private final File importsFile = new File(URLDecoder.decode(this.getClass().getProtectionDomain().getCodeSource()
            .getLocation().getPath(), StandardCharsets.UTF_8))
            .toPath().getParent().resolve("imports.xml").toFile();
    private final File artifactFile = new File(URLDecoder.decode(this.getClass().getProtectionDomain().getCodeSource()
            .getLocation().getPath(), StandardCharsets.UTF_8))
            .toPath().getParent().resolve("artifacts.xml").toFile();
    private final File settingsFile = new File(URLDecoder.decode(this.getClass().getProtectionDomain().getCodeSource()
            .getLocation().getPath(), StandardCharsets.UTF_8))
            .toPath().getParent().resolve("settings.xml").toFile();
    private final XmlMapper mapper = XmlMapper.builder()
            .defaultUseWrapper(false)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .build();

    public File getImportsFile() {
        return this.importsFile;
    }

    public File getArtifactFile() {
        return this.artifactFile;
    }

    private Document initDocument(File file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.parse(file);
        document.getDocumentElement().normalize();
        return document;
    }

    private List<String> checkDuplicateChildNodes(List<String> elements, Node node) {
        NodeList importChildren = node.getChildNodes();
        List<String> importStream = new ArrayList<>();
        for (int i = 0; i < importChildren.getLength(); i++)
            importStream.add(importChildren.item(i).getTextContent());

        List<String> filteredImports = new ArrayList<>(elements);
        filteredImports.removeAll(importStream);
        return filteredImports;
    }

    private void saveXmlChanges(File file, Document document) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult sr = new StreamResult(file);
        transformer.transform(domSource, sr);
    }

    public void writeArtifactXml(List<String> artifactList) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.newDocument();
            Node node = document.getElementsByTagName("artifacts").item(0);

            if (this.artifactFile.exists() && node != null) {
                document = builder.parse(this.artifactFile);

                for (String artifacts : this.checkDuplicateChildNodes(artifactList, node)) {
                    Element lib = document.createElement("artifact");
                    lib.appendChild(document.createTextNode(artifacts));
                    node.appendChild(lib);
                }
            } else {
                Element root = document.createElement("component");
                document.appendChild(root);

                Element artifact = document.createElement("artifacts");
                root.appendChild(artifact);

                for (String artifacts : artifactList) {
                    Element lib = document.createElement("artifact");
                    lib.appendChild(document.createTextNode(artifacts));
                    artifact.appendChild(lib);
                }
            }

            this.saveXmlChanges(this.artifactFile, document);
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> parseArtifactXml() {
        try {
            NodeList artifacts = this.initDocument(this.artifactFile).getElementsByTagName("artifact");
            List<String> artifactList = new ArrayList<>();
            for (int i = 0; i < artifacts.getLength(); i++)
                artifactList.add(artifacts.item(i).getTextContent());
            return artifactList;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeImportXml(List<String> importList) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document;

            if (this.importsFile.exists()) {
                document = builder.parse(this.importsFile);
                Node node = document.getElementsByTagName("imports").item(0);

                if (node != null) {
                    List<String> distinctImports = this.checkDuplicateChildNodes(importList, node);
                    for (String importText : distinctImports) {
                        Element anImport = document.createElement("import");
                        anImport.appendChild(document.createTextNode(importText));
                        node.appendChild(anImport);
                    }
                    if (!distinctImports.isEmpty())
                        this.saveXmlChanges(this.importsFile, document);
                    return;
                }
            }

            document = builder.newDocument();
            Element root = document.createElement("component");
            document.appendChild(root);

            Element imports = document.createElement("imports");
            root.appendChild(imports);

            for (String importText : importList) {
                Element anImport = document.createElement("import");
                anImport.appendChild(document.createTextNode(importText));
                imports.appendChild(anImport);
            }

            this.saveXmlChanges(this.importsFile, document);
        } catch (ParserConfigurationException | SAXException | TransformerException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> parseImportXml() {
        try {
            NodeList imports = this.initDocument(this.importsFile).getElementsByTagName("import");
            List<String> importList = new ArrayList<>();
            for (int i = 0; i < imports.getLength(); i++)
                importList.add(imports.item(i).getTextContent());
            return importList;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeSettingsXml(SettingsModel settings) {
        try {
            if (!this.settingsFile.exists())
                Files.createFile(this.settingsFile.toPath());
            try (FileOutputStream fileOutputStream = new FileOutputStream(this.settingsFile);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
                this.mapper.writeValue(bufferedOutputStream, settings);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SettingsModel parseSettingsXml() {
        if (!this.settingsFile.exists())
            return null;
        try (FileInputStream fileInputStream = new FileInputStream(this.settingsFile);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
            return this.mapper.readValue(bufferedInputStream, SettingsModel.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
