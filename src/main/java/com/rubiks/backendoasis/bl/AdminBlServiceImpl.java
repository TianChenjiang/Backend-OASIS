package com.rubiks.backendoasis.bl;

import com.rubiks.backendoasis.blservice.AdminBlService;
import com.rubiks.backendoasis.entity.AuthorEntity;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.model.admin.*;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AdminBlServiceImpl implements AdminBlService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public BasicResponse getConferenceInfo(int page, String name) {
        Criteria criteria = new Criteria();
        criteria.where("contentType").is("conferences");
        if (!name.isEmpty()) {
            criteria.andOperator(
                    criteria.where("publicationTitle").regex(getPattern(name))
            );
        }
        Query query = new Query(criteria);
        long size = mongoTemplate.count(query, PaperEntity.class);

        query.with(PageRequest.of(page-1, Constant.pageSize));
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);

        List<String> conferences = new ArrayList<>();
        for (PaperEntity paperEntity : res) {
            conferences.add(paperEntity.getPublicationTitle());
        }
        return new BasicResponse(200, "Success", new ConferenceInfo(conferences, size));
    }

    @Override
    public BasicResponse getAffiliationInfo(int page, String name) {
        Criteria criteria = new Criteria();
        if (!name.isEmpty()) {
            criteria.andOperator(
                    criteria.where("authors.affiliation").regex(getPattern(name))
            );
        }
        Query query = new Query(criteria);
        long size = mongoTemplate.count(query, PaperEntity.class);

        query.with(PageRequest.of(page-1, Constant.pageSize));
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);

        List<String> affiliations = new ArrayList<>();
        for (PaperEntity paperEntity : res) {
            for (AuthorEntity author : paperEntity.getAuthors()) {
                if (author.getAffiliation().contains(name)) {
                    affiliations.add(author.getAffiliation());
                }
            }
        }
        return new BasicResponse(200, "Success", new AffiliationInfo(affiliations, size));
    }

    @Override
    public BasicResponse getJournalInfo(int page, String name) {
        Criteria criteria = new Criteria();
        criteria.where("contentType").is("periodicals");
        if (!name.isEmpty()) {
            criteria.andOperator(
                    criteria.where("publicationTitle").regex(getPattern(name))
            );
        }
        Query query = new Query(criteria);
        long size = mongoTemplate.count(query, PaperEntity.class);

        query.with(PageRequest.of(page-1, Constant.pageSize));
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);

        List<String> journals = new ArrayList<>();
        for (PaperEntity paperEntity : res) {
            journals.add(paperEntity.getPublicationTitle());
        }
        return new BasicResponse(200, "Success", new JournalInfo(journals, size));
    }

    @Override
    public BasicResponse getAuthorInfo(int page, String name) {
        Criteria criteria = new Criteria();
        if (!name.isEmpty()) {
            criteria.andOperator(
                    criteria.where("authors.name").regex(getPattern(name))
            );
        }
        Query query = new Query(criteria);
        long size = mongoTemplate.count(query, PaperEntity.class);

        query.with(PageRequest.of(page-1, Constant.pageSize));
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);

        List<BriefAuthor> authors = new ArrayList<>();
        for (PaperEntity p : res) {
            for (AuthorEntity authorEntity : p.getAuthors()) {
                if (authorEntity.getId() != null && authorEntity.getName().contains(name)) {
                    String auId = authorEntity.getId();
                    // 获得该学者的count和citation
                    List<PaperEntity> authorPapers = mongoTemplate.find(new Query(Criteria.where("authors.id").is(auId)), PaperEntity.class);
                    int citation = 0;
                    for (PaperEntity pa : res) { citation += p.getMetrics().getCitationCountPaper(); }
                    authors.add(new BriefAuthor(auId, authorEntity.getName(), authorPapers.size(), citation));
                }
            }
        }

        query.with(PageRequest.of(page-1, Constant.pageSize));
        return new BasicResponse(200, "Success", new AuthorInfo(authors, size));
    }


    private Pattern getPattern(String keyword) {
        Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
        return pattern;
    }

}
