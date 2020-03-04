package com.rubiks.backendoasis.bl;

import com.mongodb.gridfs.GridFSDBFile;
import com.rubiks.backendoasis.blservice.DataSourceBlService;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.model.ImportPaperRes;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.util.MultiPartFileToFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.bson.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataSourceBlServiceImpl implements DataSourceBlService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public BasicResponse importPaperData(MultipartFile file) {
//        File f = (File) file;
        List<org.bson.Document> documents = new ArrayList<>();
        try  {
            File f = MultiPartFileToFile.convert(file);
            BufferedReader br = new BufferedReader(new FileReader(f.getPath()));
            f.delete(); // 删除转换而来的json文件
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
        } catch (IOException ex) {
            ex.getMessage();
        }
        if (documents.size() != 0) {
            mongoTemplate.getCollection("test_update").insertMany(documents);
        }

        return new BasicResponse(200, "Success", new ImportPaperRes(documents.size()));
    }
}










