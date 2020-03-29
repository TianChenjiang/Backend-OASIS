package com.rubiks.backendoasis.bl;

import com.rubiks.backendoasis.blservice.AdminBlService;
import com.rubiks.backendoasis.model.admin.*;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AdminBlServiceImpl implements AdminBlService {
    @Autowired
    private MongoTemplate mongoTemplate;

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

        List<AdminConference> res = mongoTemplate.aggregate(aggregation, Constant.collectionName, AdminConference.class).getMappedResults();
        List<ResSize> countList = mongoTemplate.aggregate(countAgg, Constant.collectionName, ResSize.class).getMappedResults();
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
                unwind("authors"),
                nameMatch,
                group(fieldName).addToSet(fieldName).as("name"),
                skip(previousNum),
                limit(Constant.pageSize)
        );

        Aggregation countAgg = newAggregation(
                unwind("authors"),
                nameMatch,
                group(fieldName),
                count().as("size")
        );

        List<AdminAffiliation> res = mongoTemplate.aggregate(aggregation, Constant.collectionName, AdminAffiliation.class).getMappedResults();
        List<ResSize> countList = mongoTemplate.aggregate(countAgg, Constant.collectionName, ResSize.class).getMappedResults();
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

        List<AdminJournal> res = mongoTemplate.aggregate(aggregation, Constant.collectionName, AdminJournal.class).getMappedResults();
        List<ResSize> countList = mongoTemplate.aggregate(countAgg, Constant.collectionName, ResSize.class).getMappedResults();
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
                unwind("authors"),
                nameMatch,
                group("authors.id").addToSet(fieldName).as("name")    // 应该是同名不同人
                .addToSet("authors.id").as("authorId")
                .addToSet("authors.name").as("authorName")
                .count().as("count")
                .sum("metrics.citationCountPaper").as("citation"),
                skip(previousNum),
                limit(Constant.pageSize)
        );

        Aggregation countAgg = newAggregation(
                unwind("authors"),
                nameMatch,
                group(fieldName),
                count().as("size")
        );

        List<AdminAuthor> res = mongoTemplate.aggregate(aggregation, Constant.collectionName, AdminAuthor.class).getMappedResults();
        List<ResSize> countList = mongoTemplate.aggregate(countAgg, Constant.collectionName, ResSize.class).getMappedResults();

//        List<AdminAuthor> authors = new ArrayList<>();
//        for (PaperEntity p : res) {
//            int citation = 0;
//            if (p.getAuthors() != null) {
//                for (AuthorEntity authorEntity : p.getAuthors()) {
//                    if (authorEntity.getId() != null) {
//                        String auId = authorEntity.getId();
//                        // 获得该学者的count和citation
//                        List<PaperEntity> authorPapers = mongoTemplate.find(new Query(Criteria.where("authors.id").is(auId)), PaperEntity.class);
//                        citation += p.getMetrics().getCitationCountPaper();
//                        authors.add(new AdminAuthor(auId, authorEntity.getName(), authorPapers.size(), citation));
//                    }
//                }
//            }
//        }
        long size = 0;
        if (countList.size() != 0) {
            size = countList.get(0).getSize();
        }
        return new BasicResponse(200, "Success", new AuthorInfo(res, size));
    }

    @Override
    public BasicResponse mergeAffiliationInfo(String src, String desc) {
        return null;
    }

    @Override
    public BasicResponse updateConferenceInfo(String src, String desc) {
        return null;
    }

    @Override
    public BasicResponse updateJournalInfo(String src, String desc) {
        return null;
    }

    @Override
    public BasicResponse updatePaperInfo(UpdateAuthorParameter parameter) {
        return null;
    }

    @Override
    public BasicResponse mergeAuthorInfo(List<String> src, String desc) {
        return null;
    }


    private Pattern getPattern(String keyword) {
        if (keyword.isEmpty()) {
            return Pattern.compile("^.*$", Pattern.CASE_INSENSITIVE);
        }
        Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
        return pattern;
    }



}
