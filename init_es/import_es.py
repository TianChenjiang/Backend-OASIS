import json
from elasticsearch import Elasticsearch
from elasticsearch import helpers
import time

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

n = 500

for i in range(0, len(all_actions), n):
    action = all_actions[i: i+n]
    helpers.bulk(es, action)
    time.sleep(1)

