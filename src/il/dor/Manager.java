package il.dor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.file.Paths.get;

/**
 * Created by Dor on 8/23/15.
 */
public class Manager {
    private static LinkedHashMap<String, String> translatedStringsMap;
    private static LinkedHashMap<String, String> englishStringsMap;


    public static int start(String translations, String strings, String output, String platform) throws IOException {
        /** returns 0 when there are no problems
         *  returns 1 when there are quotation marks problems
         *  returns 2 when there are LP problems
         *  returns 3 when there are both LP and quotation marks problems
         **/
        String cleanStrings = cleanStrings(strings, platform);
        File stringsFile = new File(cleanStrings);
        File translationsFile = new File(translations);
        translatedStringsMap = fileToMap(translationsFile, "\t");
        englishStringsMap = fileToMap(stringsFile, "\t");
        boolean lp = lpCheck(translatedStringsMap);
        boolean quotationMarks = quotationMarksCheck(translatedStringsMap);

        translate();

        writeToFile(englishStringsMap, output, platform);
        if (lp && !quotationMarks) {
            return 1;
        } else if (quotationMarks && !lp) {
            return 2;
        } else if (quotationMarks && lp) {
            return 3;
        }
        return 0;

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
                map.put(key, value.substring(1, value.length() - 1));
            }

        }
        return map;
    }

    public static void writeToFile(Map<String, String> map, String filename, String platform) throws IOException {


        if (platform.equals("iOS")) {
            FileWriter fw = new FileWriter(filename + ".strings");

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
            Path path = get("iOS.tmp");
            Files.delete(path);

        } else if (platform.equals("Android")) {
            FileWriter fw = new FileWriter(filename + ".xml");
            fw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n" + "<resources>" + "\n");

            for (String key : map.keySet()) {
                fw.write("\t" + "<string name=\"" + key + "\"" + ">" + map.get(key) + "</string>" + "\n");
            }
            fw.write("</resources>");
            fw.close();
            Path path = get("Android.tmp");
            Files.delete(path);
        }
    }

    public static void translate() {
        for (String key : translatedStringsMap.keySet()) {
            if (englishStringsMap.containsKey(key)) {
                englishStringsMap.put(key, translatedStringsMap.get(key));
            }
        }
    }

    public static boolean lpCheck(Map<String, String> map) {
        String[] lpToCheck = new String[]{"LP：", "LP: ", "LP:"};
        boolean result = false;
        for (String lp : lpToCheck) {
            for (String key : map.keySet()) {
                String value = map.get(key);
                if (value.contains(lp)) {
                    String newValue = value.substring(0, value.indexOf(lp)) + value.substring(lp.length(), value.length() - 1);
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
            if (valueArr.length>1) {
                for (int j = 0; j < valueArr.length-1; j++){
                    newValue = newValue + valueArr[j];
                    if (!valueArr[j].endsWith("\\")) {
                        newValue = newValue+"\\"+"\"";
                        result = true;
                    } else {
                        newValue = newValue +"\"";
                    }
                }
                if (valueArr[valueArr.length-1].endsWith("\\")) {
                    newValue = newValue + valueArr[valueArr.length - 1]+"\"";
                } else {
                    newValue = newValue + valueArr[valueArr.length - 1];
                }
                map.put(key,newValue);
            }
        }
        return result;
    }

    public static String cleanStrings(String file, String platform) throws IOException {
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
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (line != null) {
                if (line.contains("string name")) {
                    String newLine = line;
                    newLine = newLine.replaceAll("    <string name=\"", "");
                    newLine = newLine.replaceAll("\">", "\t");
                    newLine = newLine.replaceAll("</string>", "");
                    fw.write(newLine + "\n");

                }
                line = br.readLine();
            }
            fw.close();
            br.close();
            return ("Android.tmp");
        }
        return null;

    }
}