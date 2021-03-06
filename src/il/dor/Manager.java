package il.dor;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.file.Paths.get;

/**
 * Created by Dor on 8/23/15.
 */
public class Manager {


    public static boolean[] startTranslation(String translations, String strings, String output, String platform) throws IOException {
        /** returns array of booleans:
         * 0 - LP errors
         * 1 - quotation marks errors
         * 2 - apostrophe errors
         * 3 - additional strings
         **/
        LinkedHashMap<String, String> translatedStringsMap;
        LinkedHashMap<String, String> englishStringsMap;
        //excelTest();
        String cleanStrings = null;
        try {
            cleanStrings = cleanStrings(strings, platform);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        File stringsFile = new File(cleanStrings);
        File translationsFile = new File(translations);
        translatedStringsMap = fileToMap(translationsFile, "\t");
        englishStringsMap = fileToMap(stringsFile, "\t");

        boolean[] results = new boolean[4];
        results[0] = lpCheck(translatedStringsMap);
        results[1] = quotationMarksCheck(translatedStringsMap);
        results[2] = false;
        results[3] = false;


        if (platform.equals("Android")) {

            results[2] = apostropheCheck(translatedStringsMap);

            try {
                results[3] = androidTranslation(strings, translatedStringsMap, output);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }


        } else if (platform.equals("iOS")) {
            LinkedHashMap<String, String> finalMapAfterTranslation = translate(translatedStringsMap, englishStringsMap);
            results[3] = iosTranslation(finalMapAfterTranslation, translatedStringsMap, englishStringsMap, output);
        }

        Path path = get(platform + ".tmp");
        Files.delete(path);

        return results;

    }

    private static boolean iosTranslation(LinkedHashMap<String, String> finalMapAfterTranslation, LinkedHashMap<String, String> translatedMap, LinkedHashMap<String, String> englishStringsMap, String output) throws IOException {

        boolean isAdditional = false;

        LinkedHashMap<String, String> untranslatedStringsMap = untranslatedCheck(translatedMap, englishStringsMap);
        if (untranslatedStringsMap.size() > 0) {
            isAdditional = true;
            writeToExcelFile(untranslatedStringsMap, output + "_UNTRANSLATED_STRINGS");
        }
        LinkedHashMap<String, String> additionalStringsMap = additionalCheck(translatedMap, englishStringsMap);
        if (additionalStringsMap.size() > 0) {
            isAdditional = true;
            writeToExcelFile(additionalStringsMap, output + "_ADDITIONAL_STRINGS");
        }


        writeToIosStringFile(finalMapAfterTranslation, output);

        return isAdditional;


    }

    public static boolean[] sourceToExcel(String strings, String output, String platform) throws IOException {
        LinkedHashMap<String, String> stringsMap;
        String cleanStrings = null;
        try {
            cleanStrings = cleanStrings(strings, platform);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        File stringsFile = new File(cleanStrings);
        stringsMap = fileToMap(stringsFile, "\t");
        boolean[] results = new boolean[3];
        results[0] = lpCheck(stringsMap);
        results[1] = quotationMarksCheck(stringsMap);
        results[2] = false;

        if (platform.equals("Android")) {
            results[2] = apostropheCheck(stringsMap);
        }

        writeToExcelFile(stringsMap, output);

        return results;
    }

    public static LinkedHashMap fileToMap(File file, String letter) throws IOException {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        while (line != null) {
            if (line.length() != 0) {
                int index = line.indexOf(letter);
                if (line.indexOf(letter) == line.length() - 1) {
                    map.put(line.substring(0, index), "");
                } else {
                    try {
                        map.put(line.substring(0, index), line.substring(index + 1, line.length()));
                    } catch (StringIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        System.out.print(line);
                    }
                }
            }
            line = br.readLine();
        }


        for (String key : map.keySet()) {
            String value = map.get(key);
            if (value.startsWith("\"")) {
                map.put(key, value.substring(1, value.length()));
            }
            value = map.get(key);
            if (value.endsWith("\"")) {
                map.put(key, value.substring(0, value.length() - 1));
            }
        }
        return map;
    }

    public static void writeToIosStringFile(Map<String, String> map, String directory) throws IOException {


        File file = new File(directory);
        file.mkdir();
        FileWriter fw = new FileWriter(new File(directory, "StringsForUI.strings"));

        fw.write("/*\n" +
                " StringsForUI.strings\n" +
                " FlirFX\n" +
                " \n" +
                " Created by Maor Atlas on 6/17/14.\n" +
                " Copyright (c) 2014 Zemingo. All rights reserved.\n" +
                " */" + "\n");
        for (String key : map.keySet()) {
            fw.write("\"" + key + "\"" + " = " + "\"" + map.get(key) + "\"" + ";" + "\n");
        }
        fw.close();

    }

    public static boolean androidTranslation(String file, LinkedHashMap<String, String> translatedStringsMap, String output) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        File outputFile = new File(output);
        outputFile.mkdir();
        LinkedHashMap<String, String> englishStringsMap = new LinkedHashMap<>();


        File xmlToParse = new File(file);
        DocumentBuilderFactory dbFactory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlToParse);
        doc.getDocumentElement().normalize();

        NodeList stringNode = doc.getElementsByTagName("string");
        for (int i = 0; i < stringNode.getLength(); i++) {
            String key = stringNode.item(i).getAttributes().getNamedItem("name").getNodeValue();
            String newValue = translatedStringsMap.get(key);
            if (newValue != null && !newValue.equals("")) {
                stringNode.item(i).setTextContent(newValue);
            }
            englishStringsMap.put(key, stringNode.item(i).getTextContent());
        }

        stringNode = doc.getElementsByTagName("item");
        for (int i = 0; i < stringNode.getLength(); i++) {
            if (stringNode.item(i).getParentNode().getNodeName().equals("plurals")) {
                String key = stringNode.item(i).getParentNode().getAttributes().getNamedItem("name").getNodeValue() + "-" + stringNode.item(i).getAttributes().getNamedItem("quantity").getNodeValue();
                String newValue = translatedStringsMap.get(key);
                if (newValue != null) {
                    stringNode.item(i).setTextContent(newValue);
                }
                englishStringsMap.put(key, stringNode.item(i).getTextContent());
            }

            if (stringNode.item(i).getParentNode().getNodeName().equals("string-array")) {
                String key = stringNode.item(i).getParentNode().getAttributes().getNamedItem("name").getNodeValue() + "-" + stringNode.item(i).getTextContent();
                String newValue = translatedStringsMap.get(key);
                if (newValue != null) {
                    stringNode.item(i).setTextContent(newValue);
                }
                englishStringsMap.put(key, stringNode.item(i).getTextContent());
            }

        }

        boolean isAdditional = false;

        LinkedHashMap<String, String> untranslatedStringsMap = untranslatedCheck(translatedStringsMap, englishStringsMap);
        if (untranslatedStringsMap.size() > 0) {
            isAdditional = true;
            writeToExcelFile(untranslatedStringsMap, output + "_UNTRANSLATED_STRINGS");
        }
        LinkedHashMap<String, String> additionalStringsMap = additionalCheck(translatedStringsMap, englishStringsMap);
        if (additionalStringsMap.size() > 0) {
            isAdditional = true;
            writeToExcelFile(additionalStringsMap, output + "_ADDITIONAL_STRINGS");
        }


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(outputFile, "strings.xml"));
        transformer.transform(source, result);

