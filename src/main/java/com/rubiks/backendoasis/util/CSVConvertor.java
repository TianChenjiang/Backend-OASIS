package com.rubiks.backendoasis.util;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.rubiks.backendoasis.entity.paper.PaperEntity;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVConvertor {

    public static List<PaperEntity> csvToBean(String filePath) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("_id", "id");
        map.put("title", "title");
        map.put("authors__name", "authors.name");
        map.put("abstract", "abstract");
        map.put("publicationTitle", "publicationTitle");
        map.put("doi", "doi");
        map.put("publicationYear", "publicationYear");
        map.put("conferenceName", "conferenceName");
        map.put("link", "link");


        HeaderColumnNameTranslateMappingStrategy<PaperEntity> strategy = new HeaderColumnNameTranslateMappingStrategy<PaperEntity>();
        strategy.setType(PaperEntity.class);
        strategy.setColumnMapping(map);

        com.opencsv.CSVReader csvReader = null;
        try {
            csvReader = new com.opencsv.CSVReader(new FileReader(filePath));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CsvToBean csvToBean = new CsvToBean();
        csvToBean.setCsvReader(csvReader);
        csvToBean.setMappingStrategy(strategy);

        List<PaperEntity> list = csvToBean.parse();
        List<PaperEntity> res  = new ArrayList<>();
        for (PaperEntity e : list) {
            if (e.getId() != null & !e.getId().isEmpty()) {
                res.add(e);
//                System.out.println(e);
            }
        }
        return res;
    }

}
