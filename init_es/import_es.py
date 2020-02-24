import json
from elasticsearch import Elasticsearch
from elasticsearch import helpers

with open('papers.json', 'r') as f:
    papers = json.load(f)

es = Elasticsearch(
    ['es'],
    port=9200
)

all_actions = [
    {
        '_index': 'se3',
        '_id': i,
        '_source': p
    }
    for i, p in enumerate(papers)
]

helpers.bulk(es, all_actions)