        return isAdditional;


    }

    private static LinkedHashMap<String, String> additionalCheck(LinkedHashMap<String, String> translatedStringsMap, Map<String, String> sourceMap) {
        LinkedHashMap<String, String> additionalMap = new LinkedHashMap<>();
        for (String translatedKey : translatedStringsMap.keySet()) {
            if (sourceMap.get(translatedKey) == null) {
                additionalMap.put(translatedKey, translatedStringsMap.get(translatedKey));
            }
        }
        return additionalMap;

    }

    private static LinkedHashMap<String, String> untranslatedCheck(LinkedHashMap<String, String> translatedStringsMap, Map<String, String> sourceMap) {
        LinkedHashMap<String, String> untranslatedMap = new LinkedHashMap<>();
        for (String engKey : sourceMap.keySet()) {
            if ((translatedStringsMap.get(engKey) == null) || (translatedStringsMap.get(engKey).equals("") && !sourceMap.get(engKey).equals(""))) {
                untranslatedMap.put(engKey, sourceMap.get(engKey));
            }
        }
        return untranslatedMap;

    }

    public static void writeToExcelFile(Map<String, String> map, String directory) throws IOException {


        File file = new File(directory);
        file.mkdir();
        FileWriter fw = new FileWriter(new File(directory, "Strings table.txt"));


        for (String key : map.keySet()) {
            fw.write(key + "\t" + map.get(key) + "\n");
        }
        fw.close();

    }

    public static LinkedHashMap translate(Map<String, String> translatedStringsMap, Map<String, String> englishStringsMap) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>(englishStringsMap);
        for (String key : translatedStringsMap.keySet()) {
            if (result.containsKey(key)) {
                if (translatedStringsMap.get(key).equals("")) {
                    result.put(key, englishStringsMap.get(key));
                } else {
                    result.put(key, translatedStringsMap.get(key));
                }
            }
        }
        return result;
    }

    public static boolean lpCheck(Map<String, String> map) {
        String[] lpToCheck = new String[]{"LP：", "LP: ", "LP:"};
        boolean result = false;
        for (String lp : lpToCheck) {
            for (String key : map.keySet()) {
                String value = map.get(key);
                if (value.contains(lp)) {
                    String newValue = value.substring(0, value.indexOf(lp)) + value.substring(lp.length(), value.length());
                    map.put(key, newValue);
                    result = true;
                }
            }
        }
        return result;
    }

    public static boolean quotationMarksCheck(Map<String, String> map) {
        boolean result = false;
        for (String key : map.keySet()) {
            String value = map.get(key);
            String[] valueArr = value.split("\"");
            String newValue = "";
            if (valueArr.length > 1) {
                for (int j = 0; j < valueArr.length - 1; j++) {
                    newValue = newValue + valueArr[j];
                    if (!valueArr[j].endsWith("\\")) {
                        newValue = newValue + "\\" + "\"";
                        result = true;
                    } else {
                        newValue = newValue + "\"";
                    }
                }
                if (valueArr[valueArr.length - 1].endsWith("\\")) {
                    newValue = newValue + valueArr[valueArr.length - 1] + "\"";
                } else {
                    newValue = newValue + valueArr[valueArr.length - 1];
                }
                map.put(key, newValue);
            }
        }
        return result;
    }

    public static boolean apostropheCheck(Map<String, String> map) {
        boolean result = false;
        for (String key : map.keySet()) {
            String value = map.get(key);
            String[] valueArr = value.split("\'");
            String newValue = "";
            if (valueArr.length > 1) {
                for (int j = 0; j < valueArr.length - 1; j++) {
                    newValue = newValue + valueArr[j];
                    if (!valueArr[j].endsWith("\\")) {
                        newValue = newValue + "\\" + "\'";
                        result = true;
                    } else {
                        newValue = newValue + "\'";
                    }
                }
                if (valueArr[valueArr.length - 1].endsWith("\\")) {
                    newValue = newValue + valueArr[valueArr.length - 1] + "\'";
                } else {
                    newValue = newValue + valueArr[valueArr.length - 1];
                }
                map.put(key, newValue);
            }

        }
        return result;
    }

    public static String cleanStrings(String file, String platform) throws IOException, ParserConfigurationException, SAXException {
        if (platform.equals("iOS")) {
            FileWriter fw = new FileWriter("ios.tmp");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (line != null) {
                if (line.indexOf("\"") == 0) {
                    String newLine = line.substring(1, line.length() - 2);
                    if (newLine.indexOf("\" = \"") != -1) {
                        newLine = newLine.replaceAll("\" = \"", "\t");
                    }

                    if (newLine.indexOf("\"=\"") != -1) {
                        newLine = newLine.replaceAll("\"=\"", "\t");
                    }

                    fw.write(newLine + "\n");

                }
                line = br.readLine();
            }
            fw.close();
            br.close();
            return ("ios.tmp");
        } else if (platform.equals("Android")) {

            FileWriter fw = new FileWriter("Android.tmp");

            File xmlToParse = new File(file);
            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlToParse);
            doc.getDocumentElement().normalize();

            NodeList stringNode = doc.getElementsByTagName("string");
            for (int i = 0; i < stringNode.getLength(); i++) {
                String key = stringNode.item(i).getAttributes().getNamedItem("name").getNodeValue();
                String value = stringNode.item(i).getTextContent();
                fw.write(key + "\t" + value + "\n");
            }


            stringNode = doc.getElementsByTagName("item");
            for (int i = 0; i < stringNode.getLength(); i++) {
                if (stringNode.item(i).getParentNode().getNodeName().equals("plurals")) {
                    String key = stringNode.item(i).getParentNode().getAttributes().getNamedItem("name").getNodeValue() + "-" + stringNode.item(i).getAttributes().getNamedItem("quantity").getNodeValue();
                    String value = stringNode.item(i).getTextContent();
                    fw.write(key + "\t" + value + "\n");
                }
                if (stringNode.item(i).getParentNode().getNodeName().equals("string-array")) {
                    String key = stringNode.item(i).getParentNode().getAttributes().getNamedItem("name").getNodeValue() + "-" + stringNode.item(i).getTextContent();
                    String value = stringNode.item(i).getTextContent();
                    fw.write(key + "\t" + value + "\n");
                }
            }

            fw.close();
            return ("Android.tmp");
        }
        return null;

    }
/**
    public static void excelTest() throws IOException {

        FileInputStream file = new FileInputStream(new File("test2.xlsx"));

        //Create Workbook instance holding reference to .xlsx file
        XSSFWorkbook workbook = new XSSFWorkbook(file);

        //Get first/desired sheet from the workbook
        XSSFSheet sheet = workbook.getSheet("Android Phone 1.4");
        LinkedHashMap<int,String> languagesIndex = new LinkedHashMap<>();
        //ArrayList<Pair> languagesIndex = new ArrayList<Pair>();

        Iterator<Row> rowIterator = sheet.iterator();
        sheet.getRow(0).cellIterator()
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            //For each row, iterate through all the columns
            Iterator<Cell> cellIterator = row.cellIterator();
            if (row.getRowNum() == 0) {
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (cell.getStringCellValue().contains("@") || cell.getStringCellValue().contains("key")) {
                        java.lang.String language = cell.getStringCellValue().replaceAll("@", "");
                        languagesIndex.put(cell.getColumnIndex(),language);
                    }
                }

            }
            else {
                while (cellIterator.hasNext()) {
                    row.get
                    Cell cell = cellIterator.next();




                }

            }
        }
    }  */
}