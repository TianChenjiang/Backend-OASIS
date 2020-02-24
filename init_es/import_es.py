import json
from elasticsearch import Elasticsearch

with open('papers.json', 'r') as f:
    papers = json.load(f)

es = Elasticsearch(
    ['es'],
    port=9200
)

for p in papers:
    es.index(index='se3', body=p)

