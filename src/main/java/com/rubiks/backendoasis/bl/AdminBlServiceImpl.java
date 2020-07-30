package com.rubiks.backendoasis.bl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubiks.backendoasis.blservice.AdminBlService;
import com.rubiks.backendoasis.entity.paper.AuthorEntity;
import com.rubiks.backendoasis.entity.paper.PaperEntity;
import com.rubiks.backendoasis.entity.recommend.SimilarAffiliation;
import com.rubiks.backendoasis.entity.recommend.SimilarAuthor;
import com.rubiks.backendoasis.exception.FileFormatNotSupportException;
import com.rubiks.backendoasis.model.paper.ImportPaperRes;
import com.rubiks.backendoasis.model.admin.*;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.util.CSVConvertor;
import com.rubiks.backendoasis.util.Constant;
import com.rubiks.backendoasis.util.MultiPartFileToFile;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AdminBlServiceImpl implements AdminBlService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ObjectMapper objectMapper;


    @CacheEvict(value = {"affiliation_rank", "affiliation_advance_rank", "author_rank", "author_advance_rank", "journal_rank", "conference_rank", "keyword_rank", "active_abstract", "academic_relation_pic"}, allEntries = true)
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
                    mongoTemplate.save(newPapers, Constant.LARGE_COLLECTION);
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
                    mongoTemplate.getCollection(Constant.LARGE_COLLECTION).insertMany(documents);
                }
                resSize = documents.size();
            }
            f.delete(); // 删除转换而来的json文件
        } catch (IOException ex) {
            ex.getMessage();
        }
        return new BasicResponse(200, "Success", new ImportPaperRes(resSize));
    }


    @Override
    public BasicResponse getConferenceInfo(int page, String name) {
        String fieldName = "publicationName";
        MatchOperation typeMatch = match(Criteria.where("contentType").is("conferences"));
        MatchOperation nameMatch = match(Criteria.where(fieldName).is(getPattern(name)));

        long previousNum = (page-1) * Constant.pageSize;
        Aggregation aggregation = newAggregation(
                typeMatch,
                nameMatch,
                group(fieldName).addToSet(fieldName).as("name"),
                skip(previousNum),
                limit(Constant.pageSize)
        );

        Aggregation countAgg = newAggregation(
                typeMatch,
                nameMatch,
                group(fieldName),
                count().as("size")
        );

        List<AdminConference> res = mongoTemplate.aggregate(aggregation, Constant.LARGE_COLLECTION, AdminConference.class).getMappedResults();
        List<ResSize> countList = mongoTemplate.aggregate(countAgg, Constant.LARGE_COLLECTION, ResSize.class).getMappedResults();
        long size = 0;
        if (countList.size() != 0) {
            size = countList.get(0).getSize();
        }
        return new BasicResponse(200, "Success", new ConferenceInfo(res, size));
    }

    @Override
    public BasicResponse getAffiliationInfo(int page, String name) {
        String fieldName = "authors.affiliation";
        MatchOperation nameMatch = match(Criteria.where(fieldName).is(getPattern(name)));

        long previousNum = (page-1) * Constant.pageSize;
        Aggregation aggregation = newAggregation(
                project("authors", "_id", "metrics"),
                nameMatch,      // 先进行一次match，为了命中索引，也为了减少进入管道的文档数量
                unwind("authors"),
                nameMatch,
//                group(fieldName).addToSet(fieldName).as("name"),
                group(fieldName, "_id").first(fieldName).as("name").first("metrics").as("metrics"),
                group("name").sum("metrics.citationCountPaper").as("citationCount").first("name").as("name").count().as("acceptanceCount"),
                sort(Sort.Direction.DESC, "acceptanceCount", "name"),
                project("name"),
                skip(previousNum),
                limit(Constant.pageSize)
        ).withOptions(newAggregationOptions().allowDiskUse(true).build());

        Aggregation countAgg = newAggregation(
                project("authors"),
                nameMatch,
                unwind("authors"),
                nameMatch,
                group(fieldName),
                count().as("size"),
                project("size")
        );

        List<AdminAffiliation> res = mongoTemplate.aggregate(aggregation, Constant.LARGE_COLLECTION, AdminAffiliation.class).getMappedResults();
        List<ResSize> countList = mongoTemplate.aggregate(countAgg, Constant.LARGE_COLLECTION, ResSize.class).getMappedResults();
        long size = 0;
        if (countList.size() != 0) {
            size = countList.get(0).getSize();
        }
        return new BasicResponse(200, "Success", new AffiliationInfo(res, size));
    }

    @Override
    public BasicResponse getJournalInfo(int page, String name) {
        String fieldName = "publicationName";
        MatchOperation typeMatch = match(Criteria.where("contentType").is("periodicals"));
        MatchOperation nameMatch = match(Criteria.where(fieldName).is(getPattern(name)));

        long previousNum = (page-1) * Constant.pageSize;
        Aggregation aggregation = newAggregation(
//                project("contentType", fieldName),
                typeMatch,
                nameMatch,
                group(fieldName).addToSet(fieldName).as("name"),
                project("name"),
                skip(previousNum),
                limit(Constant.pageSize)
        );

        Aggregation countAgg = newAggregation(
                project("contentType", fieldName),
                typeMatch,
                nameMatch,
                group(fieldName),
                count().as("size")
        );

        List<AdminJournal> res = mongoTemplate.aggregate(aggregation, Constant.LARGE_COLLECTION, AdminJournal.class).getMappedResults();
        List<ResSize> countList = mongoTemplate.aggregate(countAgg, Constant.LARGE_COLLECTION, ResSize.class).getMappedResults();
        long size = 0;
        if (countList.size() != 0) {
            size = countList.get(0).getSize();
        }
        return new BasicResponse(200, "Success", new JournalInfo(res, size));
    }

    @Override
    public BasicResponse getAuthorInfo(int page, String name) {
        String fieldName = "authors.name";
        MatchOperation nameMatch = match(Criteria.where(fieldName).is(getPattern(name)));

        long previousNum = (page-1) * Constant.pageSize;
        Aggregation aggregation = newAggregation(
//                project("authors", "metrics"),
                nameMatch,
                unwind("authors"),
                nameMatch,
                group("authors.id").addToSet(fieldName).as("name")    // 应该是同名不同人
                .addToSet("authors.id").as("authorId")
                .addToSet("authors.name").as("authorName")
                .addToSet("authors.affiliation").as("affiliation")
                .count().as("count")
                .sum("metrics.citationCountPaper").as("citation"),
                skip(previousNum),
                limit(Constant.pageSize)
        ).withOptions(newAggregationOptions().allowDiskUse(true).build());;

        Aggregation countAgg = newAggregation(
                nameMatch,
                unwind("authors"),
                nameMatch,
                group(fieldName),
                count().as("size")
        );

        List<AdminAuthor> res = mongoTemplate.aggregate(aggregation, Constant.LARGE_COLLECTION, AdminAuthor.class).getMappedResults();
        List<ResSize> countList = mongoTemplate.aggregate(countAgg, Constant.LARGE_COLLECTION, ResSize.class).getMappedResults();

        long size = 0;
        if (countList.size() != 0) {
            size = countList.get(0).getSize();
        }
        return new BasicResponse(200, "Success", new AuthorInfo(res, size));
    }

    @Override
    public BasicResponse getKeywordInfo(int page, String name) {
        String fieldName = "keywords";
        MatchOperation nameMatch = match(Criteria.where(fieldName).is(getPattern(name)));

        long previousNum = (page-1) * Constant.pageSize;
        Aggregation aggregation = newAggregation(
                project("keywords"),
                nameMatch,
                unwind("keywords"),
                nameMatch,
                group(fieldName).addToSet("keywords").as("name"),
                skip(previousNum),
                limit(Constant.pageSize)
        );

        Aggregation countAgg = newAggregation(
                project(fieldName),
                nameMatch,
                unwind("keywords"),
                nameMatch,
                group(fieldName),
                count().as("size")
        );

        List<KeywordName> res = mongoTemplate.aggregate(aggregation, Constant.LARGE_COLLECTION, KeywordName.class).getMappedResults();
        List<ResSize> countList = mongoTemplate.aggregate(countAgg, Constant.LARGE_COLLECTION, ResSize.class).getMappedResults();
        long size = 0;
        if (countList.size() != 0) {
            size = countList.get(0).getSize();
        }
        return new BasicResponse(200, "Success", new KeywordInfo(res, size));
    }


    @CacheEvict(value = {"conference_rank"}, allEntries = true)
    @Override
    public BasicResponse updateConferenceInfo(String src, String desc) {
        String fieldName = "publicationName";
        Criteria criteria = new Criteria();
        criteria.andOperator(
                criteria.where(fieldName).is(src),
                criteria.where("contentType").is("conferences"));
        Query query = new Query(criteria);

        Update update = new Update();
        update.set(fieldName, desc);
        mongoTemplate.updateMulti(query, update, PaperEntity.class);

        return new BasicResponse(200, "Success", "修改成功");
    }


    @CacheEvict(value = {"journal_rank"}, allEntries = true)
    @Override
    public BasicResponse updateJournalInfo(String src, String desc) {
        String fieldName = "publicationName";
        Criteria criteria = new Criteria();
        criteria.andOperator(criteria.where(fieldName).is(src),
                criteria.where("contentType").is("periodicals")
        );
        Query query = new Query(criteria);

        Update update = new Update();
        update.set(fieldName, desc);
        mongoTemplate.updateMulti(query, update, PaperEntity.class);

        return new BasicResponse(200, "Success", "修改成功");
    }

    @CacheEvict(value = {"affiliation_rank", "affiliation_advance_rank", "author_rank", "author_advance_rank", "journal_rank", "conference_rank", "keyword_rank", "active_abstract", "academic_relation_pic"}, allEntries = true)
    @Override
    public BasicResponse updatePaperInfo(UpdatePaperParameter parm) {
        Criteria criteria = new Criteria();
        criteria.andOperator(criteria.where("id").is(parm.getId()));
        Query query = new Query(criteria);

        PaperEntity paperEntity = mongoTemplate.findOne(query, PaperEntity.class);
        if (paperEntity != null) {
            paperEntity.setTitle(parm.getTitle());
            paperEntity.set_abstract(parm.get_abstract());
            paperEntity.setPublicationYear(parm.getPublicationYear());
            paperEntity.setMetrics(parm.getMetrics());
            paperEntity.setContentType(parm.getContentType());
            paperEntity.setPublicationName(parm.getPublicationName());
            paperEntity.setLink(parm.getLink());
            paperEntity.setKeywords(parm.getKeywords());

            List<AuthorEntity> authorEntities = paperEntity.getAuthors();
            List<String> nameList = parm.getAuthors();
            for (int i = 0; i < authorEntities.size(); i++) {
                authorEntities.get(i).setName(nameList.get(i));
            }

            mongoTemplate.save(paperEntity, Constant.LARGE_COLLECTION);
        }

        return new BasicResponse(200, "Success", "修改成功");
    }

    @CacheEvict(value = {"author_rank", "author_advance_rank", "academic_relation_pic"}, allEntries = true)
    @Override
    public BasicResponse mergeAuthorInfo(List<String> src, String desc) {
        String fieldName = "authors.id";
        for (String str : src) {
            Criteria criteria = new Criteria();
            criteria.andOperator(criteria.where(fieldName).is(str));
            Query query = new Query(criteria);

            Update update = new Update();
            update.set("authors.$.name", desc);
            mongoTemplate.updateMulti(query, update, PaperEntity.class);
        }
        return new BasicResponse(200, "Success", "修改成功");

    }

    @Override
    public BasicResponse mergeKeywordsInfo(List<String> src, String dest) {
        String fieldName = "keywords";
        for (String str: src) {
            Criteria criteria = new Criteria();
            criteria.andOperator(criteria.where(fieldName).is(str));
            Query query = new Query(criteria);

            Update update = new Update();
            update.set("keywords.$", dest);
            mongoTemplate.updateMulti(query, update, PaperEntity.class);
        }
        return new BasicResponse(200, "Success", "修改成功");
    }

    @Override
    public BasicResponse getRecommendedSimilarAffiliation() {
        List<SimilarAffiliation> res = mongoTemplate.findAll(SimilarAffiliation.class, Constant.SIMILAR_AFFILIATION_RECOMMEND);
        return new BasicResponse(200, "Success", res);
    }

    @Override
    public BasicResponse getRecommendedSimilarAuthor() {
        List<SimilarAuthor> res = mongoTemplate.findAll(SimilarAuthor.class, Constant.SIMILAR_AUTHOR_RECOMMEND);
        return new BasicResponse(200, "Success", res);
    }

    @CacheEvict(value = {"affiliation_rank", "affiliation_advance_rank", "author_rank", "author_advance_rank", "journal_rank", "conference_rank", "keyword_rank", "active_abstract", "keyword_advance_rank"}, allEntries = true)
    @Override
    public BasicResponse updateMainPageCache() {
        return new BasicResponse(200, "Success", null);
    }

    @CacheEvict(value = {"affiliation_rank", "affiliation_advance_rank"}, allEntries = true)
    @Override
    public BasicResponse mergeAffiliationInfo(List<String> src, String desc) {
        String fieldName = "authors.affiliation";
        for (String str : src) { //机构的更新比较特殊，因为用数组下标$只能更新一个，而一个数组中可能有多个机构
            Criteria criteria = new Criteria();
            criteria.andOperator(criteria.where(fieldName).is(str));
            Query query = new Query(criteria);
            List<PaperEntity> matchRes = mongoTemplate.find(query, PaperEntity.class);

            for (PaperEntity p : matchRes) {
                for (AuthorEntity authorEntity : p.getAuthors()) {
                    if (authorEntity.getAffiliation() != null && authorEntity.getAffiliation().equals(str)) {
                        authorEntity.setAffiliation(desc);
                    }
                }
                mongoTemplate.save(p, Constant.LARGE_COLLECTION);
            }

        }
        return new BasicResponse(200, "Success", "修改成功");
    }




    private Pattern getPattern(String keyword) {
        if (keyword.isEmpty()) {
            return Pattern.compile("^.*$", Pattern.CASE_INSENSITIVE);
        }
        Pattern pattern = Pattern.compile("^" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
        return pattern;
    }
}
