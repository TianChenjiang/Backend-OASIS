package com.rubiks.backendoasis.bl;

import com.rubiks.backendoasis.blservice.DataSourceBlService;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.exception.FileFormatNotSupportException;
import com.rubiks.backendoasis.model.ImportPaperRes;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.util.CSVConvertor;
import com.rubiks.backendoasis.util.Constant;
import com.rubiks.backendoasis.util.MultiPartFileToFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rubiks.backendoasis.util.CSVConvertor.csvToJson;

@Service
public class DataSourceBlServiceImpl implements DataSourceBlService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public BasicResponse importPaperData(MultipartFile file) {
//        File f = (File) file;
        List<org.bson.Document> documents = new ArrayList<>();
        int resSize = 0;
        try  {
            File f = MultiPartFileToFile.convert(file);
            BufferedReader br = new BufferedReader(new FileReader(f.getPath()));
            int startIndex = f.getName().lastIndexOf(".");
            String format = f.getName().substring(startIndex+1);
            if (!format.equals("json") && !format.equals("csv")) {
                f.delete();
                throw new FileFormatNotSupportException();
            }

            if (format.equals("csv")) {
                List<PaperEntity> newPapers = new ArrayList<>();
                for (PaperEntity p : CSVConvertor.csvToBean(f.getPath())) {
                    String doi = p.getDoi();
                    Criteria criteria = Criteria.where("doi").is(doi);
                    Query query = new Query(criteria);
                    List<PaperEntity> paperEntities = mongoTemplate.find(query, PaperEntity.class);
                    if (paperEntities.size()!= 0) {
                        continue;
                    } else {
                        newPapers.add(p);
                    }
                }
                if (newPapers.size() != 0) {
                    mongoTemplate.save(newPapers, Constant.collectionName);
                }
                resSize = newPapers.size();
            } else {
                String line;
                while ((line = br.readLine()) != null) {
                    org.bson.Document d = org.bson.Document.parse(line);
                    String doi = d.get("doi").toString();
                    Criteria criteria = Criteria.where("doi").is(doi);
                    Query query = new Query(criteria);
                    List<PaperEntity> paperEntities = mongoTemplate.find(query, PaperEntity.class);
                    if (paperEntities.size()!= 0) {
                        continue;
                    } else {
                        documents.add(d);
                    }
                }
                if (documents.size() != 0) {
                    mongoTemplate.getCollection(Constant.collectionName).insertMany(documents);
                }
                resSize = documents.size();
            }
            f.delete(); // 删除转换而来的json文件
        } catch (IOException ex) {
            ex.getMessage();
        }
        return new BasicResponse(200, "Success", new ImportPaperRes(resSize));
    }
}










