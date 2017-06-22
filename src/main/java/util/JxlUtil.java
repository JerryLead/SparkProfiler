package util;

import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by Lijie on 2017/2/13.
 * Modified by YE on 2017/3/20.
 */

public class JxlUtil {

    private String filePath;

    public String getPath() {
        return filePath;
    }

    public void setPath(String filePath) {
        this.filePath = filePath;
    }


    public Map<String, List<List<String>>> parse() {
        File file = new File(filePath);
        if (!file.exists() || !file.getName().endsWith(".xls")) {
            try {
                throw new Exception("要解析的路径有问题: " + filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<String, List<List<String>>> listListMap = new HashMap<String, List<List<String>>>();
        Workbook workBook = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            workBook = Workbook.getWorkbook(fis);
            Sheet[] sheetArray = workBook.getSheets();
            for (int i = 0; sheetArray != null && i < sheetArray.length; i++) {
                Sheet sheet = sheetArray[i];
                List<List<String>> listList = parseSheet(sheet);
                if (listList != null && listList.size() > 0) {
                    listListMap.put(sheet.getName(), listList);
                }
            }
        } catch (BiffException e) {
            System.out.println("解析文件发生异常: " + e);
        } catch (IOException e) {
            System.out.println("解析文件发生异常: " + e);
        } finally {
            try {
                if (workBook != null) {
                    workBook.close();
                    workBook = null;
                }
                if (fis != null) {
                    fis.close();
                    fis = null;
                }
            } catch (Exception e) {
                System.out.println("关闭文件流发生异常: " + e);
            }
        }
        return listListMap;
    }

    private List<List<String>> parseSheet(Sheet sheet) {
        List<List<String>> listList = new ArrayList<List<String>>();
        int rowCount = sheet.getRows();
        for (int i = 1; i < rowCount; i++) {
            List<String> list = new ArrayList<String>();
            Cell[] cellArray = sheet.getRow(i);
            for (int j = 0; cellArray != null && j < cellArray.length; j++) {
                list.add(cellArray[j].getContents());
            }
            listList.add(list);
        }
        return listList;
    }


    public boolean write(Map<String, List<List<String>>> listListMap) throws WriteException {
        File file = new File(filePath);
        boolean result = false;
        WritableWorkbook workBook = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            workBook = Workbook.createWorkbook(fos);
            int sheetNo = 0;
            for (Entry<String, List<List<String>>> entry : listListMap.entrySet()) {
                String key = entry.getKey();
                List<List<String>> listList = entry.getValue();
                WritableSheet sheet = workBook.createSheet(key, sheetNo++);
                for (int i = 0; i < listList.size(); i++) {
                    List<String> list = listList.get(i);
                    for (int j = 0; j < list.size(); j++) {
                        Label label = new Label(j, i, list.get(j));
                        sheet.addCell(label);
                    }
                }
            }
            workBook.write();
            System.out.println("成功写入文件");
        } catch (Exception e) {
            System.out.println("写入文件发生异常: " + e);
        } finally {
            try {
                if (workBook != null) {
                    workBook.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                System.out.println("关闭文件流发生异常: " + e);
            }
        }
        return result;
    }
}